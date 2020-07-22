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

import red.zyc.desensitization.support.InstanceCreators;

import java.lang.annotation.Annotation;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;


/**
 * {@link CharSequence}类型对象脱敏器基类，为子类提供了一些快捷有用的方法处理该类型的敏感信息。
 *
 * @param <A> 敏感注解类型
 * @param <T> 需要脱敏的对象类型
 * @author zyc
 */
public abstract class AbstractCharSequenceDesensitizer<T extends CharSequence, A extends Annotation> implements Desensitizer<T, A> {

    /**
     * 正则表达式缓存
     */
    private static final ConcurrentMap<String, Pattern> PATTERN_CACHE = new ConcurrentHashMap<>();

    /**
     * 根据条件判断是否需要脱敏
     *
     * @param target         目标字符序列对象
     * @param conditionClass 条件的{@link Class}
     * @return 是否需要脱敏
     */
    public boolean required(T target, Class<? extends Condition<?>> conditionClass) {
        @SuppressWarnings("unchecked")
        Class<? extends Condition<T>> clazz = (Class<? extends Condition<T>>) conditionClass;
        return InstanceCreators.getInstanceCreator(clazz).create().required(target);
    }

    /**
     * 脱敏方法
     *
     * @param target      目标字符序列对象
     * @param regexp      正则表达式
     * @param start       敏感信息在原字符序列中的起始偏移
     * @param end         敏感信息在原字符序列中的结束偏移
     * @param placeholder 敏感信息替换后的占位符
     * @return 脱敏后的新字符序列对象的字符数组
     */
    public final char[] desensitize(CharSequence target, String regexp, int start, int end, char placeholder) {
        if (isNotEmptyString(regexp)) {
            return desensitize(target, regexp, placeholder);
        }
        return desensitize(target, start, end, placeholder);
    }

    /**
     * 基于正则表达式脱敏
     *
     * @param target      目标字符序列对象
     * @param regexp      正则表达式
     * @param placeholder 敏感信息替换后的占位符
     * @return 脱敏后的新字符序列对象的字符数组
     */
    private char[] desensitize(CharSequence target, String regexp, char placeholder) {
        char[] chars = chars(target);
        Matcher matcher = PATTERN_CACHE.computeIfAbsent(regexp, s -> Pattern.compile(regexp)).matcher(target);
        // 将正则匹配的每一项中的每一个字符都替换成占位符
        while (matcher.find()) {
            // 排除空字符串
            if (isNotEmptyString(matcher.group())) {
                // 将匹配项的每一个字符都替换成占位符
                replace(chars, matcher.start(), matcher.end(), placeholder);
            }
        }
        return chars;
    }

    /**
     * 基于位置偏移脱敏
     *
     * @param target      目标字符序列对象
     * @param start       敏感信息在原字符序列中的起始偏移
     * @param end         敏感信息在原字符序列中的结束偏移
     * @param placeholder 敏感信息替换后的占位符
     * @return 脱敏后的新字符序列对象的字符数组
     */
    private char[] desensitize(CharSequence target, int start, int end, char placeholder) {
        char[] chars = chars(target);
        check(start, end, target);
        replace(chars, start, target.length() - end, placeholder);
        return chars;
    }

    /**
     * 将字符序列转换为字符数组
     *
     * @param target 字符序列对象
     * @return 字符序列对象所代表的字符数组
     */
    private char[] chars(CharSequence target) {
        char[] chars = new char[target.length()];
        IntStream.range(0, target.length()).forEach(i -> chars[i] = target.charAt(i));
        return chars;
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
     * 校验起始偏移和结束偏移的合法性
     *
     * @param startOffset 敏感信息在原字符序列中的起始偏移
     * @param endOffset   敏感信息在原字符序列中的结束偏移
     * @param target      原字符序列
     */
    private void check(int startOffset, int endOffset, CharSequence target) {
        if (startOffset < 0 ||
                endOffset < 0 ||
                startOffset + endOffset > target.length()) {
            throw new IllegalArgumentException("startOffset: " + startOffset + ", " + "endOffset: " + endOffset + ", " + "target: " + target);
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
