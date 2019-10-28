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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 一个有用的解析器帮助类。用户可以通过这个类注册自己的类型解析器，移除或者覆盖默认的解析器。
 *
 * @author zyc
 */
public final class Resolvers implements Resolver<Object, AnnotatedType> {

    private final static List<Resolver<Object, AnnotatedType>> RESOLVERS = new CopyOnWriteArrayList<>();

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
    public static Resolver<Object, AnnotatedType> instance() {
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
    public static void register(Resolver<?, ? extends AnnotatedType> resolver) {
        RESOLVERS.add((Resolver<Object, AnnotatedType>) resolver);
        Collections.sort(RESOLVERS);
    }

    /**
     * 帮助方法用来类型转换
     *
     * @param <T>           将要解析的对象类型
     * @param value         将要解析的对象
     * @param annotatedType 将要解析的对象的{@link AnnotatedType}
     * @return 解析后的新对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T resolving(T value, AnnotatedType annotatedType) {
        return (T) INSTANCE.resolve(value, annotatedType);
    }


    /**
     * 对于任何需要解析的对象o，它可能继承了一些特殊的数据类型，例如{@link Collection}、{@link Map}等等，
     * 因此我们在解析对象时需要遍历所有已注册的解析器，只要解析器支持解析对象o，都应该使用该解析器解析对象。
     * 换句话说就是对于一个需要解析的对象，其本身是可能存在多个解析器的。
     *
     * @param value         将要解析的对象
     * @param annotatedType 将要解析的对象的{@link AnnotatedType}
     * @return 解析后的值
     */
    @Override
    public Object resolve(Object value, AnnotatedType annotatedType) {
        for (Resolver<Object, AnnotatedType> resolver : RESOLVERS) {
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
