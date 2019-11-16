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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * 类型解析器，用来解析一些特殊的数据类型。例如{@link Collection}，{@link Map}，{@link Array}等类型。
 * 用户可以实现该接口定义特定类型的解析器，然后调用{@link Resolvers#register}方法来注册自己的类型解析器。
 *
 * @param <T> 类型解析器支持的处理类型
 * @author zyc
 * @see CollectionResolver
 * @see MapResolver
 * @see ArrayResolver
 * @see TypeVariableResolver
 * @see WildcardTypeResolver
 * @see ObjectResolver
 * @see CascadeResolver
 */
public interface Resolver<T, AT extends AnnotatedType> extends Sortable, Comparable<Resolver<?, ? extends AnnotatedType>> {

    /**
     * 解析对象，注意实现该方法的子类如果能够解析目标对象，那么最终应当返回一个新的{@link T}实例
     *
     * @param value         将要解析的对象
     * @param annotatedType 将要解析的对象的{@link AnnotatedType}
     * @return 解析后的新对象
     */
    T resolve(T value, AT annotatedType);

    /**
     * 是否支持解析目标对象
     *
     * @param value         将要解析的对象
     * @param annotatedType 将要解析的对象的{@link AnnotatedType}
     * @return 是否支持解析目标对象
     */
    boolean support(Object value, AnnotatedType annotatedType);

    /**
     * 解析器执行顺序
     *
     * @param resolver 解析器
     * @return 解析器执行顺序
     */
    @Override
    default int compareTo(Resolver<?, ? extends AnnotatedType> resolver) {
        return Integer.compare(order(), resolver.order());
    }

    /**
     * 获取{@link Logger}
     *
     * @return {@link Logger}
     */
    default Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
    }

}
