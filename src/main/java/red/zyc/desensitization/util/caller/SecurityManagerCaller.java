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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * 通过{@link SecurityManager}来获取当前方法执行堆栈，注意{@link SecurityManager#getClassContext()}方法返回的
 * 类数组执行堆栈会忽略jdk中的类，而{@link Thread#getStackTrace()}返回的则是当前线程完整的所有执行堆栈，信息更为丰富。
 *
 * @author zyc
 */
public class SecurityManagerCaller extends SecurityManager implements Caller {

    private static Logger log = LoggerFactory.getLogger(SecurityManagerCaller.class);

    @Override
    public Class<?> getCaller() {
        return getCaller(3);
    }

    @Override
    public Class<?> getCaller(int depth) {
        Class<?>[] classContext = getClassContext();
        if (classContext != null && classContext.length > 0 && depth >= 0) {
            return getClassContext()[depth];
        }
        return null;
    }

    @Override
    public void printStackTrace() {
        log.info(Arrays.toString(getClassContext()));
    }
}
