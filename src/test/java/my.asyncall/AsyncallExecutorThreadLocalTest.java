package my.asyncall;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class AsyncallExecutorThreadLocalTest {
    private static AsyncallExecutor asyncallExecutor;
    private static ThreadLocal<Integer> contextHolder;

    @BeforeClass
    public static void beforeClass () {
        asyncallExecutor = AsyncallExecutor.createDefaultInstance();
        asyncallExecutor.init();

        contextHolder = new ThreadLocal<Integer>();
    }

    @AfterClass
    public static void afterClass () {
        asyncallExecutor.destory();
    }

    @Test
    public void testThreadLocal() {
        Asyncall asyncall = asyncallExecutor.createExecUnit();
        contextHolder.set(1);

        asyncall.exec(new Runnable() {
            @Override
            public void run() {
                Assert.assertEquals(Integer.valueOf(1), contextHolder.get());
            }
        }, true);
        for(int i = 0; i < 101; i++) {
           asyncall.exec(new Runnable() {
               @Override
               public void run() {
                   Assert.assertNull(contextHolder.get());
               }
           });
        }

        asyncall.exec(new Runnable() {
            @Override
            public void run() {
                Assert.assertEquals(Integer.valueOf(1), contextHolder.get());
            }
        }, true);

        asyncall.await();
    }
}
