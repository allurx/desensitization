package red.zyc.desensitization.handler;

import java.lang.annotation.Annotation;

/**
 * 字符序列对象敏感信息处理者基类，为子类提供了一些快捷有用的方法处理{@link CharSequence}类型的敏感信息。
 *
 * @author zyc
 */
public abstract class AbstractCharSequenceSensitiveHandler<A extends Annotation, T extends CharSequence> extends AbstractSensitiveHandler<A, T> {

    /**
     * 如果处理器支持的类型是 {@link CharSequence}类型，
     * 可以调用这个快捷方法生成"*"字符串替换原字符序列中的敏感信息。
     *
     * @param start  敏感信息在原字符序列中的起始索引
     * @param end    敏感信息在原字符序列中的结束索引
     * @param target 目标字符序列
     * @return "*"字符串，用来替换敏感信息
     */
    protected CharSequence handleCharSequence(int start, int end, T target) {
        if (target == null) {
            return null;
        }
        if (end == 0) {
            end = target.length();
        }
        check(start, end, target);
        return target.subSequence(0, start) + secret(start, end) + target.subSequence(end, target.length());
    }

    /**
     * 生成"*"字符串
     *
     * @param start 敏感信息在原字符序列中的起始索引
     * @param end   敏感信息在原字符序列中的结束索引
     * @return "*"字符串
     */
    protected String secret(int start, int end) {
        StringBuilder secret = new StringBuilder();
        while (start < end) {
            secret.append("*");
            start++;
        }
        return secret.toString();
    }

    /**
     * @param start  敏感信息在原字符序列中的起始索引
     * @param end    敏感信息在原字符序列中的结束索引
     * @param target 原字符序列
     */
    protected void check(int start, int end, T target) {
        if (start < 0 ||
                end < 0 ||
                start > end ||
                start > target.length() ||
                end > target.length()) {
            throw new IllegalArgumentException("start:" + start + "," + "end:" + end);
        }
    }

}
