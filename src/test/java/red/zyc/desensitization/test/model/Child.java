/*
 * Copyright 2023 the original author or authors.
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

package red.zyc.desensitization.test.model;

import red.zyc.desensitization.annotation.BankCardNumber;
import red.zyc.desensitization.annotation.ChineseName;
import red.zyc.desensitization.annotation.Email;
import red.zyc.desensitization.annotation.IdCardNumber;
import red.zyc.desensitization.annotation.Password;
import red.zyc.desensitization.annotation.PhoneNumber;
import red.zyc.parser.type.Cascade;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zyc
 */
public class Child<T extends List<@Email String>> {

    @ChineseName(placeholder = 'x')
    public String name = "小明";

    @PhoneNumber
    public String phoneNumber = "19962000001";

    @IdCardNumber
    public String idCardNumber = "321181199301096000";

    @Password
    public String password = "123456789";

    @BankCardNumber
    public String bankCardNumber = "6222600260001072440";

    public List<@Cascade(inherited = true) Parent> parents = Stream.of(new Father(), new Mother()).collect(Collectors.toList());

    @SuppressWarnings("unchecked")
    public T emails = (T) Stream.of("111111@qq.com", "222222@qq.com", "333333@qq.com").collect(Collectors.toList());

}
