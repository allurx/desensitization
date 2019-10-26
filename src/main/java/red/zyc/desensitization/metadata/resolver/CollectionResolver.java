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

import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * {@link Collection}类型值解析器
 *
 * @author zyc
 */
public class CollectionResolver implements Resolver<Collection<?>, AnnotatedParameterizedType> {

    @Override
    public Collection<?> resolve(Collection<?> value, AnnotatedParameterizedType annotatedParameterizedType) {
        AnnotatedType typeArgument = annotatedParameterizedType.getAnnotatedActualTypeArguments()[0];
        return value.stream().map(o -> Resolvers.resolving(o, typeArgument)).collect(collect(value));
    }

    @Override
    public boolean support(Object value, AnnotatedType annotatedType) {
        return value instanceof Collection && annotatedType instanceof AnnotatedParameterizedType;
    }

    private Collector<Object, ?, Collection<Object>> collect(Collection<?> values) {
        @SuppressWarnings("unchecked")
        Collection<Object> original = (Collection<Object>) values;
        return Collectors.toCollection(() -> ReflectionUtil.constructCollection(ReflectionUtil.getClass(original)));
    }

    @Override
    public int order() {
        return 0;
    }
}
