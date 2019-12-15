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

/**
 * 实例创建器，用户可以通过该接口实现特定类型的实例创建器，例如有以下对象定义：
 * <pre>
 *     class Person {
 *         Person(your constructor parameters){
 *              ...
 *         }
 *     }
 * </pre>
 * 在上述的类Person定义中只存在一个有参构造器，不存在无参构造器，因此在程序运行期间只能确定
 * 一个有参构造器，但不知道传入什么参数来调用该构造器，最终程序会基于{@link sun.misc.Unsafe}
 * 来构造这个对象，但是可能会丢失一些必要的对象信息，所以建议在定义自己的类时为其添加一个无参构造器
 * 以便程序能够基于反射来初始化该对象，或者你也可以实现实例创建器接口来定义类Person的默认创建器：
 * <pre>
 *     class PersonInstanceCreator implements InstanceCreator&lt;Person&gt; {
 *
 *          &#64;Override
 *          public Person create(){
 *              return new Person(your constructor parameters);
 *          }
 *
 *     }
 * </pre>
 * 然后调用{@link InstanceCreators#register}方法注册该实例创建器，最终程序在运行期间就会基于这个
 * 注册的实例创建器来创建相应的对象实例。对于实例创建器来说每次调用{@link InstanceCreator#create()}
 * 方法都应该返回一个新的对象，这个新对象就是原对象被脱敏后的对象。
 *
 * @param <T> 实例类型
 * @author zyc
 */
@FunctionalInterface
public interface InstanceCreator<T> {

    /**
     * 创建指定类型的实例
     *
     * @return 类型 {@link T}的默认实例
     */
    T create();
}
