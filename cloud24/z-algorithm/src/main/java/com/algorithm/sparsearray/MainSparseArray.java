package com.algorithm.sparsearray;

/**
 * @author QRH
 * @date 2024/7/7 18:20
 * @description 稀疏数组案例，棋谱保存
 */
public class MainSparseArray {
    public static void main(String[] args) {
        //1、创建二维数组
        //用0表示没有棋子，1表示黑子，2表示白子
        int[][] cheerArr1 = new int[11][11];

        //模拟棋子的落位
        cheerArr1[1][2] = 1;
        cheerArr1[1][3] = 1;
        cheerArr1[2][3] = 2;
        //遍历一下二维数组
        printArray(cheerArr1);

        //2、创建稀疏数组
        System.out.println("二维数组转稀疏数组：------------");
        int[][] sparseArr = array2SparseArray(cheerArr1);
        //遍历下稀疏数组
        printArray(sparseArr);


        //3、将稀疏数组转成二维数组
        System.out.println("稀疏数组转二维数组：------------");
        int[][] cheerArray2 = sparseArray2Array(sparseArr);
        printArray(cheerArray2);


    }

    /**
     * 打印二维数组
     *
     * @param arr 待打印数组
     */
    public static void printArray(int[][] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                System.out.print(arr[i][j] + "\t");
            }
            System.out.println();
        }
    }

    /**
     * 统计二维数组中非0的元素个数
     *
     * @param arr 目标数组
     * @return 统计后的元素个数
     */
    public static int getCount(int[][] arr) {
        int count = 0;
        for (int[] row : arr) {
            for (int data : row) {
                if (data != 0) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * 创建稀疏数组
     *
     * @param arr 原数组
     * @return 创建好的稀疏数组
     */
    public static int[][] array2SparseArray(int[][] arr) {
        //1、获取目标数组中非0元素个数
        int sum = getCount(arr);
        //2、创建稀疏数组
        int[][] sparseArray = new int[sum + 1][3];

        //3、稀疏数组第一行记录的是原数组的行数、列数以及非0的元素个数
        int sourceRow = arr.length;
        int sourceCol = arr[0].length;
        sparseArray[0][0] = sourceRow;
        sparseArray[0][1] = sourceCol;
        sparseArray[0][2] = sum;

        //count 用来记路二维数组的有效个数，并把这个count作为行号赋值给稀疏数组
        int count = 0;
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                if (arr[i][j] != 0) {
                    count++;
                    sparseArray[count][0] = i;
                    sparseArray[count][1] = j;
                    sparseArray[count][2] = arr[i][j];
                }
            }
        }
        return sparseArray;
    }

    /**
     * 将稀疏数组转成二维数组
     *
     * @param sparseArray 稀疏数组
     * @return 二维数组
     */
    public static int[][] sparseArray2Array(int[][] sparseArray) {
        //1、创建二维数组
        int sourceRow = sparseArray[0][0];
        int sourceCol = sparseArray[0][1];
        int sum = sparseArray[0][2];
        int[][] array = new int[sourceRow][sourceCol];

        //2、遍历稀疏数组，将数组中的元素赋值到二维数组中
        for (int i = 1; i < sparseArray.length; i++) {
            int row = sparseArray[i][0];
            int col = sparseArray[i][1];
            array[row][col] = sparseArray[i][2];
        }
        return array;
    }

}
