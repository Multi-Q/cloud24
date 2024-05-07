package com.design.pattern.proxy.statics;

/**
 * @author QRH
 * @date 2024/5/7 16:50
 * @description 被代理对象，即目标对象
 */
public class TeacherDao implements ITeacherDao{
    @Override
    public void teach() {
        System.out.println("================老师授课中。。。。。");
    }
}
