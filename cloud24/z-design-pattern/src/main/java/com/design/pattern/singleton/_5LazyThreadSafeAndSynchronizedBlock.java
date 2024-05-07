package com.design.pattern.singleton;

/**
 * @author QRH
 * @date 2024/5/6 15:40
 * @description 懒汉式（线程安全，同步代码块）
 * 优点：解决了线程安全问题。<br/>
 * 缺点：效率太低，每个线程想获得实例的时候，执行getInstant方法都要进行同步。
 *      而其实这个方法执行一次实例化代码就够了，后面想获得这个实例，直接return就行了。方法同步效率太低。<br/>
 * 结论：实际开发中，不推荐使用这种方式
 */
public class _5LazyThreadSafeAndSynchronizedBlock {
    public static void main(String[] args) {

        Singleton5 instance1 = Singleton5.getInstance();
        Singleton5 instance2 = Singleton5.getInstance();

        System.out.println(instance1==instance2);//true

        System.out.println(instance1.hashCode());//instant1和instant2的哈希值一样
        System.out.println(instance2.hashCode());//instant1和instant2的哈希值一样
    }
}
class Singleton5{
    //1、私有化无参构造器
    private Singleton5(){}

    //2、声明静态成员变量
    private static Singleton5 instant;

    //3、提供外部获取实例的方法，这个方法内部使用同步锁
    public static Singleton5 getInstance(){
        if (instant==null){
            synchronized (Singleton5.class){
                instant=new Singleton5();
            }
        }
        return instant;
    }
}