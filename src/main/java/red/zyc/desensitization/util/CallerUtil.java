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

package red.zyc.desensitization.util;

import red.zyc.desensitization.util.caller.Caller;
import red.zyc.desensitization.util.caller.SecurityManagerCaller;
import red.zyc.desensitization.util.caller.StackTraceCaller;

/**
 * @author zyc
 */
public class CallerUtil {

    private static final Caller CALLER;

    static {
        Caller temp = null;
        try {
            temp = new SecurityManagerCaller();
        } catch (Throwable t) {
            temp = new StackTraceCaller();
        } finally {
            CALLER = temp;
        }
    }

    /**
     * @return {@linkplain Caller#getCaller()}
     */
    public static Class<?> getCaller() {
        return CALLER.getCaller();
    }

    /**
     * @return {@linkplain Caller#getCallerCaller()}
     */
    public static Class<?> getCallerCaller() {
        return CALLER.getCallerCaller();
    }

    /**
     * @return {@linkplain Caller#getCaller(int)}
     */
    public static Class<?> getCaller(int depth) {
        return CALLER.getCaller(depth);
    }
}
