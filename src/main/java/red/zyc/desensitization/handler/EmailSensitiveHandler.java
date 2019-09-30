package red.zyc.desensitization.handler;

import red.zyc.desensitization.annotation.EmailSensitive;
import red.zyc.desensitization.metadata.CharSequenceSensitiveDescriptor;

/**
 * 邮箱敏感信息处理者
 *
 * @author zyc
 */
public class EmailSensitiveHandler extends AbstractCharSequenceSensitiveHandler<CharSequence, EmailSensitive> implements SensitiveHandler<CharSequence, EmailSensitive> {

    @Override
    public CharSequence handle(CharSequence target, EmailSensitive annotation) {
        return super.handleCharSequence(CharSequenceSensitiveDescriptor.<CharSequence, EmailSensitive>builder()
                .target(target)
                .annotation(annotation)
                .startOffset(annotation.startOffset())
                .endOffset(annotation.endOffset())
                .regexp(annotation.regexp())
                .placeholder(annotation.placeholder())
                .build());
    }
}
