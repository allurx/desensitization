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
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * @author zyc
 */
public class ArrayResolver implements Resolver<Object[]> {

    @Override
    public Object[] resolve(Object[] value, AnnotatedType... typeArguments) {
        AnnotatedType typeArgument = typeArguments[0];
        if (typeArgument instanceof AnnotatedParameterizedType) {
            AnnotatedParameterizedType annotatedParameterizedType = (AnnotatedParameterizedType) typeArgument;
            AnnotatedType[] annotatedActualTypeArguments = annotatedParameterizedType.getAnnotatedActualTypeArguments();
            Class<?> rawType = (Class<?>) typeArgument.getType();
            if (Collection.class.isAssignableFrom(rawType)) {
                Object[] result = Arrays.stream(value).map(o -> COLLECTION_RESOLVER.resolve((Collection<?>) o, annotatedActualTypeArguments)).toArray();
                return Arrays.copyOf(result, result.length, value.getClass());
            } else if (Map.class.isAssignableFrom(rawType)) {
                Object[] result = Arrays.stream(value).map(o -> MAP_RESOLVER.resolve((Map<?, ?>) o, annotatedActualTypeArguments)).toArray();
                return Arrays.copyOf(result, result.length, value.getClass());
            } else {
                return resolveOther(value, typeArgument);
            }
        } else if (typeArgument instanceof AnnotatedArrayType) {
            Object[] result = Arrays.stream(value).map(o -> resolve((Object[]) o, ((AnnotatedArrayType) typeArgument).getAnnotatedGenericComponentType())).toArray();
            return Arrays.copyOf(result, result.length, value.getClass());
        } else if (typeArgument instanceof AnnotatedTypeVariable) {
            for (AnnotatedType annotatedBound : ((AnnotatedTypeVariable) typeArgument).getAnnotatedBounds()) {
                Class<?> rawType = (Class<?>) annotatedBound.getType();
                if (Collection.class.isAssignableFrom(rawType)) {
                    if (annotatedBound instanceof AnnotatedParameterizedType) {
                        Object[] result = Arrays.stream(value).map(o -> COLLECTION_RESOLVER.resolve((Collection<?>) o, ((AnnotatedParameterizedType) annotatedBound).getAnnotatedActualTypeArguments())).toArray();
                        return Arrays.copyOf(result, result.length, value.getClass());
                    }
                    return value;
                } else if (Map.class.isAssignableFrom(rawType)) {
                    if (annotatedBound instanceof AnnotatedParameterizedType) {
                        Object[] result = Arrays.stream(value).map(o -> MAP_RESOLVER.resolve((Map<?, ?>) o, ((AnnotatedParameterizedType) annotatedBound).getAnnotatedActualTypeArguments())).toArray();
                        return Arrays.copyOf(result, result.length, value.getClass());
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
                        Object[] result = Arrays.stream(value).map(o -> COLLECTION_RESOLVER.resolve((Collection<?>) o, ((AnnotatedParameterizedType) annotatedBound).getAnnotatedActualTypeArguments())).toArray();
                        return Arrays.copyOf(result, result.length, value.getClass());
                    }
                    return value;
                } else if (Map.class.isAssignableFrom(rawType)) {
                    if (annotatedBound instanceof AnnotatedParameterizedType) {
                        Object[] result = Arrays.stream(value).map(o -> MAP_RESOLVER.resolve((Map<?, ?>) o, ((AnnotatedParameterizedType) annotatedBound).getAnnotatedActualTypeArguments())).toArray();
                        return Arrays.copyOf(result, result.length, value.getClass());
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
    public Object[] resolveOther(Object[] value, AnnotatedType typeArgument) {
        Annotation sensitiveAnnotation = ReflectionUtil.getFirstSensitiveAnnotationOnAnnotatedType(typeArgument);
        if (sensitiveAnnotation != null) {
            Object[] result = Arrays.stream(value).map(o -> SensitiveUtil.handling(o, sensitiveAnnotation)).toArray();
            return Arrays.copyOf(result, result.length, value.getClass());
        }
        Annotation eraseSensitiveAnnotation = ReflectionUtil.getEraseSensitiveAnnotationOnAnnotatedType(typeArgument);
        if (eraseSensitiveAnnotation != null) {
            Object[] result = Arrays.stream(value).map(SensitiveUtil::desensitize).toArray();
            return Arrays.copyOf(result, result.length, value.getClass());
        }
        return value;
    }

}
