package com.threadLocal;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * @author QRH
 * @date 2024/5/26 15:23
 * @description TODO
 */
public class ThreadLocalDemo {
    public static void main(String[] args) throws Exception {
        House house = new House();
        CountDownLatch countDownLatch = new CountDownLatch(5);
        for (int i = 1; i <= 5; i++) {
            new Thread(() -> {
                int size = new Random().nextInt(5) + 1;
                try {
                    for (int j = 1; j <= size; j++) {
                        house.saleHouse();
                        house.saleVolumeByThreadLocal();
                    }
                    System.out.println(Thread.currentThread().getName() + "\t号销售卖出了" + house.saleVolume.get() + "间房子");
                } finally {
                    house.saleVolume.remove();
                    countDownLatch.countDown();
                }
            }, String.valueOf(i)).start();
        }
        countDownLatch.await();
        System.out.println(Thread.currentThread().getName() + "\t 共卖出 " + house.saleCount);

    }
}

class House {
    int saleCount = 0;

    public synchronized void saleHouse() {
        ++saleCount;
    }

    ThreadLocal<Integer> saleVolume = ThreadLocal.withInitial(() -> 0);

    public void saleVolumeByThreadLocal() {
        saleVolume.set(1 + saleVolume.get());
    }

}