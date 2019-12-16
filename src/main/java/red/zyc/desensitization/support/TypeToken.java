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

package red.zyc.desensitization.support;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 帮助类用来获取类的泛型参数，换句话说就是获取某个{@link ParameterizedType}类型对象运行时其泛型参数的具体类型。
 * 由于java泛型擦除机制，如果我们想获取{@code new ArrayList<String>}这个对象运行时的泛型参数{@code String}，
 * 这几乎是很难做到的。而利用{@link TypeToken}你只需要构造一个它的匿名子类，我们就能获取它运行时的泛型参数：
 * <pre>
 *     TypeToken&lt;List&lt;String&gt;&gt; stringList = new TypeToken&lt;List&lt;String&gt;&gt;(){};
 *     stringList.getType()返回的泛型参数为：java.util.List&lt;java.lang.String&gt;
 * </pre>
 *
 * @param <T> 需要捕获的明确类型
 * @author zyc
 */
public abstract class TypeToken<T> extends TypeCapture<T> {

    /**
     * {@link T}运行时的类型
     */
    private final Type type;

    /**
     * {@link T}运行时被注解的类型
     */
    private final AnnotatedType annotatedType;

    protected TypeToken() {
        annotatedType = capture();
        type = annotatedType.getType();
    }

    private TypeToken(AnnotatedType annotatedType) {
        this.annotatedType = annotatedType;
        this.type = annotatedType.getType();
    }

    /**
     * 通过已知对象的{@link AnnotatedType}实例化{@link TypeToken}
     *
     * @param annotatedType 对象的{@link AnnotatedType}
     * @param <T>           对象的运行时类型
     * @return 该对象的 {@link TypeToken}
     */
    public static <T> TypeToken<T> of(AnnotatedType annotatedType) {
        return new TypeToken<T>(annotatedType) {
        };
    }

    /**
     * @return 对象运行时的类型
     */
    public final Type getType() {
        return type;
    }

    /**
     * @return 对象运行时被注解的类型
     */
    public final AnnotatedType getAnnotatedType() {
        return annotatedType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TypeToken<?> typeToken = (TypeToken<?>) o;
        return type.equals(typeToken.type);
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public String toString() {
        return "TypeToken{" +
                "type=" + type +
                ", annotatedType=" + annotatedType +
                "} ";
    }
}
