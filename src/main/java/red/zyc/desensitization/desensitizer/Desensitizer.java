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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import red.zyc.desensitization.exception.InvalidDesensitizerException;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;


/**
 * 脱敏器
 *
 * @param <A> 实现类要处理的注解类型
 * @param <T> 实现类支持的处理类型
 * @author zyc
 */
public interface Desensitizer<T, A extends Annotation> {


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
        Class<?>[] actualTypeArgumentsOfDesensitizer = getActualTypeArgumentsOfDesensitizer();
        // 类型参数T的class
        Class<?> supportedClass = actualTypeArgumentsOfDesensitizer[0];
        return supportedClass.isAssignableFrom(targetClass);
    }

    /**
     * 脱敏前的前置判断
     *
     * @param target     {@link T}
     * @param annotation {@link A}
     * @return {@link T}
     */
    default T desensitizing(T target, A annotation) {
        Class<?> targetClass = target.getClass();
        Class<?> desensitizerClass = getClass();
        if (support(targetClass)) {
            return desensitize(target, annotation);
        }
        getLogger().warn("{}不支持擦除{}类型的敏感信息", desensitizerClass, targetClass);
        return target;
    }

    /**
     * 获取当前脱敏器直接实现或者由其某个父类实现的具有明确泛型参数的 {@link Desensitizer}接口的类型参数
     *
     * @return 当前脱敏器直接实现或者由其某个父类实现的具有明确泛型参数的 {@link Desensitizer}接口的类型参数
     */
    default Class<?>[] getActualTypeArgumentsOfDesensitizer() {
        if (Desensitizer.class.isAssignableFrom(getClass())) {
            Class<?> current = getClass();
            while (current != null && current != Object.class) {
                // 递归获取当前类或者父类实现的所有泛型接口
                Type[] genericInterfaces = current.getGenericInterfaces();
                for (Type type : genericInterfaces) {
                    if (type instanceof ParameterizedType) {
                        ParameterizedType parameterizedType = (ParameterizedType) type;
                        Class<?> rawType = (Class<?>) parameterizedType.getRawType();
                        // 当泛型接口是Desensitizer时返回其明确的类型参数，注意此时的泛型参数可能为T，A之类的类型变量（TypeVariable）
                        if (rawType == Desensitizer.class) {
                            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                            if (Arrays.stream(actualTypeArguments).allMatch(actualType -> actualType instanceof Class)) {
                                return Arrays.copyOf(actualTypeArguments, actualTypeArguments.length, Class[].class);
                            }
                        }
                    }
                }
                current = current.getSuperclass();
            }
        }
        throw new InvalidDesensitizerException(getClass() + "必须直接实现或由其父类直接实现具有明确泛型参数的" + Desensitizer.class.getName() + "接口");
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
