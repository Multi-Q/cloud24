package com.design.pattern.factory;

import com.design.pattern.factory.type.BJOrderPizza;

/**
 * @author QRH
 * @date 2024/5/6 18:49
 * @description TODO
 */
public class MainFactoryMethod {
    public static void main(String[] args) {
        OrderPizza orderPizza = new BJOrderPizza();
        orderPizza.createPizza("cheese");



    }
}
