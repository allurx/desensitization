package red.zyc.desensitization.desensitizer;

import red.zyc.desensitization.annotation.EmailSensitive;
import red.zyc.desensitization.metadata.CharSequenceSensitiveDescriptor;

/**
 * 邮箱脱敏器
 *
 * @author zyc
 */
public class EmailDesensitizer extends AbstractCharSequenceDesensitizer<CharSequence, EmailSensitive> implements Desensitizer<CharSequence, EmailSensitive> {

    @Override
    public CharSequence desensitize(CharSequence target, EmailSensitive annotation) {
        return super.desensitizeCharSequence(CharSequenceSensitiveDescriptor.<CharSequence, EmailSensitive>builder()
                .target(target)
                .annotation(annotation)
                .startOffset(annotation.startOffset())
                .endOffset(annotation.endOffset())
                .regexp(annotation.regexp())
                .placeholder(annotation.placeholder())
                .build());
    }
}
