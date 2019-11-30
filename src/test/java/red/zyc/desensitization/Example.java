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
import red.zyc.desensitization.model.Child;
import red.zyc.desensitization.resolver.TypeToken;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zyc
 */
public class Example {

    private static Logger log = LoggerFactory.getLogger(Example.class);

    /**
     * 对于单个值类型的脱敏，脱敏处理必须放在静态代码块中执行，不能放在对象的实例方法中执行，
     * 这是由于jdk解析注解的一个bug导致的。
     *
     * @see <a href="http://stackoverflow.com/questions/39952812/why-annotation-on-generic-type-argument-is-not-visible-for-nested-type"></a>
     */
    private static void desensitize() {
        // 单个值
        log.info("值脱敏：{}", Sensitive.desensitize("123456@qq.com", new TypeToken<@EmailSensitive String>() {
        }));

        // Collection
        log.info("集合值脱敏：{}", Sensitive.desensitize(Stream.of("123456@qq.com", "1234567@qq.com", "1234568@qq.com").collect(Collectors.toList()),
                new TypeToken<List<@EmailSensitive String>>() {
                }));

        // Array
        log.info("数组值脱敏：{}", Arrays.toString(Sensitive.desensitize(new String[]{"123456@qq.com", "1234567@qq.com", "12345678@qq.com"},
                new TypeToken<@EmailSensitive String[]>() {
                })));

        // Map
        log.info("Map值脱敏：{}", Sensitive.desensitize(Stream.of("张三", "李四", "小明").collect(Collectors.toMap(s -> s, s -> "123456@qq.com")),
                new TypeToken<Map<@ChineseNameSensitive String, @EmailSensitive String>>() {
                }));
    }

    /**
     * 这是一个错误的示例，对于单个值脱敏，放在实例方法中是不生效的，必须将脱敏代码放在静态方法中执行。这是由于jdk解析注解的一个bug导致的。
     */
    @Test
    public void wrongDesensitizeValue() {
        log.info("不要在实例方法中脱敏单个值：{}", Sensitive.desensitize("123456@qq.com", new TypeToken<@EmailSensitive String>() {
        }));
    }

    /**
     * 对象内部域值脱敏
     */
    @Test
    public void desensitizeObject() {
        Child<?> before = new Child<>();
        log.info("脱敏前原对象:{}", before);
        Child<?> after = Sensitive.desensitize(before);
        log.info("脱敏后的新对象：{}", after);
        log.info("脱敏后原对象:{}", before);
    }

    /**
     * 单个值脱敏，脱敏代码必须放到静态方法中执行。
     */
    @Test
    public void desensitizeValue() {
        desensitize();
    }
}
