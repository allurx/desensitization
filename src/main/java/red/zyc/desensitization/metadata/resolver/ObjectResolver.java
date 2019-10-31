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
import java.util.Optional;

/**
 * 普通对象值解析器，例如对象上可能直接被标注了敏感注解。
 *
 * @author zyc
 */
public class ObjectResolver implements Resolver<Object, AnnotatedType> {

    private Annotation sensitiveAnnotation;

    @Override
    public Object resolve(Object value, AnnotatedType annotatedType) {
        return ReflectionUtil.getDesensitizer(sensitiveAnnotation).desensitizing(value, sensitiveAnnotation);
    }

    @Override
    public boolean support(Object value, AnnotatedType annotatedType) {
        return Optional.ofNullable(ReflectionUtil.getFirstSensitiveAnnotationOnAnnotatedType(annotatedType))
                .map(annotation -> {
                    sensitiveAnnotation = annotation;
                    return true;
                }).orElse(false);
    }

    @Override
    public int order() {
        return LOWEST_PRIORITY;
    }

    /**
     * 对于直接被标注敏感注解的对象，无论如何都要重新去解析一遍。
     *
     * @param target 目标对象
     * @return 目标对象是否已经被解决
     */
    @Override
    public boolean isResolved(Object target) {
        return false;
    }

}
