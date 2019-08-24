package red.zyc.desensitization.handler;

import red.zyc.desensitization.annotation.ChineseNameSensitive;

/**
 * @author zyc
 */
public class ChineseNameSensitiveHandler extends AbstractCharSequenceSensitiveHandler<ChineseNameSensitive, CharSequence> {

    @Override
    public CharSequence handle(CharSequence target, ChineseNameSensitive chineseNameSensitive) {
        return super.handleCharSequence(chineseNameSensitive.start(), chineseNameSensitive.end(), target);
    }
}
