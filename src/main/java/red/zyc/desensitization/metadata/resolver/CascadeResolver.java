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

package red.zyc.desensitization.metadata.resolver;

import red.zyc.desensitization.annotation.EraseSensitive;
import red.zyc.desensitization.util.ReflectionUtil;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 级联擦除对象内部敏感信息
 *
 * @author zyc
 */
public class CascadeResolver implements Resolver<Object, AnnotatedType> {

    /**
     * 脱敏过的对象
     */
    static final ThreadLocal<List<Object>> RESOLVED = ThreadLocal.withInitial(ArrayList::new);

    @Override
    public Object resolve(Object value, AnnotatedType annotatedType) {
        return Optional.ofNullable(value)
                .filter(this::notReferenceNested)
                .map(o -> {
                    RESOLVED.get().add(value);
                    Class<?> clazz = value.getClass();
                    Object newObject = UnsafeAllocator.newInstance(clazz);
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

    /**
     * 判断目标对象之前是否已经脱敏过（目标对象可能被引用嵌套）
     *
     * @param target 目标对象
     * @return 目标对象之前是否已经脱敏过
     */
    private boolean notReferenceNested(Object target) {
        // 没有使用contains方法，仅仅比较目标是否引用同一个对象。
        return RESOLVED.get().stream().noneMatch(o -> o == target);
    }
}
