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

import java.lang.annotation.Annotation;


/**
 * @param <A> 实现类要处理的注解类型
 * @param <T> 实现类支持的处理类型
 * @author zyc
 */
public interface SensitiveHandler<A extends Annotation, T> {

    /**
     * 由子类实现敏感信息处理逻辑
     *
     * @param target     需要处理的目标
     * @param annotation 处理目标上的敏感注解
     * @return 处理后的结果
     */
    T handle(T target, A annotation);
}
