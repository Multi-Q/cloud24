package com.design.pattern.builder.director;

import com.design.pattern.builder.pojo.House;
import com.design.pattern.builder.abstracts.HouseBuilder;

/**
 * @author QRH
 * @date 2024/5/7 13:25
 * @description TODO
 */
public class HouseDirector {
    protected HouseBuilder houseBuilder = null;

    public HouseDirector(HouseBuilder houseBuilder) {
        this.houseBuilder = houseBuilder;
    }

    public void setHouseBuilder(HouseBuilder houseBuilder) {
        this.houseBuilder = houseBuilder;
    }

    //交给指挥者来做
    public House constructHouse() {
        houseBuilder.buildBase();
        houseBuilder.buildWall();
        houseBuilder.roofed();

        return houseBuilder.buildHouse();
    }
}
