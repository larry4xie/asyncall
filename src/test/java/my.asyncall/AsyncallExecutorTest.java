package my.asyncall;

import my.asyncall.concurrent.AsyncallFuture;
import my.asyncall.domain.Dao;
import my.asyncall.domain.DaoImpl;
import my.asyncall.domain.Model;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.Callable;

/**
 * a
 * b
 * a > c
 * @author xieg
 * @since 14-3-9
 */
public class AsyncallExecutorTest {
    private static Dao dao;
    private static AsyncallExecutor asyncallExecutor;

    @BeforeClass
    public static void beforeClass () {
        dao = new DaoImpl();
        asyncallExecutor = AsyncallExecutor.createDefaultInstance();
        asyncallExecutor.init();
    }

    @AfterClass
    public static void afterClass () {
        asyncallExecutor.destory();
    }

    @Test
    public void testAsyncall() {
        long start = System.currentTimeMillis();
        Asyncall asyncall = asyncallExecutor.createExecUnit();

        final AsyncallFuture<Model> a = asyncall.exec(new Callable<Model>() {
            @Override
            public Model call() throws Exception {
                return dao.find(1000, "A");
            }
        });
        final AsyncallFuture<Model> b = asyncall.exec(new Callable<Model>() {
            @Override
            public Model call() throws Exception {
                return dao.find(1000, "b");
            }
        });
        final AsyncallFuture<Model> c = asyncall.exec(new Callable<Model>() {
            @Override
            public Model call() throws Exception {
                a.get();
                return dao.find(1000, "c");
            }
        });

        asyncall.await();

        long end = System.currentTimeMillis();

        Assert.assertTrue((end - start) >= 2000);
        Assert.assertTrue( (end - start) < 2500);
    }
}
