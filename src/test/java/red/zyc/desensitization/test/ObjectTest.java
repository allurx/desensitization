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
package red.zyc.desensitization.test;

import org.junit.jupiter.api.Test;
import red.zyc.desensitization.Sensitive;
import red.zyc.desensitization.test.model.Child;
import red.zyc.desensitization.test.model.Father;
import red.zyc.desensitization.test.model.Mother;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 对象{@link Field}脱敏
 *
 * @author zyc
 */
public class ObjectTest {

    @Test
    void desensitize() {

        var before = new Child<>();
        var after = Sensitive.desensitize(before);

        // Child Self
        assertEquals("小x", after.name);
        assertEquals("199****0001", after.phoneNumber);
        assertEquals("321181********6000", after.idCardNumber);
        assertEquals("*********", after.password);
        assertEquals("***************2440", after.bankCardNumber);
        assertEquals("1*****@qq.com", after.emails.get(0));
        assertEquals("2*****@qq.com", after.emails.get(1));
        assertEquals("3*****@qq.com", after.emails.get(2));


        // Child Father
        var father = (Father) after.parents.get(0);
        assertEquals("明明*", father.name);
        assertEquals("199****0002", father.phoneNumber);
        assertEquals("4*****@qq.com", father.email);
        assertEquals("321181********6001", father.idCardNumber);
        assertEquals("***************2441", father.bankCardNumber);
        assertEquals("******", father.password);

        // Child Mother
        var mother = (Mother) after.parents.get(1);
        assertEquals("明明*", mother.name);
        assertEquals("199****0003", mother.phoneNumber);
        assertEquals("5*****@qq.com", mother.email);
        assertEquals("321181********6002", mother.idCardNumber);
        assertEquals("***************2442", mother.bankCardNumber);
        assertEquals("******", mother.password);

    }
}
