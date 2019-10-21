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

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
     * 克隆一个和原集合类型一样的集合。
     * 注意集合对象必须遵守{@link Collection}中的约定，定义一个无参构造函数以及
     * 带有一个{@link Collection}类型参数的构造函数。
     *
     * @param collection 原集合对象
     * @return 克隆后的集合对象
     * @see Collection
     */
    public static Collection<?> cloneCollection(Collection<?> collection) {
        try {
            Constructor<?> declaredConstructor = collection.getClass().getDeclaredConstructor(Collection.class);
            return (Collection<?>) declaredConstructor.newInstance(collection);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
