package com.design.pattern.singleton;

/**
 * @author QRH
 * @date 2024/5/6 16:14
 * @description 单例模式-枚举
 *
 * 借助jdk1.5中添加的枚举类来实现单例模式，不仅能避免多线程同步的问题，而且还能防止反序列化重新建新的对象
 *
 * 推荐使用
 */
public class _8enum {
    public static void main(String[] args) {
        Singleton8 instant1 = Singleton8.INSTANT;
        Singleton8 instant2 = Singleton8.INSTANT;

        System.out.println(instant1==instant2);

        System.out.println(instant1.hashCode());
        System.out.println(instant2.hashCode());

        instant1.sayOK();
    }
}

enum Singleton8 {
    INSTANT;

    public void sayOK() {
        System.out.println("ok~");
    }
}
