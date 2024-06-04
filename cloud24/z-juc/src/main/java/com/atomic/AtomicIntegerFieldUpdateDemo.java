package com.atomic;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * @author QRH
 * @date 2024/5/25 13:44
 * @description TODO
 */
public class AtomicIntegerFieldUpdateDemo {
    public static void main(String[] args) throws Exception {
        int size = 10;
        BankAccount bankAccount = new BankAccount();
        CountDownLatch countDownLatch = new CountDownLatch(size);

        for (int i = 1; i <= size; i++) {
            new Thread(() -> {
                try {
                    for (int j = 1; j <= 1000; j++) {
                        bankAccount.transMoney(bankAccount);
                    }
                } finally {
                    countDownLatch.countDown();
                }
            }, String.valueOf(i)).start();
        }
        countDownLatch.await();
        System.out.println(Thread.currentThread().getName() + "\t res= " + bankAccount.money);
    }
}

class BankAccount {
    String bankName = "CCB";
    public volatile int money = 0;

    AtomicIntegerFieldUpdater<BankAccount> moneyUpdater = AtomicIntegerFieldUpdater.newUpdater(BankAccount.class, "money");

    public void transMoney(BankAccount bankAccount) {
        moneyUpdater.getAndIncrement(bankAccount);
    }
}
