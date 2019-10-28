package red.zyc.desensitization.desensitizer;

import red.zyc.desensitization.annotation.PasswordSensitive;
import red.zyc.desensitization.metadata.CharSequenceSensitiveDescriptor;

/**
 * 密码脱敏器
 *
 * @author zyc
 */
public class PasswordDesensitizer extends AbstractCharSequenceDesensitizer<CharSequence, PasswordSensitive> implements Desensitizer<CharSequence, PasswordSensitive> {

    @Override
    public CharSequence desensitize(CharSequence target, PasswordSensitive annotation) {
        return super.desensitizeCharSequence(CharSequenceSensitiveDescriptor.<CharSequence, PasswordSensitive>builder()
                .target(target)
                .annotation(annotation)
                .startOffset(annotation.startOffset())
                .endOffset(annotation.endOffset())
                .regexp(annotation.regexp())
                .placeholder(annotation.placeholder())
                .build());
    }
}
