package com.design.pattern.factory;


import jdk.dynalink.linker.support.TypeUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author QRH
 * @date 2024/5/6 18:34
 * @description TODO
 */
public class Pizza {

    public void prepare() {

    }

    public void bake() {

    }

    public void cut() {

    }

    public void box() {

    }

    public static void main(String[] args) {
//        int[] data = {-49, -30, -16, 9, 21, 21, 23, 30, 30};
//        List<Integer> list = binarySearch(data, 0, data.length - 1, 9);
//        System.out.println(list);
//        System.out.println("=========================");
//        int [] arr = {3, 44, 38, 5, 47, 15, 36, 26, 27, 2, 46, 4, 19, 50, 48};
//        quickSort(arr,0,arr.length-1);
//        System.out.println(Arrays.toString(arr)  );
//        System.out.println("==============");
//        BubbleSort(arr);


        System.out.println(aa("aeiou"));


    }
public static String aa(final  String st ){
     String s=st;
    String c=new String("aeiouAEIOU");

    for(int i=0;i<s.length();i++){
        if(c.contains(String.valueOf(s.charAt(i)))){
            s=s.replace(s.charAt(i),'!');
        }

    }
    return s;
}


    public static List<Integer> binarySearch(int[] arr, int left, int right, int findVal) {
        List<Integer> list = new ArrayList<>();
        if (left > right) {
            return list;
        }
        int middleIndex = (left + right) / 2;
        if (findVal < arr[middleIndex]) {
            return binarySearch(arr, left, middleIndex - 1, findVal);
        } else if (findVal > arr[middleIndex]) {
            return binarySearch(arr, middleIndex + 1, right, findVal);
        }
        System.out.println("找到了，下标为：" + middleIndex);
        int temp = middleIndex - 1;

        //向左扫描
        while (true) {
            if (temp < 0 || arr[temp] != findVal) {
                break;
            }
            if (arr[temp] == findVal) {
                list.add(temp);
            }
            temp -= 1;
        }

        list.add(middleIndex);

        //向右边扫描
        temp = middleIndex + 1;
        while (true) {
            if (temp > arr.length - 1 || arr[temp] != findVal) {
                break;
            }
            if (arr[temp] == findVal) {
                list.add(temp);
            }
            temp += 1;
        }
        return list;
    }

    public static void quickSort(int arr[], int low, int high) {
        int i, j, t, temp;
        if (low > high) {
            return;

        }
        i = low;
        j = high;
        temp = arr[low];

        System.out.println("基准位：" + temp);

        while (i < j) {
            //先看右边
            while (temp <= arr[j] && i < j) {
                j--;
            }
            //再看左边
            while (temp >= arr[i] && i < j) {
                i++;
            }

            //满足条件在交换
            if (i < j) {
                System.out.println("交换:" + arr[i] + " 和 " + arr[j]);
                t = arr[j];
                arr[j] = arr[i];
                arr[i] = t;
                System.out.println(Arrays.toString(arr));
            }
        }

        //最后将基准位与i和j位置交换
        System.out.println("基准位" + temp + "i和j相遇的位置" + arr[i] + "交换");

        arr[low] = arr[i];
        arr[i] = temp;
        System.out.println(Arrays.toString(arr));

        //递归调用左半数组
        quickSort(arr, low, j - 1);

        //递归调用右半数组
        quickSort(arr, j + 1, high);
    }

    public static void BubbleSort(int arr[]) {
        int len = arr.length;

        for (int i = 1; i < len; i++) {//外层控制轮数
            for (int j = 0; j < len - i; j++) {//内层遍历元素
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
        System.out.println(Arrays.toString(arr));
    }
}
