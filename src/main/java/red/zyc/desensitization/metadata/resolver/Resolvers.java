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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 一个有用的解析器帮助类。用户可以通过这个类注册自己的类型解析器，移除或者覆盖默认的解析器。
 *
 * @author zyc
 */
public final class Resolvers implements Resolver<Object> {

    private final static List<Resolver<Object>> RESOLVERS = new CopyOnWriteArrayList<>();

    private final static Resolvers INSTANCE = new Resolvers();

    static {
        register(new TypeVariableResolver());
        register(new WildcardTypeResolver());
        register(new CollectionResolver());
        register(new MapResolver());
        register(new ArrayResolver());
        register(new ObjectResolver());
    }

    private Resolvers() {
    }

    /**
     * 获取{@link Resolvers}实例
     *
     * @return {@link Resolvers}
     */
    public static Resolver<Object> instance() {
        return INSTANCE;
    }

    /**
     * 注册自己的类型解析器。<br><br>
     * 注意对于任何需要解析的对象o，都可以通过类型参数或者通配符来代替它，同时o本身可能也需要擦除敏感信息（o本身被标记了敏感注解）
     * 因此在注册自己的解析器时，强烈推荐遵守以下两个约定：
     * <ol>
     *     <li>
     *         自定义的解析器的执行顺序都应该晚于{@link TypeVariableResolver}和{@link WildcardTypeResolver}这两个解析器。
     *     </li>
     *     <li>
     *         自定义的解析器的执行顺序都应该早于{@link ObjectResolver}这个解析器。
     *     </li>
     * </ol>
     * 否则解析的结果可能会和预期不一样。
     *
     * @param resolver 目标类型解析器
     */
    @SuppressWarnings("unchecked")
    public static void register(Resolver<?> resolver) {
        RESOLVERS.add((Resolver<Object>) resolver);
        Collections.sort(RESOLVERS);
    }

    /**
     * 解析对象
     *
     * @param value         将要解析的对象
     * @param annotatedType 将要解析的对象的{@link AnnotatedType}
     * @return 解析后的新对象
     */
    public static Object resolving(Object value, AnnotatedType annotatedType) {
        return INSTANCE.resolve(value, annotatedType);
    }

    @Override
    public Object resolve(Object value, AnnotatedType annotatedType) {
        for (Resolver<Object> resolver : RESOLVERS) {
            if (resolver.support(value, annotatedType)) {
                value = resolver.resolve(value, annotatedType);
            }
        }
        return value;
    }

    @Override
    public final boolean support(Object value, AnnotatedType annotatedType) {
        return false;
    }

    @Override
    public int order() {
        return 0;
    }
}
