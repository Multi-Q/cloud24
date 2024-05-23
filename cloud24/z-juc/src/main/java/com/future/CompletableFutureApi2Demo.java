package com.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author QRH
 * @date 2024/5/19 12:35
 * @description TODO
 */
public class CompletableFutureApi2Demo {
    public static void main(String[] args) {

        ExecutorService threadPool = Executors.newFixedThreadPool(3);

        /**
        try {
            CompletableFuture.supplyAsync(() -> {
                        try {  TimeUnit.MILLISECONDS.sleep(1); } catch (InterruptedException e) {  e.printStackTrace(); }
                        return 1;
                    }
                    , threadPool)
                    .thenApply(f -> {
                        System.out.println("2222");
                        return f + 2;
                    })
                    .thenApply(f -> {
                        System.out.println("3333");
                        return f + 2;
                    })
                    .whenComplete((v, e) -> {
                        if (e == null) {  System.out.println("---计算结果： " + v);  }
                    })
                    .exceptionally(e -> {
                        System.out.println("----计算异常： " + e.getMessage());
                        return null;
                    });
            System.out.println(Thread.currentThread().getName() + "----主线程先去忙其他任务");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
        **/

        try {
            CompletableFuture.supplyAsync(() -> {
                        try {  TimeUnit.MILLISECONDS.sleep(1); } catch (InterruptedException e) {  e.printStackTrace(); }
                        System.out.println("111");
                        return 1;
                    }
                    , threadPool)
                    .handle((f,e) -> {
                        int i = 10 / 0;
                        System.out.println("2222");
                        return f + 2;
                    })
                    .handle((f,e) -> {
                        System.out.println("3333");
                        return f + 2;
                    })
                    .whenComplete((v, e) -> {
                        if (e == null) {  System.out.println("---计算结果： " + v);  }
                    })
                    .exceptionally(e -> {
                        System.out.println("----计算异常： " + e.getMessage());
                        return null;
                    });
            System.out.println(Thread.currentThread().getName() + "----主线程先去忙其他任务");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }

    }
}
