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


import red.zyc.desensitization.desensitizer.Desensitizer;

import java.lang.annotation.*;

/**
 * 标记注解，表明当前注解是一个用来擦除敏感信息的注解。
 * <ul>
 *     <li>被该注解标注的注解必须定义一个名称为{@code desensitizer}的方法，并且方法返回的{@code Class}代表的对象必须直接实现<br>
 *     或者由其父类实现具有明确泛型参数的{@link Desensitizer}接口，用来表明将要使用的脱敏器。
 *     </li>
 *     <li>
 *         脱敏器必须拥有无参构造函数，在擦除敏感信息时会根据{@code desensitizer}方法返回的{@code Class}对象通过反射实例化这个脱敏器。
 *     </li>
 * </ul>
 * 下面是一个敏感信息注解的定义例子：
 * <pre>
 *
 * {@code @Target({ElementType.FIELD, ElementType.TYPE_USE, ElementType.TYPE, ElementType.PARAMETER})
 * @Retention(RetentionPolicy.RUNTIME)
 * @Documented
 * @Sensitive
 * public @interface CharSequenceSensitive {
 *
 *   Class <? extends Sensitive<?,CharSequenceSensitive>>; desensitizer() default CharSequenceDesensitizer.class;
 *
 *   int startOffset() default 0;
 *
 *   int endOffset() default 0;
 *
 *   String regexp() default "";
 *
 *   char placeholder() default '*';
 *
 * }
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
