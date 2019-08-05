package com.zyc.desensitization.handler;


import com.zyc.desensitization.annotation.PhoneNumberSensitive;

/**
 * 手机号码敏感信息处理者。
 * 注意该类在擦除敏感信息时不会校验目标对象的合法性，请确保目标是合法的身份证号码，
 * 否则会抛出任何有可能的 {@link RuntimeException}。
 *
 * @author zyc
 */
public class PhoneNumberSensitiveHandler extends AbstractCharSequenceSensitiveHandler<PhoneNumberSensitive, CharSequence> {

    @Override
    public CharSequence handle(CharSequence target, PhoneNumberSensitive phoneNumberSensitive) {
        return super.handleCharSequence(phoneNumberSensitive.start(), phoneNumberSensitive.end(), target);
    }
}
