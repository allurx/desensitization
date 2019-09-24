package red.zyc.desensitization.handler;

import red.zyc.desensitization.annotation.EmailSensitive;

/**
 * 邮箱敏感信息处理者
 *
 * @author zyc
 */
public class EmailSensitiveHandler extends AbstractCharSequenceSensitiveHandler<EmailSensitive, CharSequence> {

    @Override
    public CharSequence handle(CharSequence target, EmailSensitive annotation) {
        return super.handleCharSequence(annotation.regexp(), annotation.startOffset(), annotation.endOffset(), target);
    }
}
