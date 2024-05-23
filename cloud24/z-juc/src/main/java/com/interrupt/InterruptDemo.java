package com.interrupt;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author QRH
 * @date 2024/5/20 17:03
 * @description TODO
 */
public class InterruptDemo {
    static volatile boolean isStop = false;
    static AtomicBoolean atomicBoolean = new AtomicBoolean(false);

    public static void main(String[] args) throws Exception {
//        m1();
//        m2();
        Thread t1 = new Thread(() -> {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println(Thread.currentThread().getName() + "\t thread被修改为true，程序停止");
                    break;
                }
                System.out.println("t1 ----hello interrupt");
            }
        }, "t1");
        t1.start();


        TimeUnit.MILLISECONDS.sleep(2);


        new Thread(() -> {
            t1.interrupt();
        },"t2").start();



    }

    private static void m2() throws InterruptedException {
        new Thread(() -> {
            while (true) {
                if (atomicBoolean.get()) {
                    System.out.println(Thread.currentThread().getName() + "\t atomicBoolean 被修改为true，程序停止");
                    break;
                }
                System.out.println("t1 ----hello atomicBoolean");
            }
        }, "t1").start();

        TimeUnit.MILLISECONDS.sleep(2);

        new Thread(() -> {
            atomicBoolean.set(true);
        }, "t2").start();
    }

    private static void m1() throws InterruptedException {
        new Thread(() -> {
            while (true) {
                if (isStop) {
                    System.out.println(Thread.currentThread().getName() + "\t isStop被修改为true，程序停止");
                    break;
                }
                System.out.println("t1 ----hello volatile");
            }
        }, "t1").start();

        TimeUnit.MILLISECONDS.sleep(2);

        new Thread(() -> {
            isStop = true;
        }, "t2").start();
    }
}
