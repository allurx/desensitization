package red.zyc.desensitization.annotation;

import red.zyc.desensitization.handler.AbstractSensitiveHandler;
import red.zyc.desensitization.handler.PhoneNumberSensitiveHandler;

import java.lang.annotation.*;

/**
 * 被该注解标注的字段表明是一个手机号类型的敏感字段。
 *
 * @author zyc
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Sensitive
public @interface PhoneNumberSensitive {

    /**
     * @return 用来处理被 {@link IdCardNumberSensitive}注解的字段处理器，可以自定义子类重写默认的处理逻辑
     */
    Class<? extends AbstractSensitiveHandler<PhoneNumberSensitive, ?>> handler() default PhoneNumberSensitiveHandler.class;

    /**
     * start必须大于等于0。
     *
     * @return 敏感信息在原字符序列中的起始索引
     */
    int start() default 3;

    /**
     * end必须大于0，如果end等于0，代表擦除从start到字符序列尾部的所有信息。
     *
     * @return 敏感信息在原字符序列中的结束索引
     */
    int end() default 7;
}
