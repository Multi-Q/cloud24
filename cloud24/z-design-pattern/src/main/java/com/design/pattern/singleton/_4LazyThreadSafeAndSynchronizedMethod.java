package com.design.pattern.singleton;

/**
 * @author QRH
 * @date 2024/5/6 15:31
 * @description 懒汉式（线程安全，同步方法）
 * 优点：解决了线程安全问题。<br/>
 * 缺点：效率太低，每个线程想获得实例的时候，执行getInstant方法都要进行同步。
 *      而其实这个方法执行一次实例化代码就够了，后面想获得这个实例，直接return就行了。方法同步效率太低。<br/>
 * 结论：实际开发中，不推荐使用这种方式
 */
public class _4LazyThreadSafeAndSynchronizedMethod {
    public static void main(String[] args) {
        Singleton4 instance1 = Singleton4.getInstance();
        Singleton4 instance2 = Singleton4.getInstance();

        System.out.println(instance1==instance2);//true

        System.out.println(instance1.hashCode());//instant1和instant2的哈希值一样
        System.out.println(instance2.hashCode());//instant1和instant2的哈希值一样

    }
}

class Singleton4{

    //1、私有化无参构造器
    private Singleton4(){}

    //2、声明静态成员变量
    private static Singleton4 instant;

    //3、对外提供方法实例化对象，不过该方法要用synchronized修饰
    public static synchronized Singleton4 getInstance(){
        if(instant==null){
            instant=new Singleton4();
        }
        return instant;
    }
}
