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

       /* List<Node2> nodes = getNodes(bytes);
        Node2 root = createHuffmanTree(nodes);
//        System.out.println(root);

        Map<Byte, String> huffmanCodes = getHuffmanCodes(root);
        System.out.println("生成的哈夫曼编码表:" + huffmanCodes);

        byte[] huffmanCodeBytes = zip(bytes, huffmanCodes);
        System.out.println(Arrays.toString(huffmanCodeBytes));*/

        byte[] huffmanZip = huffmanZip(bytes);
        System.out.println("压缩后的哈夫曼编码，字节：" + Arrays.toString(huffmanZip));

        byte[] decodeHuffmanCode = decodeHuffmanCode(huffmanCodes, huffmanZip);
        System.out.println("解码后的数据" + new String(decodeHuffmanCode));

    }

    /**
     * @param huffmanCodes 哈夫曼编码表map
     * @param huffmanBytes 哈夫曼得到的字节数组
     * @return 原来的字符串字节数组
     */
    public static byte[] decodeHuffmanCode(Map<Byte, String> huffmanCodes, byte[] huffmanBytes) {
        StringBuilder stringBuilder = new StringBuilder();
        //将byte数组转成二进制字符串
        for (int i = 0; i < huffmanBytes.length; i++) {
            //判断是否是最后一个字节
            boolean flag = (i == huffmanBytes.length - 1);
            stringBuilder.append(byte2BitString(!flag, huffmanBytes[i]));
        }
        //把字符串按照指定的哈弗曼编码进行解码
        //把哈夫曼编码表进行调换，要反向查询
        Map<String, Byte> map = new HashMap<>();
        for (Map.Entry<Byte, String> entry : huffmanCodes.entrySet()) {
            map.put(entry.getValue(), entry.getKey());
        }

        //创建集合存放byte
        List<Byte> list = new ArrayList<>();
        for (int i = 0; i < stringBuilder.length(); ) {
            int count = 1;
            boolean flag = true;
            Byte b = null;
            while (flag) {
                String key = stringBuilder.substring(i, i + count);
                b = map.get(key);
                if (b == null) {//说明没有匹配到
                    count++;
                } else {
                    flag = false;//匹配到
                }
            }
            list.add(b);
            i += count;//i直接移动到count

        }
        byte[] bytes = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            bytes[i] = list.get(i);
        }
        return bytes;
    }

    /**
     * 将字节转换成二进制的字符串
     *
     * @param flag 标志是否需要补高位，如果true需要补高位，false不需要补高位
     * @param b    传入的byte
     * @return 对应的二进制字符串，按补码返回
     */
    public static String byte2BitString(boolean flag, byte b) {
        int temp = b; //将byte转换成int
        //如果是正数需要补高位
        if (flag) {
            temp = temp | 256;
        }
        String str = Integer.toBinaryString(temp);
        return flag ? str.substring(str.length() - 8) : str;
    }

    /**
     * 哈夫曼压缩，就是将之前的方法封装到这里
     *
     * @param bytes 字节数组
     * @return
     */
    public static byte[] huffmanZip(byte[] bytes) {
        List<Node2> nodes = getNodes(bytes);
        //创建huffman数
        Node2 root = createHuffmanTree(nodes);
        //对应的哈夫曼编码
        Map<Byte, String> huffmanCodes = getHuffmanCodes(root);
        //压缩后的哈夫曼字节码
        byte[] huffmanCodeBytes = zip(bytes, huffmanCodes);
        return huffmanCodeBytes;
    }


    /**
     * 通过生成的哈夫曼编码表，返回一个哈弗曼编码压缩后的byte数组
     *
     * @param bytes        原始的字符串对应的byte[]
     * @param huffmanCodes 生成的哈夫曼编码map
     * @return 哈夫曼处理后的byte[]
     */
    public static byte[] zip(byte[] bytes, Map<Byte, String> huffmanCodes) {
        //利用hufffmanCodes将bytes转成哈弗曼编码对应的字符串
        StringBuilder sb = new StringBuilder();
        //遍历bytes[]
        for (byte b : bytes) {
            sb.append(huffmanCodes.get(b));
        }
        //字节编码
//        System.out.println("字节编码"+sb.toString());

        //统计返回byte[] huffmanCodeBytes长度
        int len;
        if (sb.length() % 8 == 0) {
            len = sb.length() / 8;
        } else {
            len = sb.length() / 8 + 1;
        }
        //创建压缩后的byte数组
        byte[] huffmanCodeBytes = new byte[len];
        int index = 0;
        for (int i = 0; i < sb.length(); i += 8) {
            String strByte;
            if (i + 8 > sb.length()) {//不够8位
                strByte = sb.substring(i);
            } else {
                strByte = sb.substring(i, i + 8);
            }
            //将strByte转成byte，存到huffmanCodeBytes
            huffmanCodeBytes[index++] = (byte) Integer.parseInt(strByte, 2);
        }
        return huffmanCodeBytes;
    }

    /**
     * 重载getCodes()
     *
     * @param root
     * @return
     */
    public static Map<Byte, String> getHuffmanCodes(Node2 root) {
        if (root == null) {
            return null;
        }
        //处理 root 的左子树
        getCodes(root.left, "0", sb);
        //处理 root 的右子树
        getCodes(root.right, "1", sb);
        return huffmanCodes;
    }


    /**
     * 获取哈夫曼编码
     *
     * @param node
     * @param code
     * @param stringBuilder
     */
    public static Map<Byte, String> getCodes(Node2 node, String code, StringBuilder stringBuilder) {
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
        return huffmanCodes;
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
