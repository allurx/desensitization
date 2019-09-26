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
package red.zyc.desensitization.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import red.zyc.desensitization.annotation.*;
import red.zyc.desensitization.handler.AbstractSensitiveHandler;
import red.zyc.desensitization.handler.PhoneNumberSensitiveHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zyc
 */
@Getter
@Setter
@ToString
public class Child {

    @ChineseNameSensitive(placeholder = 'x')
    private String name = "李富贵";

    @PhoneNumberSensitive(handler = CustomizedPhoneNumberSensitiveHandler.class)
    private Long phoneNumber = 12345678910L;

    @IdCardNumberSensitive
    private String idCardNumber = "321181199301096000";

    @UsccSensitive
    private String unifiedSocialCreditCode = "91310106575855456U";

    @CharSequenceSensitive
    private String string = "123456";

    @EmailSensitive
    private String email = "123456@qq.com";

    @PasswordSensitive
    private String password = "123456";

    @EraseSensitive
    private List<Parent> parents = new ArrayList<>();

    /**
     * 自定义处理器处理数字类型的手机号码，默认的处理器只支持处理{@link CharSequence}类型的手机号码。
     * 注意内部类必须定义成public的，否则反射初始化时会失败。
     *
     * @see PhoneNumberSensitiveHandler
     */
    public static class CustomizedPhoneNumberSensitiveHandler extends AbstractSensitiveHandler<PhoneNumberSensitive, Long> {

        @Override
        public Long handle(Long target, PhoneNumberSensitive annotation) {
            return Long.parseLong(target.toString().replaceAll("4567", "0000"));
        }
    }

}
