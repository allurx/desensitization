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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import red.zyc.desensitization.annotation.CharSequence;
import red.zyc.parser.AnnotationParser;
import red.zyc.parser.type.AnnotatedTypeToken;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * {@link Array}脱敏
 *
 * @author zyc
 */
public class ArrayTest {

    @Test
    void desensitize() {

        var before = new String[]{"123456", "123456", "123456"};
        var after = AnnotationParser.parse(before, new AnnotatedTypeToken<@CharSequence String[]>() {
        });

        Arrays.stream(after).forEach(s -> Assertions.assertEquals("******", s));
    }
}
