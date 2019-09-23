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
package red.zyc.desensitization.handler;

import lombok.Getter;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 敏感信息处理者基类，为子类提供了一些快捷有用的方法处理敏感信息。
 *
 * @param <A> 实现类要处理的注解类型
 * @param <T> 实现类支持的处理类型
 * @author zyc
 */
@Getter
public abstract class AbstractSensitiveHandler<A extends Annotation, T> implements SensitiveHandler<A, T> {

    /**
     * 敏感信息擦除后的占位符
     */
    protected String placeholder = "*";

    /**
     * 敏感信息处理注解的{@code Class}
     */
    protected Class<A> annotationClass;

    /**
     * 敏感信息处理注解支持的目标{@code Class}
     */
    protected Class<T> supportedClass;

    /**
     * 判断处理者是否支持将要处理的目标类型
     *
     * @param targetClass 需要擦除敏感信息的目标对象的 {@code Class}
     * @return 处理者是否支持目标类型
     */
    public boolean support(Class<?> targetClass) {
        return supportedClass.isAssignableFrom(targetClass);
    }

    @SuppressWarnings("unchecked")
    public AbstractSensitiveHandler() {
        Type[] types = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
        annotationClass = (Class<A>) types[0];
        supportedClass = (Class<T>) types[1];
    }

    /**
     * 这个方法的作用仅仅是用来类型转换
     *
     * @param target     {@link T}
     * @param annotation {@link A}
     * @return {@link T}
     */
    @SuppressWarnings("unchecked")
    public T handling(Object target, Annotation annotation) {
        return handle((T) target, (A) annotation);
    }

}
