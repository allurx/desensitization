package com.zyc.desensitization.model;

import com.zyc.desensitization.annotation.IdCardNumberSensitive;
import com.zyc.desensitization.annotation.PhoneNumberSensitive;
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
