package com.atomic;

import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * @author QRH
 * @date 2024/5/24 22:02
 * @description TODO
 */
public class AtomicIntegerArrayDemo {
    public static void main(String[] args) {
        //a1();

        AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(new int[]{1, 2, 3, 4, 5});

        for (int i=0;i<atomicIntegerArray.length();i++){
            System.out.println(atomicIntegerArray.get(i));
        }

        System.out.println("-----------------");

        int tmpId = atomicIntegerArray.getAndSet(0, 1112);
        System.out.println(tmpId+"\t "+atomicIntegerArray.get(0));

        int increment = atomicIntegerArray.getAndIncrement(0);
        System.out.println(increment+"\t "+atomicIntegerArray.get(0));

    }

    private static void a1() {
        //        AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(new int[5]);

        AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(new int[]{1, 2, 3, 4, 5});

        for (int i=0;i<atomicIntegerArray.length();i++){
            System.out.println(atomicIntegerArray.get(i));
        }

        System.out.println();
    }
}
