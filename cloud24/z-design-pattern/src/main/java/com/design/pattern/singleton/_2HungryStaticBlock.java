package com.design.pattern.singleton;

/**
 * @author QRH
 * @date 2024/5/6 15:09
 * @description 单例模式饿汉式（静态代码块）法
 *
 * 优点：写法简单，在类装载时就完成实例化，避免了线程同步的问题<br/>
 * 缺点：在类装载时就完成实例化，没有达到延迟加载(Lazy Load)的效果，如果这个实例自始至终没有使用，则会造成内存浪费<br/>
 *
 */
public class _2HungryStaticBlock {
    public static void main(String[] args) {
        Singleton2 instant1=Singleton2.getInstance();
        Singleton2 instance2 = Singleton2.getInstance();

        System.out.println(instant1== instance2); //true

        System.out.println(instant1.hashCode());//instant1和instant2的哈希值一样
        System.out.println(instance2.hashCode());//instant1和instant2的哈希值一样
    }
}

class Singleton2{

    //1、私有化无参构造器
    private Singleton2(){}

    //2、声明静态成员变量
    private static Singleton2 instant;

    //3、静态代码块内初始化instant
    static {
        instant=new Singleton2();
    }

    //4、提供静态方法供外部获取该实例对象
    public static Singleton2 getInstance(){
        return instant;
    }
}
