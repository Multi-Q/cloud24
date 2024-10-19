package com.algorithm.queue;

import java.util.Scanner;

/**
 * @author QRH
 * @date 2024/9/30 19:32
 * @description 将队列改为循环队列
 */
public class CircleQueueDemo {
    public static void main(String[] args) {
        CircleQueue queue = new CircleQueue(5);
        char key = ' ';
        Scanner scanner = new Scanner(System.in);
        boolean loop = true;

        while (loop) {
            System.out.println("s(show):显示队列");
            System.out.println("e(exit):退出程序");
            System.out.println("a(add):添加数据到队列");
            System.out.println("g(get):从队列中取出数据");
            System.out.println("p(peak):查看队列头的数据");
            System.out.println("c(count):查看队列中有效的元素个数");
            key = scanner.next().charAt(0);

            switch (key) {
                case 's':
                    queue.showQueue();
                    break;
                case 'a':
                    int value = scanner.nextInt();
                    queue.addQueue(value);
                    break;
                case 'g':
                    try {
                        int res = queue.getQueue();
                        System.out.println("取出的元素是：" + res);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 'p':
                    try {
                        int first = queue.peekQueue();
                        System.out.println("循环队列的第一个元素为：" + first);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 'c':
                    int count = queue.count();
                    System.out.println("循环队列中有效的元素个数为：" + count);
                    break;
                case 'e':
                    scanner.close();
                    loop = false;
                    break;
                default:
                    break;
            }

        }
        System.out.println("程序退出~~~");

    }
}

class CircleQueue {
    private int maxSize;
    private int front;
    private int rear;
    private int[] arr;

    public CircleQueue(int maxSize) {
        this.maxSize = maxSize;
        front = 0;
        rear = 0;
        arr = new int[maxSize];
    }

    /**
     * 判断循环队列是否为空
     *
     * @return
     */
    public boolean isEmpty() {
        return  front==rear;
    }

    /**
     * 判断循环队列是否满
     *
     * @return
     */
    public boolean isFull() {
        return (rear + 1) % maxSize == front;
    }

    /**
     * 向循环队列中添加数据
     *
     * @param n 要添加的数据
     */
    public void addQueue(int n) {
        if (isFull()) {
            System.out.println("队列已满，不能加入");
            return;
        }
        arr[rear] = n;
        rear=(rear+1)%maxSize;
    }

    /**
     * 取数据
     *
     * @return
     */
    public int getQueue() {
        if (isEmpty()) {
            throw new RuntimeException("队列为空，不能取数据");
        }
        int value = front;
        front = (front + 1) % maxSize;
        return arr[value];
    }

    /**
     * 查看队列中的第一个元素
     *
     * @return
     */
    public int peekQueue() {
        return arr[front];
    }

    /**
     * 查看队列中的所有元素
     */
    public void showQueue() {
        if (isEmpty()) {
            System.out.println("队列为空 ");
            return;
        }
        for (int i = front; i < front + count(); i++) {
            System.out.printf("arr[%d]=%d\t", i % maxSize, arr[i % maxSize]);
        }
        System.out.println();
    }

    /**
     * 获取循环队列中有效的元素个数
     *
     * @return
     */
    public int count() {
        return (rear + maxSize - front) % maxSize;
    }

}
