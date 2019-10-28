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

package red.zyc.desensitization.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import red.zyc.desensitization.annotation.EraseSensitive;
import red.zyc.desensitization.annotation.Sensitive;
import red.zyc.desensitization.exception.UnsupportedCollectionException;
import red.zyc.desensitization.exception.UnsupportedMapException;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

/**
 * @author zyc
 */
public class ReflectionUtil {

    /**
     * {@link Logger}
     */
    private static Logger log = LoggerFactory.getLogger(ReflectionUtil.class);

    /**
     * 获取{@link AnnotatedType}上的第一个敏感注解
     *
     * @param annotatedType {@link AnnotatedType}对象
     * @return {@link AnnotatedType}上的第一个敏感注解
     */
    public static Annotation getFirstSensitiveAnnotationOnAnnotatedType(AnnotatedType annotatedType) {
        Annotation[] annotations = annotatedType.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().isAnnotationPresent(Sensitive.class)) {
                return annotation;
            }
        }
        return null;
    }

    /**
     * 获取{@link AnnotatedType}上的{@link EraseSensitive}注解
     *
     * @param annotatedType {@link AnnotatedType}对象
     * @return {@link AnnotatedType}上的{@link EraseSensitive}注解
     */
    public static Annotation getEraseSensitiveAnnotationOnAnnotatedType(AnnotatedType annotatedType) {
        return annotatedType.getDeclaredAnnotation(EraseSensitive.class);
    }

    /**
     * 获取目标对象以及所有父类定义的 {@link Field}
     *
     * @param targetClass 目标对象的{@code Class}
     * @return 目标对象以及所有父类定义的 {@link Field}
     */
    public static Field[] listAllFields(Class<?> targetClass) {
        List<Field> fields = new ArrayList<>(Arrays.asList(targetClass.getDeclaredFields()));
        Class<?> superclass = targetClass.getSuperclass();
        while (superclass != null && superclass != Object.class) {
            fields.addAll(Arrays.asList(superclass.getDeclaredFields()));
            superclass = superclass.getSuperclass();
        }
        return fields.toArray(new Field[0]);
    }

    /**
     * 构造一个和原集合类型一样的包含脱敏结果的集合。
     * 注意集合对象必须遵守{@link Collection}中的约定，定义一个无参构造函数和
     * 带有一个{@link Collection}类型参数的构造函数。
     *
     * @param original 原集合对象的{@link Class}
     * @param erased   脱敏后的结果
     * @param <T>      原集合内部元素类型
     * @return 一个和原集合类型一样的包含脱敏结果的集合
     * @see Collection
     */
    public static <T> Collection<T> constructCollection(Class<Collection<T>> original, Collection<T> erased) {
        try {
            Constructor<Collection<T>> declaredConstructor = original.getDeclaredConstructor(Collection.class);
            return declaredConstructor.newInstance(erased);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            log.error(e.getMessage(), e);
        }
        throw new UnsupportedCollectionException("Collection对象必须遵守Collection中的约定，定义一个无参构造函数和带有一个Collection类型参数的构造函数。");
    }

    /**
     * 构造一个和原Map类型一样的包含脱敏结果Map。
     * 注意Map对象必须遵守{@link Map}中的约定，定义一个无参构造函数和
     * 带有一个{@link Map}类型参数的构造函数。
     *
     * @param original 原Map对象的{@link Class}
     * @param erased   脱敏后的结果
     * @return 一个和原Map类型一样的包含脱敏结果的Map
     * @see Map
     */
    public static <K, V> Map<K, V> constructMap(Class<Map<K, V>> original, Map<K, V> erased) {
        try {
            Constructor<Map<K, V>> declaredConstructor = original.getDeclaredConstructor(Map.class);
            return declaredConstructor.newInstance(erased);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            log.error(e.getMessage(), e);
        }
        throw new UnsupportedMapException("Map对象必须遵守Map中的约定，定义一个无参构造函数和带有一个Map类型参数的构造函数。");
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
    public static <T> Class<T> getClass(T value) {
        return (Class<T>) value.getClass();
    }

    /**
     * 获取{@link AnnotatedType}类型的原始{@link Class}
     * <ol>
     *     <li>
     *         对于{@link AnnotatedParameterizedType}类型，返回的是其本身（不是类型参数）的{@link Class}对象。
     *         例如{@code List<String>}，返回的就是{@code List.class}。
     *     </li>
     *     <li>
     *         对于{@link AnnotatedArrayType}类型，返回的是数组的{@link Class}对象。
     *         例如{@code String[]}，返回的就是{@code String[].class}。
     *     </li>
     *     <li>
     *         对于{@link AnnotatedTypeVariable}类型，返回的就是其边界的所有{@link Class}对象。
     *         例如{@code <O extends Number & Cloneable, T extends O> }，其中对于T和O返回的是
     *         {@code Number.class}和{@code Cloneable.class}这两个{@link Class}对象。
     *     </li>
     *     <li>
     *         对于{@link AnnotatedWildcardType}类型，返回的就是其上边界或者下边界的所有{@link Class}对象。
     *         例如{@code ? extend Number & Cloneable}，返回的是{@code Number.class}和{@code Cloneable.class}这两个{@link Class}对象。
     *     </li>
     *     <li>
     *         对于{@code AnnotatedTypeFactory.AnnotatedTypeBaseImpl}类型，（以上四种类型之外的普通类型），其本身的{@link Type}
     *         就是{@link Class}对象，所以直接返回就行了。
     *     </li>
     * </ol>
     *
     * @param type {@link Type}
     * @return {@link Type}类型的原始{@link Class}
     * @see ParameterizedType
     * @see GenericArrayType
     * @see TypeVariable
     * @see WildcardType
     */
    private static Class<?> getRawClass(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return (Class<?>) parameterizedType.getRawType();
        }
        if (type instanceof GenericArrayType) {
            Class<?> componentType = getRawClass(((GenericArrayType) type).getGenericComponentType());
            return Array.newInstance(componentType, 0).getClass();
        }
        if (type instanceof TypeVariable) {
            return Object.class;
        }
        if (type instanceof WildcardType) {
            return Object.class;
        }
        return (Class<?>) type;
    }

    /**
     * @param arrays 需要合并的二维数组
     * @param <T>    数组类型
     * @return 合并后的一维数组
     */
    @SuppressWarnings("unchecked")
    private static <T> T[] mergeArray(T[]... arrays) {
        return Arrays.stream(arrays)
                .reduce((classes1, classes2) -> {
                    T[] classes = Arrays.copyOf(classes1, classes1.length + classes2.length);
                    System.arraycopy(classes2, 0, classes, classes1.length, classes2.length);
                    return classes;
                }).orElse((T[]) Array.newInstance(arrays.getClass().getComponentType(), 0));
    }

}