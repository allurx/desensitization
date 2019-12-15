package red.zyc.desensitization.desensitizer;

import red.zyc.desensitization.annotation.EmailSensitive;

/**
 * 邮箱脱敏器
 *
 * @author zyc
 */
public class EmailDesensitizer extends AbstractCharSequenceDesensitizer<String, EmailSensitive> implements Desensitizer<String, EmailSensitive> {

    @Override
    public String desensitize(String target, EmailSensitive annotation) {
        CharSequenceSensitiveDescriptor<String, EmailSensitive> erased = desensitize(CharSequenceSensitiveDescriptor.<String, EmailSensitive>builder()
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
