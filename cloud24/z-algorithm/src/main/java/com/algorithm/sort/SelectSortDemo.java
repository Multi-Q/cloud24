package com.algorithm.sort;

import java.util.Arrays;

/**
 * @author QRH
 * @date 2024/10/25 16:15
 * @description 选择排序（简单选择排序）
 */
public class SelectSortDemo {
    public static void main(String[] args) {
        int[] arr = {101, 34, 2, 119, 1};
        selectSort(arr);
    }

    /**
     * 选择排序
     *
     * @param arr 待排序的数组
     * @return 排序好的数组
     */
    private static int[] selectSort(int[] arr) {
        boolean flag = false;
        for (int i = 0; i < arr.length - 1; i++) {
            int min = arr[i];
            int minIndex = i;

            for (int j = i + 1; j < arr.length; j++) {
                if (min > arr[j]) {
                    min = arr[j];
                    minIndex = j;
                }
            }
            //将最小值放在arr[i]，即交换
            if (minIndex != i) {
                arr[minIndex] = arr[i];
                arr[i] = min;
            }
            System.out.println("第" + (i + 1) + "趟，排序后结果：" + Arrays.toString(arr));
        }
        return arr;
    }
}
