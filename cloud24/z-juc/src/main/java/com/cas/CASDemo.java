package com.cas;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author QRH
 * @date 2024/5/24 20:42
 * @description TODO
 */
public class CASDemo {
    public static void main(String[] args) {

        AtomicInteger atomicInteger = new AtomicInteger(5);
        System.out.println(atomicInteger.compareAndSet(5, 2022)+"\t "+atomicInteger.get());
        System.out.println(atomicInteger.compareAndSet(5, 2022)+"\t "+atomicInteger.get());
    }
}
