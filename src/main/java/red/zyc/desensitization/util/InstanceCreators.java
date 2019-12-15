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
import red.zyc.desensitization.exception.UnsupportedCollectionException;
import red.zyc.desensitization.exception.UnsupportedMapException;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zyc
 */
public final class InstanceCreators {

    private static final Map<?, ?> EMPTY_MAP = new HashMap<>();
    private static final List<?> EMPTY_LIST = new ArrayList<>();
    private static final Map<Class<?>, InstanceCreator<?>> INSTANCE_CREATORS = new ConcurrentHashMap<>();

    private InstanceCreators() {
    }

    /**
     * 获取指定{@link Class}的实例创建器
     *
     * @param clazz 指定的{@link Class}
     * @param <T>   实例创建器创建的对象类型
     * @return 指定 {@link Class}的实例创建器
     */
    @SuppressWarnings("unchecked")
    public static <T> InstanceCreator<T> getCreator(Class<T> clazz) {
        return (InstanceCreator<T>) INSTANCE_CREATORS.computeIfAbsent(clazz, c -> Optional.ofNullable(collectionOrMapConstructor(clazz))
                .orElseGet(() -> Optional.ofNullable(noArgsConstructor(clazz))
                        .orElse(() -> UnsafeUtil.newInstance(clazz))
                ));
    }

    /**
     * 注册指定{@link Class}的实例创建器
     *
     * @param clazz           指定的{@link Class}
     * @param instanceCreator 指定{@link Class}的实例创建器
     * @param <T>             实例创建器创建的对象类型
     */
    public static <T> void register(Class<T> clazz, InstanceCreator<T> instanceCreator) {
        INSTANCE_CREATORS.put(clazz, instanceCreator);
    }

    /**
     * 获取指定{@link Class}的无参构造器
     *
     * @param clazz 对象的{@link Class}
     * @param <T>   对象的类型
     * @return 该对象的实例创建器
     */
    private static <T> InstanceCreator<T> noArgsConstructor(Class<T> clazz) {
        return Optional.ofNullable(ReflectionUtil.getDeclaredConstructor(clazz))
                .map(constructor -> (InstanceCreator<T>) () -> {
                    try {
                        return constructor.newInstance();
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        throw new DesensitizationException(e.getMessage(), e);
                    }
                }).orElse(null);
    }

    /**
     * 获取{@link Collection}或者{@link Map}类型对象的构造器
     *
     * @param clazz 对象的{@link Class}
     * @param <T>   对象的类型
     * @return 该对象的实例创建器
     */
    private static <T> InstanceCreator<T> collectionOrMapConstructor(Class<T> clazz) {
        if (Collection.class.isAssignableFrom(clazz)) {
            return Optional.ofNullable(noArgsConstructor(clazz))
                    .orElseGet(() -> Optional.ofNullable(ReflectionUtil.getDeclaredConstructor(clazz, Collection.class))
                            .map(constructor -> (InstanceCreator<T>) () -> {
                                try {
                                    return constructor.newInstance(EMPTY_LIST);
                                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                                    throw new DesensitizationException(e.getMessage(), e);
                                }
                            }).orElseThrow(() -> new UnsupportedCollectionException(clazz + "必须遵守Collection中的约定，定义一个无参构造函数和带有一个Collection类型参数的构造函数。"))
                    );
        } else if (Map.class.isAssignableFrom(clazz)) {
            return Optional.ofNullable(noArgsConstructor(clazz))
                    .orElseGet(() -> Optional.ofNullable(ReflectionUtil.getDeclaredConstructor(clazz, Map.class))
                            .map(constructor -> (InstanceCreator<T>) () -> {
                                try {
                                    return constructor.newInstance(EMPTY_MAP);
                                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                                    throw new DesensitizationException(e.getMessage(), e);
                                }
                            }).orElseThrow(() -> new UnsupportedMapException(clazz + "必须遵守Map中的约定，定义一个无参构造函数和带有一个Map类型参数的构造函数。"))
                    );
        }
        return null;
    }

}
