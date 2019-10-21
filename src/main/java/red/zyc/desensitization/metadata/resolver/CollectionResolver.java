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
import java.util.stream.Collectors;

/**
 * @author zyc
 */
public class CollectionResolver implements Resolver<Collection<?>> {

    @Override
    public void resolve(Collection<?> value, AnnotatedType... typeArguments) {
        AnnotatedType typeArgument = typeArguments[0];
        if (typeArgument instanceof AnnotatedParameterizedType) {
            AnnotatedParameterizedType annotatedParameterizedType = (AnnotatedParameterizedType) typeArgument;
            Class<?> rawType = (Class<?>) annotatedParameterizedType.getAnnotatedActualTypeArguments()[0].getType();
            if (Collection.class.isAssignableFrom(rawType)) {
                value.forEach(o -> resolve((Collection<?>) o, annotatedParameterizedType.getAnnotatedActualTypeArguments()[0]));
            } else if (Map.class.isAssignableFrom(rawType)) {
                value.forEach(o -> MAP_RESOLVER.resolve((Map<?, ?>) o, annotatedParameterizedType.getAnnotatedActualTypeArguments()[0], annotatedParameterizedType.getAnnotatedActualTypeArguments()[1]));
            } else {
                resolveOther(value, annotatedParameterizedType.getAnnotatedActualTypeArguments()[0]);
            }
        } else if (typeArgument instanceof AnnotatedArrayType) {
            value.forEach(o -> ARRAY_RESOLVER.resolve((Object[]) o, ((AnnotatedArrayType) typeArgument).getAnnotatedGenericComponentType()));
        } else if (typeArgument instanceof AnnotatedTypeVariable) {
            Arrays.stream(((AnnotatedTypeVariable) typeArgument).getAnnotatedBounds()).forEach(annotatedBound -> {
                Class<?> rawType = (Class<?>) annotatedBound.getType();
                if (Collection.class.isAssignableFrom(rawType)
                        && annotatedBound instanceof AnnotatedParameterizedType) {
                    value.forEach(o -> resolve((Collection<?>) o, ((AnnotatedParameterizedType) annotatedBound).getAnnotatedActualTypeArguments()[0]));
                } else if (Map.class.isAssignableFrom(rawType)
                        && annotatedBound instanceof AnnotatedParameterizedType) {
                    value.forEach(o -> MAP_RESOLVER.resolve((Map<?, ?>) o, ((AnnotatedParameterizedType) annotatedBound).getAnnotatedActualTypeArguments()[0], ((AnnotatedParameterizedType) annotatedBound).getAnnotatedActualTypeArguments()[1]));
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
                    value.forEach(o -> resolve((Collection<?>) o, ((AnnotatedParameterizedType) annotatedBound).getAnnotatedActualTypeArguments()[0]));
                } else if (Map.class.isAssignableFrom((Class<?>) annotatedBound.getType())
                        && annotatedBound instanceof AnnotatedParameterizedType) {
                    value.forEach(o -> MAP_RESOLVER.resolve((Map<?, ?>) o, ((AnnotatedParameterizedType) annotatedBound).getAnnotatedActualTypeArguments()[0], ((AnnotatedParameterizedType) annotatedBound).getAnnotatedActualTypeArguments()[1]));
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
    public void resolveOther(Collection<?> value, AnnotatedType typeArgument) {
        Optional.ofNullable(ReflectionUtil.getFirstSensitiveAnnotationOnAnnotatedType(typeArgument))
                .ifPresent(sensitiveAnnotation -> {
                    Collection<?> collect = value.stream().map(e -> SensitiveUtil.handling(e, sensitiveAnnotation)).collect(Collectors.toList());
                    value.clear();
                    value.addAll((Collection) collect);
                });
        Optional.ofNullable(ReflectionUtil.getEraseSensitiveAnnotationOnAnnotatedType(typeArgument))
                .ifPresent(eraseSensitiveAnnotation -> value.forEach(SensitiveUtil::desensitize));
    }
}
