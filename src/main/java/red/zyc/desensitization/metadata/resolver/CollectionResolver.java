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

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author zyc
 */
public class CollectionResolver implements Resolver<Collection<?>> {

    @Override
    public Collection<?> resolve(Collection<?> value, AnnotatedType... typeArguments) {
        AnnotatedType typeArgument = typeArguments[0];
        Collector collector = Collectors.toCollection(() -> ReflectionUtil.constructCollection(ReflectionUtil.getClass(value)));
        if (typeArgument instanceof AnnotatedParameterizedType) {
            AnnotatedParameterizedType annotatedParameterizedType = (AnnotatedParameterizedType) typeArgument;
            AnnotatedType[] annotatedActualTypeArguments = annotatedParameterizedType.getAnnotatedActualTypeArguments();
            Class<?> rawType = (Class<?>) typeArgument.getType();
            if (Collection.class.isAssignableFrom(rawType)) {
                return (Collection<?>) value.stream().map(o -> resolve((Collection<?>) o, annotatedActualTypeArguments)).collect(collector);
            } else if (Map.class.isAssignableFrom(rawType)) {
                return (Collection<?>) value.stream().map(o -> MAP_RESOLVER.resolve((Map<?, ?>) o, annotatedActualTypeArguments)).collect(collector);
            } else {
                return resolveOther(value, typeArgument);
            }
        } else if (typeArgument instanceof AnnotatedArrayType) {
            return (Collection<?>) value.stream().map(o -> ARRAY_RESOLVER.resolve((Object[]) o, ((AnnotatedArrayType) typeArgument).getAnnotatedGenericComponentType())).collect(collector);
        } else if (typeArgument instanceof AnnotatedTypeVariable) {
            for (AnnotatedType annotatedBound : ((AnnotatedTypeVariable) typeArgument).getAnnotatedBounds()) {
                Class<?> rawType = (Class<?>) annotatedBound.getType();
                if (Collection.class.isAssignableFrom(rawType)) {
                    if (annotatedBound instanceof AnnotatedParameterizedType) {
                        return (Collection<?>) value.stream().map(o -> resolve((Collection<?>) o, ((AnnotatedParameterizedType) annotatedBound).getAnnotatedActualTypeArguments())).collect(collector);
                    }
                    return value;
                } else if (Map.class.isAssignableFrom(rawType)) {
                    if (annotatedBound instanceof AnnotatedParameterizedType) {
                        return (Collection<?>) value.stream().map(o -> MAP_RESOLVER.resolve((Map<?, ?>) o, ((AnnotatedParameterizedType) annotatedBound).getAnnotatedActualTypeArguments())).collect(collector);
                    }
                    return value;
                } else {
                    return resolveOther(value, typeArgument);
                }
            }
            return resolveOther(value, typeArgument);
        } else if (typeArgument instanceof AnnotatedWildcardType) {
            AnnotatedWildcardType annotatedWildcardType = (AnnotatedWildcardType) typeArgument;
            AnnotatedType[] annotatedUpperBounds = annotatedWildcardType.getAnnotatedUpperBounds();
            AnnotatedType[] annotatedBounds = annotatedUpperBounds.length == 0 ? annotatedWildcardType.getAnnotatedLowerBounds() : annotatedUpperBounds;
            for (AnnotatedType annotatedBound : annotatedBounds) {
                Class<?> rawType = (Class<?>) annotatedBound.getType();
                if (Collection.class.isAssignableFrom(rawType)) {
                    if (annotatedBound instanceof AnnotatedParameterizedType) {
                        return (Collection<?>) value.stream().map(o -> resolve((Collection<?>) o, ((AnnotatedParameterizedType) annotatedBound).getAnnotatedActualTypeArguments())).collect(collector);
                    }
                    return value;
                } else if (Map.class.isAssignableFrom(rawType)) {
                    if (annotatedBound instanceof AnnotatedParameterizedType) {
                        return (Collection<?>) value.stream().map(o -> MAP_RESOLVER.resolve((Map<?, ?>) o, ((AnnotatedParameterizedType) annotatedBound).getAnnotatedActualTypeArguments())).collect(collector);
                    }
                    return value;
                } else {
                    return resolveOther(value, typeArgument);
                }
            }
            return resolveOther(value, typeArgument);
            // AnnotatedTypeBaseImpl
        } else {
            return resolveOther(value, typeArgument);
        }
    }

    @Override
    public Collection<?> resolveOther(Collection<?> value, AnnotatedType typeArgument) {
        Collector collector = Collectors.toCollection(() -> ReflectionUtil.constructCollection(ReflectionUtil.getClass(value)));
        Annotation sensitiveAnnotation = ReflectionUtil.getFirstSensitiveAnnotationOnAnnotatedType(typeArgument);
        if (sensitiveAnnotation != null) {
            return (Collection<?>) value.stream().map(o -> SensitiveUtil.handling(o, sensitiveAnnotation)).collect(collector);
        }
        Annotation eraseSensitiveAnnotation = ReflectionUtil.getEraseSensitiveAnnotationOnAnnotatedType(typeArgument);
        if (eraseSensitiveAnnotation != null) {
            return (Collection<?>) value.stream().map(SensitiveUtil::desensitize).collect(collector);
        }
        return value;
    }
}
