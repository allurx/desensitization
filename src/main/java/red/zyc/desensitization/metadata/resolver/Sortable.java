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

package red.zyc.desensitization.metadata.resolver;

/**
 * 对象是否能够进行排序
 *
 * @author zyc
 */
public interface Sortable {

    /**
     * 最高优先级（最先执行）
     *
     * @see java.lang.Integer#MIN_VALUE
     */
    int HIGHEST_PRIORITY = Integer.MIN_VALUE;

    /**
     * 最低优先级（最晚执行）
     *
     * @see java.lang.Integer#MAX_VALUE
     */
    int LOWEST_PRIORITY = Integer.MAX_VALUE;

    /**
     * 对象的顺序值，较高的顺序值将被解析为较低的优先级。相同的顺序值的对象只会取第一个对象。
     * 例如顺序值较高的{@link Resolver}将会比顺序值较低的{@link Resolver}晚执行。而两个相同
     * 顺序值的{@link Resolver}只会保留第一个解析器。
     *
     * @return 对应的顺序
     */
    int order();
}
