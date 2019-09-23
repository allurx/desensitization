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
 * 字符序列对象敏感信息处理者基类，为子类提供了一些快捷有用的方法处理{@link CharSequence}类型的敏感信息。
 *
 * @author zyc
 */
public abstract class AbstractCharSequenceSensitiveHandler<A extends Annotation, T extends CharSequence> extends AbstractSensitiveHandler<A, T> {

    /**
     * 如果处理器支持的类型是 {@link CharSequence}类型，
     * 可以调用这个快捷方法生成{@link AbstractSensitiveHandler#placeholder}字符串替换原字符序列中的敏感信息。
     *
     * @param startOffset 敏感信息在原字符序列中的起始偏移
     * @param endOffset   敏感信息在原字符序列中的结束偏移
     * @param target      目标字符序列
     * @return {@link AbstractSensitiveHandler#placeholder}字符串，用来替换敏感信息
     */
    public CharSequence handleCharSequence(String regexp, int startOffset, int endOffset, T target) {
        if (target == null || target.length() == 0) {
            return target;
        }
        // 使用正则表达式匹配敏感信息
        if (!"".equals(regexp)) {
            return target.toString().replaceAll(regexp, placeholder);
        }
        // 使用位置偏移匹配敏感信息
        check(startOffset, endOffset, target);
        return target.subSequence(0, startOffset) + secret(target.length() - startOffset - endOffset) + target.subSequence(target.length() - endOffset, target.length());
    }

    /**
     * 生成"*"字符串
     *
     * @param length 敏感信息字符序列长度
     * @return {@link AbstractSensitiveHandler#placeholder}字符串
     */
    private String secret(int length) {
        StringBuilder secret = new StringBuilder();
        while (length != 0) {
            secret.append(placeholder);
            length--;
        }
        return secret.toString();
    }

    /**
     * @param startOffset 敏感信息在原字符序列中的起始偏移
     * @param endOffset   敏感信息在原字符序列中的结束偏移
     * @param target      原字符序列
     */
    private void check(int startOffset, int endOffset, T target) {
        if (startOffset < 0 ||
                endOffset < 0 ||
                startOffset + endOffset > target.length()) {
            throw new IllegalArgumentException("startOffset:" + startOffset + "," + "endOffset:" + endOffset);
        }
    }

}
