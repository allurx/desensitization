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

import red.zyc.desensitization.annotation.CharSequenceSensitive;

/**
 * 通用的{@link CharSequence}类型敏感信息处理者
 *
 * @author zyc
 */
public class CharSequenceSensitiveHandler extends AbstractCharSequenceSensitiveHandler<CharSequenceSensitive, CharSequence> {

    @Override
    public CharSequence handle(CharSequence target, CharSequenceSensitive annotation) {
        return handleCharSequence(annotation.regexp(), annotation.startOffset(), annotation.endOffset(), target);
    }
}
