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

import red.zyc.desensitization.util.ReflectionUtil;

import java.lang.reflect.AnnotatedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * @author zyc
 */
public class MapResolver implements Resolver<Map<?, ?>> {


    @Override
    public Map<?, ?> resolve(Map<?, ?> value, AnnotatedType... typeArguments) {
        Collection<?> keys = COLLECTION_RESOLVER.resolve(new ArrayList<>(value.keySet()), typeArguments[0]);
        Collection<?> values = COLLECTION_RESOLVER.resolve(new ArrayList<>(value.values()), typeArguments[1]);
        Map<Object, Object> map = (Map<Object, Object>) ReflectionUtil.constructMap(ReflectionUtil.getClass(value));
        Iterator<?> keyIterator = keys.iterator();
        Iterator<?> valueIterator = values.iterator();
        while (keyIterator.hasNext() && valueIterator.hasNext()) {
            map.put(keyIterator.next(), valueIterator.next());
        }
        return map;
    }

    @Override
    public Map<?, ?> resolveOther(Map<?, ?> value, AnnotatedType typeArgument) {
        throw new UnsupportedOperationException();
    }
}
