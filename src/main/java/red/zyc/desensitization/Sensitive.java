/*
 * Copyright 2023 the original author or authors.
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

import red.zyc.parser.AnnotationParser;
import red.zyc.parser.type.AnnotatedTypeToken;
import red.zyc.parser.type.Cascade;

import java.lang.reflect.Field;
import java.util.Optional;

/**
 * 提供两个有用的方法进行数据脱敏：
 * <ol>
 *     <li>{@link Sensitive#desensitize(Object) 脱敏对象内部所有非常量域}</li>
 *     <li>{@link Sensitive#desensitize(Object, AnnotatedTypeToken) 根据对象的AnnotatedTypeToken进行脱敏}</li>
 * </ol>
 *
 * @author zyc
 * @see AnnotatedTypeToken
 */
public final class Sensitive {

    private Sensitive() {
    }

    /**
     * 对象{@link Field}脱敏
     *
     * @param <T>    目标对象类型
     * @param target 目标对象
     * @return 脱敏后的新对象
     */
    public static <T> T desensitize(T target) {
        return desensitize(target, new AnnotatedTypeToken<@Cascade T>() {
        });
    }

    /**
     * 根据对象的{@link AnnotatedTypeToken}进行脱敏
     *
     * @param target    目标对象
     * @param typeToken 目标对象的{@link AnnotatedTypeToken}
     * @param <T>       目标对象类型
     * @return 脱敏后的新对象
     */
    public static <T> T desensitize(T target, AnnotatedTypeToken<T> typeToken) {
        return Optional.ofNullable(target)
                .map(t -> typeToken)
                .map(AnnotatedTypeToken::getAnnotatedType)
                .map(annotatedType -> AnnotationParser.parse(target, annotatedType))
                .orElse(target);
    }
}
