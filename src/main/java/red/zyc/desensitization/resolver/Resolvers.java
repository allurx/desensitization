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

import red.zyc.desensitization.annotation.EraseSensitive;

import java.lang.reflect.AnnotatedType;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * 一个有用的解析器帮助类。用户可以通过这个类注册自己的类型解析器。
 *
 * @author zyc
 */
public final class Resolvers {

    /**
     * 所有注册的{@link Resolver}
     */
    private final static Set<Resolver<?, ? extends AnnotatedType>> RESOLVERS = new TreeSet<>();

    static {
        register(new TypeVariableResolver());
        register(new WildcardTypeResolver());
        register(new CollectionResolver());
        register(new MapResolver());
        register(new ArrayResolver());
        register(new ObjectResolver());
        register(new CascadeResolver());
    }

    private Resolvers() {
    }

    /**
     * 注册自己的类型解析器。<br><br>
     * 对于任何需要解析的对象o，本质上都可以通过类型参数或者通配符来代替它，同时o本身可能也需要擦除敏感信息（o本身被标记了敏感注解）
     * 或者需要擦除o内部域中的敏感信息（o本身被标记了{@link EraseSensitive }注解），因此在注册解析器时，需要遵守以下两个约定：
     *
     * <ol>
     *     <li>
     *         注册的解析器执行顺序都应该晚于{@link TypeVariableResolver}和{@link WildcardTypeResolver}这两个解析器。
     *     </li>
     *     <li>
     *         注册的解析器执行顺序都应该早于{@link ObjectResolver}和{@link CascadeResolver}这两个解析器。
     *     </li>
     * </ol>
     * 否则解析的结果可能会和预期不一样。注意如果注册的解析器的{@link Sortable#order()}方法返回值已经有其它解析器占用了，那么该解析器将会被忽略。
     *
     * @param resolver 目标类型解析器
     * @see TreeSet
     */
    public static void register(Resolver<?, ? extends AnnotatedType> resolver) {
        RESOLVERS.add(resolver);
    }


    /**
     * 对于任何需要解析的对象o，它可能继承了一些特殊的数据类型，例如{@link Collection}、{@link Map}等等，
     * 因此我们在解析对象时需要遍历所有已注册的解析器，只要解析器支持解析对象o，都应该使用该解析器解析对象。
     * 换句话说就是对于一个需要解析的对象，其本身是可能存在多个解析器能够解析它的。
     *
     * @param value         将要解析的对象
     * @param annotatedType 将要解析的对象的{@link AnnotatedType}
     * @param <T>           将要解析的对象的类型
     * @param <AT>          将要解析的对象的{@link AnnotatedType}的类型
     * @return 解析后的值
     */
    public static <T, AT extends AnnotatedType> T resolve(T value, AT annotatedType) {
        for (Resolver<?, ? extends AnnotatedType> resolver : RESOLVERS) {
            if (resolver.support(value, annotatedType)) {
                @SuppressWarnings("unchecked")
                Resolver<T, AT> supportedResolver = (Resolver<T, AT>) resolver;
                value = supportedResolver.resolve(value, annotatedType);
            }
        }
        return value;
    }
}
