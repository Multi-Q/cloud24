package com.atomic;

import java.sql.Time;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * @author QRH
 * @date 2024/5/25 13:56
 * @description TODO
 */
public class AtomicReferenceFieldDemo {
    public static void main(String[] args) {
        MyVar myVar = new MyVar();

        for (int i = 1; i <= 5; i++) {
            new Thread(() -> {
                myVar.init(myVar);
            }, String.valueOf(i)).start();
        }

    }
}

class MyVar {
    public volatile Boolean isInit = false;

    AtomicReferenceFieldUpdater<MyVar, Boolean> updater = AtomicReferenceFieldUpdater.newUpdater(MyVar.class, Boolean.class, "isInit");

    public void init(MyVar myVar) {
        if (updater.compareAndSet(myVar, Boolean.FALSE, Boolean.TRUE)) {
            System.out.println(Thread.currentThread().getName() + "\t ---start init need 2 seconds");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + "\t ---over init");
        } else {
            System.out.println(Thread.currentThread().getName() + "\t ---已有线程正在初始化工作");
        }
    }
}
