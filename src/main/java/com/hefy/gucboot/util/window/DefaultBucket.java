package com.hefy.gucboot.util.window;

import java.util.concurrent.atomic.LongAdder;

/**
 * @Create by hefy
 * @Date 2022/5/19 17:33
 */
public class DefaultBucket  {

    private final LongAdder count;

    public DefaultBucket() {
        this.count = new LongAdder();
    }

    public void reset(){
        this.count.reset();
    }

    public long pass(){
        return count.sum();
    }

    public void addPass(long n){
        this.count.add(n);
    }

}
