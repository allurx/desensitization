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

import red.zyc.desensitization.annotation.Sensitive;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zyc
 */
public class ReflectionUtil {

    /**
     * 获取对象域上的第一个敏感注解
     *
     * @param field 对象域
     * @return 对象域上的第一个敏感注解
     */
    public static Annotation getFirstSensitiveAnnotationOnField(Field field) {
        Annotation[] annotations = field.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().isAnnotationPresent(Sensitive.class)) {
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
    public static Field[] listAllFields(Class<?> targetClass) {
        List<Field> fields = new ArrayList<>(Arrays.asList(targetClass.getDeclaredFields()));
        Class<?> superclass = targetClass.getSuperclass();
        while (superclass != null && superclass != Object.class) {
            fields.addAll(Arrays.asList(superclass.getDeclaredFields()));
            superclass = superclass.getSuperclass();
        }
        return fields.toArray(new Field[0]);
    }
}
