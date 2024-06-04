package com.atomic;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicMarkableReference;

/**
 * @author QRH
 * @date 2024/5/25 13:28
 * @description TODO
 */
public class AtomicMarkableReferenceDemo {
    static AtomicMarkableReference atomicMarkableReference = new AtomicMarkableReference(100, false);

    public static void main(String[] args) {
    new Thread(()->{
        boolean marked = atomicMarkableReference.isMarked();
        System.out.println(Thread.currentThread().getName()+"\t 默认标识："+marked);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        atomicMarkableReference.compareAndSet(100,1000,marked,!marked);
    },"t1").start();


    new Thread(()->{
        boolean marked = atomicMarkableReference.isMarked();
        System.out.println(Thread.currentThread().getName()+"\t 默认标识："+marked);
        try {
            TimeUnit.SECONDS.sleep(2);
        }  catch (InterruptedException e) {
            e.printStackTrace();
        }
        boolean b = atomicMarkableReference.compareAndSet(100, 2000, marked, !marked);
        System.out.println(Thread.currentThread().getName()+"\t 修改成功否："+b);
        System.out.println(Thread.currentThread().getName()+"\t  "+atomicMarkableReference.isMarked());
        System.out.println(Thread.currentThread().getName()+"\t  "+atomicMarkableReference.getReference());

    },"t2").start();

    }
}
