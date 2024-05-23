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
public class CompletableFutureApi3Demo {
    public static void main(String[] args) {

        /**

         CompletableFuture.supplyAsync(()->{
         return 1;
         })
         .thenAccept(r->{
         System.out.println(r);
         });

         System.out.println(Thread.currentThread().getName()+"--------main线程忙其他业务");
         **/


        CompletableFuture.supplyAsync(() -> "Hello Accept").thenRun(() -> {
        }).join();

        CompletableFuture.supplyAsync(() -> "Hello A2").thenAccept(System.out::println).join();

        System.out.println(CompletableFuture.supplyAsync(() -> "Hello A3").thenApply(r -> r + "  B").join());

    }
}
