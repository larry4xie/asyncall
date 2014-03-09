package my.asyncall;

import my.asyncall.domain.Dao;
import my.asyncall.domain.DaoImpl;
import my.asyncall.domain.Model;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.*;

/**
 * a
 * b
 * a > c
 * @author xiegang
 * @since 14-3-7
 */
public class ConcurrentTest {
    private static Dao dao;
    private static ExecutorService pool;

    @BeforeClass
    public static void beforeClass () {
        dao = new DaoImpl();
        pool = Executors.newFixedThreadPool(10);
    }

    @Test
    public void testCallable() throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();

        final Future<Model> a = pool.submit(new Callable<Model>() {
            @Override
            public Model call() throws Exception {
                return dao.find(1000, "A");
            }
        });
        final Future<Model> b = pool.submit(new Callable<Model>() {
            @Override
            public Model call() throws Exception {
                return dao.find(1000, "B");
            }
        });
        final Future<Model> c = pool.submit(new Callable<Model>() {
            @Override
            public Model call() throws Exception {
                a.get();
                return dao.find(1000, "C");
            }
        });

        // await
        a.get();
        b.get();
        c.get();

        long end = System.currentTimeMillis();

        Assert.assertTrue( (end - start) >= 2000);
        Assert.assertTrue( (end - start) < 2500);
    }

    @Test
    public void testDirect() {
        long start = System.currentTimeMillis();

        dao.find(1000, "A");
        dao.find(1000, "B");
        dao.find(1000, "C");

        long end = System.currentTimeMillis();

        Assert.assertTrue( (end - start) >= 3000);
    }
}
