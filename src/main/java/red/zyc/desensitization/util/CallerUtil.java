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
 * 调用者工具类，用来返回当前调用该方法的对象的{@code Class}。
 * 注意由于{@link Thread#getStackTrace()}相比较于{@link SecurityManager#getClassContext()}返回的执行堆栈信息更加全面，
 * 因此当调用{@link CallerUtil#getCaller(int)}方法时，不同的{@link Caller}返回的值可能会不同。默认的{@link Caller}
 * 是{@link SecurityManagerCaller}。可以调用{@link Caller#printStackTrace()}方法来输出当前执行的堆栈信息。
 *
 * @author zyc
 */
public final class CallerUtil {

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
     * @param depth 深度
     * @return {@linkplain Caller#getCaller(int)}
     */
    public static Class<?> getCaller(int depth) {
        return CALLER.getCaller(depth);
    }

    /**
     * {@linkplain Caller#printStackTrace()}
     */
    public static void printStackTrace() {
        CALLER.printStackTrace();
    }
}
