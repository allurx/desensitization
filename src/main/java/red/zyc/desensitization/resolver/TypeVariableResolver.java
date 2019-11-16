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

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.AnnotatedTypeVariable;
import java.lang.reflect.TypeVariable;

/**
 * {@link TypeVariable}类型值解析器
 *
 * @author zyc
 */
public class TypeVariableResolver implements Resolver<Object, AnnotatedTypeVariable> {

    @Override
    public Object resolve(Object value, AnnotatedTypeVariable annotatedTypeVariable) {
        AnnotatedType[] annotatedBounds = annotatedTypeVariable.getAnnotatedBounds();
        for (AnnotatedType annotatedBound : annotatedBounds) {
            value = Resolvers.resolve(value, annotatedBound);
        }
        return value;
    }

    @Override
    public boolean support(Object value, AnnotatedType annotatedType) {
        return annotatedType instanceof AnnotatedTypeVariable;
    }

    @Override
    public int order() {
        return HIGHEST_PRIORITY;
    }
}
