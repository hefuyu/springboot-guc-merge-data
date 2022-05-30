package com.hefy.gucboot.util.window;

import com.hefy.gucboot.util.TimeUtil;
import com.hefy.gucboot.util.window.WindowWrap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Create by hefy
 * @Date 2022/5/18 15:56
 */
public abstract class LeapArray<T> {

    protected int windowLengthInMs;
    protected int sampleCount;
    protected int intervalInMs;
    protected final AtomicReferenceArray<WindowWrap<T>> array;
    private final ReentrantLock updateLock = new ReentrantLock();

    public LeapArray(int sampleCount, int intervalInMs) {
        // 1000ms = 1s  count=5  lengthInMs = 200ms
        this.windowLengthInMs = intervalInMs / sampleCount;
        this.intervalInMs = intervalInMs;
        this.sampleCount = sampleCount;
        this.array = new AtomicReferenceArray(sampleCount);
    }

    public WindowWrap<T> currentWindow() {
        return this.currentWindow(TimeUtil.currentTimeMillis());
    }

    public abstract T newEmptyBucket(long var1);

    protected abstract WindowWrap<T> resetWindowTo(WindowWrap<T> var1, long var2);

    private int calculateTimeIdx(long timeMillis) {
        long timeId = timeMillis / (long)this.windowLengthInMs;
        return (int)(timeId % (long)this.array.length());
    }

    protected long calculateWindowStart(long timeMillis) {
        return timeMillis - timeMillis % (long)this.windowLengthInMs;
    }

    public WindowWrap<T> currentWindow(long timeMillis) {
        if (timeMillis < 0L) {
            return null;
        } else {
            int idx = this.calculateTimeIdx(timeMillis);
            long windowStart = this.calculateWindowStart(timeMillis);


            while(true) {
                    WindowWrap<T> old = this.array.get(idx);
                    WindowWrap<T> window;
                    if (old == null) {
                        window = new WindowWrap(this.windowLengthInMs, windowStart, this.newEmptyBucket(timeMillis));
                        if (this.array.compareAndSet(idx,  null, window)) {
                            return window;
                        }

                        Thread.yield();
                    } else {
                        if (windowStart == old.windowStart()) {
                            return old;
                        }

                        if (windowStart > old.windowStart()) {
                            if (this.updateLock.tryLock()) {
                                try {
                                    window = this.resetWindowTo(old, windowStart);
                                } finally {
                                    this.updateLock.unlock();
                                }

                                return window;
                            }

                            Thread.yield();
                        } else if (windowStart < old.windowStart()) {
                            return new WindowWrap(this.windowLengthInMs, windowStart, this.newEmptyBucket(timeMillis));
                        }
                    }
                }

        }
    }

    public WindowWrap<T> getPreviousWindow(long timeMillis) {
        if (timeMillis < 0L) {
            return null;
        } else {
            int idx = this.calculateTimeIdx(timeMillis - (long)this.windowLengthInMs);
            timeMillis -= this.windowLengthInMs;
            WindowWrap<T> wrap = this.array.get(idx);
            if (wrap != null && !this.isWindowDeprecated(wrap)) {
                return wrap.windowStart() + (long)this.windowLengthInMs < timeMillis ? null : wrap;
            } else {
                return null;
            }
        }
    }

    public WindowWrap<T> getPreviousWindow() {
        return this.getPreviousWindow(TimeUtil.currentTimeMillis());
    }

    public T getWindowValue(long timeMillis) {
        if (timeMillis < 0L) {
            return null;
        } else {
            int idx = this.calculateTimeIdx(timeMillis);
            WindowWrap<T> bucket = this.array.get(idx);
            return bucket != null && bucket.isTimeInWindow(timeMillis) ? bucket.value() : null;
        }
    }

    public boolean isWindowDeprecated(WindowWrap<T> windowWrap) {
        return this.isWindowDeprecated(TimeUtil.currentTimeMillis(), windowWrap);
    }

    public boolean isWindowDeprecated(long time, WindowWrap<T> windowWrap) {
        return time - windowWrap.windowStart() > (long)this.intervalInMs;
    }

    public List<WindowWrap<T>> list() {
        return this.list(TimeUtil.currentTimeMillis());
    }

    public List<WindowWrap<T>> list(long validTime) {
        int size = this.array.length();
        List<WindowWrap<T>> result = new ArrayList(size);

        for(int i = 0; i < size; ++i) {
            WindowWrap<T> windowWrap = (WindowWrap)this.array.get(i);
            if (windowWrap != null && !this.isWindowDeprecated(validTime, windowWrap)) {
                result.add(windowWrap);
            }
        }

        return result;
    }

    public List<WindowWrap<T>> listAll() {
        int size = this.array.length();
        List<WindowWrap<T>> result = new ArrayList(size);

        for(int i = 0; i < size; ++i) {
            WindowWrap<T> windowWrap = this.array.get(i);
            if (windowWrap != null) {
                result.add(windowWrap);
            }
        }

        return result;
    }

    public List<T> values() {
        return this.values(TimeUtil.currentTimeMillis());
    }

    public List<T> values(long timeMillis) {
        if (timeMillis < 0L) {
            return new ArrayList();
        } else {
            int size = this.array.length();
            List<T> result = new ArrayList(size);

            for(int i = 0; i < size; ++i) {
                WindowWrap<T> windowWrap = this.array.get(i);
                if (windowWrap != null && !this.isWindowDeprecated(timeMillis, windowWrap)) {
                    result.add(windowWrap.value());
                }
            }

            return result;
        }
    }



    public int getSampleCount() {
        return this.sampleCount;
    }

    public int getIntervalInMs() {
        return this.intervalInMs;
    }

    public double getIntervalInSecond() {
        return (double)this.intervalInMs / 1000.0D;
    }

    public long currentWaiting() {
        return 0L;
    }

    public void addWaiting(long time, int acquireCount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "LeapArray{" +
                "array=" + array +
                '}';
    }
}