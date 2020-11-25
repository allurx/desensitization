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
package red.zyc.desensitization;

import red.zyc.desensitization.annotation.CascadeSensitive;
import red.zyc.desensitization.resolver.TypeResolvers;
import red.zyc.desensitization.support.TypeToken;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * 提供两个有用的方法进行数据脱敏：
 * <ol>
 *     <li>
 *         {@link Sensitive#desensitize(Object)} 的功能是将对象内部的所有域值进行脱敏处理。你可以传入任意一个对象，
 *         该对象的某些域上可能被标注了敏感注解，最终该方法会返回一个脱敏后的新对象，不会改变原对象。
 *     </li>
 *     <li>
 *         {@link Sensitive#desensitize(Object, TypeToken)}的功能是对单个值进行脱敏。传入的对象可以是
 *         {@link Collection}、{@link Map}、{@link Array}、{@link  String}等等类型中的某一种类型，
 *         然后需要再传入一个{@link TypeToken}以便我们能够在运行时捕获脱敏对象具体的类型以及对象上的注解。
 *         最终该方法会返回一个脱敏后的新对象，不会改变原对象。
 *     </li>
 * </ol>
 * 注意：对于以上两种方法在脱敏时可能存在对象与对象循环引用的情况，例如对象A中包含对象B的引用，
 * 对象B中也包含对象A的引用，甚至是对象中包含自身的引用，脱敏时必定会发生{@link StackOverflowError}，
 * 因此建议不要在那些循环引用的域上标注敏感注解或者避免对那些包含循环引用的对象进行脱敏。
 *
 * @author zyc
 */
public final class Sensitive {

    /**
     * 对象内部域值脱敏，该方法不会改变原对象。
     *
     * @param <T>    目标对象类型
     * @param target 目标对象
     * @return 脱敏后的新对象
     */
    public static <T> T desensitize(T target) {
        return desensitize(target, new TypeToken<@CascadeSensitive T>() {
        });
    }

    /**
     * 根据{@link TypeToken}脱敏对象，该方法不会改变原对象。
     * <p><b>注意：{@link TypeToken}不能在实例方法、实例代码块中初始化同时也不能作为成员变量初始化，
     * 必须在静态方法、静态代码块中初始化或者作为静态变量初始化。</b></p>
     *
     * @param target    目标对象
     * @param typeToken {@link TypeToken}
     * @param <T>       目标对象类型
     * @return 脱敏后的新对象
     */
    public static <T> T desensitize(T target, TypeToken<T> typeToken) {
        return Optional.ofNullable(target)
                .map(t -> typeToken)
                .map(TypeToken::getAnnotatedType)
                .map(annotatedType -> TypeResolvers.resolve(target, annotatedType))
                .orElse(target);
    }

    private Sensitive() {
    }
}
