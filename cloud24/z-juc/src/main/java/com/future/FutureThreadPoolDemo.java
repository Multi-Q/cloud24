package com.future;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * @author QRH
 * @date 2024/5/18 16:56
 * @description TODO
 */
public class FutureThreadPoolDemo {

    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        long start = System.currentTimeMillis();

       FutureTask<String> futureTask1 = new FutureTask<String>(()->{
            try { TimeUnit.MILLISECONDS.sleep(500);  } catch (InterruptedException e) {  e.printStackTrace();}
            return "task1 over";
        });
       threadPool.submit(futureTask1);

        FutureTask<String> futureTask2 = new FutureTask<String>(()->{
            try { TimeUnit.MILLISECONDS.sleep(300);  } catch (InterruptedException e) {  e.printStackTrace();}
            return "task2 over";
        });
        threadPool.submit(futureTask2);

        try { TimeUnit.MILLISECONDS.sleep(300);  } catch (InterruptedException e) {  e.printStackTrace();}


        long end = System.currentTimeMillis();
        System.out.println("--costTime= "+(end-start)+" 毫秒");


        threadPool.shutdown();
    }
    public static void m1( ) {
        //3个任务，目前只有一个线程main来处理，请问耗时多少
        long start = System.currentTimeMillis();

        //暂停毫秒
        try { TimeUnit.MILLISECONDS.sleep(500);  } catch (InterruptedException e) {  e.printStackTrace();}
        try { TimeUnit.MILLISECONDS.sleep(500);  } catch (InterruptedException e) {  e.printStackTrace();}
        try { TimeUnit.MILLISECONDS.sleep(500);  } catch (InterruptedException e) {  e.printStackTrace();}

        long end = System.currentTimeMillis();
        System.out.println("--costTime= "+(end-start)+" 毫秒");
    }
}
