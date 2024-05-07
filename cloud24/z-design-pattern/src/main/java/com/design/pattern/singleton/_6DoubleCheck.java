package com.design.pattern.singleton;

/**
 * @author QRH
 * @date 2024/5/6 15:46
 * @description 单例模式-双重检查
 * 优点：双重检查概念是多线程开发中常使用到的，如代码所示，使用了两次if(instant==null)检查，这样可以保证线程安全。
        这样代码只用执行一次，后面再访问时，判断if(instant==null)，直接return实例化对象，也避免了反复进行方法同步<br/>
 * 线程安全：延迟加载，效率较高。<br/>
 * 结论：实际开发中，推荐这种方式
 */
public class _6DoubleCheck {
    public static void main(String[] args) {
        Singleton6 instance1 = Singleton6.getInstance();
        Singleton6 instance2 = Singleton6.getInstance();

        System.out.println(instance1==instance2);//true

        System.out.println(instance1.hashCode());//instant1和instant2的哈希值一样
        System.out.println(instance2.hashCode());//instant1和instant2的哈希值一样
    }
}
class Singleton6{
    //1、私有化无参构造器
    private Singleton6(){}

    //2、声明静态成员变量
    private static Singleton6 instant;

    //提供静态共有方法，加入双重检查代码，解决线程安全问题，同时解决懒加载问题，以及同步效率问题
    public static Singleton6 getInstance(){
        if (instant==null){
            synchronized (Singleton6.class){
                if (instant==null){
                    instant=new Singleton6();
                }
            }
        }
        return instant;
    }
}