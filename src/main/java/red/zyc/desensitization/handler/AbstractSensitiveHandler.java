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
import lombok.extern.slf4j.Slf4j;
import red.zyc.desensitization.exception.InvalidSensitiveHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * 敏感信息处理者基类，为子类提供了一些快捷有用的方法处理敏感信息。
 *
 * @param <A> 实现类要处理的注解类型
 * @param <T> 实现类支持的处理类型
 * @author zyc
 */
@Slf4j
@Getter
public abstract class AbstractSensitiveHandler<T, A extends Annotation> implements SensitiveHandler<T, A> {

    /**
     * 敏感信息处理注解的{@code Class}
     */
    protected Class<A> annotationClass;

    /**
     * 敏感信息处理注解支持的目标{@code Class}
     */
    protected Class<T> supportedClass;

    @SuppressWarnings("unchecked")
    public AbstractSensitiveHandler() {
        Type[] actualTypeArgumentsOfSensitiveHandler = getActualTypeArgumentsOfSensitiveHandler();
        annotationClass = (Class<A>) actualTypeArgumentsOfSensitiveHandler[0];
        supportedClass = (Class<T>) actualTypeArgumentsOfSensitiveHandler[1];
    }

    /**
     * 判断处理者是否支持将要处理的目标类型
     *
     * @param targetClass 需要擦除敏感信息的目标对象的 {@code Class}
     * @return 处理者是否支持目标类型
     */
    public boolean support(Class<?> targetClass) {
        return supportedClass.isAssignableFrom(targetClass);
    }

    /**
     * @return 当前敏感处理器直接实现或者由其某个父类实现的具有明确泛型参数的 {@link SensitiveHandler}接口的类型参数
     */
    private Type[] getActualTypeArgumentsOfSensitiveHandler() {
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

}
