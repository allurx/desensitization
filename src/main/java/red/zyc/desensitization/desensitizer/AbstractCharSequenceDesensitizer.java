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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * {@link CharSequence}类型对象脱敏器基类，为子类提供了一些快捷有用的方法处理该类型的敏感信息。
 *
 * @param <A> 敏感注解类型
 * @param <T> 目标对象类型
 * @author zyc
 */
public abstract class AbstractCharSequenceDesensitizer<T extends CharSequence, A extends Annotation> extends AbstractDesensitizer<T, A> {

    /**
     * 如果目标对象是{@link CharSequence}类型，可以通过构造一个{@link CharSequenceSensitiveDescriptor}
     * 然后调用该方法擦除字符序列中的敏感信息。
     * <p><strong>注意：请确保在构造{@link CharSequenceSensitiveDescriptor}时填充完整的目标对象信息，
     * 否则会抛出任何可能的 {@link RuntimeException}。</strong></p>
     *
     * @param descriptor {@link CharSequenceSensitiveDescriptor}
     * @return 方法执行完毕后其内部的 {@link CharSequenceSensitiveDescriptor#getChars() 字符数组}即为擦除后的新对象
     */
    public final CharSequenceSensitiveDescriptor<T, A> desensitize(CharSequenceSensitiveDescriptor<T, A> descriptor) {
        if (descriptor.getTarget() == null || descriptor.getTarget().length() == 0) {
            return descriptor;
        }
        // 使用正则表达式匹配擦除敏感信息
        if (isNotEmptyString(descriptor.getRegexp())) {
            Matcher matcher = Pattern.compile(descriptor.getRegexp()).matcher(descriptor.getTarget());
            // 将正则匹配的每一项中的每一个字符都替换成占位符
            while (matcher.find()) {
                // 排除空字符串
                if (isNotEmptyString(matcher.group())) {
                    // 将匹配项的每一个字符都替换成占位符
                    replace(descriptor.getChars(), matcher.start(), matcher.end(), descriptor.getPlaceholder());
                }
            }
            return descriptor;
        }

        // 使用位置偏移匹配擦除敏感信息
        replace(descriptor.getChars(), descriptor.getStartOffset(), descriptor.getTarget().length() - descriptor.getEndOffset(), descriptor.getPlaceholder());
        return descriptor;
    }

    /**
     * 替换字符序列中的敏感信息
     *
     * @param chars       字符序列对应的字符数组
     * @param start       敏感信息在字符序列中的起始索引
     * @param end         敏感信息在字符序列中的结束索引
     * @param placeholder 用来替换敏感字符的占位符
     */
    private void replace(char[] chars, int start, int end, char placeholder) {
        while (start < end) {
            chars[start++] = placeholder;
        }
    }

    /**
     * @param string 字符串对象
     * @return 字符串是否不为空字符串
     */
    private boolean isNotEmptyString(String string) {
        return !"".equals(string);
    }

}
