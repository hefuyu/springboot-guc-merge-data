package com.hefy.gucboot.util.window;

/**
 * @Create by hefy
 * @Date 2022/5/18 16:10
 */
public class WindowWrap<T> {
    private final long windowLengthInMs;
    private long windowStart;
    private T value;

    public WindowWrap(long windowLengthInMs, long windowStart, T value) {
        this.windowLengthInMs = windowLengthInMs;
        this.windowStart = windowStart;
        this.value = value;
    }

    public long windowLength() {
        return this.windowLengthInMs;
    }

    public long windowStart() {
        return this.windowStart;
    }

    public T value() {
        return this.value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public WindowWrap<T> resetTo(long startTime) {
        this.windowStart = startTime;
        return this;
    }

    public boolean isTimeInWindow(long timeMillis) {
        return this.windowStart <= timeMillis && timeMillis < this.windowStart + this.windowLengthInMs;
    }

    public String toString() {
        return "WindowWrap{windowLengthInMs=" + this.windowLengthInMs + ", windowStart=" + this.windowStart + ", value=" + this.value + '}';
    }
}
