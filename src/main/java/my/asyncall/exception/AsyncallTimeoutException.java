package my.asyncall.exception;

import java.util.concurrent.TimeoutException;

/**
 * 异步调用工具线程执行过程中超时异常的运行时异常包装
 *
 * @author xiegang
 * @since 14-3-7
 */
public class AsyncallTimeoutException extends AsyncallException {
    public AsyncallTimeoutException(String message, TimeoutException e) {
        super(message, e);
    }

    public AsyncallTimeoutException(TimeoutException e) {
        super(e);
    }
}
