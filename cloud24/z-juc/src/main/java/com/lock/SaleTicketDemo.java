package com.lock;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author QRH
 * @date 2024/5/20 15:24
 * @description TODO
 */
public class SaleTicketDemo {
    public static void main(String[] args) {

        Ticket ticket = new Ticket();

        new Thread(()->{for(int i=0;i<55;i++)ticket.sale();},"a").start();

        new Thread(()->{for(int i=0;i<55;i++)ticket.sale();},"b").start();

        new Thread(()->{for(int i=0;i<55;i++)ticket.sale();},"c").start();
    }
}

class Ticket {
    private int number = 50;
    //无参就是非公平锁，有参是公平锁
    private ReentrantLock lock = new ReentrantLock(true);

    public void sale() {
        lock.lock();
        try {
            if (number > 0) {
                System.out.println(Thread.currentThread().getName() + "卖出第:  " + (number--) + "票，剩余" + number);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
