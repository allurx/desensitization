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

package red.zyc.desensitization.resolver;

import red.zyc.desensitization.annotation.EraseSensitive;
import red.zyc.desensitization.util.ReflectionUtil;
import red.zyc.desensitization.util.UnsafeUtil;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Modifier;
import java.util.Optional;

/**
 * 级联擦除对象内部敏感信息
 *
 * @author zyc
 */
public class CascadeResolver implements Resolver<Object, AnnotatedType> {

    @Override
    public Object resolve(Object value, AnnotatedType annotatedType) {
        return Optional.ofNullable(value)
                .map(o -> {
                    Class<?> clazz = value.getClass();
                    Object newObject = UnsafeUtil.newInstance(clazz);
                    ReflectionUtil.listAllFields(clazz).forEach(field -> {
                        Object fieldValue;
                        if (!Modifier.isFinal(field.getModifiers()) && (fieldValue = ReflectionUtil.getFieldValue(value, field)) != null) {
                            ReflectionUtil.setFieldValue(newObject, field, Resolvers.resolve(fieldValue, field.getAnnotatedType()));
                        }
                    });
                    return newObject;
                }).orElse(value);
    }

    @Override
    public boolean support(Object value, AnnotatedType annotatedType) {
        return annotatedType.isAnnotationPresent(EraseSensitive.class);
    }

    @Override
    public int order() {
        return LOWEST_PRIORITY;
    }
}
