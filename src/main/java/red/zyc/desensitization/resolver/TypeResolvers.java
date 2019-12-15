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
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 一个有用的类型解析器帮助类，用户可以通过这个类注册自己的类型解析器或者删除已存在的类型解析器。
 *
 * @author zyc
 */
public final class TypeResolvers {

    /**
     * 所有注册的{@link TypeResolver}
     */
    private static final SortedSet<TypeResolver<?, ? extends AnnotatedType>> TYPE_RESOLVERS = Collections.synchronizedSortedSet(new TreeSet<>());

    static {
        register(new TypeVariableTypeResolver());
        register(new WildcardTypeTypeResolver());
        register(new CollectionTypeResolver());
        register(new MapTypeResolver());
        register(new ArrayTypeResolver());
        register(new ObjectTypeResolver());
        register(new CascadeTypeResolver());
    }

    private TypeResolvers() {
    }

    /**
     * 注册自己的类型解析器。<br>
     * 注意：如果类型解析器的{@link Sortable#order()}方法返回值已经被其它解析器占用了，
     * 那么该解析器将会被忽略。这是由{@link TreeSet}类的特性所导致的。
     *
     * @param typeResolver 目标类型解析器
     * @see TreeSet
     */
    public static void register(TypeResolver<?, ? extends AnnotatedType> typeResolver) {
        TYPE_RESOLVERS.add(typeResolver);
    }

    /**
     * 从已注册的类型解析器中移除指定的解析器
     *
     * @param typeResolver 需要移除的类型解析器
     */
    public static void remove(TypeResolver<?, ? extends AnnotatedType> typeResolver) {
        TYPE_RESOLVERS.remove(typeResolver);
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
        for (TypeResolver<?, ? extends AnnotatedType> typeResolver : TYPE_RESOLVERS) {
            if (typeResolver.support(value, annotatedType)) {
                @SuppressWarnings("unchecked")
                TypeResolver<T, AT> supportedTypeResolver = (TypeResolver<T, AT>) typeResolver;
                value = supportedTypeResolver.resolve(value, annotatedType);
            }
        }
        return value;
    }

    /**
     * @return 一个不会与已注册的类型解析器顺序冲突的随机顺序值
     */
    public static int randomOrder() {
        int order = ThreadLocalRandom.current().nextInt(Sortable.HIGHEST_PRIORITY, Sortable.LOWEST_PRIORITY);
        synchronized (TYPE_RESOLVERS) {
            if (TYPE_RESOLVERS.stream().noneMatch(resolver -> resolver.order() == order)) {
                return order;
            }
            return randomOrder();
        }
    }

    /**
     * @return 所有已注册的类型解析器，返回结果是一个通过{@link Collections#synchronizedSortedSet(java.util.SortedSet)}
     * 方法包装的{@link SortedSet}，有关线程安全需要注意的事项请自行参照该包装方法。
     */
    public static SortedSet<TypeResolver<?, ? extends AnnotatedType>> registeredResolvers() {
        return TYPE_RESOLVERS;
    }
}
