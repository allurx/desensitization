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
import red.zyc.desensitization.handler.IdCardNumberSensitiveHandler;

import java.lang.annotation.*;

/**
 * 被该注解标注的字段表明是一个身份证类型的敏感字段。
 *
 * @author zyc
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Sensitive
public @interface IdCardNumberSensitive {

    /**
     * @return 处理被 {@link IdCardNumberSensitive}注解的字段处理器，可以自定义子类重写默认的处理逻辑。
     */
    Class<? extends AbstractSensitiveHandler<IdCardNumberSensitive, ?>> handler() default IdCardNumberSensitiveHandler.class;

    /**
     * @return 敏感信息在原字符序列中的起始偏移
     */
    int startOffset() default 6;

    /**
     * @return 敏感信息在原字符序列中的结束偏移
     */
    int endOffset() default 4;

    /**
     * @return 正则表达式匹配的敏感信息，如果regexp不为{@code ""}的话则会
     * 忽略{@link IdCardNumberSensitive#startOffset()}和{@link IdCardNumberSensitive#endOffset()}的值
     */
    String regexp() default "";
}
