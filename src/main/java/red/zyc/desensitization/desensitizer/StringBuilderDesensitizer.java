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

import red.zyc.desensitization.annotation.CharSequenceSensitive;
import red.zyc.desensitization.support.InstanceCreators;

/**
 * {@link StringBuilder}类型对象脱敏器
 *
 * @author zyc
 */
public class StringBuilderDesensitizer extends AbstractCharSequenceDesensitizer<StringBuilder, CharSequenceSensitive> implements Desensitizer<StringBuilder, CharSequenceSensitive> {

    @Override
    public StringBuilder desensitize(StringBuilder target, CharSequenceSensitive annotation) {
        @SuppressWarnings("unchecked")
        Condition<StringBuilder> condition = (Condition<StringBuilder>) InstanceCreators.getInstanceCreator(annotation.condition()).create();
        if (!condition.required(target)) {
            return target;
        }
        int length = target.length();
        char[] chars = new char[length];
        target.getChars(0, target.length(), chars, 0);
        CharSequenceSensitiveDescriptor<StringBuilder, CharSequenceSensitive> erased = desensitize(CharSequenceSensitiveDescriptor.<StringBuilder, CharSequenceSensitive>builder()
                .target(target)
                .chars(chars)
                .annotation(annotation)
                .startOffset(annotation.startOffset())
                .endOffset(annotation.endOffset())
                .regexp(annotation.regexp())
                .placeholder(annotation.placeholder())
                .build());
        return new StringBuilder().append(erased.getChars());
    }
}
