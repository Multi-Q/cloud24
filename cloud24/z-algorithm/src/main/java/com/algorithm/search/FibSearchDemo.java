package com.algorithm.search;

import java.util.Arrays;

/**
 * @author QRH
 * @date 2024/10/28 14:29
 * @description TODO
 */
public class FibSearchDemo {
    public static void main(String[] args) {
        int[] arr = {1, 8, 10, 89, 1000, 1024};
        System.out.println(fibSearch(arr, 1024));
    }

    private static int fibSearch(int[] arr, int key) {
        int low = 0;
        int high = arr.length - 1;
        int k = 0;//斐波那契分割的下标
        int mid;
        int[] f = fib();

        while (high > f[k] - 1) {
            k++;
        }
        //f[k]的值可能大于arr的长度，因此我们需要使用Array类构造一个新的数组，并指向arr
        int[] temp = Arrays.copyOf(arr, f[k]);
        for (int i = high + 1; i < temp.length; i++) {
            temp[i] = arr[high];
        }

        while (low <= high) {
            mid = low + f[k - 1] - 1;
            if (key < temp[mid]) {
                //向数组前面查找
                high = mid - 1;
                k--;
            } else if (key > temp[mid]) {
                //向右边查找
                low = mid + 1;
                k -= 2;
            } else {
                return mid <= high ? mid : high;
            }
        }
        return -1;
    }

    private static int[] fib() {
        int[] f = new int[20];
        f[0] = 1;
        f[1] = 1;
        for (int i = 2; i < f.length; i++) {
            f[i] = f[i - 1] + f[i - 2];
        }
        return f;
    }
}
