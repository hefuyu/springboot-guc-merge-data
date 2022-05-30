package com.hefy.gucboot.util.window;


import java.util.Iterator;
import java.util.List;

/**
 * @Create by hefy
 * @Date 2022/5/18 19:27
 */
public class ArrayMetric implements Metric {

    private final LeapArray<DefaultBucket> data;

    public ArrayMetric(int sampleCount, int intervalInMs) {
        this.data = new BucketLeapArray(sampleCount, intervalInMs);
    }

    @Override
    public long pass(){
        this.data.currentWindow();
        long pass = 0L;
        List<DefaultBucket> list = this.data.values();

        DefaultBucket window;
        for(Iterator<DefaultBucket> var4 = list.iterator(); var4.hasNext(); pass += window.pass()) {
            window = var4.next();
        }

        return pass;
    }

    @Override
    public void addPass(long n) {
        this.data.currentWindow().value().addPass(n);
    }

    @Override
    public double getWindowIntervalInSec() {
        return (double) this.data.intervalInMs / 1000.0D;
    }

    @Override
    public String toString() {
        return "ArrayMetric{" + "data=" + data + '}';
    }
}
