package com.design.pattern.factory.type;

import com.design.pattern.factory.Pizza;

/**
 * @author QRH
 * @date 2024/5/6 18:42
 * @description TODO
 */
public class BJPepperPizza extends Pizza {
    @Override
    public void prepare() {
        System.out.println("准备制作pepper披萨");
    }

    @Override
    public void bake() {
        System.out.println("烘焙pepper披萨");
    }

    @Override
    public void cut() {
        System.out.println("裁剪pepper披萨");
    }

    @Override
    public void box() {
        System.out.println("给pepper披萨装箱");
    }
}
