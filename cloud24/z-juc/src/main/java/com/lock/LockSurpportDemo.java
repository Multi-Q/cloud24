package com.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author QRH
 * @date 2024/5/23 10:56
 * @description TODO
 */
public class LockSurpportDemo {

    public static void main(String[] args) throws Exception {
        //1、
//        syncAwaitNotify();


        //2、
//        conditionLock();


        lockSupport();

    }

    private static void lockSupport() throws InterruptedException {
        Thread t1=new Thread(()->{
//            try {
//                TimeUnit.MILLISECONDS.sleep(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            System.out.println(Thread.currentThread().getName() + "\t --come in");
            LockSupport.park();
            System.out.println(Thread.currentThread().getName() + "\t ---被唤醒");
        },"t1");
        t1.start();

        TimeUnit.MILLISECONDS.sleep(1);

        new Thread(()->{
            LockSupport.unpark(t1);
            System.out.println(Thread.currentThread().getName() + "\t ---发出通知");
        },"t2").start();
    }

    private static void conditionLock() {
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        new Thread(() -> {
//            try {
//                TimeUnit.MILLISECONDS.sleep(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + "\t ---come in");
                condition.await();
                System.out.println(Thread.currentThread().getName() + "\t ---被唤醒");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }

        }, "t1").start();

//        TimeUnit.MILLISECONDS.sleep(1);

        new Thread(() -> {
            lock.lock();
            try {
                condition.signal();
                System.out.println(Thread.currentThread().getName() + "\t ---发出通知");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }

        }, "t2").start();
    }

    private static void syncAwaitNotify() throws InterruptedException {
        Object o = new Object();
        new Thread(() -> {
//            try {
//                TimeUnit.MILLISECONDS.sleep(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            synchronized (o) {
                System.out.println(Thread.currentThread().getName() + "\t ---come in");
                try {
                    o.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + "\t ---被唤醒");
            }
        }, "t1").start();

        TimeUnit.MILLISECONDS.sleep(1);

        new Thread(() -> {
            synchronized (o) {
                o.notify();
                System.out.println(Thread.currentThread().getName() + "\t ---发出通知");
            }
        }, "t2").start();
    }
}
