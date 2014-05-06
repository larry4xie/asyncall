Java轻量级异步并行调用工具
=========================
基于java.util.concurrent线程池，主要做了如下简化或处理

轻量级：没有通过使用代理降低侵入性，因为那样有很多因素开发人员无法知道，增加复杂性和未知因素

```java
// 三个任务A、B、C，其中C依赖A
// asyncall init
asyncallExecutor = AsyncallExecutor.createDefaultInstance();
asyncallExecutor.init();

// create execute unit
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
// hold 住等待执行完成
asyncall.await();
```

## 线程池初始化
AsyncallConfig
AsyncallExecutor#init
AsyncallExecutor#destory

## 异常处理
AsyncallExecutionHandler
默认处理：AsyncallExecutionHandler.DefaultPolic
```java
...
@Override
public void handlerExecutionException(ExecutionException e) {
    Throwable ex = e.getCause();
    if (ex instanceof RuntimeException) {
        throw (RuntimeException) ex;
    } else {
        throw new AsyncallExecutionException(e);
    }
}
...
```

## hold住异步线程等待执行完成
Asyncall#await();

## threadlocal线程拷贝
`谨慎使用`，因为这样会打破原先ThreadLocal的语义，导致出现线程安全问题
```java
public <V> AsyncallFuture<V> exec(Callable<V> callable, boolean threadLocalSupport)
public <V> AsyncallFuture<V> exec(Runnable runnable, boolean threadLocalSupport)
```

## 默认超时控制
AsyncallConfig#timeout

TODO
============
执行情况统计
