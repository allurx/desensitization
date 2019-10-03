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

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Arrays;


/**
 * @param <A> 实现类要处理的注解类型
 * @param <T> 实现类支持的处理类型
 * @author zyc
 */
@FunctionalInterface
public interface SensitiveHandler<T, A extends Annotation> extends Serializable {

    /**
     * 由子类实现敏感信息处理逻辑
     *
     * @param target     需要处理的目标
     * @param annotation 处理目标上的敏感注解
     * @return 处理后的结果
     */
    T handle(T target, A annotation);

    /**
     * 获取当前敏感处理器上的第一个敏感处理注解
     *
     * @return 目标对象上的第一个敏感处理注解
     */
    @SuppressWarnings("unchecked")
    default A getSensitiveAnnotation()  {
        System.out.println(getClass().getDeclaringClass());
        System.out.println(Arrays.toString(getClass().getDeclaredMethods()));
        MethodHandle writeReplace = null;
        try {
            writeReplace = MethodHandles.lookup().in(this.getClass()).findVirtual(Object.class, "writeReplace", MethodType.methodType(Object.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
        System.out.println(writeReplace);
        Annotation[] annotations = getClass().getAnnotations();
        if (annotations.length > 0) {
            return (A) getClass().getAnnotations()[0];
        }
        return null;
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
        return handle((T) target, (A) annotation);
    }
}
