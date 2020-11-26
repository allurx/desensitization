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

package red.zyc.desensitization.support;

import red.zyc.desensitization.exception.DesensitizationException;
import red.zyc.desensitization.exception.UnsupportedCollectionException;
import red.zyc.desensitization.exception.UnsupportedMapException;
import red.zyc.desensitization.util.ReflectionUtil;
import red.zyc.desensitization.util.UnsafeUtil;
import sun.misc.Unsafe;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实例创建器帮助类，用户可以通过这个类注册或者删除指定类型的实例创建器。
 *
 * @author zyc
 * @see InstanceCreator
 */
public final class InstanceCreators {

    private static final Map<?, ?> EMPTY_MAP = new HashMap<>();
    private static final List<?> EMPTY_LIST = new ArrayList<>();
    private static final Map<Class<?>, InstanceCreator<?>> INSTANCE_CREATORS = new ConcurrentHashMap<>();

    /**
     * 获取指定{@link Class}的实例创建器，尝试获取的顺序如下：
     * <ol>
     *     <li>如果{@link InstanceCreators#INSTANCE_CREATORS}中已存在该类型的实例创建器则直接返回。</li>
     *     <li>如果该类型存在无参构造器，则通过该构造器来生成实例创建器，然后存放到{@link InstanceCreators#INSTANCE_CREATORS}中。</li>
     *     <li>如果该类型是{@link Collection}或者{@link Map}类型则尝试获取其带有一个{@link Collection}或者{@link Map}
     *     类型参数的构造器来生成实例创建器，具体原因请参照{@link Collection}或者{@link Map}的定义规范，
     *     然后存放到{@link InstanceCreators#INSTANCE_CREATORS}中。
     *     </li>
     *     <li>通过{@link Unsafe#allocateInstance}方法来生成实例创建器，然后存放到{@link InstanceCreators#INSTANCE_CREATORS}中。</li>
     * </ol>
     *
     * @param clazz 指定的{@link Class}
     * @param <T>   实例创建器创建的对象类型
     * @return 指定 {@link Class}的实例创建器
     */
    @SuppressWarnings("unchecked")
    public static <T> InstanceCreator<T> getInstanceCreator(Class<T> clazz) {
        return (InstanceCreator<T>) INSTANCE_CREATORS.computeIfAbsent(clazz, c -> Optional.ofNullable(noArgsConstructor(clazz))
                .orElseGet(() -> Optional.ofNullable(collectionOrMapConstructor(clazz))
                        .orElse(() -> UnsafeUtil.newInstance(clazz))
                ));
    }

    /**
     * 注册指定{@link Class}的实例创建器。
     * <p><strong>注意：不要去注册基本类型、接口类型、抽象类型、数组类型的实例创建器，
     * 这些类型的实例创建器是没有意义的，而应该注册那些在初始化过程中可能需要进行一些额外操作的具体类型。
     * 因为程序运行期间不一定能够“准确的实例化对象”。
     * </strong></p>
     *
     * @param clazz           指定的{@link Class}
     * @param instanceCreator 指定{@link Class}的实例创建器
     * @param <T>             实例创建器创建的对象类型
     */
    public static <T> void register(Class<T> clazz, InstanceCreator<T> instanceCreator) {
        INSTANCE_CREATORS.put(clazz, instanceCreator);
    }

    /**
     * 移除指定{@link Class}的实例创建器
     *
     * @param clazz 指定的{@link Class}
     * @param <T>   指定的{@link Class}的实例类型
     */
    public static <T> void remove(Class<T> clazz) {
        INSTANCE_CREATORS.remove(clazz);
    }

    /**
     * @return 所有实例创建器
     */
    public static Map<Class<?>, InstanceCreator<?>> instanceCreators() {
        return INSTANCE_CREATORS;
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
            return Optional.ofNullable(ReflectionUtil.getDeclaredConstructor(clazz, Collection.class))
                    .map(constructor -> (InstanceCreator<T>) () -> ReflectionUtil.newInstance(constructor, EMPTY_LIST))
                    .orElseThrow(() -> new UnsupportedCollectionException(String.format("%s必须遵守Collection中的约定，定义一个无参构造函数和带有一个Collection类型参数的构造函数。", clazz)));
        } else if (Map.class.isAssignableFrom(clazz)) {
            return Optional.ofNullable(ReflectionUtil.getDeclaredConstructor(clazz, Map.class))
                    .map(constructor -> (InstanceCreator<T>) () -> ReflectionUtil.newInstance(constructor, EMPTY_MAP))
                    .orElseThrow(() -> new UnsupportedMapException(String.format("%s必须遵守Map中的约定，定义一个无参构造函数和带有一个Map类型参数的构造函数。", clazz)));
        }
        return null;
    }

    private InstanceCreators() {
    }
}
