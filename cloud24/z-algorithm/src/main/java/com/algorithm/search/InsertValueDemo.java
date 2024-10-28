package com.algorithm.search;

/**
 * @author QRH
 * @date 2024/10/28 13:47
 * @description 插值查找
 */
public class InsertValueDemo {
    public static void main(String[] args) {
        int[] arr = {1, 3, 7, 8, 12, 34, 67, 88, 90, 98};
        System.out.println(insertValue(arr, 0, arr.length - 1, 1 ));
    }

    /**
     * @param arr     源数组
     * @param left    数组左边索引
     * @param right   数组右边索引
     * @param findVal 查找值
     * @return 所在的下标
     */
    private static int insertValue(int[] arr, int left, int right, int findVal) {
        if (left > right || findVal < arr[0] || findVal > arr[arr.length - 1]) {
            return -1;
        }
        int mid = left + (right - left) * (findVal - arr[left]) / (arr[right] - arr[left]);
        int midVal = arr[mid];
            if (findVal > midVal) {//向右递归
                return insertValue(arr, mid + 1, right, findVal);
            } else if (findVal < midVal) {//向左递归
                return insertValue(arr, left, mid - 1, findVal);
            } else  {
                return mid;
            }
    }
}
