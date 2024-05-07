package com.design.pattern.proxy.dynamic;

/**
 * @author QRH
 * @date 2024/5/7 17:12
 * @description TODO
 */
public class TeacherDao2 implements ITeacherDao2{
    @Override
    public void teach() {
        System.out.println("------老师上课中。。。。");
    }

    @Override
    public void sayHello(String name) {
        System.out.println("hello， "+name);
    }
}
