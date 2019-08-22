package red.zyc.desensitization.model;

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

    String name = "child";

    @PhoneNumberSensitive
    String phoneNumber = "12345678922";

    @IdCardNumberSensitive
    String idCardNumber = "321181199301096999";
}
