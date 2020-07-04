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

import red.zyc.desensitization.exception.DesensitizationException;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author zyc
 */
public final class UnsafeUtil {

    private static final Unsafe UNSAFE;

    static {
        try {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Field f = unsafeClass.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            UNSAFE = (Unsafe) f.get(null);
        } catch (Exception e) {
            throw new DesensitizationException(String.format("初始化%s失败！", Unsafe.class), e);
        }
    }

    private UnsafeUtil() {
    }

    /**
     * 实例化指定{@link Class}
     *
     * @param clazz 对象的{@link Class}
     * @param <T>   对象的类型
     * @return 指定 {@link Class}的对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Class<T> clazz) {
        try {
            return (T) UNSAFE.allocateInstance(clazz);
        } catch (InstantiationException e) {
            throw new DesensitizationException("实例化" + clazz + "失败", e);
        }
    }

}
