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
import red.zyc.desensitization.util.ReflectionUtil;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * @author zyc
 */
public class ArrayResolver implements Resolver<Object[]> {

    @Override
    public void resolve(Object[] value, AnnotatedType... typeArguments) {
        AnnotatedType typeArgument = typeArguments[0];
        if (typeArgument instanceof AnnotatedParameterizedType) {
            AnnotatedParameterizedType annotatedParameterizedType = (AnnotatedParameterizedType) typeArgument;
            ParameterizedType type = (ParameterizedType) typeArgument.getType();
            Class<?> rawType = (Class<?>) type.getRawType();
            if (Collection.class.isAssignableFrom(rawType)) {
                Arrays.stream(value).forEach(o -> COLLECTION_RESOLVER.resolve((Collection<?>) o, annotatedParameterizedType.getAnnotatedActualTypeArguments()[0]));
            } else if (Map.class.isAssignableFrom(rawType)) {
                Arrays.stream(value).forEach(o -> MAP_RESOLVER.resolve((Map<?, ?>) o, annotatedParameterizedType.getAnnotatedActualTypeArguments()[0], annotatedParameterizedType.getAnnotatedActualTypeArguments()[1]));
            } else {
                resolveOther(value, typeArgument);
            }
        } else if (typeArgument instanceof AnnotatedArrayType) {
            Arrays.stream(value).forEach(o -> resolve((Object[]) o, ((AnnotatedArrayType) typeArgument).getAnnotatedGenericComponentType()));
        } else if (typeArgument instanceof AnnotatedTypeVariable) {
            Arrays.stream(((AnnotatedTypeVariable) typeArgument).getAnnotatedBounds()).forEach(annotatedBound -> {
                Class<?> rawType = (Class<?>) annotatedBound.getType();
                if (Collection.class.isAssignableFrom(rawType)
                        && annotatedBound instanceof AnnotatedParameterizedType) {
                    Arrays.stream(value).forEach(o -> COLLECTION_RESOLVER.resolve((Collection<?>) o, ((AnnotatedParameterizedType) annotatedBound).getAnnotatedActualTypeArguments()[0]));
                } else if (Map.class.isAssignableFrom(rawType)
                        && annotatedBound instanceof AnnotatedParameterizedType) {
                    Arrays.stream(value).forEach(o -> MAP_RESOLVER.resolve((Map<?, ?>) o, ((AnnotatedParameterizedType) annotatedBound).getAnnotatedActualTypeArguments()[0], ((AnnotatedParameterizedType) annotatedBound).getAnnotatedActualTypeArguments()[1]));
                } else {
                    resolveOther(value, typeArgument);
                }
            });
        } else if (typeArgument instanceof AnnotatedWildcardType) {
            AnnotatedWildcardType annotatedWildcardType = (AnnotatedWildcardType) typeArgument;
            AnnotatedType[] annotatedUpperBounds = annotatedWildcardType.getAnnotatedUpperBounds();
            AnnotatedType[] annotatedBounds = annotatedUpperBounds.length == 0 ? annotatedWildcardType.getAnnotatedLowerBounds() : annotatedUpperBounds;
            Arrays.stream(annotatedBounds).forEach(annotatedBound -> {
                if (Collection.class.isAssignableFrom((Class<?>) annotatedBound.getType())
                        && annotatedBound instanceof AnnotatedParameterizedType) {
                    Arrays.stream(value).forEach(o -> COLLECTION_RESOLVER.resolve((Collection<?>) o, ((AnnotatedParameterizedType) annotatedBound).getAnnotatedActualTypeArguments()[0]));
                } else if (Map.class.isAssignableFrom((Class<?>) annotatedBound.getType())
                        && annotatedBound instanceof AnnotatedParameterizedType) {
                    Arrays.stream(value).forEach(o -> MAP_RESOLVER.resolve((Map<?, ?>) o, ((AnnotatedParameterizedType) annotatedBound).getAnnotatedActualTypeArguments()[0], ((AnnotatedParameterizedType) annotatedBound).getAnnotatedActualTypeArguments()[1]));
                } else {
                    resolveOther(value, typeArgument);
                }
            });
            // AnnotatedTypeBaseImpl
        } else {
            resolveOther(value, typeArgument);
        }
    }

    @Override
    public void resolveOther(Object[] value, AnnotatedType typeArgument) {
        Optional.ofNullable(ReflectionUtil.getFirstSensitiveAnnotationOnAnnotatedType(typeArgument))
                .ifPresent(sensitiveAnnotation -> {
                    Object[] result = Arrays.stream(value).map(o -> SensitiveUtil.handling(o, sensitiveAnnotation)).toArray();
                    System.arraycopy(result, 0, value, 0, value.length);
                });
        Optional.ofNullable(ReflectionUtil.getEraseSensitiveAnnotationOnAnnotatedType(typeArgument))
                .ifPresent(eraseSensitiveAnnotation -> {
                    Object[] result = Arrays.stream(value).map(SensitiveUtil::desensitize).toArray();
                    System.arraycopy(result, 0, value, 0, value.length);
                });
    }
}
