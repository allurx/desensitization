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
import red.zyc.desensitization.handler.PhoneNumberSensitiveHandler;

import java.lang.annotation.*;

/**
 * 手机号敏感标记注解
 *
 * @author zyc
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Sensitive
public @interface PhoneNumberSensitive {

    /**
     * @return 处理被 {@link PhoneNumberSensitive}注解的字段处理器，注意手机号码字段类型可能为数字类型，
     * 所以此处的处理器支持的类型并没有作限制，可以自定义子类重写默认的处理逻辑
     */
    Class<? extends AbstractSensitiveHandler<PhoneNumberSensitive, ?>> handler() default PhoneNumberSensitiveHandler.class;

    /**
     * @return 敏感信息在原字符序列中的起始偏移
     */
    int startOffset() default 3;

    /**
     * @return 敏感信息在原字符序列中的结束偏移
     */
    int endOffset() default 4;

    /**
     * @return 正则表达式匹配的敏感信息，如果regexp不为{@code ""}的话则会
     * 忽略{@link PhoneNumberSensitive#startOffset()}和{@link PhoneNumberSensitive#endOffset()}的值
     */
    String regexp() default "";

    /**
     * @return 敏感信息替换后的占位符
     */
    char placeholder() default '*';
}
