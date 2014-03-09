package my.asyncall;

import my.asyncall.concurrent.AsyncallThreadPoolExecutor;
import my.asyncall.concurrent.AsyncallFuture;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * 异步调用执行单元，默认不支持ThreadLocal
 *
 * @author xiegang
 * @since 14-3-7
 */
public class Asyncall {
    private AsyncallExecutor asyncallExecutor;

    private AsyncallThreadPoolExecutor executor;

    private List<AsyncallFuture> fatures = new ArrayList<AsyncallFuture>();

    // protected
    Asyncall(AsyncallExecutor asyncallExecutor) {
        this.asyncallExecutor = asyncallExecutor;
        this.executor = asyncallExecutor.executor;
    }

    /**
     * 执行没有返回值的过程
     * @param <V> null
     */
    public <V> AsyncallFuture<V> exec(Runnable runnable) {
        return this.exec(runnable, false);
    }

    /**
     * 执行有返回值得函数
     * @param <V> 返回值类型
     */
    public <V> AsyncallFuture<V> exec(Callable<V> callable) {
        return this.exec(callable, false);
    }

    /**
     * 执行没有返回值的过程
     * @param threadLocalSupport 是否开启ThreadLocal共享
     * @param <V> null
     * @return
     */
    public <V> AsyncallFuture<V> exec(Runnable runnable, boolean threadLocalSupport) {
        AsyncallFuture<V> future = new AsyncallFuture<V>(runnable, asyncallExecutor.config);
        // threadLocalSupport
        if (future.isThreadLocalSupport() != threadLocalSupport) {
            future.setThreadLocalSupport(threadLocalSupport);
        }
        executor.submit(future);
        fatures.add(future);
        return future;
    }

    /**
     * 执行有返回值得函数
     * @param threadLocalSupport 是否开启ThreadLocal共享
     * @param <V> 返回值类型
     */
    public <V> AsyncallFuture<V> exec(Callable<V> callable, boolean threadLocalSupport) {
        AsyncallFuture<V> future = new AsyncallFuture<V>(callable, asyncallExecutor.config);
        // threadLocalSupport
        if (future.isThreadLocalSupport() != threadLocalSupport) {
            future.setThreadLocalSupport(threadLocalSupport);
        }
        executor.submit(future);
        fatures.add(future);
        return future;
    }

    /**
     * 等待所有调用执行完成
     */
    public void await() {
        for (AsyncallFuture<?> fature : fatures) {
            fature.get();
        }
    }
}
