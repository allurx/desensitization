package com.zyc.desensitization.handler;


import com.zyc.desensitization.annotation.IdCardNumberSensitive;

/**
 * 身份证号码敏感信息处理者
 * 注意该类在擦除敏感信息时不会校验目标对象的合法性，请确保目标对象是合法的身份证号码，
 * 否则会抛出任何有可能的 {@link RuntimeException}
 *
 * @author zyc
 */
public class IdCardNumberSensitiveHandler extends AbstractCharSequenceSensitiveHandler<IdCardNumberSensitive, CharSequence> {

    @Override
    public CharSequence handle(CharSequence target, IdCardNumberSensitive idCardNumberSensitive) {
        return super.handleCharSequence(idCardNumberSensitive.start(), idCardNumberSensitive.end(), target);
    }

}
