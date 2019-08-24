package red.zyc.desensitization.model;

import red.zyc.desensitization.annotation.ChineseNameSensitive;
import red.zyc.desensitization.annotation.IdCardNumberSensitive;
import red.zyc.desensitization.annotation.PhoneNumberSensitive;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author zyc
 */
@Getter
@Setter
@ToString
public class Child extends Parent {

    @ChineseNameSensitive
    String name = "张三";

    @PhoneNumberSensitive(start = 2,end = 4)
    String phoneNumber = "12345678922";

    @IdCardNumberSensitive
    String idCardNumber = "321181199301096999";
}
