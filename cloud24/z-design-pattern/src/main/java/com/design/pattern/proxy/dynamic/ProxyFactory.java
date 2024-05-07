package com.design.pattern.proxy.dynamic;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 * @author QRH
 * @date 2024/5/7 17:13
 * @description TODO
 */
public class ProxyFactory {

    //目标对象
    private Object target;

    public ProxyFactory(Object target) {
        this.target = target;
    }

    /**
     * 给目标对象生成一个代理对象
     *
     * public static Object Proxy.newProxyInstance(ClassLoader loader,
     *                                              Class<?>[] interfaces,
     *                                              InvocationHandler h)
     *
     *   ClassLoader loader:指定当前目标对象使用的类加载器，获取加载器的方法固定
     *   Class<?>[] interfaces:目标对象实现的接口类型，使用泛型方法确认类型
     *   InvocationHandler h：事情处理，执行目标对象的方法时，会触发事情处理器方法，会把当前的目标对象方法作为参数传入
     * @return
     */
    public Object getProxyInstance() {
        return Proxy.newProxyInstance(target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        System.out.println("JDK代理开始~~");
                        //反射机制调用目标对象的方法
                        Object invoke = method.invoke(target, args);
                        System.out.println("JDK代理提交。。");
                        return invoke;
                    }
                }
        );
    }
}
