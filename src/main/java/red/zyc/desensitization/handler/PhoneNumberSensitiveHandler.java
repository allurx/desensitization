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


import red.zyc.desensitization.annotation.PhoneNumberSensitive;
import red.zyc.desensitization.metadata.CharSequenceSensitiveDescriptor;

/**
 * 手机号码敏感信息处理者。
 * 注意该类在擦除敏感信息时不会校验目标对象的合法性，请确保目标对象是合法的手机号码，
 * 否则会抛出任何有可能的 {@link RuntimeException}。
 *
 * @author zyc
 */
public class PhoneNumberSensitiveHandler extends AbstractCharSequenceSensitiveHandler<PhoneNumberSensitive, CharSequence> implements SensitiveHandler<PhoneNumberSensitive, CharSequence> {

    @Override
    public CharSequence handle(CharSequence target, PhoneNumberSensitive annotation) {
        return super.handleCharSequence(CharSequenceSensitiveDescriptor.<PhoneNumberSensitive, CharSequence>builder()
                .target(target)
                .annotation(annotation)
                .startOffset(annotation.startOffset())
                .endOffset(annotation.endOffset())
                .regexp(annotation.regexp())
                .placeholder(annotation.placeholder())
                .build());
    }
}
