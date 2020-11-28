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

/**
 * 用来判断是否需要对目标对象进行脱敏处理的先决条件
 *
 * @param <T> 目标对象类型
 * @author zyc
 */
public interface Condition<T> {

    /**
     * 返回{@code true}代表需要对目标对象进行脱敏处理，{@code false}代表不作任何处理
     *
     * @param target 目标对象
     * @return 是否需要对目标对象进行脱敏处理
     */
    boolean required(T target);
}
