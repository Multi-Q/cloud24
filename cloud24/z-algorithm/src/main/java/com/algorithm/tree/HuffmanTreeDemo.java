package com.algorithm.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author QRH
 * @date 2024/11/2 15:19
 * @description 哈夫曼树
 */
public class HuffmanTreeDemo {
    public static void main(String[] args) {
        int[] arr = {13, 7, 8, 3, 29, 6, 1};
        System.out.println(createHuffmanTree(arr));

    }

    public static Node createHuffmanTree(int[] arr) {
        //为了方便，将数组每个元素构造成Node
        List<Node> nodes = new ArrayList<>();
        for (int value : arr) {
            nodes.add(new Node(value));
        }
        while (nodes.size() > 1) {
            //排序，从小到大
            Collections.sort(nodes);

            System.out.println("nodes= " + nodes);

            Node left = nodes.get(0);
            Node right = nodes.get(1);

            //构建一个新的二叉树
            Node parent = new Node(left.value + right.value);
            parent.left = left;
            parent.right = right;

            nodes.remove(left);
            nodes.remove(right);
            //将parent加到nodes
            nodes.add(parent);
        }
        //返回根节点
        return nodes.get(0);
    }
}

class Node implements Comparable<Node> {
    int value;//结点权值
    Node left;//左子节点
    Node right;//右子节点

    public Node(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Node { value = " + value + " } ";
    }

    @Override
    public int compareTo(Node o) {
        return this.value - o.value;
    }
}