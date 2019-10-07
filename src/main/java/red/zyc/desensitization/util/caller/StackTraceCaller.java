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
 * @author zyc
 */
public class StackTraceCaller implements Caller {

    private static Logger log = LoggerFactory.getLogger(StackTraceCaller.class);

    @Override
    public Class<?> getCaller() {
        return getCaller(4);
    }

    @Override
    public Class<?> getCaller(int depth) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        if (stackTraceElements.length > 0 && depth >= 0) {
            try {
                return Class.forName(stackTraceElements[depth].getClassName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void printStackTrace() {
        log.info(Arrays.toString(Thread.currentThread().getStackTrace()));
    }
}
