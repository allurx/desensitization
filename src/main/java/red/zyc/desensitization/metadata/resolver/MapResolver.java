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

import java.lang.reflect.AnnotatedType;
import java.util.Map;

/**
 * @author zyc
 */
public class MapResolver implements Resolver<Map<?, ?>> {


    @Override
    public void resolve(Map<?, ?> value, AnnotatedType... typeArguments) {
        COLLECTION_RESOLVER.resolve(value.keySet(), typeArguments[0]);
        COLLECTION_RESOLVER.resolve(value.values(), typeArguments[1]);
    }

    @Override
    public void resolveOther(Map<?, ?> value, AnnotatedType typeArgument) {
    }
}
