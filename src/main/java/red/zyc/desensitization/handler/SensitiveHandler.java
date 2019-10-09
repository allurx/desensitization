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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import red.zyc.desensitization.exception.InvalidSensitiveHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;


/**
 * @param <A> 实现类要处理的注解类型
 * @param <T> 实现类支持的处理类型
 * @author zyc
 */
public interface SensitiveHandler<T, A extends Annotation> {


    /**
     * 由子类实现敏感信息处理逻辑
     *
     * @param target     需要处理的目标
     * @param annotation 处理目标上的敏感注解
     * @return 处理后的结果
     */
    T handle(T target, A annotation);

    /**
     * 判断处理者是否支持将要处理的目标类型
     *
     * @param targetClass 需要擦除敏感信息的目标对象的 {@code Class}
     * @return 处理者是否支持目标类型
     */
    @SuppressWarnings("unchecked")
    default boolean support(Class<?> targetClass) {
        Type[] actualTypeArgumentsOfSensitiveHandler = getActualTypeArgumentsOfSensitiveHandler();
        Class<T> supportedClass = (Class<T>) actualTypeArgumentsOfSensitiveHandler[0];
        return supportedClass.isAssignableFrom(targetClass);
    }

    /**
     * 这个方法的作用仅仅是用来类型转换
     *
     * @param target     {@link T}
     * @param annotation {@link A}
     * @return {@link T}
     */
    @SuppressWarnings("unchecked")
    default T handling(Object target, Annotation annotation) {
        Class<?> targetClass = target.getClass();
        Class<?> handlerClass = getClass();
        if (support(targetClass)) {
            return handle((T) target, (A) annotation);
        }
        getLogger().warn("{}不支持擦除{}类型的敏感信息", handlerClass, targetClass);
        return (T) target;
    }

    /**
     * 获取当前敏感处理器直接实现或者由其某个父类实现的具有明确泛型参数的 {@link SensitiveHandler}接口的类型参数
     *
     * @return 当前敏感处理器直接实现或者由其某个父类实现的具有明确泛型参数的 {@link SensitiveHandler}接口的类型参数
     */
    default Type[] getActualTypeArgumentsOfSensitiveHandler() {
        if (SensitiveHandler.class.isAssignableFrom(getClass())) {
            Class<?> current = getClass();
            while (current != null && current != Object.class) {
                // 递归获取当前类或者父类实现的所有泛型接口
                Type[] genericInterfaces = current.getGenericInterfaces();
                for (Type type : genericInterfaces) {
                    if (type instanceof ParameterizedType) {
                        ParameterizedType parameterizedType = (ParameterizedType) type;
                        Class<?> rawType = (Class<?>) parameterizedType.getRawType();
                        // 当泛型接口是SensitiveHandler时返回其明确的类型参数，注意此时的参数可能为T，A之类的类型变量（TypeVariable）
                        if (rawType == SensitiveHandler.class) {
                            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                            if (Arrays.stream(actualTypeArguments).allMatch(actualType -> actualType instanceof Class)) {
                                return parameterizedType.getActualTypeArguments();
                            }
                        }
                    }
                }
                current = current.getSuperclass();
            }
        }
        throw new InvalidSensitiveHandler(getClass() + "必须直接或间接实现具有明确泛型参数的" + SensitiveHandler.class.getName() + "接口");
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
