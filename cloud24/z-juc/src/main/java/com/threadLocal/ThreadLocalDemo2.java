package com.threadLocal;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author QRH
 * @date 2024/5/26 15:23
 * @description TODO
 */
public class ThreadLocalDemo2 {
    public static void main(String[] args) throws Exception {
        MyData myData = new MyData();
        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        try {
            for (int i = 0; i < 10; i++) {
                threadPool.submit(() -> {
                    try {
                        Integer beforeCount = myData.threadLocalField.get();
                        myData.add();
                        Integer afterCount = myData.threadLocalField.get();
                        System.out.println(Thread.currentThread().getName() + ": " + beforeCount + "->" + afterCount);
                    } finally {
                        myData.threadLocalField.remove();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
    }
}

class MyData {

    Integer count = 0;

    ThreadLocal<Integer> threadLocalField = ThreadLocal.withInitial(() -> 0);

    public  void add() {
        threadLocalField.set(1+threadLocalField.get());
    }
}