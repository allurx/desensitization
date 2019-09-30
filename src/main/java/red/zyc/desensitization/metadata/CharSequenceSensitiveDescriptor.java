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

import lombok.Builder;
import lombok.Getter;

import java.lang.annotation.Annotation;

/**
 * 用来描述{@link CharSequence}类型目标对象中的敏感信息
 *
 * @param <A> 敏感信息处理注解类型
 * @param <T> 目标对象类型
 * @author zyc
 */
@Builder
@Getter
public class CharSequenceSensitiveDescriptor<T extends CharSequence, A extends Annotation> {

    /**
     * 目标对象
     */
    T target;

    /**
     * 敏感信息处理注解
     */
    A annotation;

    /**
     * 敏感信息在原字符序列中的起始偏移
     */
    int startOffset;

    /**
     * 敏感信息在原字符序列中的结束偏移
     */
    int endOffset;

    /**
     * 正则表达式匹配的敏感信息
     */
    String regexp;

    /**
     * 敏感信息替换后的占位符
     */
    char placeholder;
}
