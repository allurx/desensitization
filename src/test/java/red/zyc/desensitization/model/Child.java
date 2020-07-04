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

import red.zyc.desensitization.annotation.BankCardNumberSensitive;
import red.zyc.desensitization.annotation.CascadeSensitive;
import red.zyc.desensitization.annotation.CharSequenceSensitive;
import red.zyc.desensitization.annotation.ChineseNameSensitive;
import red.zyc.desensitization.annotation.EmailSensitive;
import red.zyc.desensitization.annotation.IdCardNumberSensitive;
import red.zyc.desensitization.annotation.PasswordSensitive;
import red.zyc.desensitization.annotation.PhoneNumberSensitive;
import red.zyc.desensitization.annotation.UsccSensitive;
import red.zyc.desensitization.desensitizer.Condition;
import red.zyc.desensitization.desensitizer.Desensitizer;
import red.zyc.desensitization.desensitizer.PhoneNumberDesensitizer;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zyc
 */
public class Child<T extends Collection<@EmailSensitive String>> extends Parent {

    @ChineseNameSensitive(placeholder = 'x')
    private String name = "李富贵";

    @PhoneNumberSensitive(desensitizer = CustomizedPhoneNumberDesensitizer.class)
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

    @BankCardNumberSensitive
    private String bankCardNumber = "6222600260001072444";

    @CascadeSensitive
    private Mother mother = new Mother();

    @CascadeSensitive
    private Father father = new Father();

    private List<@CascadeSensitive Parent> parents1 = Stream.of(new Father(), new Mother(), null).collect(Collectors.toList());

    private List<@EmailSensitive String> emails1 = Stream.of("123456@qq.com", "1234567@qq.com", "1234568@qq.com").collect(Collectors.toList());

    private Map<@ChineseNameSensitive String, @EmailSensitive String> emails2 = Stream.of("张三", "李四", "小明").collect(Collectors.toMap(s -> s, s -> "123456@qq.com"));

    private Map<@CascadeSensitive Parent, @EmailSensitive String> parents2 = Stream.of(new Father(), new Mother()).collect(Collectors.toMap(p -> p, p -> "123456@qq.com"));

    private @PasswordSensitive String[] passwords = {"123456", "1234567", "12345678", null};

    private @EmailSensitive String[][] emails3 = {{"123456@qq.com", "1234567@qq.com"}, {"12345678@qq.com"}};

    private @EmailSensitive String[][][] emails4 = {{{"123456@qq.com", "1234567@qq.com"}, {"12345678@qq.com"}}, {{"123456@qq.com"}, {"123456@qq.com"}, {"123456@qq.com"}}};

    private @CascadeSensitive Parent[] parents3 = {new Father(), new Mother()};

    private Map<List<@EmailSensitive String[]>, Map<@CascadeSensitive Parent, List<@EmailSensitive String>[]>> map1 = new HashMap<>();

    @SuppressWarnings("unchecked")
    private T t = (T) Stream.of("123456@qq.com", "1234567@qq.com", "1234568@qq.com").collect(Collectors.toList());

    private List<@CascadeSensitive ? extends Parent> parents = Stream.of(new Father(), new Mother()).collect(Collectors.toList());

    @SuppressWarnings("unchecked")
    private List<? extends T> list = (List<? extends T>) Stream.of(Stream.of("123456@qq.com", "1234567@qq.com", "1234568@qq.com", null).collect(Collectors.toList())).collect(Collectors.toList());

    private List<@CharSequenceSensitive(condition = StringCondition.class) String> condition = Stream.of("1", "2", "3", "4", "5", "6").collect(Collectors.toList());

    // 复杂字段赋值
    {
        // map1
        List<String[]> list = Stream.of(new String[]{"123456@qq.com", null}, new String[]{"1234567@qq.com"}, new String[]{"1234567@qq.com", "12345678@qq.com"}).collect(Collectors.toList());
        @SuppressWarnings("unchecked")
        Map<Parent, List<String>[]> map = Stream.of(new Father(), new Mother()).collect(Collectors.toMap(p -> p, p -> (List<String>[]) new List<?>[]{Stream.of("123456@qq.com", "1234567@qq.com", "1234568@qq.com").collect(Collectors.toList())}));
        map1.put(list, map);
    }

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
                ", bankCardNumber='" + bankCardNumber + '\'' +
                ", mother=" + mother +
                ", father=" + father +
                ", parents1=" + parents1 +
                ", emails1=" + emails1 +
                ", emails2=" + emails2 +
                ", parents2=" + parents2 +
                ", passwords=" + Arrays.toString(passwords) +
                ", emails3=" + Arrays.deepToString(emails3) +
                ", emails4=" + Arrays.deepToString(emails4) +
                ", parents3=" + Arrays.deepToString(parents3) +
                ", map1=" + map1 +
                ", t=" + t +
                ", parents=" + parents +
                ", list=" + list +
                ", condition=" + condition +
                "} " + super.toString();
    }

    /**
     * 自定义脱敏器处理数字类型的手机号码，默认的脱敏器只支持处理{@link String}类型的手机号码。
     *
     * @see PhoneNumberDesensitizer
     */
    private static class CustomizedPhoneNumberDesensitizer implements Desensitizer<Long, PhoneNumberSensitive> {

        @Override
        public Long desensitize(Long target, PhoneNumberSensitive annotation) {
            return Long.parseLong(target.toString().replace("4567", "0000"));
        }
    }

    private static class StringCondition implements Condition<String> {

        @Override
        public boolean required(String target) {
            return !target.equals("1") && !target.equals("3") && !target.equals("5");
        }
    }
}
