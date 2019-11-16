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
 * 用来描述{@link CharSequence}类型对象中的敏感信息
 *
 * @param <A> 敏感信息处理注解类型
 * @param <T> 目标对象类型
 * @author zyc
 */
public class CharSequenceSensitiveDescriptor<T extends CharSequence, A extends Annotation> {
    /**
     * 目标对象
     */
    private T target;
    /**
     * 敏感信息处理注解
     */
    private A annotation;
    /**
     * 敏感信息在原字符序列中的起始偏移
     */
    private int startOffset;
    /**
     * 敏感信息在原字符序列中的结束偏移
     */
    private int endOffset;
    /**
     * 正则表达式匹配的敏感信息
     */
    private String regexp;
    /**
     * 敏感信息替换后的占位符
     */
    private char placeholder;

    public CharSequenceSensitiveDescriptor(CharSequenceSensitiveDescriptorBuilder<T, A> builder) {
        this.target = builder.target;
        this.annotation = builder.annotation;
        this.startOffset = builder.startOffset;
        this.endOffset = builder.endOffset;
        this.regexp = builder.regexp;
        this.placeholder = builder.placeholder;
    }

    public static <T extends CharSequence, A extends Annotation> CharSequenceSensitiveDescriptorBuilder<T, A> builder() {
        return new CharSequenceSensitiveDescriptorBuilder<>();
    }

    public T getTarget() {
        return target;
    }

    public A getAnnotation() {
        return annotation;
    }

    public int getStartOffset() {
        return startOffset;
    }

    public int getEndOffset() {
        return endOffset;
    }

    public String getRegexp() {
        return regexp;
    }

    public char getPlaceholder() {
        return placeholder;
    }

    public static class CharSequenceSensitiveDescriptorBuilder<T extends CharSequence, A extends Annotation> {

        private T target;

        private A annotation;

        private int startOffset;

        private int endOffset;

        private String regexp;

        private char placeholder;

        public CharSequenceSensitiveDescriptorBuilder<T, A> target(T target) {
            this.target = target;
            return this;
        }

        public CharSequenceSensitiveDescriptorBuilder<T, A> annotation(A annotation) {
            this.annotation = annotation;
            return this;
        }

        public CharSequenceSensitiveDescriptorBuilder<T, A> startOffset(int startOffset) {
            this.startOffset = startOffset;
            return this;
        }

        public CharSequenceSensitiveDescriptorBuilder<T, A> endOffset(int endOffset) {
            this.endOffset = endOffset;
            return this;
        }

        public CharSequenceSensitiveDescriptorBuilder<T, A> regexp(String regexp) {
            this.regexp = regexp;
            return this;
        }

        public CharSequenceSensitiveDescriptorBuilder<T, A> placeholder(char placeholder) {
            this.placeholder = placeholder;
            return this;
        }

        public CharSequenceSensitiveDescriptor<T, A> build() {
            return new CharSequenceSensitiveDescriptor<>(this);
        }
    }
}
