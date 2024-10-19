package com.algorithm.linklist;

/**
 * @author QRH
 * @date 2024/9/30 20:35
 * @description 单向链表
 */
public class SingleLinkListDemo {
    public static void main(String[] args) {
        HeroNode heroNode1 = new HeroNode(1, "宋江", "及时雨");
        HeroNode heroNode2 = new HeroNode(2, "卢俊义", "玉麒麟");
        HeroNode heroNode3 = new HeroNode(3, "无用", "智多星");
        HeroNode heroNode4 = new HeroNode(4, "林冲", "豹子头");

        SingleLinkedList singleLinkedList = new SingleLinkedList();
        singleLinkedList.add(heroNode1);
        singleLinkedList.add(heroNode2);
        singleLinkedList.add(heroNode3);
        singleLinkedList.add(heroNode4);

        singleLinkedList.list();
        System.out.println("--------------");
//        System.out.println(singleLinkedList.findLastNode(5));
//        singleLinkedList.reverseLinkedList();
//        singleLinkedList.list();


//        System.out.println("-------------");
//
//        System.out.println(singleLinkedList.findByNo(2));

//        System.out.println("-------------");

//        System.out.println(singleLinkedList.delFirst());
//        System.out.println("-------------");
//        singleLinkedList.list();

//        System.out.println("-------------");
//        System.out.println(singleLinkedList.delLast());
//        System.out.println("-------------");
//        singleLinkedList.list();


//        System.out.println("-------------");
//        System.out.println(singleLinkedList.update(2, new HeroNode(1, "宋江1", "JJ1")));
//        System.out.println("-------------");
//        singleLinkedList.list();


   /*     System.out.println("--======");
        singleLinkedList.del(3);
        System.out.println("-----====");
        singleLinkedList.list();*/


    }
}

class SingleLinkedList {
    //初始化头结点
    private HeroNode head = new HeroNode(0, "", "");


    /**
     * 反转链表
     */
    public void reverseLinkedList() {
        if (head.getNext() == null || getLength() == 1) {
            return;
        }
        HeroNode first = head.getNext();
        HeroNode last = head.getNext().getNext();
        while (true) {
            if (last == null) {
                break;
            }
            HeroNode tmp = last;
            last = last.getNext();
            first.setNext(tmp.getNext());
            tmp.setNext(head.getNext());
            head.setNext(tmp);
        }
    }


    /**
     * 获取链表的有效元素个数
     *
     * @return length
     */
    public int getLength() {
        if (head.getNext() == null) {
            return 0;
        }
        int length = 0;
        HeroNode cur = head.getNext();
        while (cur != null) {
            length++;
            cur = cur.getNext();
        }
        System.out.println("有效元素个数为：" + length);
        return length;
    }

    /**
     * 获取倒数第n个元素
     *
     * @param n
     * @return
     */
    public HeroNode findLastNode(int n) {
        int length = getLength();
        if (n <= 0) {
            throw new RuntimeException("n要大于0");
        }
        if (length < n) {
            throw new RuntimeException("链表长度较短");
        }
        int index = 0;
        HeroNode tmp = head.getNext();
        while (index < length - n) {
            index++;
            tmp = tmp.getNext();
        }
        return tmp;
    }


    /**
     * 向链表中添加数据，尾插法
     *
     * @param heroNode
     */
    public void add(HeroNode heroNode) {
        HeroNode tmp = head;
        //遍历链表
        while (true) {
            //找到链表的最后元素
            if (tmp.getNext() == null) {
                break;
            }
            //如果没找到最后一个元素，继续向后找
            tmp = tmp.getNext();
        }
        //当退出while循环时，tmp就指向了链表的最后
        tmp.setNext(heroNode);
    }

    /**
     * 遍历链表数据
     */
    public void list() {
        if (head.getNext() == null) {
            System.out.println("链表为空");
            return;
        }
        HeroNode tmp = head.getNext();
        while (true) {
            //判断是否到链表最后
            if (tmp == null) {
                break;
            }
            System.out.println(tmp.toString());
            tmp = tmp.getNext();
        }

    }

    /*public void del(int no) {
        HeroNode tmp = head;
        boolean flag = false;
        while (true) {
            if (tmp.getNext() == null) {
                break;
            }
            if (tmp.getNext().getNo() == no) {
                flag = true;
                break;
            }
            tmp = tmp.getNext();
        }
        if (flag) {
            tmp.setNext(tmp.getNext().getNext());
        }else {
            System.out.println("没有找到");
        }
    }*/

    /**
     * 删除元素，头删法
     *
     * @return 返回第一个元素
     */
    public HeroNode delFirst() {
        if (head.getNext() == null) {
            throw new RuntimeException("数据为空，不能删除");
        }
        HeroNode first = head.getNext();
        head.setNext(first.getNext());
        return first;
    }

    /**
     * 删除元素，尾删法
     *
     * @return 返回最后一个元素
     */
    public HeroNode delLast() {
        if (head.getNext() == null) {
            throw new RuntimeException("数据为空，不能删除");
        }
        HeroNode last = head;
        HeroNode tmp = head.getNext();
        while (true) {
            if (tmp.getNext() == null) {
                last.setNext(tmp.getNext());
                break;
            }
            last = tmp;
            tmp = tmp.getNext();
        }
        return tmp;
    }

    /**
     * 根据no修改指定节点
     *
     * @param no          HeroNode中的no属性
     * @param newHeroNode 要修改的内容
     * @return true|false
     */
    public boolean update(int no, HeroNode newHeroNode) {
        if (head.getNext() == null) {
            return false;
        }
        HeroNode res = null;
        HeroNode tmp = head.getNext();
        while (true) {
            if (tmp.getNo() == no || tmp.getNext() == null) {
                if (tmp.getNo() == no) {
                    res = tmp;
                }
                break;
            }
            tmp = tmp.getNext();
        }
        if (res == null) {
            System.out.println("没有该no对应的元素，无法修改");
            return false;
        } else {
            tmp.setName(newHeroNode.getName());
            tmp.setNickName(newHeroNode.getNickName());
            return true;
        }
    }

    /**
     * 根据no查找节点
     *
     * @param no
     * @return
     */
    public HeroNode findByNo(int no) {
        if (head.getNext() == null) {
            throw new RuntimeException("数据为空，无法查找");
        }
        HeroNode res = null;
        HeroNode tmp = head.getNext();
        while (true) {
            if (tmp.getNo() == no || tmp.getNext() == null) {
                if (tmp.getNo() == no) {
                    res = tmp;
                }
                break;
            }
            tmp = tmp.getNext();
        }
        if (res == null) {
            System.out.println("找不到对应no的元素");
            return null;
        } else {
            return res;
        }
    }

}

class HeroNode {
    private int no;
    private String name;
    private String nickName;
    private HeroNode next;

    public HeroNode(int no, String name, String nickName) {
        this.no = no;
        this.name = name;
        this.nickName = nickName;
        this.next = null;
    }

    @Override
    public String toString() {
        return "HeroNode{" +
                "no=" + no +
                ", name='" + name + '\'' +
                ", nickName='" + nickName + '\'' +
                '}';
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public HeroNode getNext() {
        return next;
    }

    public void setNext(HeroNode next) {
        this.next = next;
    }
}
