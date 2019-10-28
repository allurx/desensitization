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

import red.zyc.desensitization.Sensitive;
import red.zyc.desensitization.util.Optional;
import red.zyc.desensitization.util.ReflectionUtil;

import java.lang.reflect.AnnotatedType;

/**
 * 普通对象值解析器，例如对象上可能直接被标注了敏感注解。
 *
 * @author zyc
 */
public class ObjectResolver implements Resolver<Object, AnnotatedType> {

    @Override
    public Object resolve(Object value, AnnotatedType annotatedType) {
        return Optional.ofNullable(ReflectionUtil.getFirstSensitiveAnnotationOnAnnotatedType(annotatedType))
                .map(sensitiveAnnotation -> Sensitive.handling(value, sensitiveAnnotation))
                .or(() -> Optional.ofNullable(ReflectionUtil.getEraseSensitiveAnnotationOnAnnotatedType(annotatedType))
                        .map(eraseSensitiveAnnotation -> Sensitive.desensitize(value)))
                .orElse(value);
    }

    @Override
    public boolean support(Object value, AnnotatedType annotatedType) {
        return true;
    }

    @Override
    public int order() {
        return LOWEST_PRIORITY;
    }

}
