package red.zyc.desensitization.desensitizer;

import red.zyc.desensitization.annotation.EmailSensitive;
import red.zyc.desensitization.support.InstanceCreators;

/**
 * 邮箱脱敏器
 *
 * @author zyc
 */
public class EmailDesensitizer extends AbstractCharSequenceDesensitizer<String, EmailSensitive> implements Desensitizer<String, EmailSensitive> {

    @Override
    public String desensitize(String target, EmailSensitive annotation) {
        @SuppressWarnings("unchecked")
        Condition<String> condition = (Condition<String>) InstanceCreators.getInstanceCreator(annotation.condition()).create();
        if (!condition.required(target)) {
            return target;
        }
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
