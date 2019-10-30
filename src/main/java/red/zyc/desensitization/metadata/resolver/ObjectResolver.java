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

import red.zyc.desensitization.util.ReflectionUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Optional;

/**
 * 普通对象值解析器，例如对象上可能直接被标注了敏感注解。
 *
 * @author zyc
 */
public class ObjectResolver implements Resolver<Object, AnnotatedType> {

    @Override
    public Object resolve(Object value, AnnotatedType annotatedType) {
        return Optional.ofNullable(ReflectionUtil.getFirstSensitiveAnnotationOnAnnotatedType(annotatedType))
                .map(sensitiveAnnotation -> erase(value, sensitiveAnnotation))
                .orElse(Optional.ofNullable(ReflectionUtil.getEraseSensitiveAnnotationOnAnnotatedType(annotatedType))
                        .map(eraseSensitiveAnnotation -> cascadeErase(value))
                        .orElse(value));
    }

    @Override
    public boolean support(Object value, AnnotatedType annotatedType) {
        return true;
    }

    @Override
    public int order() {
        return LOWEST_PRIORITY;
    }

    /**
     * 级联擦除敏感对象
     *
     * @param target 目标对象
     */
    private <T> T cascadeErase(T target) {
        try {
            if (target == null) {
                return null;
            }
            List<Field> allFields = ReflectionUtil.listAllFields(target.getClass());
            for (Field field : allFields) {
                if (Modifier.isFinal(field.getModifiers())) {
                    continue;
                }
                field.setAccessible(true);
                Object fieldValue = field.get(target);
                if (fieldValue == null) {
                    continue;
                }
                field.set(target, Resolvers.instance().resolve(fieldValue, field.getAnnotatedType()));
            }
        } catch (Throwable e) {
            getLogger().error(e.getMessage(), e);
        }
        return target;
    }


    /**
     * 擦除单个敏感值
     *
     * @param value               敏感值
     * @param sensitiveAnnotation 敏感注解
     * @param <T>                 敏感值类型
     * @return 脱敏后的值
     */
    private <T> T erase(T value, Annotation sensitiveAnnotation) {
        return ReflectionUtil.<T, Annotation>getDesensitizer(sensitiveAnnotation).desensitizing(value, sensitiveAnnotation);
    }

}
