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
import red.zyc.desensitization.annotation.ChineseNameSensitive;
import red.zyc.desensitization.annotation.EraseSensitive;
import red.zyc.desensitization.annotation.IdCardNumberSensitive;
import red.zyc.desensitization.annotation.PhoneNumberSensitive;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zyc
 */
@Getter
@Setter
@ToString
public class Child {

    @ChineseNameSensitive
    String name = "李富贵";

    @PhoneNumberSensitive(start = 2, end = 4)
    String phoneNumber = "12345678910";

    @IdCardNumberSensitive
    String idCardNumber = "321181199301096000";

    @EraseSensitive
    private List<Parent> parents = new ArrayList<>();
}