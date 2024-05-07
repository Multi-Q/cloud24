package com.design.pattern.factory;

/**
 * @author QRH
 * @date 2024/5/6 18:33
 * @description TODO
 */
public abstract class OrderPizza {

    public abstract Pizza createPizza(String orderType);

    public OrderPizza() {
        Pizza pizza = null;
        String orderType; //订购披萨类型


        orderType = this.getType();
        pizza = createPizza(orderType); //抽象方法有工厂子类完成
        //输出制作过程
        pizza.prepare();
        pizza.bake();
        pizza.cut();
        pizza.box();


    }

    private String getType() {
        return "pepper";
    }

}
