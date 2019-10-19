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

import red.zyc.desensitization.annotation.*;
import red.zyc.desensitization.handler.PhoneNumberSensitiveHandler;
import red.zyc.desensitization.handler.SensitiveHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zyc
 */
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
    private Mother mother = new Mother();

    @EraseSensitive
    private Father father = new Father();

    private List<@EraseSensitive Parent> parents1 = Arrays.asList(new Father(), new Mother());

    private List<@EmailSensitive String> emails1 = Arrays.asList("123456@qq.com", "1234567@qq.com", "1234568@qq.com");

    private Map<@ChineseNameSensitive String, @EmailSensitive String> emails2 = Stream.of("张三", "李四", "小明").collect(Collectors.toMap(s -> s, s -> "123456@qq.com"));

    private Map<@EraseSensitive Parent, @EmailSensitive String> parents2 = Stream.of(new Father(), new Mother()).collect(Collectors.toMap(p -> p, p -> "123456@qq.com"));

    private @PasswordSensitive String[] passwords = {"123456", "1234567", "12345678"};

    private @EraseSensitive Parent[] parents3 = {new Father(), new Mother()};

    @Override
    public String toString() {
        return new StringJoiner(", ", Child.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("phoneNumber=" + phoneNumber)
                .add("idCardNumber='" + idCardNumber + "'")
                .add("unifiedSocialCreditCode='" + unifiedSocialCreditCode + "'")
                .add("string='" + string + "'")
                .add("email='" + email + "'")
                .add("password='" + password + "'")
                .add("mother=" + mother)
                .add("father=" + father)
                .add("parents1=" + parents1)
                .add("emails1=" + emails1)
                .add("emails2=" + emails2)
                .add("parents2=" + parents2)
                .add("passwords=" + Arrays.toString(passwords))
                .add("parents3=" + Arrays.toString(parents3))
                .toString();
    }

    /**
     * 自定义处理器处理数字类型的手机号码，默认的处理器只支持处理{@link CharSequence}类型的手机号码。
     * 注意内部类必须定义成public的，否则反射初始化时会失败。
     *
     * @see PhoneNumberSensitiveHandler
     */
    public static class CustomizedPhoneNumberSensitiveHandler implements SensitiveHandler<Long, PhoneNumberSensitive> {

        @Override
        public Long handle(Long target, PhoneNumberSensitive annotation) {
            return Long.parseLong(target.toString().replaceAll("4567", "0000"));
        }
    }
}
