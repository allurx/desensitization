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
package red.zyc.desensitization.metadata;

import red.zyc.desensitization.exception.SensitiveDescriptionNotFoundException;
import red.zyc.desensitization.util.ReflectionUtil;
import sun.invoke.util.BytecodeDescriptor;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;

/**
 * 敏感信息描述者，用来承载敏感信息注解。
 * 该接口实现{@link Serializable}的目的是为了在运行时如果{@code this}对象是以Lambda表达式的形式存在的话，
 * 则获取相应的{@link SerializedLambda}对象
 *
 * @param <T>敏感信息的类型
 * @author zyc
 */
@FunctionalInterface
public interface SensitiveDescriptor<T> extends Serializable {

    /**
     * 用来承载敏感注解以处理敏感信息
     *
     * @param value 敏感信息值
     */
    void describe(T value);

    /**
     * 获取描述敏感信息的描述方法
     * <ol>
     *     <li>
     *         如果{@code this}是Lambda表达式，则返回Lambda表达式生成的静态方法
     *     </li>
     *     <li>
     *         如果{@code this}是匿名类，则返回该类重写的方法
     *     </li>
     * </ol>
     *
     * @return 描述敏感信息的描述方法
     */
    default AnnotatedType getAnnotatedType() {
        try {
            Class<?> clazz = getClass();
            // Lambda
            if (clazz.isSynthetic()) {
                Method writeReplace = clazz.getDeclaredMethod("writeReplace");
                writeReplace.setAccessible(true);
                SerializedLambda serializedLambda = (SerializedLambda) writeReplace.invoke(this);
                List<Class<?>> classes = BytecodeDescriptor.parseMethod(serializedLambda.getImplMethodSignature(), Thread.currentThread().getContextClassLoader());
                Field capturingClassField = serializedLambda.getClass().getDeclaredField("capturingClass");
                capturingClassField.setAccessible(true);
                Class<?> capturingClass = (Class<?>) capturingClassField.get(serializedLambda);
                Method lambdaStaticMethod = capturingClass.getDeclaredMethod(serializedLambda.getImplMethodName(), Arrays.copyOfRange(classes.toArray(new Class<?>[0]), 0, classes.size() - 1));
                lambdaStaticMethod.setAccessible(true);
                return lambdaStaticMethod.getAnnotatedParameterTypes()[0];
                // AnonymousClass
            } else if (clazz.isAnonymousClass()) {
                AnnotatedType annotatedInterface = clazz.getAnnotatedInterfaces()[0];
                AnnotatedParameterizedType annotatedParameterizedType= (AnnotatedParameterizedType) annotatedInterface;
                System.out.println(Arrays.toString(((AnnotatedParameterizedType) (annotatedParameterizedType.getAnnotatedActualTypeArguments()[0])).getAnnotatedActualTypeArguments()[0].getDeclaredAnnotations()));
                Type actualTypeArgument = ((ParameterizedType) clazz.getGenericInterfaces()[0]).getActualTypeArguments()[0];
                return clazz.getDeclaredMethod("describe", ReflectionUtil.getRawClass(actualTypeArgument)).getAnnotatedParameterTypes()[0];
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        throw new SensitiveDescriptionNotFoundException("没有在" + getClass() + "中找到敏感描述方法");
    }


}
