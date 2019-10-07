package red.zyc.desensitization.util.caller;

/**
 * @author zyc
 */
public interface Caller {

    /**
     * 获取调用当前方法对象的{@code Class}
     *
     * @return 调用当前方法对象的 {@code Class}
     */
    Class<?> getCaller();

    /**
     * 获取调用指定堆栈深度所在的对象的{@code Class}。例如当前堆栈深度为1，2则是调用当前方法所在的堆栈深度。
     *
     * @param depth 调用堆栈深度
     * @return 调用指定堆栈深度所在的对象的 {@code Class}
     */
    Class<?> getCaller(int depth);

    /**
     * 打印执行堆栈
     */
    void printStackTrace();
}
