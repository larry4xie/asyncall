package my.asyncall;

/**
 * 异步调用工具配置<br/>
 * 默认值：
 * <ul>
 *     <li>timeout：0l,即不限制</li>
 *     <li>poolSize: 20</li>
 *     <li>acceptCount: 100</li>
 *     <li>mode:CALLERRUN(使用调用线程执行)</li>
 * </ul>
 *
 * @author xiegang
 * @since 14-3-7
 */
public class AsyncallConfig {
    /** <=0 表示不做超时控制 */
    public static final long DEFAULT_TIMEOUT = 0l;
    public static final int DEFAULT_POOL_SIZE = 20;
    public static final int DEFAULT_ACCEPT_COUNT = 100;
    public static final HandleMode DEFAULT_MODE = HandleMode.CALLERRUN; // 默认使用调用线程执行
    public static final AsyncallExecutionHandler EXECUTION_HANDLER = new AsyncallExecutionHandler.DefaultPolicy();

    public static enum HandleMode {
        REJECT, CALLERRUN
    }

    volatile long timeout = DEFAULT_TIMEOUT; // ms

    private volatile int poolSize = DEFAULT_POOL_SIZE;

    /** 等待队列长度，避免无限制提交请求 */
    private volatile int acceptCount = DEFAULT_ACCEPT_COUNT;

    /** 线程池和阻塞队列都无法接受处理时的处理方式 */
    private HandleMode mode = DEFAULT_MODE;

    /** Asyncall异常处理 **/
    private AsyncallExecutionHandler executionHandler = EXECUTION_HANDLER;

    public AsyncallConfig() {}

    public AsyncallConfig(long timeout) {
        this.timeout = timeout;
    }

    public AsyncallConfig(int acceptCount, int poolSize) {
        this.acceptCount = acceptCount;
        this.poolSize = poolSize;
    }

    public AsyncallConfig(long timeout, int poolSize, int acceptCount) {
        this.timeout = timeout;
        this.poolSize = poolSize;
        this.acceptCount = acceptCount;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    public int getAcceptCount() {
        return acceptCount;
    }

    public void setAcceptCount(int acceptCount) {
        this.acceptCount = acceptCount;
    }

    public HandleMode getMode() {
        return mode;
    }

    public void setMode(HandleMode mode) {
        this.mode = mode;
    }

    public AsyncallExecutionHandler getExecutionHandler() {
        return executionHandler;
    }

    public void setExecutionHandler(AsyncallExecutionHandler executionHandler) {
        this.executionHandler = executionHandler;
    }
}
