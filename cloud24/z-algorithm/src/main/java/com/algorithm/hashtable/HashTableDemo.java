package com.algorithm.hashtable;

import java.util.Scanner;

/**
 * @author QRH
 * @date 2024/10/28 15:00
 * @description 哈希表
 */
public class HashTableDemo {
    public static void main(String[] args) {
        HashTab hashTab = new HashTab(7);
        String key = "";
        Scanner scanner = new Scanner(System.in);
        boolean flag = true;
        while (flag) {
            System.out.println("a:添加雇员");
            System.out.println("l:显示雇员");
            System.out.println("f:根据id查找雇员");
            System.out.println("d:根据id查删除雇员");
            System.out.println("e:退出系统");

            key = scanner.next();
            switch (key) {
                case "a":
                    System.out.println("输入雇员id：");
                    int id = scanner.nextInt();
                    System.out.println("输入雇员姓名：");
                    String name = scanner.next();
                    Emp emp = new Emp(id, name);
                    hashTab.add(emp);
                    break;
                case "l":
                    hashTab.list();
                    break;
                case "f":
                    System.out.println("输入雇员id：");
                    hashTab.findEmpById(scanner.nextInt());
                    break;
                case "e":
                    scanner.close();
                    flag = false;
                    break;
            }
        }

    }

}

class HashTab {
    private EmpLinkedList[] empLinkedList;
    private int size;

    public HashTab(int size) {
        this.size = size;
        empLinkedList = new EmpLinkedList[size];
        for (int i = 0; i < empLinkedList.length; i++) {
            empLinkedList[i] = new EmpLinkedList();
        }
    }

    /**
     * 添加
     *
     * @param emp
     */
    public void add(Emp emp) {
        int empLinkedNo = hashFun(emp.id);
        empLinkedList[empLinkedNo].add(emp);
    }

    /**
     * 根据id查找雇员
     *
     * @param id 雇员id
     * @return
     */
    public void findEmpById(int id) {
        int empLinkedNo = hashFun(id);
        Emp emp = empLinkedList[empLinkedNo].findEmpById(id);
        if (emp != null) {
            System.out.println("在第" + (empLinkedNo + 1) + "号链表找到雇员：" + emp.toString());
        } else {
            System.out.println("没有找到该雇员");
        }

    }


    /**
     * 遍历
     */
    public void list() {
        for (int i = 0; i < size; i++) {
            empLinkedList[i].list(i);
        }
    }

    /**
     * 取散列值
     *
     * @param id 雇员id
     * @return 根据id生成的hash、值
     */
    public int hashFun(int id) {
        return id % size;
    }
}

class EmpLinkedList {
    private Emp head;


    /**
     * 尾插法，不带头结点，添加结点
     *
     * @param emp
     */
    public void add(Emp emp) {
        if (head == null) {
            head = emp;
            return;
        }
        Emp curEmp = head;
        while (true) {
            if (curEmp.next == null) {
                break;
            }
            curEmp = curEmp.next;
        }
        curEmp.next = emp;
    }

    /**
     * 根据id查找雇员
     *
     * @param id 雇员id
     * @return
     */
    public Emp findEmpById(int id) {
        if (head == null) {
            System.out.println("链表为空");
            return null;
        }
        Emp cur = head;
        while (true) {
            if (cur.id == id) {
                break;
            }
            if (cur.next == null) {
                cur = null;
                break;
            }
            cur = cur.next;
        }
        return cur;
    }

    /**
     * 遍历链表
     *
     * @param no 链表id
     */
    public void list(int no) {
        if (head == null) {
            System.out.println((no + 1) + "号链表为空");
            return;
        }
        Emp curEmp = head;
        while (true) {
            System.out.print((no + 1) + "号链表：" + curEmp.toString() + " => ");
            if (curEmp.next == null) {
                break;
            }
            curEmp = curEmp.next;
        }
        System.out.println();
    }

}

class Emp {
    public int id;
    public String name;
    public Emp next;

    public Emp(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Emp{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
