package my.asyncall.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 异步调用工具ThreadFactory
 * @author xieg
 * @since 14-3-9
 */
public class AsyncallThreadFactory implements ThreadFactory {
    private final static String DEFAULT_NAME = "asyncall-pool";
    private final String name;
    private final boolean daemon;
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1); // start 1

    public AsyncallThreadFactory(){
        this(DEFAULT_NAME, true);
    }

    public AsyncallThreadFactory(String name, boolean daemon){
        this.name = name;
        this.daemon = daemon;
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, name + "-" + threadNumber.getAndIncrement(), 0);
        t.setDaemon(daemon);
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }

}
