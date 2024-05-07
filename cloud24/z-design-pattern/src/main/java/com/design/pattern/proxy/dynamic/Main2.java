package com.design.pattern.proxy.dynamic;

/**
 * @author QRH
 * @date 2024/5/7 17:22
 * @description TODO
 */
public class Main2 {
    public static void main(String[] args) {
        //声明目标对象
        ITeacherDao2 teacherDao2 = new TeacherDao2();

        //把目标对象交给代理对象,获得代理实例
        ITeacherDao2 proxyInstance =(ITeacherDao2) new ProxyFactory(teacherDao2).getProxyInstance();

//        System.out.println(proxyInstance.getClass());

        proxyInstance.teach();

        proxyInstance.sayHello("zs");
    }
}
