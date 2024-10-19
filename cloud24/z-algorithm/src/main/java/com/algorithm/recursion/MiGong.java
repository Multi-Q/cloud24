package com.algorithm.recursion;

/**
 * @author QRH
 * @date 2024/10/10 14:56
 * @description TODO
 */
public class MiGong {
    public static void main(String[] args) {
        int[][] map = new int[8][7];

        //使用1表示墙，上下全部置为1
        map = initWall(map);

        setWay(map, 1, 1);

        display(map);

    }


    /**
     * 使用递归回溯给小球找路
     * 约定：当map[i][j]为0表示该店没有走过，当为1表示墙，2表示通路可以走，3表示该点已经走过，但是走不通
     * 再走迷宫时，需要确定一个策略 下->右->上->左，如果该店走不通就回溯
     *
     * @param map 地图
     * @param i   小球从地图的位置出发的横坐标
     * @param j   小球从地图的位置出发的纵坐标
     * @return 找到通路返回true，否者false
     */
    public static boolean setWay(int[][] map, int i, int j) {
        if (map[6][5] == 2) {//通路已经找到
            return true;
        } else {
            if (map[i][j] == 0) {//如果当前这个点还没有走过
                map[i][j] = 2;//假设该点可以走通
                if (setWay(map, i + 1, j)) {//向下走
                    return true;
                } else if (setWay(map, i, j + 1)) {//向右走
                    return true;
                } else if (setWay(map, i - 1, j)) {//向上走
                    return true;
                } else if (setWay(map, i, j - 1)) {//向左走
                    return true;
                } else {
                    //说明该点走不通，是死路
                    map[i][j] = 3;
                    return false;
                }
            } else {
                //map[i][j]!=0,可能是1,2,3,
                return false;
            }
        }
    }

    /**
     * 遍历迷宫图
     *
     * @param map
     */
    private static void display(int[][] map) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                System.out.print(map[i][j] + " ");
            }
            System.out.println();
        }
    }

    /**
     * 初始化迷宫的墙
     *
     * @param map
     * @return
     */
    private static int[][] initWall(int[][] map) {
        for (int i = 0; i < 7; i++) {
            map[0][i] = 1;
            map[7][i] = 1;
            map[i][0] = 1;
            map[i][6] = 1;
        }
        map[3][1] = 1;
        map[3][2] = 1;
        return map;
    }
}
