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

import red.zyc.desensitization.annotation.SensitiveAnnotation;
import red.zyc.desensitization.desensitizer.Desensitizer;
import red.zyc.desensitization.exception.DesensitizationException;
import red.zyc.desensitization.exception.UnsupportedCollectionException;
import red.zyc.desensitization.exception.UnsupportedMapException;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zyc
 */
public final class ReflectionUtil {

    /**
     * 域缓存
     */
    private static final Map<Class<?>, List<Field>> FIELD_CACHE = new ConcurrentHashMap<>();
    /**
     * 脱敏器缓存
     */
    private static final Map<Class<? extends Desensitizer<?, ? extends Annotation>>, Desensitizer<?, ? extends Annotation>> DESENSITIZER_CACHE = new ConcurrentHashMap<>();

    private ReflectionUtil() {
    }

    /**
     * 获取{@link AnnotatedType}上的第一个直接存在的敏感注解
     *
     * @param annotatedType {@link AnnotatedType}对象
     * @return {@link AnnotatedType}上的第一个敏感注解
     */
    public static Annotation getFirstDirectlyPresentSensitiveAnnotation(AnnotatedType annotatedType) {
        Annotation[] annotations = annotatedType.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().isAnnotationPresent(SensitiveAnnotation.class)) {
                return annotation;
            }
        }
        return null;
    }

    /**
     * 获取目标对象以及所有父类定义的 {@link Field}
     *
     * @param targetClass 目标对象的{@code Class}
     * @return 目标对象以及所有父类定义的 {@link Field}
     */
    public static List<Field> listAllFields(Class<?> targetClass) {
        return Optional.ofNullable(targetClass)
                .filter(clazz -> clazz != Object.class)
                .map(c -> FIELD_CACHE.computeIfAbsent(c, clazz -> {
                    List<Field> fields = Stream.of(targetClass.getDeclaredFields()).collect(Collectors.toList());
                    fields.addAll(listAllFields(targetClass.getSuperclass()));
                    return fields;
                })).orElseGet(ArrayList::new);
    }

    /**
     * 构造一个和原集合类型一样的包含脱敏结果的集合。
     * 注意集合对象必须遵守{@link Collection}中的约定，定义一个无参构造函数和
     * 带有一个{@link Collection}类型参数的构造函数。
     *
     * @param original 原集合对象的{@link Class}
     * @param erased   脱敏后的结果
     * @param <T>      集合内部元素类型
     * @return 一个和原集合类型一样的包含脱敏结果的集合
     * @see Collection
     */
    public static <T> Collection<T> constructCollection(Class<? extends Collection<T>> original, Collection<T> erased) {
        try {
            Constructor<? extends Collection<T>> declaredConstructor = original.getDeclaredConstructor(Collection.class);
            return declaredConstructor.newInstance(erased);
        } catch (Exception e) {
            throw new UnsupportedCollectionException(original + "必须遵守Collection中的约定，定义一个无参构造函数和带有一个Collection类型参数的构造函数。", e);
        }
    }

    /**
     * 构造一个和原Map类型一样的包含脱敏结果Map。
     * 注意Map对象必须遵守{@link Map}中的约定，定义一个无参构造函数和
     * 带有一个{@link Map}类型参数的构造函数。
     *
     * @param original 原Map对象的{@link Class}
     * @param erased   脱敏后的结果
     * @param <K>      Map的键类型
     * @param <V>      Map的值类型
     * @return 一个和原Map类型一样的包含脱敏结果的Map
     * @see Map
     */
    public static <K, V> Map<K, V> constructMap(Class<? extends Map<K, V>> original, Map<K, V> erased) {
        try {
            Constructor<? extends Map<K, V>> declaredConstructor = original.getDeclaredConstructor(Map.class);
            return declaredConstructor.newInstance(erased);
        } catch (Exception e) {
            throw new UnsupportedMapException(original + "必须遵守Map中的约定，定义一个无参构造函数和带有一个Map类型参数的构造函数。", e);
        }
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
    public static <T> Class<? extends T> getClass(T value) {
        return (Class<? extends T>) value.getClass();
    }

    /**
     * 实例化敏感注解对应的{@link Desensitizer}
     *
     * @param annotation 敏感注解
     * @param <T>        脱敏器支持的目标类型
     * @param <A>        脱敏器支持的注解类型
     * @return 敏感注解对应的 {@link Desensitizer}
     */
    @SuppressWarnings("unchecked")
    public static <T, A extends Annotation> Desensitizer<T, A> getDesensitizer(A annotation) {
        try {
            Class<? extends Annotation> annotationClass = annotation.annotationType();
            Method method = annotationClass.getDeclaredMethod("desensitizer");
            Class<? extends Desensitizer<T, A>> desensitizerClass = (Class<? extends Desensitizer<T, A>>) method.invoke(annotation);
            return (Desensitizer<T, A>) DESENSITIZER_CACHE.computeIfAbsent(desensitizerClass, UnsafeUtil::newInstance);
        } catch (Exception e) {
            throw new DesensitizationException("通过" + annotation.annotationType() + "实例化脱敏器失败", e);
        }
    }

    /**
     * 获取目标对象中某个{@link Field}的值
     *
     * @param target 目标对象
     * @param field  目标对象的{@link Field}
     * @return {@link Field}的值
     */
    public static Object getFieldValue(Object target, Field field) {
        try {
            if (field.isAccessible()) {
                return field.get(target);
            }
            field.setAccessible(true);
            return field.get(target);
        } catch (Exception e) {
            throw new DesensitizationException("获取" + target.getClass() + "的域" + field.getName() + "失败。", e);
        }
    }

    /**
     * 设置目标对象某个域的值
     *
     * @param target   目标对象
     * @param field    目标对象的{@link Field}
     * @param newValue 将要设置的新值
     */
    public static void setFieldValue(Object target, Field field, Object newValue) {
        try {
            if (field.isAccessible()) {
                field.set(target, newValue);
                return;
            }
            field.setAccessible(true);
            field.set(target, newValue);
        } catch (Exception e) {
            throw new DesensitizationException("设置" + target.getClass() + "的域" + field.getName() + "失败。", e);
        }
    }

    /**
     * 获取{@link Type}类型的原始{@link Class}
     * <ol>
     *     <li>
     *         对于{@link ParameterizedType}类型，返回的是其本身（不是类型参数）的{@link Class}对象。
     *         例如{@code List<String>}，返回的就是{@code List.class}。
     *     </li>
     *     <li>
     *         对于{@link GenericArrayType}类型，返回的是数组的{@link Class}对象。
     *         例如{@code String[]}，返回的就是{@code String[].class}。
     *     </li>
     *     <li>
     *         对于{@link TypeVariable}类型，返回的就是{@code Object.class}。
     *     </li>
     *     <li>
     *         对于{@link WildcardType}类型，返回的就是{@code Object.class}。
     *     </li>
     *     <li>
     *         以上四种类型之外的类型，其本身就是{@link Class}对象，所以直接返回就行了。
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
     * 合并数组
     *
     * @param arrays 需要合并的二维数组
     * @param <T>    数组类型
     * @return 合并后的一维数组
     */
    @SuppressWarnings("unchecked")
    private static <T> T[] mergeArray(T[]... arrays) {
        return Arrays.stream(arrays)
                .reduce((array1, array2) -> {
                    T[] array = Arrays.copyOf(array1, array1.length + array2.length);
                    System.arraycopy(array2, 0, array, array1.length, array2.length);
                    return array;
                }).orElse((T[]) Array.newInstance(arrays.getClass().getComponentType(), 0));
    }

}