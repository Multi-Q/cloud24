package com.algorithm.search;

/**
 * @author QRH
 * @date 2024/10/27 19:45
 * @description TODO
 */
public class BinarySearchDemo {
    public static void main(String[] args) {
        int[] arr = {1, 8, 10, 89, 1000, 124};
        int index = binarySearch(arr, 0, arr.length - 1, 89);
        System.out.println(index);
    }

    private static int binarySearch(int[] arr, int left, int right, int findVal) {
        int mid = (left + right) / 2;
        int midVal = arr[mid];

        if (findVal > midVal) {
            return binarySearch(arr, mid + 1, right, findVal);
        } else if (findVal < midVal) {
            return binarySearch(arr, left, mid - 1, findVal);
        } else if (findVal == midVal) {
            return mid;
        }
        return -1;
    }


}
