package red.zyc.desensitization.annotation;

import java.lang.annotation.*;


/**
 * 被注解的元素在运行期间需要擦除一些敏感信息
 * <ul>
 *     <li>
 *         标注在方法上代表对该方法的返回值进行敏感信息擦除。
 *     </li>
 *     <li>
 *         标注在对象域上代表级联擦除该域中的敏感信息
 *     </li>
 * </ul>
 *
 * @author zyc
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EraseSensitive {
}
