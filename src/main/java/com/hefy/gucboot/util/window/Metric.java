package com.hefy.gucboot.util.window;

/**
 * @Create by hefy
 * @Date 2022/5/19 17:55
 */
public interface Metric {

    long pass();

    void addPass(long n);

    double getWindowIntervalInSec();

}
