package my.asyncall.concurrent;

import my.asyncall.AsyncallConfig;
import my.asyncall.AsyncallExecutionHandler;
import my.asyncall.exception.AsyncallException;

import java.util.concurrent.*;

/**
 * 自定义FutureTask，主要增加：异常处理功能
 *
 * @author xiegang
 * @since 14-3-7
 */
public class AsyncallFuture<V> extends FutureTask<V> {
    private Thread callerThread;
    private Thread runnerThread;
    private long startTime = 0; // 记录下future开始执行的时间
    private long endTime = 0; // 记录下future执行结束时间

    private AsyncallConfig config;
    /** 是否支持ThreadLocal,谨慎开启 **/
    private boolean threadLocalSupport = false;

    public AsyncallFuture(Runnable runnable, AsyncallConfig config) {
        super(runnable, null);
        this.config = config;
        callerThread = Thread.currentThread();
    }

    public AsyncallFuture(Callable<V> callable, AsyncallConfig config) {
        super(callable);
        this.config = config;
        callerThread = Thread.currentThread();
    }

    @Override
    public void run() {
        startTime = System.currentTimeMillis();
        runnerThread = Thread.currentThread(); // 记录的下具体pool中的runnerThread，可能是caller自己
        super.run();
    }

    @Override
    protected void done() {
        endTime = System.currentTimeMillis();
        super.done();
    }

    /**
     * 增加异常处理，和全局超时处理
     */
    @Override
    public V get() {
        if (config.getTimeout() > 0) {
            return this.get(config.getTimeout(), TimeUnit.MILLISECONDS);
        }

        AsyncallExecutionHandler handler = config.getExecutionHandler();
        try {
            return super.get();
        } catch (InterruptedException e) {
            handler.handlerInterruptedException(e);
            throw new AsyncallException("no handler throw RumteimeException", e);
        } catch (ExecutionException e) {
            handler.handlerExecutionException(e);
            throw new AsyncallException("no handler throw RumteimeException", e);
        }
    }

    /**
     * 增加异常处理
     */
    @Override
    public V get(long timeout, TimeUnit unit) {
        AsyncallExecutionHandler handler = config.getExecutionHandler();

        try {
            return super.get(timeout, unit);
        } catch (InterruptedException e) {
            handler.handlerInterruptedException(e);
            throw new AsyncallException("no handler throw RumteimeException", e);
        } catch (TimeoutException e) {
            handler.handlerTimeoutException(e);
            throw new AsyncallException("no handler throw RumteimeException", e);
        } catch (ExecutionException e) {
            handler.handlerExecutionException(e);
            throw new AsyncallException("no handler throw RumteimeException", e);
        }
    }

    public Thread getCallerThread() {
        return callerThread;
    }

    public Thread getRunnerThread() {
        return runnerThread;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public AsyncallConfig getConfig() {
        return config;
    }

    public boolean isThreadLocalSupport() {
        return threadLocalSupport;
    }

    public void setThreadLocalSupport(boolean threadLocalSupport) {
        this.threadLocalSupport = threadLocalSupport;
    }
}
