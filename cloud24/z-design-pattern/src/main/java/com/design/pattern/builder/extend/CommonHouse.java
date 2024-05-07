package com.design.pattern.builder.extend
        ;

import com.design.pattern.builder.abstracts.HouseBuilder;

/**
 * @author QRH
 * @date 2024/5/7 13:12
 * @description TODO
 */
public class CommonHouse extends HouseBuilder {
    @Override
    public void buildBase() {
        System.out.println("Common-打地基5m");
    }

    @Override
    public void buildWall() {
        System.out.println("Common-建墙10m");
    }

    @Override
    public void roofed() {
        System.out.println("Common-封顶");
    }


}
