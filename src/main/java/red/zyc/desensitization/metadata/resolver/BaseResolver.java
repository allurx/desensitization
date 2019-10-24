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

import red.zyc.desensitization.SensitiveUtil;
import red.zyc.desensitization.util.Optional;
import red.zyc.desensitization.util.ReflectionUtil;

import java.lang.reflect.AnnotatedType;
import java.util.Collection;
import java.util.Map;

/**
 * @author zyc
 */
public class BaseResolver implements Resolver<Object> {

    @Override
    public Object resolve(Object value, AnnotatedType annotatedType) {
        if (value instanceof Collection) {
            value = Resolvers.getResolver(value).resolve((Collection<?>) value, annotatedType);
        } else if (value instanceof Map) {
            value = Resolvers.getResolver(value).resolve((Map<?, ?>) value, annotatedType);
        } else if (value instanceof Object[]) {
            value = Resolvers.getResolver(value).resolve((Object[]) value, annotatedType);
        }
        Object finalValue = value;
        return Optional.ofNullable(ReflectionUtil.getFirstSensitiveAnnotationOnAnnotatedType(annotatedType))
                .map(sensitiveAnnotation -> SensitiveUtil.handling(finalValue, sensitiveAnnotation))
                .or(() -> Optional.ofNullable(ReflectionUtil.getEraseSensitiveAnnotationOnAnnotatedType(annotatedType))
                        .map(eraseSensitiveAnnotation -> SensitiveUtil.desensitize(finalValue)))
                .orElse(value);
    }
}
