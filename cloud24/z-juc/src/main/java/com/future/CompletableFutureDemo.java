package com.future;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author QRH
 * @date 2024/5/18 16:46
 * @description TODO
 */
public class CompletableFutureDemo {
    public static void main(String[] args) {
        FutureTask<String> ft=new FutureTask<>(new MyCallable());

        Thread t1 = new Thread(ft, "t1");
        t1.start();

        try {
            System.out.println(ft.get());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

class MyRunnable implements Runnable{
    @Override
    public void run() {
        System.out.println("MyRunnable类实现Runnable类，重写的方法没有返回值");
    }
}

class MyCallable implements Callable<String> {
    @Override
    public String call() throws Exception {
        System.out.println("--------come in");
        return "MyCallable";
    }
}
