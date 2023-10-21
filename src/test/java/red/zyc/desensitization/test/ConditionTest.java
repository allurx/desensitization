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
import red.zyc.desensitization.annotation.Condition;
import red.zyc.desensitization.annotation.Strings;
import red.zyc.parser.type.AnnotatedTypeToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * 自定义脱敏生效的条件
 *
 * @author zyc
 */
public class ConditionTest {

    @Test
    void desensitize() {

        var before = new String[]{"", null, "123456"};

        var after = Sensitive.desensitize(before, new AnnotatedTypeToken<@Strings(condition = StringCondition.class) String[]>() {
        });

        assertEquals("", after[0]);
        assertNull(after[1]);
        assertEquals("******", after[2]);
    }

    /**
     * 只对非null值和非空字符串进行脱敏
     */
    private static class StringCondition implements Condition<String> {

        @Override
        public boolean required(String target) {
            return target != null && !target.isEmpty();
        }
    }
}
