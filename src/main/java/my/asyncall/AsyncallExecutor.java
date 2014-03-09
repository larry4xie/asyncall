package my.asyncall;

import my.asyncall.concurrent.AsyncallThreadFactory;
import my.asyncall.concurrent.AsyncallThreadPoolExecutor;

import java.util.concurrent.*;

/**
 * 异步调用工具
 *
 * @author xiegang
 * @since 14-3-7
 */
public class AsyncallExecutor {
    // 配置
    AsyncallConfig config;

    // 线程池执行器
    AsyncallThreadPoolExecutor executor;

    private volatile boolean isInit = false; // 是否已经初始化了

    public AsyncallExecutor() {}

    public AsyncallExecutor(AsyncallConfig config) {
        this.config = config;
    }

    /**
     * 初始化
     * @throws java.lang.IllegalArgumentException AsyncallExecutor config must be not null
     */
    public void init() {
        if (!isInit) {
            if (null == this.config) {
                throw new IllegalArgumentException("asyncall config must be not null");
            }
            BlockingQueue<Runnable> queue = getBlockingQueue(config);
            RejectedExecutionHandler handler = getRejectedExecutionHandler(config);
            executor = new AsyncallThreadPoolExecutor(config.getPoolSize(), config.getPoolSize(), 0l, TimeUnit.MILLISECONDS,
                                                        queue, new AsyncallThreadFactory(), handler);
            isInit = true;
        }
    }

    /**
     * 销毁
     */
    public void destory() {
        if (isInit && executor != null) {
            executor.shutdown();
            executor = null;

            isInit = false;
        }
    }

    /**
     * 创建异步调用执行单元
     * @throws java.lang.IllegalArgumentException asyncall must be init
     */
    public Asyncall createExecUnit() {
        if (!isInit) {
            throw new IllegalArgumentException("asyncall must be init...");
        }
        return new Asyncall(this);
    }

    /**
     * 根据配置生成阻塞队列
     */
    private BlockingQueue<Runnable> getBlockingQueue(AsyncallConfig config) {
        int acceptCount = config.getAcceptCount();
        if (acceptCount < 0) {
            // 不限制大小
            return new LinkedBlockingQueue<Runnable>();
        } else if (acceptCount == 0) {
            // 等于0时等价于队列1
            return new ArrayBlockingQueue<Runnable>(1);
        } else {
            return new ArrayBlockingQueue<Runnable>(acceptCount);
        }
    }

    /**
     * 根据配置生成RejectedExecutionHandler
     */
    private RejectedExecutionHandler getRejectedExecutionHandler(AsyncallConfig config) {
        AsyncallConfig.HandleMode mode = config.getMode();
        return AsyncallConfig.HandleMode.REJECT == mode ? new ThreadPoolExecutor.AbortPolicy() : new ThreadPoolExecutor.CallerRunsPolicy();
    }

    /** 创建一个默认实例 **/
    public static AsyncallExecutor createDefaultInstance() {
        return new AsyncallExecutor(new AsyncallConfig());
    }

    public AsyncallConfig getConfig() {
        return config;
    }

    public void setConfig(AsyncallConfig config) {
        this.config = config;
    }
}
