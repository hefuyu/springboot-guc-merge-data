package com.hefy.gucboot.util;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.hefy.gucboot.entity.DataWrapper;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;

public class DataMerge<T, R> {

    private static final Log log = LogFactory.get();

    private final LongAdder threadNum = new LongAdder();

    private final BlockingQueue<MergeData<T, R>> blockingQueue;

    private final int millions;

    private final int maxThreadNum;

    private final Consumer<List<MergeData<T, R>>> consumer;

    private final int maxWaitMillionTime;

    private Thread thread;

    public DataMerge(int handleThreadIntervalTime, int maxThreadNum, int limitSize, int maxWaitMillionTime, Consumer<List<MergeData<T, R>>> consumer) {
        this.millions = handleThreadIntervalTime;
        this.consumer = consumer;
        this.blockingQueue = new LinkedBlockingQueue<>(limitSize);
        this.maxThreadNum = maxThreadNum;
        this.maxWaitMillionTime = maxWaitMillionTime;
        init();
    }

    public DataMerge(int handleThreadIntervalTime, int maxThreadNum, Consumer<List<MergeData<T, R>>> consumer) {
        this(handleThreadIntervalTime, maxThreadNum, 5000, 200, consumer);
    }

    public DataMerge(int handleThreadIntervalTime, Consumer<List<MergeData<T, R>>> consumer) {
        this(handleThreadIntervalTime, 3, consumer);
    }

    public void incrementThreadNum() {
        threadNum.increment();
    }

    public void decrementThreadNum() {
        threadNum.decrement();
    }

    private void init() {
        thread = new Thread(() -> {
            while (true) {
                if (blockingQueue.isEmpty()) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(millions);
                        continue;
                    } catch (InterruptedException e) {
                    }
                }
                List<DataMerge.MergeData<T, R>> currentMergeData = new ArrayList<>(blockingQueue.size());
                DataMerge.MergeData<T, R> data;
                while ((data = blockingQueue.poll()) != null) {
                    currentMergeData.add(data);
                }

                AsyncUtil.execute(() -> consumer.accept(currentMergeData));
            }
        });
        thread.setDaemon(true);
        thread.setName("merge-data-handle-thread");
        thread.start();
    }

    public ResponseData<R> putAndGet(T data) throws Exception {
        incrementThreadNum();
        try {
            long totalThreadNum = threadNum.sum();
            if (totalThreadNum < maxThreadNum) {
                log.debug("当前线程数:" + totalThreadNum);
                MergeData<T, R> mergeData = new MergeData(data);
                consumer.accept(Collections.singletonList(mergeData));
                return mergeData.getRes();
            }
            MergeData<T, R> mergeData = new MergeData(data, true);
            boolean putSuccess = blockingQueue.offer(mergeData, maxWaitMillionTime, TimeUnit.MILLISECONDS);
            if (putSuccess) {
                try {
                    return mergeData.hold(maxWaitMillionTime);
                } catch (TimeoutException e) {
                    return ResponseData.fail(null, "接口等待超时");
                }
            }
            return ResponseData.fail(null, "系统繁忙");
        } finally {
            decrementThreadNum();
        }
    }

    public static class ResponseData<R> {

        private boolean success ;

        private final String msg;

        private final R res;

        public String getMsg() {
            return msg;
        }

        public R getRes() {
            return res;
        }

        public ResponseData setSuccess(boolean success) {
            this.success = success;
            return this;
        }

        public boolean isSuccess() {
            return success;
        }

        private ResponseData(R res, boolean isSuccess, String msg) {
            this.res = res;
            this.msg = msg;
            this.success = isSuccess;
        }

        public static <R> ResponseData ok(R r, String msg) {
            return new ResponseData<>(r, true, msg);
        }

        public static <R> ResponseData fail(R r, String msg) {
            return new ResponseData<>(r, false, msg);
        }

    }

    public static class MergeData<T, R> implements DataWrapper<T, CompletableFuture<ResponseData<R>>> {

        private final T data;

        private ResponseData<R> res;

        private final boolean isHold;

        private final CompletableFuture<ResponseData<R>> completableFuture;

        public MergeData(T data, boolean isHold) {
            this.data = data;
            this.completableFuture = new CompletableFuture<>();
            this.isHold = isHold;
        }

        public MergeData(T data) {
            this(data, false);
        }

        @Override
        public T getData() {
            return data;
        }

        @Override
        public CompletableFuture<ResponseData<R>> get() {
            return completableFuture;
        }

        public ResponseData<R> getRes() {
            return res;
        }

        public ResponseData<R> hold(long maxWaitMillionTime) throws Exception {
            return get().get(maxWaitMillionTime, TimeUnit.MILLISECONDS);
        }

        public void complete(ResponseData<R> res) {
            if (isHold)
                get().complete(res);
            else
                this.res = res;
        }

    }


}
