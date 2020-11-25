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

import org.junit.Assert;
import org.junit.Test;
import red.zyc.desensitization.annotation.ChineseNameSensitive;
import red.zyc.desensitization.annotation.EmailSensitive;
import red.zyc.desensitization.model.Child;
import red.zyc.desensitization.support.TypeToken;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zyc
 */
public class DesensitizationTest {

    /**
     * 这是一个错误的示例：<p>{@link TypeToken}不能在实例方法、实例代码块中初始化同时也不能作为成员变量初始化，必须放在静态方法、静态代码块中初始化或者作为静态变量初始化。</p>
     * 这是由于<a href="http://stackoverflow.com/questions/39952812/why-annotation-on-generic-type-argument-is-not-visible-for-nested-type">jdk解析注解的bug</a>导致的。
     */
    @Test
    public void wrongDesensitize() {
        String desensitize = Sensitive.desensitize("123456@qq.com", new TypeToken<@EmailSensitive String>() {
        });
        Assert.assertEquals("123456@qq.com", desensitize);
        System.err.printf("TypeToken不能在实例方法、实例代码块中初始化同时也不能作为成员变量进行初始化，必须放在静态方法、静态代码块中初始化或者作为静态变量初始化: %s%n", desensitize);
    }

    /**
     * 对象内部域值脱敏
     */
    @Test
    public void desensitizeObject() {
        Child<?> before = new Child<>();
        Child<?> after = Sensitive.desensitize(before);
        Assert.assertNotEquals(before, after);
        System.out.printf("对象脱敏: %s%n", after);
    }

    /**
     * 根据{@link TypeToken}脱敏对象
     */
    @Test
    public void desensitizeAccordingToTypeToken() {
        desensitize();
    }

    private static void desensitize() {

        // String
        System.out.printf("字符串脱敏: %s%n", Sensitive.desensitize("123456@qq.com", new TypeToken<@EmailSensitive String>() {
        }));

        // Collection
        System.out.printf("集合脱敏: %s%n", Sensitive.desensitize(Stream.of("123456@qq.com", "1234567@qq.com", "1234568@qq.com").collect(Collectors.toList()),
                new TypeToken<List<@EmailSensitive String>>() {
                }));

        // Array
        System.out.printf("数组脱敏: %s%n", Arrays.toString(Sensitive.desensitize(new String[]{"123456@qq.com", "1234567@qq.com", "12345678@qq.com"},
                new TypeToken<@EmailSensitive String[]>() {
                })));

        // Map
        System.out.printf("Map脱敏: %s%n", Sensitive.desensitize(Stream.of("张三", "李四", "小明").collect(Collectors.toMap(s -> s, s -> "123456@qq.com")),
                new TypeToken<Map<@ChineseNameSensitive String, @EmailSensitive String>>() {
                }));

        Assert.assertTrue(true);
    }

}
