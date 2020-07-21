package red.zyc.desensitization.desensitizer;

import red.zyc.desensitization.annotation.PasswordSensitive;

/**
 * 密码脱敏器
 *
 * @author zyc
 */
public class PasswordDesensitizer extends AbstractCharSequenceDesensitizer<String, PasswordSensitive> {

    @Override
    public String desensitize(String target, PasswordSensitive annotation) {
        if (required(target, annotation.condition())) {
            return String.valueOf(desensitize(target, annotation.regexp(), annotation.startOffset(), annotation.endOffset(), annotation.placeholder()));
        }
        return target;
    }

}
