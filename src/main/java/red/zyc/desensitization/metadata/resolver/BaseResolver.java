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

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * @author zyc
 */
public class BaseResolver<T> {

    private CollectionResolver collectionResolver = new CollectionResolver();

    public void resolve(T value, AnnotatedType typeArgument) {
        if (typeArgument instanceof AnnotatedParameterizedType) {
            AnnotatedParameterizedType annotatedParameterizedType = (AnnotatedParameterizedType) typeArgument;
            ParameterizedType type = (ParameterizedType) typeArgument.getType();
            Class<?> rawTypeOfTypeArgument = (Class<?>) type.getRawType();
            if (Collection.class.isAssignableFrom(rawTypeOfTypeArgument)) {
                ((Collection<?>) value).forEach(o -> collectionResolver.resolve((Collection<?>) o, annotatedParameterizedType.getAnnotatedActualTypeArguments()[0]));
            } else if (Map.class.isAssignableFrom(rawTypeOfTypeArgument)) {

            } else {
                resolveOther(value, typeArgument);
            }
        } else if (typeArgument instanceof AnnotatedArrayType) {
            AnnotatedType annotatedGenericComponentType = ((AnnotatedArrayType) typeArgument).getAnnotatedGenericComponentType();

        } else if (typeArgument instanceof AnnotatedTypeVariable) {
            AnnotatedTypeVariable annotatedTypeVariable = (AnnotatedTypeVariable) typeArgument;
            AnnotatedType[] annotatedBounds = annotatedTypeVariable.getAnnotatedBounds();
            Arrays.stream(annotatedBounds).forEach(annotatedBound -> {
                if (Collection.class.isAssignableFrom((Class<?>) annotatedBound.getType())
                        && annotatedBound instanceof AnnotatedParameterizedType) {
                    ((Collection<?>) value).forEach(o -> collectionResolver.resolve((Collection<?>) o, ((AnnotatedParameterizedType) annotatedBound).getAnnotatedActualTypeArguments()[0]));
                } else if (Map.class.isAssignableFrom((Class<?>) annotatedBound.getType())) {

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
                    ((Collection<?>) value).forEach(o -> collectionResolver.resolve((Collection<?>) o, ((AnnotatedParameterizedType) annotatedBound).getAnnotatedActualTypeArguments()[0]));
                } else if (Map.class.isAssignableFrom((Class<?>) annotatedBound.getType())) {

                } else {
                    resolveOther(value, typeArgument);
                }
            });
            // AnnotatedTypeBaseImpl
        } else {
            resolveOther(value, typeArgument);
        }
    }

    private void resolveOther(T value, AnnotatedType typeArgument) {

    }
}
