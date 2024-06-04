package com.lock;

/**
 * @author QRH
 * @date 2024/5/20 16:27
 * @description TODO
 */
public class ReentrantLockDemo {
    public static void main(String[] args) {

        new ReentrantLockDemo().m1();
    }


    public synchronized void m1() {
        System.out.println(Thread.currentThread().getName() + " ----come in");
        m2();
        System.out.println(Thread.currentThread().getName() + " ----end m1");
    }

    public synchronized void m2() {
        System.out.println(Thread.currentThread().getName() + " ----come in");
        m3();
    }

    public synchronized void m3() {
        System.out.println(Thread.currentThread().getName() + " ----come in");
    }


    private static void reentrantLock1() {
        Object o = new Object();

        new Thread(() -> {
            synchronized (o) {
                System.out.println(Thread.currentThread().getName() + " ----外层调用");
                synchronized (o) {
                    System.out.println(Thread.currentThread().getName() + " ----中层调用");
                    synchronized (o) {
                        System.out.println(Thread.currentThread().getName() + " ----内层调用");
                    }
                }
            }
        }, "t1").start();
    }
}
