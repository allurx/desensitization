package red.zyc.desensitization.handler;

import red.zyc.desensitization.annotation.EmailSensitive;

/**
 * @author zyc
 */
public class EmailSensitiveHandler extends AbstractCharSequenceSensitiveHandler<EmailSensitive, CharSequence> {

    @Override
    public CharSequence handle(CharSequence target, EmailSensitive emailSensitive) {
        return super.handleCharSequence(emailSensitive.regexp(), emailSensitive.startOffset(), emailSensitive.endOffset(), target);
    }
}
