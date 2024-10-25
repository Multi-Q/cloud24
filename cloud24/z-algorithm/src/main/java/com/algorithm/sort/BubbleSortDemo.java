package com.algorithm.sort;

import java.util.Arrays;

/**
 * @author QRH
 * @date 2024/10/25 15:48
 * @description m冒泡排序
 */
public class BubbleSortDemo {
    public static void main(String[] args) {
        int arr[] = {3, 0, -1, 10, -2};

        arr = bubbleSort(arr);
    }

    /**
     * 冒泡排序改进版
     * <p>
     * 如果在某一趟交换中没有发生交换就说明数组已经有序了，没必要再进行下面的交换了
     *
     * @param arr 待排序的数组
     * @return 已排好序的数组
     */
    private static int[] bubbleSort(int[] arr) {
        int temp;
        boolean flag = false;//是否进行过交换

        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    flag = true;
                    temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
            System.out.println("第" + (i + 1) + "趟排序后的数组：" + Arrays.toString(arr));
            if (!flag) {
                break;//一趟排序中，如果没有发生交换就退出循环
            } else {
                flag = false;//重置flag，让其进行下次交换
            }

        }
        return arr;
    }
}
