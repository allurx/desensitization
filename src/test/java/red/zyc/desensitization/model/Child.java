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
import red.zyc.desensitization.desensitizer.Desensitizer;
import red.zyc.desensitization.desensitizer.PhoneNumberDesensitizer;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zyc
 */
public class Child<T extends Collection<@EmailSensitive String>> {

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

    @EraseSensitive
    private Mother mother = new Mother();

    @EraseSensitive
    private Father father = new Father();

    private List<@EraseSensitive Parent> parents1 = Stream.of(new Father(), new Mother()).collect(Collectors.toList());

    private List<@EmailSensitive String> emails1 = Stream.of("123456@qq.com", "1234567@qq.com", "1234568@qq.com").collect(Collectors.toList());

    private Map<@ChineseNameSensitive String, @EmailSensitive String> emails2 = Stream.of("张三", "李四", "小明").collect(Collectors.toMap(s -> s, s -> "123456@qq.com"));

    private Map<@EraseSensitive Parent, @EmailSensitive String> parents2 = Stream.of(new Father(), new Mother()).collect(Collectors.toMap(p -> p, p -> "123456@qq.com"));

    private @PasswordSensitive String[] passwords = {"123456", "1234567", "12345678"};

    private @EraseSensitive Parent[] parents3 = {new Father(), new Mother()};

    private Map<List<@EmailSensitive String[]>, Map<@EraseSensitive Parent, List<@EmailSensitive String>[]>> map1 = new HashMap<>();

    @SuppressWarnings("unchecked")
    private T t = (T) Stream.of("123456@qq.com", "1234567@qq.com", "1234568@qq.com").collect(Collectors.toList());

    private List<@EraseSensitive ? extends Parent> parents = Stream.of(new Father(), new Mother()).collect(Collectors.toList());

    @SuppressWarnings("unchecked")
    private List<? extends T> list = (List<? extends T>) Stream.of(Stream.of("123456@qq.com", "1234567@qq.com", "1234568@qq.com").collect(Collectors.toList())).collect(Collectors.toList());

    // 复杂字段赋值
    {
        // map1
        List<String[]> list = Stream.of(new String[]{"123456@qq.com"}, new String[]{"1234567@qq.com"}, new String[]{"1234567@qq.com", "12345678@qq.com"}).collect(Collectors.toList());
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
                ", mother=" + mother +
                ", father=" + father +
                ", parents1=" + parents1 +
                ", emails1=" + emails1 +
                ", emails2=" + emails2 +
                ", parents2=" + parents2 +
                ", passwords=" + Arrays.toString(passwords) +
                ", parents3=" + Arrays.toString(parents3) +
                ", map1=" + map1 +
                ", t=" + t +
                ", parents=" + parents +
                ", list=" + list +
                '}';
    }

    /**
     * 自定义脱敏器处理数字类型的手机号码，默认的脱敏器只支持处理{@link CharSequence}类型的手机号码。
     * 注意内部类必须定义成public的，否则反射初始化时会失败。
     *
     * @see PhoneNumberDesensitizer
     */
    public static class CustomizedPhoneNumberDesensitizer implements Desensitizer<Long, PhoneNumberSensitive> {

        @Override
        public Long desensitize(Long target, PhoneNumberSensitive annotation) {
            return Long.parseLong(target.toString().replaceAll("4567", "0000"));
        }
    }
}
