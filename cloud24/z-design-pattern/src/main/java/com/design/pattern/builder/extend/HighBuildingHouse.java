package com.design.pattern.builder.extend
        ;

import com.design.pattern.builder.abstracts.HouseBuilder;

/**
 * @author QRH
 * @date 2024/5/7 13:12
 * @description TODO
 */
public class HighBuildingHouse extends HouseBuilder {


    @Override
    public void buildBase() {
        System.out.println("HighBuilding-打地基100m");
    }

    @Override
    public void buildWall() {
        System.out.println("HighBuilding-建墙20m");
    }

    @Override
    public void roofed() {
        System.out.println("HighBuilding-封顶");
    }
}
