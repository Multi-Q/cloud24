package com.design.pattern.builder.abstracts;

import com.design.pattern.builder.pojo.House;

/**
 * @author QRH
 * @date 2024/5/7 12:48
 * @description TODO
 */
public abstract class HouseBuilder {
    protected static House house=new House();

    //打地基
    public abstract void buildBase();

    //建墙
    public abstract void buildWall();

    //封顶
    public abstract void roofed();

    public House buildHouse(){
        return house;
    }


}
