package com.design.pattern.proxy.statics;

/**
 * @author QRH
 * @date 2024/5/7 16:49
 * @description 代理对象
 */
public class TeacherDaoProxy implements ITeacherDao {

    private ITeacherDao target; //目标对象，通过接口来聚合

    public TeacherDaoProxy(ITeacherDao iTeacherDao) {
        this.target = iTeacherDao;
    }

    @Override
    public void teach() {
        System.out.println("开始代理，完成某些操作。。。。。");
        this.target.teach();
        System.out.println("提交。。。。");
    }
}
