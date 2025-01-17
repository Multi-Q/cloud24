package com.algorithm.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * @author QRH
 * @date 2024/11/11 14:47
 * @description 二叉排序树
 */
public class BinarySortTreeDemo {
    public static void main(String[] args) {
        int[] arr = {7, 3, 10, 12, 5, 1, 9};

        BinarySortTree bs = new BinarySortTree();

        for (int a : arr) {
            bs.add(new Node3(a));
        }

        bs.infixOrder();

    }


}

class BinarySortTree {
    private Node3 root;

    /**
     * 寻找待删除的节点
     *
     * @param value
     * @return
     */
    public Node3 search(int value) {
        if (root == null) {
            return null;
        } else {
            return root.search(value);
        }
    }

    /**
     * 寻找要删除的结点的父节点
     *
     * @param value
     * @return 父节点
     */
    public Node3 searchParent(int value) {
        if (root == null) {
            return null;
        } else {
            return root.searchParent(value);
        }
    }

    /**
     * @param node 传入的节点
     * @return 返回一node为根节点的二叉排序树的最小节点的值
     */
    public int delRightTreeMin(Node3 node) {
        Node3 target = node;
        //循环找到左节点，就会找到最小值
        while (target.left != null) {
            target = target.left;
        }
        //target找到了最小节点
        this.delNode(target.value);
        return target.value;
    }

    /**
     * 删除节点
     *
     * @param value 要删除节点的值
     */
    public void delNode(int value) {
        if (root == null) {
            return;
        } else {
            Node3 targetNode = this.search(value);//先去找到要删除的结点
            if (targetNode == null) return;//没有找到要删除的节点

            //如果这棵树只有一个节点
            if (root.left == null && root.right == null) {
                root = null;
                return;
            }

            //找到targetNode的父节点
            Node3 parent = this.searchParent(value);
            //正是要删除的节点
            if (targetNode.left == null && targetNode.right == null) {//叶子结点
                if (parent.left != null && parent.left.value == value) {
                    parent.left = null;
                } else if (parent.right != null && parent.right.value == value) {
                    parent.right = null;
                }
            } else if (targetNode.left != null && targetNode.right != null) {//删除有两颗子树的节点
                int minVal = this.delRightTreeMin(targetNode.right);
                targetNode.value = minVal;
            } else {//删除只有一颗子树的节点
                if (targetNode.left != null) {//如果要删除的节点有左子节点
                    //如果targetNode是parent的左子节点
                    if (parent != null) {
                        if (parent.left.value == value) {
                            parent.left = targetNode.left;
                        } else {//targetNode是parent的右子节点
                            parent.right = targetNode.left;
                        }
                    } else {
                        root = targetNode.left;
                    }
                } else { //如果要删除的节点有右子节点
                    //如果targetNode是parent的左子节点
                    if (parent != null) {
                        if (parent.left.value == value) {
                            parent.left = targetNode.right;
                        } else {//targetNode是parent的右子节点
                            parent.right = targetNode.right;
                        }
                    } else {
                        root = targetNode.right;
                    }

                }
            }


        }
    }

    public void add(Node3 node) {
        if (root == null) root = node;
        else root.add(node);
    }

    public void infixOrder() {
        if (root == null) return;
        root.infixOrder();
    }

}

class Node3 {
    int value;
    Node3 left;
    Node3 right;

    Node3() {
    }

    Node3(int value) {
        this.value = value;
    }

    /**
     * 寻找要删除的节点
     *
     * @param value 要删除节点的值
     * @return 要删除的结点
     */
    public Node3 search(int value) {
        if (value == this.value) { //找到就是该结点
            return this;
        } else if (value < this.value) {//如果查找的值小于当前结点，向左子树递归查找
            //如果左子结点为空,返回null
            return this.left == null ? null : this.left.search(value);
        } else { //如果查找的值不小于当前结点，向右子树递归查找
            return this.right == null ? null : this.right.search(value);
        }
    }


    /**
     * 寻找父节点
     *
     * @param value 要删除的结点的值
     * @return 父节点
     */
    public Node3 searchParent(int value) {
        if ((this.left != null && this.left.value == value) ||
                (this.right != null && this.right.value == value)) {
            return this;
        } else {
            //如果找到的值小于当前节点的值，并且当前节点的左子节点不为空
            if (value < this.value && this.left != null) {
                return this.left.searchParent(value);//向左子树递归查找
            } else if (value >= this.value && this.right != null) {
                return this.right.searchParent(value);//向右子树递归查找
            } else {
                return null;//没有找到父节点
            }
        }
    }

    public void add(Node3 node) {
        if (node == null) return;
        if (node.value < this.value) {
            if (this.left == null) {
                this.left = node;
            } else {
                this.left.add(node);
            }
        } else {
            if (this.right == null) {
                this.right = node;
            } else {
                this.right.add(node);
            }
        }
    }

    public void infixOrder() {
        if (this.left != null) {
            this.left.infixOrder();
        }
        System.out.print(this.value + "  ");
        if (this.right != null) {
            this.right.infixOrder();
        }
    }

    @Override
    public String toString() {
        return "Node3{" +
                "value=" + value +
                '}';
    }
}
