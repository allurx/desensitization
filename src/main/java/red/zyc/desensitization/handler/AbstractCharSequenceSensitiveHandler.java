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

import lombok.extern.slf4j.Slf4j;
import red.zyc.desensitization.metadata.CharSequenceSensitiveDescriptor;

import java.lang.annotation.Annotation;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 字符序列对象敏感信息处理者基类，为子类提供了一些快捷有用的方法处理{@link CharSequence}类型的敏感信息。
 *
 * @param <A> 敏感处理注解类型
 * @param <T> 目标对象类型
 * @author zyc
 */
@Slf4j
public abstract class AbstractCharSequenceSensitiveHandler<A extends Annotation, T extends CharSequence> extends AbstractSensitiveHandler<A, T> {

    /**
     * * 如果处理器支持的目标对象是 {@link CharSequence}类型，
     * * 可以调用这个快捷方法擦除原字符序列中的敏感信息。
     *
     * @param descriptor {@link CharSequenceSensitiveDescriptor}
     * @return 敏感信息被擦除后的字符序列
     */
    public CharSequence handleCharSequence(CharSequenceSensitiveDescriptor<A, T> descriptor) {
        if (descriptor.getTarget() == null || descriptor.getTarget().length() == 0) {
            return descriptor.getTarget();
        }
        // 判断Handler是否支持目标对象类型
        if (!support(descriptor.getTarget().getClass())) {
            log.warn(getClass().getName() + "不支持擦除" + descriptor.getTarget().getClass() + "类型的敏感信息");
            return descriptor.getTarget();
        }
        // 字符序列对应的字符数组
        char[] chars = descriptor.getTarget().toString().toCharArray();

        // 使用正则表达式匹配擦除敏感信息
        if (isNotEmptyString(descriptor.getRegexp())) {
            Matcher matcher = Pattern.compile(descriptor.getRegexp()).matcher(descriptor.getTarget());
            // 将正则匹配的每一项中的每一个字符都替换成占位符
            while (matcher.find()) {
                // 排除空字符串
                if (isNotEmptyString(matcher.group())) {
                    // 将匹配项的每一个字符都替换成占位符
                    erase(chars, matcher.start(), matcher.end(), descriptor.getPlaceholder());
                }
            }
            return String.valueOf(chars);
        }

        // 使用位置偏移匹配擦除敏感信息
        check(descriptor.getStartOffset(), descriptor.getEndOffset(), descriptor.getTarget());
        erase(chars, descriptor.getStartOffset(), descriptor.getTarget().length() - descriptor.getEndOffset(), descriptor.getPlaceholder());
        return String.valueOf(chars);
    }

    /**
     * 擦除字符序列中的敏感信息
     *
     * @param chars 字符序列对应的字符数组
     * @param start 敏感信息在字符序列中的起始索引
     * @param end   敏感信息在字符序列中的结束索引
     */
    private void erase(char[] chars, int start, int end, char placeholder) {
        while (start < end) {
            chars[start++] = placeholder;
        }
    }

    /**
     * 校验起始偏移和结束偏移的合法性
     *
     * @param startOffset 敏感信息在原字符序列中的起始偏移
     * @param endOffset   敏感信息在原字符序列中的结束偏移
     * @param target      原字符序列
     */
    private void check(int startOffset, int endOffset, T target) {
        if (startOffset < 0 ||
                endOffset < 0 ||
                startOffset + endOffset >= target.length()) {
            throw new IllegalArgumentException("startOffset:" + startOffset + "," + "endOffset:" + endOffset);
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
