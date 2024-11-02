package com.algorithm.tree;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author QRH
 * @date 2024/11/2 17:03
 * @description TODO
 */
public class HuffmanCodeDemo {

    static Map<Byte, String> huffmanCodes = new HashMap<>();
    static StringBuilder sb = new StringBuilder();


    public static void main(String[] args) {
        String arr = "i like like like java do you like a java";
        byte[] bytes = arr.getBytes(StandardCharsets.UTF_8);
        List<Node2> nodes = getNodes(bytes);
        Node2 root = createHuffmanTree(nodes);
        System.out.println(root);

        getCodes(root, "", sb);
    }



    /**
     * 获取哈夫曼编码
     *
     * @param node
     * @param code
     * @param stringBuilder
     */
    public static void getCodes(Node2 node, String code, StringBuilder stringBuilder) {
        StringBuilder sb2 = new StringBuilder(stringBuilder);
        sb2.append(code);
        if (node != null) {
            //非叶子节点
            if (node.data == null) {
                //递归处理
                getCodes(node.left, "0", sb2);
                getCodes(node.right, "1", sb2);
            } else {
                huffmanCodes.put(node.data, sb2.toString());
            }
        }
    }


    /**
     * 创建哈夫曼树
     *
     * @param nodes
     * @return 哈夫曼根节点
     */
    public static Node2 createHuffmanTree(List<Node2> nodes) {
        while (nodes.size() > 1) {
            Collections.sort(nodes);
            Node2 left = nodes.get(0);
            Node2 right = nodes.get(1);
            Node2 parent = new Node2(null, left.weight + right.weight);
            parent.left = left;
            parent.right = right;

            nodes.remove(left);
            nodes.remove(right);
            nodes.add(parent);
        }
        return nodes.get(0);
    }

    public static List<Node2> getNodes(byte[] bytes) {
        List<Node2> nodes = new ArrayList<>();
        Map<Byte, Integer> counts = new HashMap<>();
        for (byte b : bytes) {
            Integer count = counts.get(b);
            if (count == null) {
                counts.put(b, 1);
            } else {
                counts.put(b, count + 1);
            }
        }

        for (Map.Entry<Byte, Integer> entry : counts.entrySet()) {
            nodes.add(new Node2(entry.getKey(), entry.getValue()));
        }
        return nodes;
    }

}

class Node2 implements Comparable<Node2> {
    Byte data;
    int weight;
    Node2 left;
    Node2 right;

    public Node2(Byte data, int weight) {
        this.data = data;
        this.weight = weight;
    }

    @Override
    public int compareTo(Node2 o) {
        return this.weight - o.weight;
    }

    @Override
    public String toString() {
        return "Node2{" +
                "data=" + data +
                ", weight=" + weight +
                '}';
    }

    public void preOrder(Node2 root) {
        if (root == null) {
            return;
        }
        System.out.println(root.toString());
        if (root.left != null) {
            preOrder(root.left);
        }
        if (root.right != null) {
            preOrder(root.right);
        }
    }
}
