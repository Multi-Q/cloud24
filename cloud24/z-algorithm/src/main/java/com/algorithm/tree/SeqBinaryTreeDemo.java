package com.algorithm.tree;

/**
 * @author QRH
 * @date 2024/10/29 13:27
 * @description 顺序存储二叉树
 */
public class SeqBinaryTreeDemo {
    public static void main(String[] args) {
        ArrayBinaryTree arrayBinaryTree = new ArrayBinaryTree(new int[]{1, 2, 3, 4, 5, 6, 7});
//        arrayBinaryTree.preOrder(0); //1 2 4 5 3 6 7
//        arrayBinaryTree.infixOrder(0); //4 2 5 1 6 3 7
        arrayBinaryTree.postOrder(0);//4 5 2 6 7 3 1

    }
}

class ArrayBinaryTree {
    private int[] arr;//存储数据结点的数组

    public ArrayBinaryTree(int[] arr) {
        this.arr = arr;
    }

    /**
     * 以前序遍历方式将数组转换成顺序二叉树
     *
     * @param rootIndex 根节点下标
     */
    public void preOrder(int rootIndex) {
        //如果数组为空
        if (arr == null || arr.length == 0) {
            System.out.println("数组为空，不能遍历");
            return;
        }
        System.out.println(arr[rootIndex]);
        //向左递归
        if ((rootIndex * 2 + 1) < arr.length) {
            preOrder(2 * rootIndex + 1);
        }
        //向右递归
        if ((rootIndex * 2 + 2) < arr.length) {
            preOrder(2 * rootIndex + 2);
        }
    }

    /**
     * 以中序遍历方式将数组转换成顺序二叉树
     *
     * @param rootIndex 根节点下标
     */
    public void infixOrder(int rootIndex) {
        if (arr == null || arr.length == 0) {
            System.out.println("数组为空，无法遍历");
            return;
        }
        //向左遍历
        if ((rootIndex * 2 + 1) < arr.length) {
            infixOrder(2 * rootIndex + 1);
        }
        System.out.println(arr[rootIndex]);
        //向右遍历
        if ((rootIndex * 2 + 2) < arr.length) {
            infixOrder(2 * rootIndex + 2);
        }
    }

    /**
     * 以后序遍历方式将数组转换成顺序二叉树
     *
     * @param rootIndex 根节点下标
     */
    public void postOrder(int rootIndex) {
        if (arr == null || arr.length == 0) {
            System.out.println("数组为空，无法遍历");
            return;
        }
        //向左遍历
        if ((rootIndex * 2 + 1) < arr.length) {
            postOrder(2 * rootIndex + 1);
        }
        //向右遍历
        if ((rootIndex * 2 + 2) < arr.length) {
            postOrder(2 * rootIndex + 2);
        }
        System.out.println(arr[rootIndex]);
    }
}
