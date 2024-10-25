package com.algorithm.sort;

import java.util.Arrays;

/**
 * @author QRH
 * @date 2024/10/25 16:56
 * @description 插入排序
 */
public class InsertSortDemo {
    public static void main(String[] args) {
        int[] arr = {101, 34, 119, 1};
        insertSort(arr);
    }

    /**
     * 插入排序
     *
     * @param arr 待排序的数组
     * @return 已排好序的数组
     */
    private static int[] insertSort(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            int insertVal = arr[i]; //待插入的数
            int insertIndex = i - 1;//有序数组中的最后一个元素的下标

            while (insertIndex >= 0 && insertVal < arr[insertIndex]) {
                arr[insertIndex + 1] = arr[insertIndex];
                insertIndex--;
            }
            arr[insertIndex + 1] = insertVal;

            System.out.println("第" + (i + 1) + "趟排序后的数组：" + Arrays.toString(arr));
        }

        return arr;
    }
}
