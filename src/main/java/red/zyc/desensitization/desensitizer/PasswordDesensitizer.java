package red.zyc.desensitization.desensitizer;

import red.zyc.desensitization.annotation.PasswordSensitive;

/**
 * 密码脱敏器
 *
 * @author zyc
 */
public class PasswordDesensitizer extends AbstractCharSequenceDesensitizer<String, PasswordSensitive> implements Desensitizer<String, PasswordSensitive> {

    @Override
    public String desensitize(String target, PasswordSensitive annotation) {
        CharSequenceSensitiveDescriptor<String, PasswordSensitive> erased = desensitize(CharSequenceSensitiveDescriptor.<String, PasswordSensitive>builder()
                .target(target)
                .chars(target.toCharArray())
                .annotation(annotation)
                .startOffset(annotation.startOffset())
                .endOffset(annotation.endOffset())
                .regexp(annotation.regexp())
                .placeholder(annotation.placeholder())
                .build());
        return String.valueOf(erased.getChars());
    }
}
