package com.fengdui.framework.concurrent;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

public class ThreadPool {

    public void runAsync() {
        CompletableFuture.runAsync(() -> System.out.println("hello"));
    }

    /**
     * guava ThreadFactoryBuilder
     */
    public void threadPool() {
        ExecutorService poolExecutor = new ThreadPoolExecutor(
                2, 2, 0L, TimeUnit.NANOSECONDS, new LinkedBlockingQueue<>(),
                new ThreadFactoryBuilder().setNameFormat("thread-pool").build());
    }
}
