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
import red.zyc.desensitization.annotation.ChineseNameSensitive;
import red.zyc.desensitization.annotation.EmailSensitive;
import red.zyc.desensitization.metadata.resolver.TypeToken;
import red.zyc.desensitization.model.Child;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zyc
 */
public class Example {

    private Logger log = LoggerFactory.getLogger(Example.class);


    public static void main(String[] args) {
        // 单个值
        System.out.println(Sensitive.desensitize("123456@qq.com", new TypeToken<@EmailSensitive String>() {
        }));

        // Collection
        System.out.println(Sensitive.desensitize(Stream.of("123456@qq.com", "1234567@qq.com", "1234568@qq.com").collect(Collectors.toList()),
                new TypeToken<List<@EmailSensitive String>>() {
                }));

        // Array
        System.out.println(Arrays.toString(Sensitive.desensitize(new String[]{"123456@qq.com", "1234567@qq.com", "12345678@qq.com"},
                new TypeToken<@EmailSensitive String[]>() {
                })));

        // Map
        System.out.println(Sensitive.desensitize(Stream.of("张三", "李四", "小明").collect(Collectors.toMap(s -> s, s -> "123456@qq.com")),
                new TypeToken<Map<@ChineseNameSensitive String, @EmailSensitive String>>() {
                }));
    }

    void t() {
        System.out.println(Sensitive.desensitize("123456@qq.com", new TypeToken<@EmailSensitive String>() {
        }));
    }


    /**
     * 对象内部域值脱敏
     */
    @Test
    public void desensitizeObject() {
        Child<?> child = new Child<>();
        log.info("before擦除复杂对象内部敏感信息:{}", child.toString());
        Child<?> c = Sensitive.desensitize(child);
        log.info("after擦除复杂对象内部敏感信息:{}", c);
    }
}
