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
 * 通过{@link SecurityManager}来获取当前方法执行堆栈
 *
 * @author zyc
 */
public class SecurityManagerCaller extends SecurityManager implements Caller {

    @Override
    public Class<?> getCaller() {
        Class<?>[] classContext = getClassContext();
        if (classContext != null && classContext.length > CURRENT_DEPTH) {
            return getClassContext()[CURRENT_DEPTH];
        }
        return null;
    }

    @Override
    public Class<?> getCallerCaller() {
        Class<?>[] classContext = getClassContext();
        if (classContext != null && classContext.length > CURRENT_DEPTH) {
            return getClassContext()[CURRENT_DEPTH + 1];
        }
        return null;
    }
}
