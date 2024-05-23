package com.future;

import java.util.concurrent.*;

/**
 * @author QRH
 * @date 2024/5/18 18:32
 * @description TODO
 */
public class CompletableFutureUseDemo {

    public static void main(String[] args) throws Exception {

        ExecutorService threadPool = Executors.newFixedThreadPool(3);

        try {
            CompletableFuture.supplyAsync(()->{
                System.out.println(Thread.currentThread().getName());
                int res = ThreadLocalRandom.current().nextInt(10);
                try{TimeUnit.MILLISECONDS.sleep(1);}catch (Exception e){e.printStackTrace();}
                System.out.println("----1秒后出结果： "+res);
                return res>5 ? res/0 : res;
            },threadPool).whenComplete((v,e)->{
               if(e==null){
                   System.out.println("----计算完成，更新系统value= "+v);
               }
            }).exceptionally(e->{
                e.printStackTrace();
                System.out.println("异常情况： "+e.getCause()+"\t"+e.getMessage());
                return null;
            });

            System.out.println(Thread.currentThread().getName() + "现成先去忙其他任务");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }

        //主线程不要立刻结束，否则CompletableFuture默认使用的线程会立刻关闭，这里设置暂停3秒
        //try{TimeUnit.MILLISECONDS.sleep(5);}catch (Exception e){e.printStackTrace();}
    }
}
