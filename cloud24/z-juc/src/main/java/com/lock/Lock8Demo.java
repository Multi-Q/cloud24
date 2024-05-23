package com.lock;

import java.util.concurrent.TimeUnit;

/**
 * @author QRH
 * @date 2024/5/20 14:42
 * @description 谈谈你对多线程锁的理解，8索案例说明
 * <p>
 * 1、标准访问有ab两个线程，请问先打印邮件还是短信 答：先打印邮件
 * <p>
 * 2、sendEmail方法中暂停3秒钟，请问先打印邮件还是短信 答：邮件
 * <p>
 * 3、添加一个普通的hello方法，请问先打印邮件还是短信
 */
public class Lock8Demo {
    public static void main(String[] args) {
        Phone phone = new Phone();
        Phone phone2 = new Phone();

        new Thread(() -> phone.sendEmail(), "a").start();

        try {
            TimeUnit.MILLISECONDS.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
//            phone.sendSMS()
//            phone.hello();
            phone2.sendSMS();
        }, "b").start();

    }
}

class Phone {

    //1、
//    public   synchronized void sendSMS() {
//        System.out.println("发短信");
//    }

    //2、
    public synchronized void sendSMS() {
        try {
            TimeUnit.MILLISECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("发短信");
    }

    public synchronized void sendEmail() {
        System.out.println("发邮件");
    }

    public void hello() {
        System.out.println("hello");
    }

}