package red.zyc.desensitization.desensitizer;

import red.zyc.desensitization.annotation.EmailSensitive;

/**
 * 邮箱脱敏器
 *
 * @author zyc
 */
public class EmailDesensitizer extends AbstractCharSequenceDesensitizer<String, EmailSensitive> {

    @Override
    public String desensitize(String target, EmailSensitive annotation) {
        return required(target, annotation.condition()) ? String.valueOf(desensitize(target, annotation.regexp(), annotation.startOffset(), annotation.endOffset(), annotation.placeholder())) : target;
    }

}
