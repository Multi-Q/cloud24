package com.atomic;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author QRH
 * @date 2024/5/24 21:50
 * @description TODO
 */
public class AtomicIntegerDemo {
    final static  int size=50;
    public static void main(String[] args) throws InterruptedException {
        MyNumber myNumber = new MyNumber();
        CountDownLatch countDownLatch = new CountDownLatch(size);
        for (int i = 1; i <= size; i++) {
            new Thread(() -> {
                try {
                    for (int j = 1; j <= 1000; j++) {
                        myNumber.addPlus();
                    }
                } finally {
                    countDownLatch.countDown();
                }
            }, String.valueOf(i)).start();
        }
        countDownLatch.await();

        System.out.println(Thread.currentThread().getName() + "\t res=" + myNumber.atomicInteger.get());
    }
}

class MyNumber {
    AtomicInteger atomicInteger = new AtomicInteger();

    public void addPlus() {
        atomicInteger.getAndIncrement();
    }
}
