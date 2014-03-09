package my.asyncall.concurrent;

import my.asyncall.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.concurrent.*;

/**
 * 扩展ThreadLocal线程共享实现
 *
 * @author xiegang
 * @since 14-3-7
 */
public class AsyncallThreadPoolExecutor extends ThreadPoolExecutor {
    // ==================== threadlocal ====================
    private static final Field threadLocalField  = ReflectionUtils.findField(Thread.class, "threadLocals");
    private static final Field inheritableThreadLocalField = ReflectionUtils.findField(Thread.class, "inheritableThreadLocals");
    static {
        // 强制的声明accessible
        ReflectionUtils.makeAccessible(threadLocalField);
        ReflectionUtils.makeAccessible(inheritableThreadLocalField);
    }

    public AsyncallThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public AsyncallThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public AsyncallThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public AsyncallThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    // =================== 扩展功能 ========================

    /**
     * 如果提交的是AsyncallFuture,则不需要重新new FutureTask
     */
    @Override
    public Future<?> submit(Runnable task) {
        if (null != task && task instanceof AsyncallFuture) {
            FutureTask futureTask = (FutureTask) task;
            execute(futureTask);
            return futureTask;
        }
        // super
        return super.submit(task);
    }

    /**
     * 如果当前Thread没有初始化ThreadLocal,InheritableThreadLocal，则帮助创建一个空的
     */
    @Override
    public void execute(Runnable command) {
        if (command instanceof AsyncallFuture) {
            AsyncallFuture future = (AsyncallFuture) command;
            // 线程ThreadLocal处理
            if (future.isThreadLocalSupport()) {
                Thread thread = Thread.currentThread();
                if ( null == ReflectionUtils.getField(threadLocalField, thread)) {
                    // 创建一个空的ThreadLocal,立马写回去
                    // 这时会在runner线程产生一空记录的ThreadLocalMap记录
                    new ThreadLocal<Boolean>();
                }
                if ( null == ReflectionUtils.getField(inheritableThreadLocalField, thread) ) {
                    // 创建一个空的ThreadLocal,立马写回去
                    // 可继承的ThreadLocal
                    new InheritableThreadLocal<Boolean>();
                }
            }
        }

        // execute
        super.execute(command);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable command) {
        if (command instanceof AsyncallFuture) {
            AsyncallFuture future = (AsyncallFuture) command;
            // 线程ThreadLocal处理
            if (future.isThreadLocalSupport()) {
                initThreadLocal(threadLocalField, future.getCallerThread(), t);
                initThreadLocal(inheritableThreadLocalField, future.getCallerThread(), t);
            }
        }

        super.beforeExecute(t, command);
    }

    @Override
    protected void afterExecute(Runnable command, Throwable t) {
        if (command instanceof AsyncallFuture) {
            AsyncallFuture future = (AsyncallFuture) command;
            // 线程ThreadLocal处理
            if (future.isThreadLocalSupport()) {
                recoverThreadLocal(threadLocalField, future.getCallerThread(), future.getRunnerThread());
                recoverThreadLocal(inheritableThreadLocalField, future.getCallerThread(), future.getRunnerThread());
            }
        }

        super.afterExecute(command, t);
    }

    /**
     * 复制caller的ThreadLocal信息到runner线程上
     *
     * @param field Thread中ThreadLocal的字段
     * @param caller 调用方
     * @param runner 执行方
     */
    private void initThreadLocal(Field field, Thread caller, Thread runner) {
        if (caller == null || runner == null) {
            return;
        }
        // 因为在提交Runnable时已经同步创建了一个ThreadLocalMap对象，所以runner线程只需要复制caller对应的引用即可，不需要进行合并，简化处理
        // threadlocal属性复制,注意是引用复制
        Object callerThreadLocalMap = ReflectionUtils.getField(field, caller);
        if (callerThreadLocalMap != null) {
            ReflectionUtils.setField(field, runner, callerThreadLocalMap);
        }
    }

    /**
     * 清理runner线程的ThreadLocal，为下一个task服务
     *
     * @param field Thread中ThreadLocal的字段
     * @param caller 调用方
     * @param runner 执行方
     */
    private void recoverThreadLocal(Field field, Thread caller, Thread runner) {
        if (runner == null) {
            return;
        }
        ReflectionUtils.setField(field, runner, null);
    }
}
