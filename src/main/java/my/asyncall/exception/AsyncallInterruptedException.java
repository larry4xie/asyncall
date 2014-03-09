package my.asyncall.exception;

/**
 * 异步调用工具线程执行过程中线程被打断异常的运行时异常包装
 *
 * @author xiegang
 * @since 14-3-7
 */
public class AsyncallInterruptedException extends AsyncallException {
    public AsyncallInterruptedException(String message, InterruptedException e) {
        super(message, e);
    }

    public AsyncallInterruptedException(InterruptedException e) {
        super(e);
    }
}
