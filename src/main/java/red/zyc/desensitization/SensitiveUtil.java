/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package red.zyc.desensitization;

import lombok.extern.slf4j.Slf4j;
import red.zyc.desensitization.annotation.EraseSensitive;
import red.zyc.desensitization.annotation.Sensitive;
import red.zyc.desensitization.handler.SensitiveHandler;
import red.zyc.desensitization.metadata.SensitiveDescriptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author zyc
 */
@Slf4j
public class SensitiveUtil {

    /**
     * 保存被处理过的对象
     */
    private static ThreadLocal<List<Object>> targets = ThreadLocal.withInitial(ArrayList::new);

    /**
     * 擦除对象内部敏感数据
     *
     * @param target 目标对象
     */
    public static void desensitize(Object target) {
        try {
            handle(target);
        } finally {
            targets.remove();
        }
    }

    /**
     * 擦除单个敏感值
     *
     * @param target     目标对象
     * @param descriptor 敏感信息描述者{@link SensitiveDescriptor}
     * @param <T>        目标对象类型
     * @param <A>        敏感注解类型
     * @return 敏感信息被擦除后的值
     */
    public static <T, A extends Annotation> T desensitize(T target, SensitiveDescriptor<T, A> descriptor) {
        try {
            A sensitiveAnnotation = descriptor.getSensitiveAnnotation();
            if (sensitiveAnnotation != null && sensitiveAnnotation.annotationType().isAnnotationPresent(Sensitive.class)) {
                SensitiveHandler<T, A> sensitiveHandler = getSensitiveHandler(sensitiveAnnotation);
                // 找出field上的敏感注解
                if (sensitiveHandler != null) {
                    return sensitiveHandler.handle(target, sensitiveAnnotation);
                }
            }
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
        // 发生任何异常不作任何处理，直接返回
        return target;
    }


    /**
     * 处理复杂的对象
     *
     * @param target 目标对象
     */
    private static void handle(Object target) {
        try {
            if (target == null) {
                return;
            }
            // 引用嵌套
            if (isReferenceNested(target)) {
                return;
            }
            Class<?> targetClass = target.getClass();
            // 目标对象是集合
            if (Collection.class.isAssignableFrom(targetClass)) {
                Collection<?> collection = (Collection<?>) target;
                collection.forEach(SensitiveUtil::handle);
            }
            // 目标对象是数组
            if (target instanceof Object[]) {
                Object[] objects = (Object[]) target;
                Arrays.stream(objects).forEach(SensitiveUtil::handle);
            }
            // 目标是普通对象
            Field[] allFields = getAllFields(targetClass);
            for (Field field : allFields) {
                // 跳过final修饰的field
                if (Modifier.isFinal(field.getModifiers())) {
                    continue;
                }
                field.setAccessible(true);
                // 跳过值为null的field
                Object fieldValue = field.get(target);
                if (fieldValue == null) {
                    continue;
                }
                // 递归擦除域中的敏感信息
                if (field.isAnnotationPresent(EraseSensitive.class)) {
                    handle(fieldValue);
                }
                // 找出field上的所有注解
                Annotation[] annotations = field.getAnnotations();
                for (Annotation annotation : annotations) {
                    SensitiveHandler<?, ? extends Annotation> sensitiveHandler = getSensitiveHandler(annotation);
                    // 找出field上的敏感注解
                    if (sensitiveHandler != null) {
                        field.set(target, sensitiveHandler.handling(fieldValue, annotation));
                        // 只处理field上的第一个敏感注解
                        break;
                    }
                }
            }
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 通过反射实例化敏感注解对应的{@link SensitiveHandler}
     *
     * @param annotation 敏感注解
     * @param <T>        目标对象的类型
     * @param <A>        敏感注解类型
     * @return {@link SensitiveHandler}
     * @throws NoSuchMethodException     敏感注解没有定义handler方法
     * @throws IllegalAccessException    无法调用handler方法
     * @throws InstantiationException    无法实例化{@link SensitiveHandler}
     * @throws InvocationTargetException 无法调用handler方法
     */
    private static <T, A extends Annotation> SensitiveHandler<T, A> getSensitiveHandler(Annotation annotation) throws NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Class<? extends Annotation> annotationClass = annotation.annotationType();
        if (annotationClass.isAnnotationPresent(Sensitive.class)) {
            Method method = annotationClass.getDeclaredMethod("handler");
            // 通过反射实例化敏感注解的Handler
            @SuppressWarnings("unchecked")
            Class<? extends SensitiveHandler<T, A>> handlerClass = (Class<? extends SensitiveHandler<T, A>>) method.invoke(annotation);
            return handlerClass.newInstance();
        }
        return null;
    }

    /**
     * 目标对象可能被循环引用嵌套
     *
     * @param target 目标对象
     * @return 目标对象之前是否已经被处理过
     */
    private static boolean isReferenceNested(Object target) {
        List<Object> list = targets.get();
        for (Object o : list) {
            // 没有使用contains方法，仅仅比较目标是否引用同一个对象
            if (o == target) {
                return true;
            }
        }
        list.add(target);
        return false;
    }

    /**
     * @param targetClass 目标对象的{@code Class}
     * @return 目标对象以及所有父类定义的 {@link Field}
     */
    private static Field[] getAllFields(Class<?> targetClass) {
        List<Field> fields = new ArrayList<>(Arrays.asList(targetClass.getDeclaredFields()));
        Class<?> superclass = targetClass.getSuperclass();
        while (superclass != null && superclass != Object.class) {
            fields.addAll(Arrays.asList(superclass.getDeclaredFields()));
            superclass = superclass.getSuperclass();
        }
        return fields.toArray(new Field[0]);
    }
}
