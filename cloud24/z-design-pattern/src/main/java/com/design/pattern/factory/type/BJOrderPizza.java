package com.design.pattern.factory.type;

import com.design.pattern.factory.OrderPizza;
import com.design.pattern.factory.Pizza;

/**
 * @author QRH
 * @date 2024/5/6 18:40
 * @description TODO
 */
public class BJOrderPizza extends OrderPizza {

    @Override
    public Pizza createPizza(String orderType) {
        Pizza pizza =null;
        if (orderType.equals("cheese")){
            pizza=new BJCheesePizza();
        }else if(orderType.equals("pepper")){
            pizza=new BJPepperPizza();
        }
        return pizza;
    }
}
