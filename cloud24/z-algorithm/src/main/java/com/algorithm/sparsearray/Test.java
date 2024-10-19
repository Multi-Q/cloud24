package com.algorithm.sparsearray;


/**
 * @author QRH
 * @date 2024/7/9 12:22
 * @description TODO
 */
public class Test {
    public static void main(String[] args) {
        int nums[] = new int[]{1,0,2,3,1};
        moveZeroes(nums);
    }

    public static void moveZeroes(int[] nums) {
        if (nums.length <= 1) {
            return;
        }

        for(int i = 0, j =0; i < nums.length; i++){
            if(nums[i] != 0){
                int temp = nums[i];
                nums[i] = 0;
                nums[j] = temp;

                j++;
            }
        }

        for (int i : nums) {
            System.out.print(i + "\t");
        }

    }

}


