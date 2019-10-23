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
     * 设置指定对象中某个{@link Field}的值，注意该方法可能会导致{@link IllegalAccessException}，
     * 请确保在调用该方法请提前调用{@link AccessibleObject#setAccessible(boolean)}方法设置允许访问域对象。
     *
     * @param target 指定对象
     * @param field  指定对象的{@link Field}
     * @param value  将要设置的值
     */
    public static void setFieldValue(Object target, Field field, Object value) {
        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        }
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
     * 构造一个和原集合类型一样的空集合。
     * 注意集合对象必须遵守{@link Collection}中的约定，定义一个无参构造函数和
     * 带有一个{@link Collection}类型参数的构造函数。
     *
     * @param collectionClass 原集合对象的{@link Class}
     * @param <T>             原集合内部元素类型
     * @return 一个和原集合类型一样的空集合
     * @see Collection
     */
    public static <T> Collection<T> constructCollection(Class<? extends Collection<T>> collectionClass) {
        try {
            Constructor<? extends Collection<T>> declaredConstructor = collectionClass.getDeclaredConstructor(Collection.class);
            return declaredConstructor.newInstance(new ArrayList<>());
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            log.error(e.getMessage(), e);
        }
        throw new UnsupportedCollectionException("Collection对象必须遵守Collection中的约定，定义一个无参构造函数和带有一个Collection类型参数的构造函数。");
    }

    /**
     * 构造一个和原Map类型一样的空Map。
     * 注意Map对象必须遵守{@link Map}中的约定，定义一个无参构造函数和
     * 带有一个{@link Map}类型参数的构造函数。
     *
     * @param mapClass 原Map对象的{@link Class}
     * @return 一个和原Map类型一样的空Map
     * @see Map
     */
    public static Map<?, ?> constructMap(Class<? extends Map<?, ?>> mapClass) {
        try {
            Constructor<? extends Map<?, ?>> declaredConstructor = mapClass.getDeclaredConstructor(Map.class);
            return declaredConstructor.newInstance(new HashMap<>(16));
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
     *         例如{@code List<String>}，返回的就是{@code List}的{@link Class}对象。
     *     </li>
     *     <li>
     *         对于{@link AnnotatedArrayType}类型，返回的是数组内部元素的{@link Class}对象。
     *         例如{@code String[]}，返回的就是{@code String}的{@link Class}对象。
     *     </li>
     *     <li>
     *         对于{@link AnnotatedTypeVariable}类型，返回的就是其边界的所有{@link Class}对象。
     *         例如{@code <O extends Number & Cloneable, T extends O> }，其中对于T和O返回的都是
     *         {@link Number}和{@link Cloneable}两个{@link Class}对象。
     *     </li>
     *     <li>
     *         对于{@link AnnotatedWildcardType}类型，返回的就是其上边界或者下边界的所有{@link Class}对象。
     *         例如{@code ? extend Number & Cloneable}，返回的就是{@link Number}和{@link Cloneable}两个{@link Class}对象。
     *     </li>
     *     <li>
     *         对于{@code AnnotatedTypeFactory.AnnotatedTypeBaseImpl}类型，（以上四种类型之外的普通类型），其本身的{@link Type}
     *         就是{@link Class}对象，所以直接返回就行了。
     *     </li>
     * </ol>
     *
     * @param type {@link AnnotatedType}
     * @return {@link AnnotatedType}类型的原始{@link Class}
     * @see AnnotatedParameterizedType
     * @see AnnotatedArrayType
     * @see AnnotatedTypeVariable
     * @see AnnotatedWildcardType
     */
    public static Class<?>[] getRawClass(AnnotatedType type) {
        if (type instanceof AnnotatedParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type.getType();
            return new Class<?>[]{(Class<?>) parameterizedType.getRawType()};
        }
        if (type instanceof AnnotatedArrayType) {
            AnnotatedArrayType annotatedArrayType = (AnnotatedArrayType) type;
            return getRawClass(annotatedArrayType.getAnnotatedGenericComponentType());
        }
        if (type instanceof AnnotatedTypeVariable) {
            AnnotatedTypeVariable annotatedTypeVariable = (AnnotatedTypeVariable) type;
            AnnotatedType[] annotatedBounds = annotatedTypeVariable.getAnnotatedBounds();
            return Arrays.stream(annotatedBounds)
                    .map(ReflectionUtil::getRawClass)
                    .reduce((classes1, classes2) -> mergeArray(classes1, classes2)).orElse(new Class<?>[0]);
        }
        if (type instanceof AnnotatedWildcardType) {
            AnnotatedWildcardType annotatedWildcardType = (AnnotatedWildcardType) type;
            AnnotatedType[] annotatedUpperBounds = annotatedWildcardType.getAnnotatedUpperBounds();
            AnnotatedType[] annotatedBounds = annotatedUpperBounds.length == 0 ? annotatedWildcardType.getAnnotatedLowerBounds() : annotatedUpperBounds;
            return Arrays.stream(annotatedBounds)
                    .map(ReflectionUtil::getRawClass)
                    .reduce((classes1, classes2) -> mergeArray(classes1, classes2)).orElse(new Class<?>[0]);
        }
        return new Class<?>[]{(Class<?>) type.getType()};
    }

    /**
     * 判断{@link AnnotatedType}代表的原始{@link Class}是否是{@link Collection}
     *
     * @param type {@link AnnotatedType}
     * @return {@link AnnotatedType}代表的原始{@link Class}是否是{@link Collection}
     */
    public static boolean isCollection(AnnotatedType type) {
        return Arrays.stream(getRawClass(type)).anyMatch(Collection.class::isAssignableFrom);
    }

    /**
     * 判断{@link AnnotatedType}代表的原始{@link Class}是否是{@link Map}
     *
     * @param type {@link AnnotatedType}
     * @return {@link AnnotatedType}代表的原始{@link Class}是否是{@link Map}
     */
    public static boolean isMap(AnnotatedType type) {
        return Arrays.stream(getRawClass(type)).anyMatch(Map.class::isAssignableFrom);
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