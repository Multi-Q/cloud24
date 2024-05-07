package com.design.pattern.builder;

import com.design.pattern.builder.abstracts.HouseBuilder;
import com.design.pattern.builder.director.HouseDirector;
import com.design.pattern.builder.extend.CommonHouse;
import com.design.pattern.builder.extend.HighBuildingHouse;
import com.design.pattern.builder.pojo.House;

/**
 * @author QRH
 * @date 2024/5/7 13:13
 * @description TODO
 */
public class Main {
    public static void main(String[] args) {
       //件普通房子
        CommonHouse commonHouse = new CommonHouse();
        HouseDirector houseDirector = new HouseDirector(commonHouse);
        House house = houseDirector.constructHouse();

        System.out.println("==========输出过程==========");

        //盖高楼
        HighBuildingHouse highBuildingHouse = new HighBuildingHouse();
        houseDirector.setHouseBuilder(highBuildingHouse);
        House house1 = houseDirector.constructHouse();
        System.out.println("==========输出过程==========");

    }
}
