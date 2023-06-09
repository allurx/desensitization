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
import red.zyc.desensitization.annotation.PhoneNumber;

/**
 * @author zyc
 */
public class Father extends Parent {

    @ChineseName(regexp = "爸")
    public String name = "明明爸";

    @PhoneNumber
    public String phoneNumber = "19962000002";

    @Email
    public String email = "444444@qq.com";

    @IdCardNumber
    public String idCardNumber = "321181198301096001";

    @BankCardNumber
    public String bankCardNumber = "6222600260001072441";

}
