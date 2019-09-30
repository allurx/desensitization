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

    /**
     * 获取用指定堆栈深度所在的对象的{@code Class}
     *
     * @param depth 调用堆栈深度
     * @return 调用指定堆栈深度所在的对象的{@code Class}
     */
    Class<?> getCaller(int depth);
}
