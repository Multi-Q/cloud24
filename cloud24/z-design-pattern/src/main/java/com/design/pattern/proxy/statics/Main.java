package com.design.pattern.proxy.statics;

import com.design.pattern.proxy.statics.TeacherDao;
import com.design.pattern.proxy.statics.TeacherDaoProxy;

/**
 * @author QRH
 * @date 2024/5/7 16:47
 * @description TODO
 */
public class Main {
    public static void main(String[] args) {
        //创建目标对象
        TeacherDao teacherDao = new TeacherDao();

        //创建代理对象，同时将目标对象交给代理对象
        TeacherDaoProxy proxy = new TeacherDaoProxy(teacherDao);

        //执行的是被代理对象的方法
        proxy.teach();
    }
}
