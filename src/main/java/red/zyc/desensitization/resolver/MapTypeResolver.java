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
import java.util.Map;
import java.util.stream.Collectors;

/**
 * {@link Map}对象解析器
 *
 * @author zyc
 */
public class MapTypeResolver implements TypeResolver<Map<Object, Object>, AnnotatedParameterizedType> {

    @Override
    public Map<Object, Object> resolve(Map<Object, Object> value, AnnotatedParameterizedType annotatedParameterizedType) {
        AnnotatedType[] annotatedActualTypeArguments = annotatedParameterizedType.getAnnotatedActualTypeArguments();
        return value.entrySet().parallelStream().collect(Collectors.collectingAndThen(Collectors.toMap(
                entry -> TypeResolvers.resolve(entry.getKey(), annotatedActualTypeArguments[0]),
                entry -> TypeResolvers.resolve(entry.getValue(), annotatedActualTypeArguments[1])), erased -> {
            Map<Object, Object> map = InstanceCreators.getInstanceCreator(ReflectionUtil.getClass(value)).create();
            map.putAll(erased);
            return map;
        }));
    }

    @Override
    public boolean support(Object value, AnnotatedType annotatedType) {
        return value instanceof Map && annotatedType instanceof AnnotatedParameterizedType;
    }

    @Override
    public int order() {
        return 1;
    }
}
