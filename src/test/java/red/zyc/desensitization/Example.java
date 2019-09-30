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
package red.zyc.desensitization;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import red.zyc.desensitization.annotation.EmailSensitive;
import red.zyc.desensitization.model.Child;
import red.zyc.desensitization.model.Father;
import red.zyc.desensitization.model.Mother;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * @author zyc
 */
@Slf4j
public class Example {


//    @Test
//    public void desensitizeEmail() {
//        String email = "123456@qq.com";
//        log.info("before:" + email);
//        email = (String) SensitiveUtil.desensitize("123456@qq.com", new @EmailSensitive EmailSensitiveHandler() {
//        });
//        log.info("end:" + email);
//    }

    @Test
    public void desensitizeObject() {
        Child child = new Child();
        child.getParents().add(new Father());
        child.getParents().add(new Mother());
        log.info("before:" + child.toString());
        SensitiveUtil.desensitize(child);
        log.info("end:" + child.toString());
    }

    @Test
    public void desensitizeEmail1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String email = "123456@qq.com";

        email = SensitiveUtil.
                desensitize("123456@qq.com", (@EmailSensitive(regexp = "222") String target, EmailSensitive annotation) -> null);

    }

    @Test
    public void t()  {
        System.out.println(Arrays.toString(getClass().getDeclaredMethods()));

    }

}
