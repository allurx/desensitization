package red.zyc.desensitization.annotation;

import red.zyc.desensitization.handler.AbstractSensitiveHandler;
import red.zyc.desensitization.handler.PasswordSensitiveHandler;

import java.lang.annotation.*;

/**
 * 密码敏感标记注解
 *
 * @author zyc
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Sensitive
public @interface PasswordSensitive {

    /**
     * @return 处理被 {@link PasswordSensitive}注解的字段处理器，可以自定义子类重写默认的处理逻辑
     */
    Class<? extends AbstractSensitiveHandler<PasswordSensitive, ?>> handler() default PasswordSensitiveHandler.class;

    /**
     * @return 敏感信息在原字符序列中的起始偏移
     */
    int startOffset() default 0;

    /**
     * @return 敏感信息在原字符序列中的结束偏移
     */
    int endOffset() default 0;

    /**
     * @return 正则表达式匹配的敏感信息，如果regexp不为{@code ""}的话则会
     * 忽略{@link PasswordSensitive#startOffset()}和{@link PasswordSensitive#endOffset()}的值
     */
    String regexp() default "";
}
