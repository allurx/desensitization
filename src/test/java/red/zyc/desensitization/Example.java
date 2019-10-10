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

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import red.zyc.desensitization.annotation.EmailSensitive;
import red.zyc.desensitization.metadata.SensitiveDescriptor;
import red.zyc.desensitization.model.Child;
import red.zyc.desensitization.model.Father;
import red.zyc.desensitization.model.Mother;
import red.zyc.desensitization.util.CallerUtil;

import java.util.*;

/**
 * @author zyc
 */
public class Example {

    private Logger log = LoggerFactory.getLogger(Example.class);

    /**
     * 对象内部域值脱敏
     */
    @Test
    public void desensitizeObject() {
        Child child = new Child();
        child.getParents().add(new Father());
        child.getParents().add(new Mother());
        log.info("before:{}", child.toString());
        Child c = SensitiveUtil.desensitize(child);
        log.info("after:{}", c);
    }

    /**
     * 单个值脱敏
     */
    @Test
    public void desensitizeValue() {

        // 使用Lambda表达式
        String email1 = SensitiveUtil.
                desensitize("123456@qq.com", (@EmailSensitive String value) -> false);
        log.info("after使用Lambda表达式指定敏感信息描述者:{}", email1);


        // 使用匿名内部类
        String email2 = SensitiveUtil.
                desensitize("123456@qq.com", new @EmailSensitive SensitiveDescriptor<String, EmailSensitive>() {
                    @Override
                    public boolean isContainer(String value) {
                        return false;
                    }
                });
        log.info("after使用匿名内部类指定敏感信息描述者:{}", email2);
    }

    /**
     * 集合内部值脱敏
     */
    @Test
    public void desensitizeList() {

        // 使用Lambda表达式
        Set<String> emails1 = SensitiveUtil.
                desensitize(new HashSet<>(Arrays.asList("123456@qq.com", "1234567@qq.com", "12345678@qq.com")),
                        (@EmailSensitive Set<String> value) -> true);
        log.info("after使用Lambda表达式指定敏感信息描述者:{}", emails1);

        // 使用匿名内部类
        List<String> emails2 = SensitiveUtil.
                desensitize(new ArrayList<>(Arrays.asList("123456@qq.com", "1234567@qq.com", "12345678@qq.com")),
                        new @EmailSensitive SensitiveDescriptor<List<String>, EmailSensitive>() {
                            @Override
                            public boolean isContainer(List<String> value) {
                                return true;
                            }
                        });
        log.info("after使用匿名内部类指定敏感信息描述者:{}", emails2);
    }

    /**
     * 数组内部值脱敏
     */
    @Test
    public void desensitizeArray() {

        // 使用Lambda表达式
        String[] emails1 = SensitiveUtil.desensitize(new String[]{"123456@qq.com", "1234567@qq.com", "12345678@qq.com"}, (@EmailSensitive String[] value) -> true);
        log.info("after使用Lambda表达式指定敏感信息描述者:{}", Arrays.toString(emails1));

        // 使用匿名内部类
        String[] emails2 = SensitiveUtil.
                desensitize(new String[]{"123456@qq.com", "1234567@qq.com", "12345678@qq.com"},
                        new @EmailSensitive SensitiveDescriptor<String[], EmailSensitive>() {
                            @Override
                            public boolean isContainer(String[] value) {
                                return true;
                            }
                        });
        log.info("after使用匿名内部类指定敏感信息描述者:{}", Arrays.toString(emails2));
    }

    @Test
    public void printStackTrace() {
        log.info(CallerUtil.getCaller().toString());
        CallerUtil.printStackTrace();
    }
}
