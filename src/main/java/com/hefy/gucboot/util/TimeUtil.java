package com.hefy.gucboot.util;

import java.util.concurrent.TimeUnit;

/**
 * @Create by hefy
 * @Date 2022/5/18 10:49
 */
public final class TimeUtil {

    private static volatile long currentTimeMillis = System.currentTimeMillis();

    public TimeUtil() {
    }

    public static long currentTimeMillis() {
        return currentTimeMillis;
    }

    static {
        Thread daemon = new Thread(new Runnable() {
            public void run() {
                while(true) {
                    TimeUtil.currentTimeMillis = System.currentTimeMillis();

                    try {
                        TimeUnit.MILLISECONDS.sleep(1L);
                    } catch (Throwable var2) {
                    }
                }
            }
        });
        daemon.setDaemon(true);
        daemon.setName("dc-time-tick-thread");
        daemon.start();
    }
}
