package com.zyc.desensitization.Model;

import com.zyc.desensitization.annotation.IdCardNumberSensitive;
import com.zyc.desensitization.annotation.PhoneNumberSensitive;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zyc
 */
@Getter
@Setter
public class Parent {

    String name = "Parent";

    @PhoneNumberSensitive
    String phoneNumber = "12345678911";

    @IdCardNumberSensitive
    String idCardNumber = "321181199301096990";
}
