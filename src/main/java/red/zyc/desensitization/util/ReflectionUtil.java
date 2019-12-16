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
import red.zyc.desensitization.support.InstanceCreators;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zyc
 */
public final class ReflectionUtil {

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
     * 获取目标对象以及所有父类定义的 {@link Field}。
     * <p><strong>注意：不要缓存域对象，否则在多线程运行环境下会有线程安全问题。</strong></p>
     *
     * @param targetClass 目标对象的{@link Class}
     * @return 目标对象以及所有父类定义的 {@link Field}
     */
    public static List<Field> listAllFields(Class<?> targetClass) {
        return Optional.ofNullable(targetClass)
                .filter(clazz -> clazz != Object.class)
                .map(clazz -> {
                    List<Field> fields = Stream.of(clazz.getDeclaredFields()).collect(Collectors.toList());
                    fields.addAll(listAllFields(clazz.getSuperclass()));
                    return fields;
                }).orElseGet(ArrayList::new);
    }

    /**
     * 从指定的{@code class}中获取带有指定参数的构造器
     *
     * @param clazz          指定的{@code class}
     * @param parameterTypes 构造器的参数
     * @param <T>            构造器代表的对象类型
     * @return 带有指定参数的构造器
     */
    public static <T> Constructor<T> getDeclaredConstructor(Class<T> clazz, Class<?>... parameterTypes) {
        try {
            Constructor<T> declaredConstructor = clazz.getDeclaredConstructor(parameterTypes);
            if (!declaredConstructor.isAccessible()) {
                declaredConstructor.setAccessible(true);
            }
            return declaredConstructor;
        } catch (NoSuchMethodException e) {
            return null;
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
    public static <T> Class<T> getClass(T value) {
        return (Class<T>) value.getClass();
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
            Class<Desensitizer<T, A>> desensitizerClass = (Class<Desensitizer<T, A>>) method.invoke(annotation);
            return (Desensitizer<T, A>) DESENSITIZER_CACHE.computeIfAbsent(desensitizerClass, clazz -> InstanceCreators.getInstanceCreator(clazz).create());
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

}