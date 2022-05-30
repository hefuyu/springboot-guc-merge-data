package com.hefy.gucboot.util.window;

/**
 * @Create by hefy
 * @Date 2022/5/19 17:52
 */
public class BucketLeapArray extends LeapArray<DefaultBucket>{

    public BucketLeapArray(int sampleCount, int intervalInMs) {
        super(sampleCount, intervalInMs);
    }

    @Override
    public DefaultBucket newEmptyBucket(long var1) {
        return new DefaultBucket();
    }

    @Override
    protected WindowWrap<DefaultBucket> resetWindowTo(WindowWrap<DefaultBucket> var1, long var2) {
        var1.resetTo(var2);
        var1.value().reset();
        return var1;
    }
}
