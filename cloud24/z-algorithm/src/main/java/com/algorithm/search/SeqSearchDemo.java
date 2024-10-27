package com.algorithm.search;

/**
 * @author QRH
 * @date 2024/10/27 19:38
 * @description TODO
 */
public class SeqSearchDemo {
    public static void main(String[] args) {
        int[] arr = {1, 5, 2, 90, 2, 8};
        int index = seqSearch(arr, 12);
        if (index==-1){
            System.out.println("数组中没有该数据");
        }else{
            System.out.println(index);
        }
    }

    /**
     * 线性查找，找到第一个就返回下标
     * @param arr 源数组
     * @param findVal 要查找的数据
     * @return 该数据在arr中的下标 | -1，没有找到
     */
    private static int seqSearch(int[] arr, int findVal) {
        for (int i = 0; i < arr.length; i++) {
            if (findVal == arr[i]) {
                return i;
            }
        }
        return -1;
    }
}
