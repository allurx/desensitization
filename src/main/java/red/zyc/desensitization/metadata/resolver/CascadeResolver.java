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
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * 级联擦除对象内部敏感信息
 *
 * @author zyc
 */
public class CascadeResolver implements Resolver<Object, AnnotatedType> {

    @Override
    public Object resolve(Object value, AnnotatedType annotatedType) {
        try {
            if (value == null) {
                return null;
            }
            if (isReferenceNested(value)) {
                return value;
            }
            RESOLVED.get().add(value);
            List<Field> allFields = ReflectionUtil.listAllFields(value.getClass());
            for (Field field : allFields) {
                if (Modifier.isFinal(field.getModifiers())) {
                    continue;
                }
                field.setAccessible(true);
                Object fieldValue = field.get(value);
                if (fieldValue == null) {
                    continue;
                }
                field.set(value, Resolvers.instance().resolve(fieldValue, field.getAnnotatedType()));
            }
        } catch (Throwable e) {
            getLogger().error(e.getMessage(), e);
        }
        return value;
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
    public boolean isReferenceNested(Object target) {
        // 没有使用contains方法，仅仅比较目标是否引用同一个对象。
        return RESOLVED.get().stream().anyMatch(o -> o == target);
    }
}
