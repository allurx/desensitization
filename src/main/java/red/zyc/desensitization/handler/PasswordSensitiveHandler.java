package red.zyc.desensitization.handler;

import red.zyc.desensitization.annotation.PasswordSensitive;

/**
 * 密码敏感信息处理者
 *
 * @author zyc
 */
public class PasswordSensitiveHandler extends AbstractCharSequenceSensitiveHandler<PasswordSensitive, CharSequence> {

    @Override
    public CharSequence handle(CharSequence target, PasswordSensitive annotation) {
        return super.handleCharSequence(annotation.regexp(), annotation.startOffset(), annotation.endOffset(), target);
    }
}
