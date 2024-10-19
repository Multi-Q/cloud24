package com.algorithm.linklist;

/**
 * @author QRH
 * @date 2024/10/1 19:41
 * @description 双向链表
 */
public class DoubleLinkedListDemo {
    public static void main(String[] args) {
        HeroNode1 heroNode1 = new HeroNode1(1, "宋江", "及时雨");
        HeroNode1 heroNode2 = new HeroNode1(2, "卢俊义", "玉麒麟");
        HeroNode1 heroNode3 = new HeroNode1(3, "无用", "智多星");
        HeroNode1 heroNode4 = new HeroNode1(4, "林冲", "豹子头");

        DoubleLinkedList doubleLinkedList = new DoubleLinkedList();
        doubleLinkedList.add(heroNode1);
        doubleLinkedList.add(heroNode2);
        doubleLinkedList.add(heroNode3);
        doubleLinkedList.add(heroNode4);

        doubleLinkedList.list();

        System.out.println("------------------------------------------");

    }
}

class DoubleLinkedList {

    private HeroNode1 head = new HeroNode1(0, "", "");

    /**
     * 获取头结点
     *
     * @return
     */
    public HeroNode1 getHead() {
        return head.next;
    }

    /**
     * 遍历链表
     */
    public void list() {
        if (head.next == null) {
            System.out.println("链表为空");
            return;
        }
        HeroNode1 temp = head.next;
        while (true) {
            if (temp == null) {
                break;
            }
            System.out.println(temp);
            temp = temp.next;
        }
    }

    /**
     * 添加元素
     * @param heroNode1
     */
    public void add(HeroNode1 heroNode1) {
        HeroNode1 temp = head;
        while (true) {
            if (temp.next == null) {
                break;
            }
            temp = temp.next;
        }
        temp.next=heroNode1;
        heroNode1.prev=temp;
    }

}

class HeroNode1 {
    public HeroNode1 prev;
    public int no;
    public String name;
    public String nickName;
    public HeroNode1 next;

    public HeroNode1(int no, String name, String nickName) {
        this.prev = null;
        this.no = no;
        this.name = name;
        this.nickName = nickName;
        this.next = null;
    }

    @Override
    public String toString() {
        return "HeroNode1{" +
                "no=" + no +
                ", name='" + name + '\'' +
                ", nickName='" + nickName + '\'' +
                '}';
    }
}