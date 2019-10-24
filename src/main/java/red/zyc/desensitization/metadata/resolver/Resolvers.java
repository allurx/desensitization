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

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zyc
 */
public final class Resolvers {

    private final static Map<Class<?>, Resolver<?>> RESOLVERS = new ConcurrentHashMap<>();

    static {
        RESOLVERS.put(Collection.class, new CollectionResolver());
        RESOLVERS.put(Map.class, new MapResolver());
        RESOLVERS.put(Object[].class, new ArrayResolver());
    }

    public static Resolver<Object> getResolver(Object value) {
        if (value instanceof Collection) {
            return (Resolver<Object>) RESOLVERS.get(Collection.class);
        }
        if (value instanceof Map) {
            return (Resolver<Object>) RESOLVERS.get(Map.class);
        }
        if (value instanceof Object[]) {
            return (Resolver<Object>) RESOLVERS.get(Object[].class);
        }
        return (Resolver<Object>) RESOLVERS.get(value.getClass());
    }

}
