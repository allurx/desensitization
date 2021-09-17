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

import java.lang.annotation.Annotation;

/**
 * 脱敏器
 *
 * @param <A> 敏感注解类型
 * @param <T> 需要脱敏的对象类型
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

}
