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

import com.zyc.My;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import red.zyc.desensitization.annotation.EraseSensitive;
import red.zyc.desensitization.exception.SensitiveHandlerNotFound;
import red.zyc.desensitization.handler.SensitiveHandler;
import red.zyc.desensitization.metadata.MapSensitiveDescriptor;
import red.zyc.desensitization.metadata.SensitiveDescriptor;
import red.zyc.desensitization.util.ReflectionUtil;

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
@My(name = "1")
public class SensitiveUtil<@My(name = "2") T> {

    private List<@My(name = "3") String> aaaaaa = new ArrayList<String>();

    public void t() {
        @My(name = "4") List<@My(name = "5") String> bbbbbbb = new ArrayList<>();
        System.out.println(bbbbbbb);
    }

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


    /**
     * 对象内部域值脱敏，注意该方法会改变原对象内部的域值。
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
     * 注意该方法是否改变原对象值取决于相应的敏感注解所对应的敏感处理器的处理逻辑
     *
     * @param target     目标对象
     * @param descriptor 敏感信息描述者{@link SensitiveDescriptor}
     * @param <T>        目标对象类型
     * @return 敏感信息被擦除后的值
     */
    public static <T> T desensitize(T target, SensitiveDescriptor<T> descriptor) {
        return Optional.ofNullable(target)
                .map(t -> descriptor)
                .map(SensitiveDescriptor::getSensitiveAnnotation)
                .map(sensitiveAnnotation -> handling(target, sensitiveAnnotation))
                .orElse(target);
    }


    /**
     * 擦除{@link Map}内部的敏感值。
     * 注意该方法是否改变原对象值取决于相应的敏感注解所对应的敏感处理器的处理逻辑
     *
     * @param target     Map对象
     * @param descriptor 敏感值描述者，用来描述Map内部的敏感信息
     * @param <K>        Map的键类型
     * @param <V>        Map的值类型
     * @return 一个新的 {@link HashMap}，其中包含了原先Map中键值对被擦除敏感信息后的值
     */
    public static <K, V> Map<K, V> desensitizeMap(Map<K, V> target, MapSensitiveDescriptor<K, V> descriptor) {
        return Optional.ofNullable(target)
                .map(t -> descriptor)
                .map(sensitiveDescriptor -> target.entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                entry -> desensitize(entry.getKey(), sensitiveDescriptor.keySensitiveDescriptor()),
                                entry -> desensitize(entry.getValue(), sensitiveDescriptor.valueSensitiveDescriptor())
                        )))
                .orElse(target);


    }

    /**
     * 擦除数组内部单一类型的敏感值。
     * 注意该方法是否改变原对象值取决于相应的敏感注解所对应的敏感处理器的处理逻辑
     *
     * @param target     数组对象
     * @param descriptor 敏感值描述者，用来描述数组内部的敏感信息
     * @param <E>        数组中元素的类型
     * @return 一个新的和原数组大小类型相同数组，其中包含了原先数组中每一个被擦除敏感信息的元素
     */
    public static <E> E[] desensitizeArray(E[] target, SensitiveDescriptor<E> descriptor) {
        return Optional.ofNullable(target)
                .map(t -> descriptor)
                .map(SensitiveDescriptor::getSensitiveAnnotation)
                // 返回的是Object[]对象，内部存放的是E类型的值，直接返回会抛出ClassCastException，所以这里需要将其转换成原有的数组类型
                .map(sensitiveAnnotation -> Arrays.stream(target).map(o -> handling(o, sensitiveAnnotation)).toArray())
                .map(result -> Arrays.copyOf(result, result.length, getClass(target)))
                .orElse(target);


    }


    /**
     * 擦除集合内部单一类型的敏感值。
     * 注意该方法是否改变原对象值取决于相应的敏感注解所对应的敏感处理器的处理逻辑
     *
     * @param target     集合对象
     * @param descriptor 敏感值描述者，用来描述集合内部的敏感信息
     * @param <E>        集合中元素类型
     * @return 一个新的 {@link ArrayList}，其中包含了原先集合中元素被擦除敏感信息后的值
     */
    public static <E> Collection<E> desensitizeCollection(Collection<E> target, SensitiveDescriptor<E> descriptor) {
        return Optional.ofNullable(target)
                .map(t -> descriptor)
                .map(SensitiveDescriptor::getSensitiveAnnotation)
                .map(sensitiveAnnotation -> target.stream().map(e -> handling(e, sensitiveAnnotation)).collect(Collectors.toList()))
                .orElse((List<E>) target);
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
            Field[] allFields = ReflectionUtil.listAllFields(targetClass);
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
                Annotation sensitiveAnnotation = ReflectionUtil.getFirstSensitiveAnnotationOnField(field);
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
     * @see SensitiveUtil#getSensitiveHandler(java.lang.annotation.Annotation)
     */
    private static <T, A extends Annotation, E> T eraseContainerSensitiveValue(T value, A sensitiveAnnotation) {
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
     * 通过反射实例化敏感注解对应的{@link SensitiveHandler}
     *
     * @param annotation 敏感注解
     * @param <T>        目标对象的类型
     * @param <A>        敏感注解类型
     * @return 敏感注解对应的 {@link SensitiveHandler}
     */
    private static <T, A extends Annotation> SensitiveHandler<T, A> getSensitiveHandler(A annotation) {
        try {
            Class<? extends Annotation> annotationClass = annotation.annotationType();
            Method method = annotationClass.getDeclaredMethod("handler");
            @SuppressWarnings("unchecked")
            Class<? extends SensitiveHandler<T, A>> handlerClass = (Class<? extends SensitiveHandler<T, A>>) method.invoke(annotation);
            return handlerClass.newInstance();
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
        throw new SensitiveHandlerNotFound("没有在" + annotation + "中找到敏感处理者");
    }

    /**
     * 通过敏感注解处理相应的敏感值
     *
     * @param value               敏感值
     * @param sensitiveAnnotation 敏感注解
     * @param <T>                 敏感值类型
     * @return 处理后的值
     */
    private static <T> T handling(T value, Annotation sensitiveAnnotation) {
        return SensitiveUtil.<T, Annotation>getSensitiveHandler(sensitiveAnnotation).handling(value, sensitiveAnnotation);
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
     * 类型转换方法用来获取指定类型对象的{@link Class}，因为{@link Object#getClass()}方法返回的
     * {@link Class}的泛型是通配符类型
     *
     * @param value 对象值
     * @param <T>   对象类型
     * @return 指定类型对象的 {@link Class}
     */
    @SuppressWarnings("unchecked")
    private static <T> Class<T> getClass(T value) {
        return (Class<T>) value.getClass();
    }

}
