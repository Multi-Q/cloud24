package com.algorithm.sort;

import java.util.Arrays;

/**
 * @author QRH
 * @date 2024/10/27 16:02
 * @description 归并排序
 */
public class MergeSortDemo {
    public static void main(String[] args) {
        int[] arr = {8, 4, 5, 7, 1, 3, 6 };
         mergeSort(arr, 0,  arr.length - 1, new int[arr.length]);
        System.out.println(Arrays.toString(arr));
    }

    private static void mergeSort(int[] arr, int left, int right, int[] temp) {
        if (left < right) {
            int mid = (left + right) / 2;
            //向左递归
            mergeSort(arr, left, mid, temp);
            //向右递归
            mergeSort(arr, mid + 1, right, temp);

            //合并
           merge(arr, left, mid, right, temp);
        }
    }

    private static void merge(int[] arr, int left, int mid, int right, int[] temp) {
        int i = left;//初始化i，左边有序序列的初始索引
        int j = mid + 1;//初始化j，右边有序序列的初始索引
        int t = 0;//指向temp数组的当前索引

        //先把左右两边的数据按照规则填充到temp数组直到左右两边的有序序列，有一边处理完成为止
        while (i <= mid && j <= right) {
            //如果左边的有序序列的当前元素钓鱼等于右边有序序列的当前元素，即将左边的当前元素拷贝到temp数组
            if (arr[i] < arr[j]) {
                temp[t] = arr[i];
                t++;
                i++;
            } else {//反之，将右边有序序列的当前元素，填充到temp数组
                temp[t] = arr[j];
                t++;
                j++;
            }
        }

        //把剩余数据的一编的数据依次填充到temp
        while (i <= mid) {
            //左边的有序序列还有剩余的元素，就全部填充到temp
            temp[t] = arr[i];
            t++;
            i++;
        }
        while (j <= right) {
            //右边的有序序列还有剩余的元素，就全部填充到temp
            temp[t] = arr[j];
            t++;
            j++;
        }

        //将temp数组的元素拷贝到arr，但并不是每次都拷贝所有
        t = 0;
        for (int tempLeft = left; tempLeft <= right; tempLeft++, t++) {
            arr[tempLeft] = temp[t];
        }

//        int tempLeft = left;
//        while (tempLeft <= right) {
//            arr[tempLeft] = temp[t];
//            t++;
//            tempLeft++;
//        }

    }
}
