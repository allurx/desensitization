package red.zyc.desensitization.util.caller;

/**
 * @author zyc
 */
public interface Caller {

    /**
     * 当前类所在的执行堆栈深度
     */
    int CURRENT_DEPTH = 2;

    /**
     * 获取调用当前方法对象的{@code Class}
     *
     * @return 调用当前方法对象的{@code Class}
     */
    Class<?> getCaller();

    /**
     * 获取调用调用当前方法对象的{@code Class}
     *
     * @return 调用调用当前方法对象的{@code Class}
     */
    Class<?> getCallerCaller();
}
