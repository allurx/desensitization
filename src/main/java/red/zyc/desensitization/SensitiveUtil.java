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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import red.zyc.desensitization.annotation.EraseSensitive;
import red.zyc.desensitization.annotation.Sensitive;
import red.zyc.desensitization.handler.SensitiveHandler;
import red.zyc.desensitization.metadata.MapSensitiveDescriptor;
import red.zyc.desensitization.metadata.SensitiveDescriptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * @author zyc
 */
public class SensitiveUtil {

    /**
     * 保存被处理过的对象
     */
    private static ThreadLocal<List<Object>> targets = ThreadLocal.withInitial(ArrayList::new);

    /**
     * {@link ReentrantLock}
     */
    private static ReentrantLock lock = new ReentrantLock();

    /**
     * {@link Logger}
     */
    private static Logger log = LoggerFactory.getLogger(SensitiveUtil.class);


    public static <T extends Map<K, V>, K, V> T desensitizeMap(T target, MapSensitiveDescriptor<K, V> descriptor) {
        try {
            target.entrySet().stream().map(entry -> {
                desensitize(entry.getKey(), descriptor.keySensitiveDescriptor());
                desensitize(entry.getValue(), descriptor.valueSensitiveDescriptor());
                return null;
            });
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
        return target;
    }

    public static <E> E[] desensitizeArray(E[] target, SensitiveDescriptor<E> descriptor) {
        try {
            Annotation sensitiveAnnotation = descriptor.getSensitiveAnnotation();
            SensitiveHandler<E, Annotation> sensitiveHandler = getSensitiveHandler(descriptor.getSensitiveAnnotation());
            // 返回的是Object[]对象，内部存放的是E类型的值，直接返回会抛出ClassCastException
            Object[] result = Arrays.stream(target).map(o -> sensitiveHandler.handling(o, sensitiveAnnotation)).toArray();
            System.arraycopy(result, 0, target, 0, target.length);
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
        return target;
    }

    public static <T extends Collection<E>, E> T desensitizeCollection(T target, SensitiveDescriptor<E> descriptor) {
        try {
            try {
                Annotation sensitiveAnnotation = descriptor.getSensitiveAnnotation();
                SensitiveHandler<E, Annotation> sensitiveHandler = getSensitiveHandler(descriptor.getSensitiveAnnotation());
                // 集合可能是非线程安全的容器
                lock.lock();
                List<E> result = target.stream().map(o -> sensitiveHandler.handling(o, sensitiveAnnotation)).collect(Collectors.toList());
                return (T) result;
            } finally {
                lock.unlock();
            }
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
        return target;
    }


    /**
     * 对象内部域值脱敏，注意该方法会改变原对象值
     *
     * @param target 目标对象
     */
    public static <T> T desensitize(T target) {
        try {
            handle(target);
        } finally {
            targets.remove();
        }
        return target;
    }

    /**
     * 单个值脱敏，如果在脱敏期间发生任何异常或者没有找到敏感注解则不作任何处理，直接返回原值。
     * 注意该方法会改变原对象值
     *
     * @param target     目标对象
     * @param descriptor 敏感信息描述者{@link SensitiveDescriptor}
     * @param <T>        目标对象类型
     * @return 敏感信息被擦除后的值
     */
    public static <T> T desensitize(T target, SensitiveDescriptor<T> descriptor) {
        if (target == null || descriptor == null) {
            return target;
        }
        return Optional.ofNullable(descriptor.getSensitiveAnnotation()).map(sensitiveAnnotation -> {
            try {
                return SensitiveUtil.<T, Annotation>getSensitiveHandler(sensitiveAnnotation).handling(target, sensitiveAnnotation);
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
                return target;
            }
        }).orElseGet(() -> {
            log.warn("没有在{}上找到敏感注解", descriptor.getClass());
            return target;
        });
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
                Annotation sensitiveAnnotation = getFirstSensitiveAnnotationOnField(field);
                if (field.isAnnotationPresent(EraseSensitive.class)) {
                    // 域是集合，数组之类的容器，需要擦除内部的是单个敏感值
                    if (sensitiveAnnotation != null) {
                        field.set(target, eraseContainerSensitiveValue(fieldValue, sensitiveAnnotation));
                        // 域是普通的级联对象，需要递归擦除对象内部的敏感域值
                    } else {
                        handle(fieldValue);
                    }
                    continue;
                }
                // 域是单个敏感值
                if (sensitiveAnnotation != null) {
                    field.set(target, getSensitiveHandler(sensitiveAnnotation).handling(fieldValue, sensitiveAnnotation));
                }
            }
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 擦除容器类型内的单一类型的敏感值，例如集合中存放 {@link String} 类型的邮箱。目前只处理集合和数组类型的容器。
     *
     * @param value               容器对象
     * @param sensitiveAnnotation 单一类型的敏感值被标记的敏感注解，需要配合{@link EraseSensitive}注解使用
     * @param <T>                 敏感对象类型
     * @param <A>                 敏感注解类型
     * @param <E>                 容器内部元素类型
     * @return 擦除后的对象值
     * @throws Throwable 获取{@link SensitiveHandler}时可能会抛出相关异常
     * @see SensitiveUtil#getSensitiveHandler(java.lang.annotation.Annotation)
     */
    private static <T, A extends Annotation, E> T eraseContainerSensitiveValue(T value, A sensitiveAnnotation) throws Throwable {
        SensitiveHandler<E, A> sensitiveHandler = getSensitiveHandler(sensitiveAnnotation);
        // 集合
        if (value instanceof Collection) {
            @SuppressWarnings("unchecked")
            Collection<E> original = (Collection<E>) value;
            // 集合可能是非线程安全的容器
            try {
                lock.lock();
                Collection<E> result = original.stream().map(o -> sensitiveHandler.handling(o, sensitiveAnnotation)).collect(Collectors.toList());
                original.clear();
                original.addAll(result);
            } finally {
                lock.unlock();
            }
            return value;
        }
        // 数组
        if (value instanceof Object[]) {
            Object[] original = (Object[]) value;
            // 返回的是Object[]对象，内部存放的是E类型的值，直接返回会抛出ClassCastException
            Object[] result = Arrays.stream(original).map(o -> sensitiveHandler.handling(o, sensitiveAnnotation)).toArray();
            System.arraycopy(result, 0, original, 0, original.length);
            return value;
        }
        return value;
    }

    /**
     * 获取对象域上的第一个敏感注解
     *
     * @param field 对象域
     * @return 对象域上的第一个敏感注解
     */
    private static Annotation getFirstSensitiveAnnotationOnField(Field field) {
        Annotation[] annotations = field.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().isAnnotationPresent(Sensitive.class)) {
                return annotation;
            }
        }
        return null;
    }

    /**
     * 通过反射实例化敏感注解对应的{@link SensitiveHandler}
     *
     * @param annotation 敏感注解
     * @param <T>        目标对象的类型
     * @param <A>        敏感注解类型
     * @return 敏感注解对应的 {@link SensitiveHandler}
     * @throws Throwable 反射获取敏感处理器时可能发生的任何异常
     */
    private static <T, A extends Annotation> SensitiveHandler<T, A> getSensitiveHandler(A annotation) throws Throwable {
        Class<? extends Annotation> annotationClass = annotation.annotationType();
        Method method = annotationClass.getDeclaredMethod("handler");
        @SuppressWarnings("unchecked")
        Class<? extends SensitiveHandler<T, A>> handlerClass = (Class<? extends SensitiveHandler<T, A>>) method.invoke(annotation);
        return handlerClass.newInstance();
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
