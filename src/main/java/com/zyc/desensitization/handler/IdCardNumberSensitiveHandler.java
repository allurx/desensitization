package com.zyc.desensitization.handler;


import com.zyc.desensitization.annotation.IdCardNumberSensitive;

/**
 * 身份证号码敏感信息处理者
 *
 * @author zyc
 */
public class IdCardNumberSensitiveHandler extends AbstractCharSequenceSensitiveHandler<IdCardNumberSensitive, CharSequence> {

    @Override
    public CharSequence handle(CharSequence target, IdCardNumberSensitive idCardNumberSensitive) {
        return super.handleCharSequence(idCardNumberSensitive.start(), idCardNumberSensitive.end(), target);
    }

}
