package com.design.pattern.singleton;

/**
 * @author QRH
 * @date 2024/5/6 15:20
 * @description 单例模式懒汉式（线程不安全）
 *
 * 优点：起到懒加载的效果，但是只能在单线程中使用。<br/>
 * 缺点：如果在多线程下，一个线程进入if(instant==null)判断语句块，还未来得及往下执行，另一个线程也通过了这个判断语句，这时便会产生多个实例。
 * 所以多线程环境下不可以使用这种方式<br/>
 *
 *实际开发中，不要使用这种方式
 */
public class _3LazyThreadUnsafe {
    public static void main(String[] args) {
        Singleton3 instance1 = Singleton3.getInstance();
        Singleton3 instance2 = Singleton3.getInstance();

        System.out.println(instance1==instance2);//true

        System.out.println(instance1.hashCode());//instant1和instant2的哈希值一样
        System.out.println(instance2.hashCode());//instant1和instant2的哈希值一样
    }
}
class Singleton3{
    //1、私有化无参构造器
    private Singleton3(){};

    //2、声明一个静态成员变量
    private static Singleton3 instant;

    //3、提供外部访问函数，当使用到该方法时才去实例化对象
    public static Singleton3 getInstance(){
        if (instant==null){
            instant=new Singleton3();
        }
        return instant;
    }
}
