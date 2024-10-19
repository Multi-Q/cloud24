package com.algorithm.stack;

import java.util.Scanner;

/**
 * @author QRH
 * @date 2024/10/5 13:12
 * @description TODO
 */
public class ArrayStackDemo {
    public static void main(String[] args) {
        ArrayStack stack = new ArrayStack(4);
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
                        int res = stack.pop();
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

class ArrayStack {
    private int maxSize;//占的大小
    private int[] stack;//数组，模拟栈，用于存放数据
    private int top = -1;//栈顶，初始化为-1

    public ArrayStack(int maxSize) {
        this.maxSize = maxSize;
        stack = new int[this.maxSize];
    }

    /**
     * 判断栈顶
     *
     * @return
     */
    public boolean isFull() {
        return top == maxSize - 1;
    }

    /**
     * 判断栈空
     *
     * @return
     */
    public boolean isEmpty() {
        return top == -1;
    }


    /**
     * 入栈
     *
     * @param value 数据
     */
    public void push(int value) {
        //先判满
        if (isFull()) {
            System.out.println("栈满");
            return;
        }
        top++;
        stack[top] = value;
    }

    /**
     * 出栈
     *
     * @return 栈顶数据
     */
    public int pop() {
        //先判空
        if (isEmpty()) {
            throw new RuntimeException("栈空，没有数据");
        }
        int value = stack[top];
        top--;
        return value;
    }

    /**
     * 遍历栈
     */
    public void list() {
        if (isEmpty()) {
            System.out.println("栈空，不能遍历");
            return;
        }
        for (int i = top; i >= 0; i--) {
            System.out.printf("stack[%d]=%d\t", i, stack[i]);
        }
        System.out.println();
    }
}
