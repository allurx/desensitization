package red.zyc.desensitization.handler;

import red.zyc.desensitization.annotation.PasswordSensitive;
import red.zyc.desensitization.metadata.CharSequenceSensitiveDescriptor;

/**
 * 密码敏感信息处理者
 *
 * @author zyc
 */
public class PasswordSensitiveHandler extends AbstractCharSequenceSensitiveHandler<CharSequence, PasswordSensitive> implements SensitiveHandler<CharSequence, PasswordSensitive> {

    @Override
    public CharSequence handle(CharSequence target, PasswordSensitive annotation) {
        return super.handleCharSequence(CharSequenceSensitiveDescriptor.<CharSequence, PasswordSensitive>builder()
                .target(target)
                .annotation(annotation)
                .startOffset(annotation.startOffset())
                .endOffset(annotation.endOffset())
                .regexp(annotation.regexp())
                .placeholder(annotation.placeholder())
                .build());
    }
}
