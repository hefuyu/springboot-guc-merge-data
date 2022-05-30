package com.hefy.gucboot.util.window;

import com.hefy.gucboot.util.TimeUtil;

import java.util.concurrent.atomic.AtomicLong;

public class RateLimit {
    private final int maxQueueingTimeMs;
    private final double count;
    private final AtomicLong latestPassedTime = new AtomicLong(-1L);

    public RateLimit(int timeOut, double count) {
        this.maxQueueingTimeMs = timeOut;
        this.count = count;
    }

    public boolean canPass(int acquireCount) {
        return this.canPass(acquireCount, false);
    }

    public boolean canPass( int acquireCount, boolean prioritized) {
        if (acquireCount <= 0) {
            return true;
        } else if (this.count <= 0.0D) {
            return false;
        } else {
            long currentTime = TimeUtil.currentTimeMillis();
            long costTime = Math.round(1.0D * (double)acquireCount / this.count * 1000.0D);
            long expectedTime = costTime + this.latestPassedTime.get();
            if (expectedTime <= currentTime) {
                this.latestPassedTime.set(currentTime);
                return true;
            } else {
                long waitTime = costTime + this.latestPassedTime.get() - TimeUtil.currentTimeMillis();
                if (waitTime > (long)this.maxQueueingTimeMs) {
                    return false;
                } else {
                    long oldTime = this.latestPassedTime.addAndGet(costTime);

                    try {
                        waitTime = oldTime - TimeUtil.currentTimeMillis();
                        if (waitTime > (long)this.maxQueueingTimeMs) {
                            this.latestPassedTime.addAndGet(-costTime);
                            return false;
                        } else {
                            if (waitTime > 0L) {
                                Thread.sleep(waitTime);
                            }

                            return true;
                        }
                    } catch (InterruptedException var15) {
                        return false;
                    }
                }
            }
        }
    }
}
