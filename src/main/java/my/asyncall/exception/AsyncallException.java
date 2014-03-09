package my.asyncall.exception;

/**
 * 异步调用工具运行时异常
 * @author xiegang
 * @since 14-3-7
 */
public class AsyncallException extends RuntimeException {
    public AsyncallException() {
    }

    public AsyncallException(String message) {
        super(message);
    }

    public AsyncallException(String message, Throwable cause) {
        super(message, cause);
    }

    public AsyncallException(Throwable cause) {
        super(cause);
    }
}
