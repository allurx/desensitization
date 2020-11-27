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

import red.zyc.desensitization.support.InstanceCreators;
import red.zyc.desensitization.util.ReflectionUtil;

import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link Collection}对象解析器
 *
 * @author zyc
 */
public class CollectionTypeResolver implements TypeResolver<Collection<Object>, AnnotatedParameterizedType> {

    @Override
    public Collection<Object> resolve(Collection<Object> value, AnnotatedParameterizedType annotatedParameterizedType) {
        AnnotatedType typeArgument = annotatedParameterizedType.getAnnotatedActualTypeArguments()[0];
        List<Object> erased = value.parallelStream().map(o -> TypeResolvers.resolve(o, typeArgument)).collect(Collectors.toList());
        Collection<Object> collection = InstanceCreators.getInstanceCreator(ReflectionUtil.getClass(value)).create();
        collection.addAll(erased);
        return collection;
    }

    @Override
    public boolean support(Object value, AnnotatedType annotatedType) {
        return value instanceof Collection && annotatedType instanceof AnnotatedParameterizedType;
    }

    @Override
    public int order() {
        return 0;
    }
}
