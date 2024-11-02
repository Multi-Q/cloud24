package com.algorithm.tree;

import java.util.Arrays;

/**
 * @author QRH
 * @date 2024/11/2 14:21
 * @description 堆排序
 */
public class HeapSortDemo {
    public static void main(String[] args) {
        int[] arr={4,6,8,5,9};
        heapSort(arr);
        System.out.println(Arrays.toString(arr));
    }

    public static void heapSort(int[] arr) {
        int temp = 0;
        for (int i = arr.length / 2 - 1; i >= 0; i--) {
            adjustHeap(arr, i, arr.length);
        }
        for (int j = arr.length - 1; j > 0; j--) {
            //交换
            temp = arr[j];
            arr[j] = arr[0];
            arr[0] = temp;
            adjustHeap(arr, 0, j);
        }
    }

    /**
     * @param arr
     * @param i      叶子结点在数组中的下标
     * @param length 对多少个元素进行调整，length是在逐渐减少的
     */
    public static void adjustHeap(int[] arr, int i, int length) {
        int temp = arr[i];//先取出当前元素的值，保存在临时变量中
        //开始调整
        for (int k = 2 * i + 1; k < length; k = 2 * k + 1) {
            //默认小顶堆，大顶堆将arr[k] < arr[k + 1]改为arr[k] > arr[k + 1]
            if (k + 1 < length && arr[k] < arr[k + 1]) {//说明做子节点的值小于右子节点的值
                k++;//k指向右子节点
            }
            //默认小顶堆，大顶堆将arr[k] > temp改为arr[k] < temp
            if (arr[k] > temp) {//如果子节点大于父节点
                arr[i] = arr[k];//把较大的值赋给当前节点
                i = k;//i指向k，继续循环比较
            } else {
                break;
            }
        }
        //当for循环结束后，把i为父节点的树的最大值，放在了最顶
        arr[i] = temp;//将temp值放在调整后的位置
    }
}
