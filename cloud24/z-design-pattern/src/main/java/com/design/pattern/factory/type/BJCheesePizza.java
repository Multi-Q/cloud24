package com.design.pattern.factory.type;

import com.design.pattern.factory.Pizza;

/**
 * @author QRH
 * @date 2024/5/6 18:42
 * @description TODO
 */
public class BJCheesePizza extends Pizza {
    @Override
    public void prepare() {
        System.out.println("准备制作cheese披萨");
    }

    @Override
    public void bake() {
        System.out.println("烘焙cheese披萨");
    }

    @Override
    public void cut() {
        System.out.println("裁剪cheese披萨");
    }

    @Override
    public void box() {
        System.out.println("给cheese披萨装箱");
    }
}
