package com.algorithm.sort;

import java.util.Arrays;

/**
 * @author QRH
 * @date 2024/10/25 19:00
 * @description 希尔排序
 */
public class ShellSortDemo {
    public static void main(String[] args) {
        int[] arr = {8, 9, 1, 7, 2, 3, 5, 4, 6, 0};

        shellSort(arr);
        System.out.println(Arrays.toString(arr));
    }

    /**
     * 希尔排序
     *
     * @param arr
     * @return
     */
    private static int[] shellSort(int[] arr) {
        for (int gap = arr.length / 2; gap > 0; gap /= 2) {
            for (int i = gap; i < arr.length; i++) {
                int j = i;
                int temp = arr[j];
                if (arr[j] < arr[j - gap]) {
                    while (j - gap >= 0 && temp < arr[j - gap]) {
                        //移动
                        arr[j] = arr[j - gap];
                        j -= gap;
                    }
                    //退出while后，就给temp找到插入位置
                    arr[j] = temp;
                }
            }
        }
        return arr;
    }


}
