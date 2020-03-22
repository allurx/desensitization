package red.zyc.desensitization.desensitizer;

/**
 * 用来判断是否需要对目标对象进行脱敏处理的先决条件
 *
 * @param <T> 目标对象类型
 * @author zyc
 */
public interface Condition<T> {

    /**
     * 返回{@code true}代表需要对目标对象进行脱敏处理，{@code false}代表不作任何处理
     *
     * @param target 目标对象
     * @return 是否需要对目标对象进行脱敏处理
     */
    boolean required(T target);
}
