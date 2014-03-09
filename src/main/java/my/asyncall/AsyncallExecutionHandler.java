package my.asyncall;

import my.asyncall.exception.AsyncallExecutionException;
import my.asyncall.exception.AsyncallInterruptedException;
import my.asyncall.exception.AsyncallTimeoutException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Asyncall异常处理
 * 处理程序必须抛出运行时异常
 * @author xieg
 * @since 14-3-9
 */
public interface AsyncallExecutionHandler {
    public void handlerInterruptedException(InterruptedException e);

    public void handlerTimeoutException(TimeoutException e);

    public void handlerExecutionException(ExecutionException e);

    /** 默认处理 **/
    public static class DefaultPolicy implements AsyncallExecutionHandler {

        @Override
        public void handlerInterruptedException(InterruptedException e) {
            throw new AsyncallInterruptedException(e);
        }

        @Override
        public void handlerTimeoutException(TimeoutException e) {
            throw new AsyncallTimeoutException(e);
        }

        @Override
        public void handlerExecutionException(ExecutionException e) {
            Throwable ex = e.getCause();
            if (ex instanceof RuntimeException) {
                throw (RuntimeException) ex;
            } else {
                throw new AsyncallExecutionException(e);
            }
        }
    }
}
