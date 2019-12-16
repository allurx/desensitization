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

import red.zyc.desensitization.annotation.CascadeSensitive;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Array;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Collection;
import java.util.Map;

/**
 * 类型解析器，用来解析一些特殊的数据类型。例如{@link Collection}，{@link Map}，{@link Array}等类型。
 * 用户可以实现该接口定义特定类型的解析器，然后调用{@link TypeResolvers#register}方法来注册自己的类型解析器。
 * 对于任何需要解析的对象o来说，本质上都可以通过{@link TypeVariable 类型变量}或者{@link WildcardType 通配符}来代替它，
 * 同时o本身也可能需要擦除敏感信息（o被标记了敏感注解）或者需要擦除o内部域中的敏感信息（o被标记了{@link CascadeSensitive}注解），
 * 因此在注册解析器时，需要遵守以下两个约定：
 *
 * <ol>
 *     <li>
 *         注册的类型解析器执行顺序都应该晚于{@link TypeVariableResolver}和{@link WildcardTypeResolver}这两个解析器。
 *     </li>
 *     <li>
 *         注册的类型解析器执行顺序都应该早于{@link ObjectTypeResolver}和{@link CascadeTypeResolver}这两个解析器。
 *     </li>
 * </ol>
 * 否则解析的结果可能会和预期不一致。
 *
 * @param <T> 类型解析器支持的处理类型
 * @author zyc
 * @see CollectionTypeResolver
 * @see MapTypeResolver
 * @see ArrayTypeResolver
 * @see TypeVariableResolver
 * @see WildcardTypeResolver
 * @see ObjectTypeResolver
 * @see CascadeTypeResolver
 */
public interface TypeResolver<T, AT extends AnnotatedType> extends Sortable, Comparable<TypeResolver<?, ? extends AnnotatedType>> {

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
     * @param typeResolver 解析器
     * @return 解析器执行顺序
     */
    @Override
    default int compareTo(TypeResolver<?, ? extends AnnotatedType> typeResolver) {
        return Integer.compare(order(), typeResolver.order());
    }

}
