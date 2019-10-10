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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private List<Parent> parents = new ArrayList<>();

    @EraseSensitive
    @EmailSensitive
    private List<String> emails = new ArrayList<>(Arrays.asList("123456@qq.com", "1234567@qq.com", "1234568@qq.com"));

    @Override
    public String toString() {
        return "Child{" +
                "name='" + name + '\'' +
                ", phoneNumber=" + phoneNumber +
                ", idCardNumber='" + idCardNumber + '\'' +
                ", unifiedSocialCreditCode='" + unifiedSocialCreditCode + '\'' +
                ", string='" + string + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", parents=" + parents +
                ", emails=" + emails +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getIdCardNumber() {
        return idCardNumber;
    }

    public void setIdCardNumber(String idCardNumber) {
        this.idCardNumber = idCardNumber;
    }

    public String getUnifiedSocialCreditCode() {
        return unifiedSocialCreditCode;
    }

    public void setUnifiedSocialCreditCode(String unifiedSocialCreditCode) {
        this.unifiedSocialCreditCode = unifiedSocialCreditCode;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Parent> getParents() {
        return parents;
    }

    public void setParents(List<Parent> parents) {
        this.parents = parents;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
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
