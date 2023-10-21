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
import red.zyc.desensitization.Sensitive;
import red.zyc.desensitization.annotation.Strings;
import red.zyc.parser.type.AnnotatedTypeToken;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * {@link Map}脱敏
 *
 * @author zyc
 */
public class MapTest {

    @Test
    void desensitize() {

        var before = IntStream.range(0, 10).boxed().collect(Collectors.toMap(Function.identity(), Object::toString));
        var after = Sensitive.desensitize(before, new AnnotatedTypeToken<Map<Integer, @Strings String>>() {
        });

        after.forEach((k, v) -> Assertions.assertEquals("*", v));
    }
}
