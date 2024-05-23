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
public class CompletableFutureApi4Demo {
    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(5);

        /**
        //没有使用自定义线程池
        try {
            CompletableFuture<Void> cf = CompletableFuture.supplyAsync(() -> {
                try{  TimeUnit.MILLISECONDS.sleep(20);}catch (InterruptedException e){e.printStackTrace();}
                System.out.println("任务1 " + Thread.currentThread().getName());
                return 1;
            })
                    .thenRun(() -> {
                        try{  TimeUnit.MILLISECONDS.sleep(20);}catch (InterruptedException e){e.printStackTrace();}
                        System.out.println("任务2 " + Thread.currentThread().getName());
                    })
                    .thenRun(() -> {
                        try{  TimeUnit.MILLISECONDS.sleep(20);}catch (InterruptedException e){e.printStackTrace();}
                        System.out.println("任务3 " + Thread.currentThread().getName());
                    })
                    .thenRun(() -> {
                        try{  TimeUnit.MILLISECONDS.sleep(20);}catch (InterruptedException e){e.printStackTrace();}
                        System.out.println("任务4 " + Thread.currentThread().getName());
                    });

            System.out.println(cf.get(2L, TimeUnit.SECONDS));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
        **/

        /**
        //使用自定义线程池
        try {
            CompletableFuture<Void> cf = CompletableFuture.supplyAsync(() -> {
                try{  TimeUnit.MILLISECONDS.sleep(20);}catch (InterruptedException e){e.printStackTrace();}
                System.out.println("任务1 " + Thread.currentThread().getName());
                return 1;
            },threadPool)
                    .thenRun(() -> {
                        try{  TimeUnit.MILLISECONDS.sleep(20);}catch (InterruptedException e){e.printStackTrace();}
                        System.out.println("任务2 " + Thread.currentThread().getName());
                    })
                    .thenRun(() -> {
                        try{  TimeUnit.MILLISECONDS.sleep(20);}catch (InterruptedException e){e.printStackTrace();}
                        System.out.println("任务3 " + Thread.currentThread().getName());
                    })
                    .thenRun(() -> {
                        try{  TimeUnit.MILLISECONDS.sleep(20);}catch (InterruptedException e){e.printStackTrace();}
                        System.out.println("任务4 " + Thread.currentThread().getName());
                    });

            System.out.println(cf.get(2L, TimeUnit.SECONDS));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
        **/


        /**
        //第一个任务使用自定义线程池，第2个任务使用thenRunAsync()且不自定义线程池
        try {
            CompletableFuture<Void> cf = CompletableFuture.supplyAsync(() -> {
                try{  TimeUnit.MILLISECONDS.sleep(20);}catch (InterruptedException e){e.printStackTrace();}
                System.out.println("任务1 " + Thread.currentThread().getName());
                return 1;
            },threadPool)
                    .thenRunAsync(() -> {
                        try{  TimeUnit.MILLISECONDS.sleep(20);}catch (InterruptedException e){e.printStackTrace();}
                        System.out.println("任务2 " + Thread.currentThread().getName());
                    })
                    .thenRun(() -> {
                        try{  TimeUnit.MILLISECONDS.sleep(20);}catch (InterruptedException e){e.printStackTrace();}
                        System.out.println("任务3 " + Thread.currentThread().getName());
                    })
                    .thenRun(() -> {
                        try{  TimeUnit.MILLISECONDS.sleep(20);}catch (InterruptedException e){e.printStackTrace();}
                        System.out.println("任务4 " + Thread.currentThread().getName());
                    });

            System.out.println(cf.get(2L, TimeUnit.SECONDS));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
        **/


        /**
        //使用thenRunAsync（）并使用自定义线程池
        try {
            CompletableFuture<Void> cf = CompletableFuture.supplyAsync(() -> {
                try{  TimeUnit.MILLISECONDS.sleep(20);}catch (InterruptedException e){e.printStackTrace();}
                System.out.println("任务1 " + Thread.currentThread().getName());
                return 1;
            })
                    .thenRunAsync(() -> {
                        try{  TimeUnit.MILLISECONDS.sleep(20);}catch (InterruptedException e){e.printStackTrace();}
                        System.out.println("任务2 " + Thread.currentThread().getName());
                    },threadPool)
                    .thenRun(() -> {
                        try{  TimeUnit.MILLISECONDS.sleep(20);}catch (InterruptedException e){e.printStackTrace();}
                        System.out.println("任务3 " + Thread.currentThread().getName());
                    })
                    .thenRun(() -> {
                        try{  TimeUnit.MILLISECONDS.sleep(20);}catch (InterruptedException e){e.printStackTrace();}
                        System.out.println("任务4 " + Thread.currentThread().getName());
                    });

            System.out.println(cf.get(2L, TimeUnit.SECONDS));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
         **/

        try {
            CompletableFuture<Void> cf = CompletableFuture.supplyAsync(() -> {
                //try{  TimeUnit.MILLISECONDS.sleep(20);}catch (InterruptedException e){e.printStackTrace();}
                System.out.println("任务1 " + Thread.currentThread().getName());
                return 1;
            },threadPool)
                    .thenRun(() -> {
                        try{  TimeUnit.MILLISECONDS.sleep(1);}catch (InterruptedException e){e.printStackTrace();}
                        System.out.println("任务2 " + Thread.currentThread().getName());
                    })
                    .thenRun(() -> {
                        try{  TimeUnit.MILLISECONDS.sleep(1);}catch (InterruptedException e){e.printStackTrace();}
                        System.out.println("任务3 " + Thread.currentThread().getName());
                    })
                    .thenRun(() -> {
                        try{  TimeUnit.MILLISECONDS.sleep(1);}catch (InterruptedException e){e.printStackTrace();}
                        System.out.println("任务4 " + Thread.currentThread().getName());
                    });

            System.out.println(cf.get(2L, TimeUnit.SECONDS));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
    }



}
