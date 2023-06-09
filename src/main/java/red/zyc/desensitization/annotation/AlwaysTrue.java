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
package red.zyc.desensitization.annotation;

/**
 * @author zyc
 */
public class AlwaysTrue {

    public static class Object implements Condition<java.lang.Object> {

        @Override
        public boolean required(java.lang.Object target) {
            return true;
        }
    }

    public static class CharSequence implements Condition<java.lang.CharSequence> {

        @Override
        public boolean required(java.lang.CharSequence target) {
            return true;
        }
    }

}
