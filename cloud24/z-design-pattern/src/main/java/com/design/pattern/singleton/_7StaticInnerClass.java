package com.design.pattern.singleton;

/**
 * @author QRH
 * @date 2024/5/6 16:00
 * @description 单例模式-静态内部类
 * * 这种采用类装载机制来保证初始化实例时只有一个线程。
 * * 静态内部类方式在Singleton7类被加载时并不会立即实例化，而是需要实例化时，调用getInstant方法，才会状态SingletonInstance类，从完成Singleton的实例化。
 * * 类的静态属性只会在第一次加载类的时候，所以，jvm帮我们保证了线程的安全性，在类进行初始化时，别的线程是无法进入的。
 * * `线程安全`,`利用静态内部类实现延迟加载`，`效率高`。
 * * 结论：实际开发中，<span style="font-size:18px;font-weight:bolder;color:red;>推荐</span>这种方式
 */
public class _7StaticInnerClass {
    public static void main(String[] args) {

        Singleton7 instance1 = Singleton7.getInstance();
        Singleton7 instance2 = Singleton7.getInstance();

        System.out.println(instance1==instance2);//true

        System.out.println(instance1.hashCode());//instant1和instant2的哈希值一样
        System.out.println(instance2.hashCode());//instant1和instant2的哈希值一样

    }
}
class Singleton7{
    //1、私有化无参构造器
    private Singleton7(){}

    //2、声明静态成员变量，不过该变量要用volatile修饰
//    private static volatile Singleton7 instant;

    //3、创建静态内部类
    private static class SingletonInstance{
        private final static Singleton7 INSTANT=new Singleton7();
    }

    //4、提供静态的共有方法，放回SingletonInstance.INSTANT，该方法用synchronized修饰
    public static synchronized Singleton7 getInstance(){
        return SingletonInstance.INSTANT;
    }
}