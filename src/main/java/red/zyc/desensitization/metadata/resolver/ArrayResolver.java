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

import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * {@link Array}类型值解析器
 *
 * @author zyc
 */
public class ArrayResolver implements Resolver<Object[], AnnotatedArrayType> {

    @Override
    public Object[] resolve(Object[] value, AnnotatedArrayType annotatedArrayType) {
        AnnotatedType typeArgument = annotatedArrayType.getAnnotatedGenericComponentType();
        Object[] erased = Arrays.stream(value).map(o -> Resolvers.resolve(o, typeArgument)).toArray();
        return Arrays.copyOf(erased, erased.length, value.getClass());
    }

    @Override
    public boolean support(Object value, AnnotatedType annotatedType) {
        return value instanceof Object[] && annotatedType instanceof AnnotatedArrayType;
    }

    @Override
    public int order() {
        return 2;
    }

}
