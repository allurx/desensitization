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

package red.zyc.desensitization.util.caller;

/**
 * @author zyc
 */
public class StackTraceCaller implements Caller {

    @Override
    public Class<?> getCaller() {
        return getCaller(1);
    }

    @Override
    public Class<?> getCallerCaller() {
        return getCaller(2);
    }

    @Override
    public Class<?> getCaller(int depth) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length > CURRENT_DEPTH) {
            try {
                return Class.forName(stackTrace[depth + CURRENT_DEPTH].getClassName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
