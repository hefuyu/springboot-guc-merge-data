package com.hefy.gucboot.util.window;



import java.util.concurrent.atomic.LongAdder;

public class SphU {

    private transient volatile Metric rollingCounterInSecond;
    private LongAdder curThreadNum;
    private long maxCount;

    public SphU(int sampleCount, int intervalInMs, long maxCount) {
        this.rollingCounterInSecond = new ArrayMetric(sampleCount, intervalInMs);
        this.curThreadNum = new LongAdder();
        this.maxCount = maxCount;
    }

    public double passQps() {
        return (double) this.rollingCounterInSecond.pass() / this.rollingCounterInSecond.getWindowIntervalInSec();
    }

    public void addPass() {
        this.rollingCounterInSecond.addPass(1);
    }

    public void incrementThreadNum() {
        this.curThreadNum.increment();
    }

    public void decrementThreadNum() {
        this.curThreadNum.decrement();
    }

    public void canPass() {
        double v = passQps();
        if (v + 1 > maxCount) {
            throw new RuntimeException(toString());
        }
    }

    public void entry() throws Exception {
        canPass();
        addPass();
        incrementThreadNum();
        decrementThreadNum();
    }

    @Override
    public String toString() {
        return Thread.currentThread().getName() + "{" +
                "rollingCounterInSecond=" + rollingCounterInSecond +
                ", curThreadNum=" + curThreadNum.sum() +
                '}';
    }
}
