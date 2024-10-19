package com.algorithm.stack;

import java.util.Scanner;

/**
 * @author QRH
 * @date 2024/10/9 11:29
 * @description 用单链表 不带头结点的头插法实现栈
 */
public class LinkedListStackDemo {
    public static void main(String[] args) {
        LinkedListStack stack = new LinkedListStack();
        String key = " ";
        boolean loop = true;
        Scanner scanner = new Scanner(System.in);

        while (loop) {
            System.out.println("s:表示显示栈");
            System.out.println("e:退出程序");
            System.out.println("i:入栈");
            System.out.println("o:出栈");
            System.out.println("请输入你的选择：");

            key = scanner.next();
            switch (key) {
                case "s":
                    stack.list();
                    break;
                case "i":
                    int value = scanner.nextInt();
                    stack.push(value);
                    break;
                case "o":
                    try {
                        LinkedListNode res = stack.pop();
                        System.out.println("出栈-数据为：" + res);
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                case "e":
                    scanner.close();
                    loop = false;
                    break;
                default:
                    break;
            }
        }
        System.out.println("程序退出!!");

    }
}

class LinkedListNode {
    public int data;
    public LinkedListNode next = null;

    public LinkedListNode(int data, LinkedListNode next) {
        this.data = data;
        this.next = next;
    }

    public LinkedListNode() {
    }

    @Override
    public String toString() {
        return "LinkedListNode{" +
                "data=" + data +
                '}';
    }
}

class LinkedListStack {
    private LinkedListNode first = new LinkedListNode();

    private LinkedListNode top;

    /**
     * 判空
     */
    public boolean isEmpty() {
        return top == null;
    }

    /**
     * 入栈
     *
     * @param value 数据
     */
    public void push(int value) {
        if (isEmpty()) {
            first.data = value;
            top = first;
        } else {
            LinkedListNode newNode = new LinkedListNode(value, top);
            top = newNode;
        }
    }

    /**
     * 出栈
     *
     * @return 数据
     */
    public LinkedListNode pop() {
        if (isEmpty()) {
            System.out.println("栈空，无法操作");
            return null;
        }
        LinkedListNode res = top;
        top = top.next;
        return res;
    }

    /**
     * 遍历
     */
    public void list() {
        if (top == null) {
            System.out.println("栈空~~");
            return;
        }
        LinkedListNode tmp = top;
        while (tmp != null) {
            System.out.println(tmp.toString());
            tmp = tmp.next;
        }
    }

}