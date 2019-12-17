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
package red.zyc.desensitization.desensitizer;


import red.zyc.desensitization.exception.InvalidDesensitizerException;
import red.zyc.desensitization.util.ReflectionUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Optional;


/**
 * 脱敏器
 *
 * @param <A> 实现类要处理的注解类型
 * @param <T> 实现类支持的处理类型
 * @author zyc
 */
public interface Desensitizer<T, A extends Annotation> {

    /**
     * 获取当前脱敏器直接实现的具有明确泛型参数的 {@link Desensitizer}接口的类型参数
     *
     * @param desensitizerClass 脱敏器的{@code class}
     * @return 当前脱敏器直接实现的具有明确泛型参数的 {@link Desensitizer}接口的类型参数
     */
    static Class<?>[] getActualTypeArgumentsOfDesensitizer(Class<? extends Desensitizer<?, ? extends Annotation>> desensitizerClass) {
        return Optional.of(desensitizerClass)
                .map(Class::getGenericInterfaces)
                .map(genericInterfaces -> (ParameterizedType) Arrays.stream(genericInterfaces)
                        .filter(genericInterface -> genericInterface instanceof ParameterizedType && ((ParameterizedType) genericInterface).getRawType() == Desensitizer.class)
                        .findFirst().orElse(null))
                .map(ParameterizedType::getActualTypeArguments)
                .filter(actualTypeArguments -> Arrays.stream(actualTypeArguments).allMatch(actualType -> actualType instanceof Class))
                .map(actualTypeArguments -> Arrays.copyOf(actualTypeArguments, actualTypeArguments.length, Class[].class))
                .orElseThrow(new InvalidDesensitizerException(desensitizerClass + "必须直接实现具有明确泛型参数的" + Desensitizer.class.getName() + "接口"));
    }

    /**
     * 由子类实现敏感信息脱敏逻辑
     *
     * @param target     需要脱敏的目标
     * @param annotation 目标对象上的敏感注解
     * @return 脱敏后的结果
     */
    T desensitize(T target, A annotation);

    /**
     * 判断脱敏器是否支持目标类型脱敏
     *
     * @param targetClass 需要擦除敏感信息的目标对象的 {@code Class}
     * @return 脱敏器是否支持目标类型脱敏
     */
    default boolean support(Class<?> targetClass) {
        Class<?>[] actualTypeArgumentsOfDesensitizer = getActualTypeArgumentsOfDesensitizer(ReflectionUtil.getClass(this));
        Class<?> supportedClass = actualTypeArgumentsOfDesensitizer[0];
        return supportedClass.isAssignableFrom(targetClass);
    }

}
