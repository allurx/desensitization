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

import java.lang.reflect.AnnotatedType;
import java.util.Optional;

/**
 * 对象解析器，解析被直接标注敏感注解的对象。
 *
 * @author zyc
 */
public class ObjectResolver implements Resolver<Object, AnnotatedType> {

    @Override
    public Object resolve(Object value, AnnotatedType annotatedType) {
        return Optional.ofNullable(ReflectionUtil.getFirstSensitiveAnnotationOnAnnotatedType(annotatedType))
                .map(annotation -> ReflectionUtil.getDesensitizer(annotation).desensitizing(value, annotation))
                .orElse(value);
    }

    @Override
    public boolean support(Object value, AnnotatedType annotatedType) {
        return true;
    }

    @Override
    public int order() {
        return LOWEST_PRIORITY - 1;
    }

}
