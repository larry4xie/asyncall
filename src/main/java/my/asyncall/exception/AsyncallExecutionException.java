package my.asyncall.exception;

/**
 * 异步调用工具线程执行过程中发生的异常的运行时异常包装
 *
 * @author xiegang
 * @since 14-3-7
 */
public class AsyncallExecutionException extends AsyncallException {
    public AsyncallExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public AsyncallExecutionException(Throwable cause) {
        super(cause);
    }
}
