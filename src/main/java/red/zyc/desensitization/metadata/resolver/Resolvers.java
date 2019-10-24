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
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zyc
 */
public final class Resolvers implements Resolver<Object> {

    private final static Map<Class<?>, Resolver<?>> RESOLVERS = new ConcurrentHashMap<>();

    private static final Resolvers INSTANCE = new Resolvers();

    static {
        register(Collection.class, new CollectionResolver());
        register(Map.class, new MapResolver());
        register(Object[].class, new ArrayResolver());
    }

    private Resolvers() {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    static Resolver<Object> getResolver(Object value) {
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

    /**
     * @return {@link Resolvers}
     */
    public static Resolver<Object> instance() {
        return INSTANCE;
    }

    /**
     * 注册自己的类型解析器
     *
     * @param clazz    目标类型
     * @param resolver 目标类型解析器
     */
    public static void register(Class<?> clazz, Resolver<?> resolver) {
        RESOLVERS.put(clazz, resolver);
    }

    @Override
    public Object resolve(Object value, AnnotatedType annotatedType) {
        return resolving(value, annotatedType);
    }
}
