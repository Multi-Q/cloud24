package com.design.pattern.singleton;


import java.util.HashMap;
import java.util.Objects;

/**
 * @author QRH
 * @date 2024/5/6 14:40
 * @description 单列模式中的饿汉式（静态常量）法
 * <p>
 * 优点：写法简单，在类装载时就完成实例化，避免了线程同步的问题<br/>
 * 缺点：在类装载时就完成实例化，没有达到延迟加载(Lazy Load)的效果，如果这个实例自始至终没有使用，则会造成内存浪费<br/>
 */
public class _1HungryStaticInstant {
    public static void main(String[] args) {
        Singleton1 instance1 = Singleton1.getInstance();
        Singleton1 instance2 = Singleton1.getInstance();

        System.out.println(instance1 == instance2); //true

        System.out.println(instance1.hashCode());//instant1和instant2的哈希值一样
        System.out.println(instance2.hashCode());//instant1和instant2的哈希值一样


    }
}

class Singleton1 {

    //1、私有化无参构造器
    private Singleton1() {
    }

    //2、将该变量用final修饰，并初始化
    private final static Singleton1 instant = new Singleton1();

    //3、提供一个静态函数供外部获取该类的实例对象
    public static Singleton1 getInstance() {
        return instant;
    }



}