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

import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author zyc
 */
public class CollectionResolver extends BaseResolver implements Resolver<Collection<?>> {

    @Override
    public Collection<?> resolve(Collection<?> value, AnnotatedType annotatedType) {
        if (!(annotatedType instanceof AnnotatedParameterizedType)) {
            return value;
        }
        AnnotatedParameterizedType annotatedParameterizedType = (AnnotatedParameterizedType) annotatedType;
        AnnotatedType typeArgument = annotatedParameterizedType.getAnnotatedActualTypeArguments()[0];
        return value.stream().map(o -> resolve(o, typeArgument)).collect(collectValue(value));
    }

    private Collector<Object, ?, Collection<Object>> collectValue(Collection<?> values) {
        @SuppressWarnings("unchecked")
        Collection<Object> original = (Collection<Object>) values;
        return Collectors.toCollection(() -> ReflectionUtil.constructCollection(ReflectionUtil.getClass(original)));
    }
}
