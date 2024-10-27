package com.algorithm.sort;

import java.util.Arrays;

/**
 * @author QRH
 * @date 2024/10/26 22:48
 * @description 快读排序
 */
public class QuickSortDemo {
    public static void main(String[] args) {
        int[] arr = {-9, 78, 0, 23, -567,0, 70};
        arr = quickSort(arr, 0, arr.length - 1);
        System.out.println(Arrays.toString(arr));
    }

    private static int[] quickSort(int[] arr, int left, int right) {
        int l = left;//左下表
        int r = right;//右下标
        int pivot = arr[(left + right) / 2];//中轴值
        int temp;

        //比piovt值大的放右边
        while (l < r) {
            //在piovt的左边一直找，找到大于等于piovt值才退出
            while (arr[l] < pivot) {
                l++;
            }
            //在piovt的右边一直找，找到小于等于piovt值才退出
            while (arr[r] > pivot) {
                r--;
            }
            //如果l>=r说明piovt的左右值，已经按照左边全部是小于等于piovt值，右边全部是大于等于piovt值
            if (l >= r) {
                break;
            }
            //交换
            temp = arr[l];
            arr[l] = arr[r];
            arr[r] = temp;
            //如果交换完后，发现这个arr[l]==piovt，r--，前移
            if (arr[l] == pivot) {
                r--;
            }
            //如果交换完后，发现这个arr[r]==piovt，l++，后移
            if (arr[r] == pivot) {
                l++;
            }
        }

        //如果l==r，必须l++,r--,否者出现栈溢出
        if (l == r) {
            l++;
            r--;
        }
        //向左递归
        if (left < r) {
            quickSort(arr, left, r);
        }
        //向右递归
        if (right > l) {
            quickSort(arr, l, right);
        }
        return arr;
    }
}
