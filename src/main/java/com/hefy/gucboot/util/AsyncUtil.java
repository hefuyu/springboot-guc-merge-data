package com.hefy.gucboot.util;

import java.util.concurrent.*;

public class AsyncUtil {

    private static ThreadPoolExecutor threadPoolExecutor;

    static {
        int coreSize = Runtime.getRuntime().availableProcessors() + 20;

        int maxSize = (int) (coreSize / (1-0.9));

        threadPoolExecutor = new ThreadPoolExecutor(coreSize,maxSize,3, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1024 * 1024),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());

    }


    public static void execute(Runnable task){
        threadPoolExecutor.execute(task);
    }

    public static <V> Future<V> submit(Callable<V> task){
        return threadPoolExecutor.submit(task);
    }

}
