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

import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.AnnotatedTypeVariable;
import java.lang.reflect.AnnotatedWildcardType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * @author zyc
 */
public class ArrayResolver implements Resolver<Object[]> {

    @Override
    public Object[] resolve(Object[] value, AnnotatedType annotatedType) {
        AnnotatedArrayType annotatedArrayType = (AnnotatedArrayType) annotatedType;
        AnnotatedType typeArgument = annotatedArrayType.getAnnotatedGenericComponentType();
        if (ReflectionUtil.isCollection(typeArgument)) {
            Object[] result = Arrays.stream(value).map(o -> COLLECTION_RESOLVER.resolve((Collection<?>) o, typeArgument)).toArray();
            return Arrays.copyOf(result, result.length, value.getClass());
        } else if (ReflectionUtil.isMap(typeArgument)) {
            Object[] result = Arrays.stream(value).map(o -> MAP_RESOLVER.resolve((Map<?, ?>) o, typeArgument)).toArray();
            return Arrays.copyOf(result, result.length, value.getClass());
        } else if (typeArgument instanceof AnnotatedArrayType) {
            Object[] result = Arrays.stream(value).map(o -> resolve((Object[]) o, typeArgument)).toArray();
            return Arrays.copyOf(result, result.length, value.getClass());
        } else if (typeArgument instanceof AnnotatedTypeVariable) {
            for (AnnotatedType annotatedBound : ((AnnotatedTypeVariable) typeArgument).getAnnotatedBounds()) {
                if (ReflectionUtil.isCollection(annotatedBound)) {
                    value = Arrays.stream(value).map(o -> COLLECTION_RESOLVER.resolve((Collection<?>) o, annotatedBound)).toArray();
                } else if (ReflectionUtil.isMap(annotatedBound)) {
                    value = Arrays.stream(value).map(o -> MAP_RESOLVER.resolve((Map<?, ?>) o, annotatedBound)).toArray();
                }
            }
            return resolveValue(value, typeArgument);
        } else if (typeArgument instanceof AnnotatedWildcardType) {
            AnnotatedWildcardType annotatedWildcardType = (AnnotatedWildcardType) typeArgument;
            AnnotatedType[] annotatedUpperBounds = annotatedWildcardType.getAnnotatedUpperBounds();
            AnnotatedType[] annotatedBounds = annotatedUpperBounds.length == 0 ? annotatedWildcardType.getAnnotatedLowerBounds() : annotatedUpperBounds;
            for (AnnotatedType annotatedBound : annotatedBounds) {
                if (ReflectionUtil.isCollection(annotatedBound)) {
                    value = Arrays.stream(value).map(o -> COLLECTION_RESOLVER.resolve((Collection<?>) o, typeArgument)).toArray();
                } else if (ReflectionUtil.isMap(annotatedBound)) {
                    value = Arrays.stream(value).map(o -> MAP_RESOLVER.resolve((Map<?, ?>) o, typeArgument)).toArray();
                }
            }
            return resolveValue(value, typeArgument);
        } else {
            return resolveValue(value, typeArgument);
        }
    }

    @Override
    public Object[] resolveValue(Object[] value, AnnotatedType typeArgument) {
        return Optional.ofNullable(ReflectionUtil.getFirstSensitiveAnnotationOnAnnotatedType(typeArgument))
                .map(sensitiveAnnotation -> {
                    Object[] result = Arrays.stream(value).map(o -> SensitiveUtil.handling(o, sensitiveAnnotation)).toArray();
                    return Arrays.copyOf(result, result.length, value.getClass());
                })
                .or(() -> Optional.ofNullable(ReflectionUtil.getEraseSensitiveAnnotationOnAnnotatedType(typeArgument))
                        .map(eraseSensitiveAnnotation -> {
                            Object[] result = Arrays.stream(value).map(SensitiveUtil::desensitize).toArray();
                            return Arrays.copyOf(result, result.length, value.getClass());
                        }))
                .orElse(value);
    }

}
