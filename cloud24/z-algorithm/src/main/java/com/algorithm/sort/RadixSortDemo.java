package com.algorithm.sort;

import java.text.SimpleDateFormat;
import java.util.Arrays;

/**
 * @author QRH
 * @date 2024/10/27 16:02
 * @description 归并排序
 */
public class RadixSortDemo {
    public static void main(String[] args) {
        int[] arr = {53, 3, 542, 748, 14, 214};
        radixSort(arr);
//        System.out.println(Arrays.toString(arr));
    }

    private static void radixSort(int[] arr) {
        //得到属数组中最大的位数
        int max = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }
        //得到最大数
        int maxLength = (max + "").length();

        //定义一个二维数组，表示10个桶，每个桶都是一维数组
        //每个桶的大小定为arr.length
        int[][] bucket = new int[10][arr.length];
        //记录每个桶中实际存放了多少个数据，定义一个一维数组来记录各个桶的每次放入的数据个数
        //比如bucketOfElement[0]，就表示放入第0个桶
        int[] bucketElementCounts = new int[10];

        for (int i = 0, n = 1; i < maxLength; i++, n *= 10) {

            for (int j = 0; j < arr.length; j++) {
                //取出每个元素对应的位数值，个位，十位，百位
                int digitOfElement = arr[j] / n % 10;
                //放入到对应的桶中
                bucket[digitOfElement][bucketElementCounts[digitOfElement]] = arr[j];
                bucketElementCounts[digitOfElement]++;
            }

            //便利每个桶，将桶的数据放回原数组
            int index = 0;
            for (int k = 0; k < bucketElementCounts.length; k++) {
                //如果桶中有数据，才放回到原数组
                if (bucketElementCounts[k] != 0) {
                    //循环将该桶中的元素放入
                    for (int l = 0; l < bucketElementCounts[k]; l++) {
                        arr[index++] = bucket[k][l];
                    }
                }
                //第n轮后需要将每个bucketElementCounts[k]设置为0
                bucketElementCounts[k] = 0;
            }
            System.out.println("第" + (i + 1) + "轮，arr=" + Arrays.toString(arr));
        }
    }


}