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
package red.zyc.desensitization.annotation;


import red.zyc.desensitization.handler.AbstractSensitiveHandler;

import java.lang.annotation.*;

/**
 * 标记注解，表明当前注解是一个用来擦除敏感信息的注解。
 * <ul>
 *     <li>被该注解标注的注解必须定义一个名称为{@code handler}的方法，并且方法的返回值是{@link AbstractSensitiveHandler}<br>
 *     子类的{@code Class}对象。用来表明擦除敏感信息的处理器。
 *     </li>
 *     <li>
 *         处理器必须拥有无参构造函数，在擦除敏感信息时会根据{@code handler}方法返回的{@code Class}对象通过反射实例化这个处理器。
 *     </li>
 * </ul>
 * 下面是一个擦除敏感信息注解的定义例子：
 * <pre>
 * &#64;Target(ElementType.FIELD)
 * &#64;Retention(RetentionPolicy.RUNTIME)
 * &#64;Documented
 * &#64;Sensitive
 * public &#64;interface IdCardNumberSensitive {
 *
 *   Class &lt;? extends AbstractSensitiveHandler&lt;IdCardNumberSensitive, ?&gt;&gt; handler() default IdCardNumberSensitiveHandler.class;
 *
 * }
 * </pre>
 *
 * @author zyc
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Sensitive {
}
