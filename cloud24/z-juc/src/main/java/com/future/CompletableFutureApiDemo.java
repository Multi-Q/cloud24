package com.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author QRH
 * @date 2024/5/19 11:32
 * @description TODO
 */
public class CompletableFutureApiDemo {
    public static void main(String[] args) {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "abc";
        });

        /**
         //getNow("默认值")如果没计算完成，则返回给定的默认值，否则则返回计算结果
         System.out.println(completableFuture.getNow("xxx"));
         try { TimeUnit.MILLISECONDS.sleep(3); } catch (InterruptedException e) {  e.printStackTrace();  }
         System.out.println(completableFuture.getNow("xxx"));
         **/

        /**
         //complete("打断值"):如果打断计算，则把默认值给打断方法，否则不打断，返回计算结果

         //        try { TimeUnit.MILLISECONDS.sleep(1); } catch (InterruptedException e) {  e.printStackTrace();  }
         //        System.out.println(completableFuture.complete("打断值")+"\t"+completableFuture.join());

         try { TimeUnit.MILLISECONDS.sleep(2); } catch (InterruptedException e) {  e.printStackTrace();  }
         System.out.println(completableFuture.complete("打断值")+"\t"+completableFuture.join());

         **/




    }
}
