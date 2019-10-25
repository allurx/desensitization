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

import red.zyc.desensitization.annotation.Sensitive;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 单个敏感信息描述者，用来承载敏感信息注解
 *
 * @param <T>敏感信息的类型
 * @author zyc
 */
@FunctionalInterface
public interface SensitiveDescriptor<T> extends Descriptor {

    /**
     * 用来承载敏感注解以处理敏感信息
     *
     * @param value 敏感信息值
     */
    void describe(T value);

    /**
     * 获取{@link SensitiveDescriptor#describe(T)}参数上的第一个敏感注解
     *
     * @return {@link SensitiveDescriptor#describe(T)}参数上的第一个敏感注解
     */
    default Annotation getSensitiveAnnotation() {
        Method description = getSensitiveDescription();
        Annotation[] annotations = description.getParameters()[0].getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().isAnnotationPresent(Sensitive.class)) {
                return annotation;
            }
        }
        getLogger().warn("没有在{}上找到敏感注解", getClass());
        return null;
    }


}
