package com.algorithm.tree;

/**
 * @author QRH
 * @date 2024/10/28 22:02
 * @description 二叉树
 */
public class BinaryTreeDemo {
    public static void main(String[] args) {
        BinaryTree binaryTree = new BinaryTree();//空的二叉树
        HeroNode root=new HeroNode(1,"宋江");
        HeroNode node2=new HeroNode(2,"无用");
        HeroNode node3=new HeroNode(3,"卢俊义");
        HeroNode node4=new HeroNode(4,"林冲");
        HeroNode node5=new HeroNode(5,"关胜");

        //手动创建二叉树
        root.setLeft(node2);
        root.setRight(node3);
        node3.setRight(node4);
        node3.setLeft(node5);

        binaryTree.setRoot(root);
        /////////////////////////////////////////////
        //前序遍历
//        System.out.println("===========前序遍历==================");
//        binaryTree.preOrder();
//        System.out.println("===========中序遍历==================");
//        binaryTree.infixOrder();
//        System.out.println("===========后序遍历==================");
//        binaryTree.postOrder();

        binaryTree.findNodeByPreOrder(5);

    }
}

class BinaryTree {
    private HeroNode root;

    public void setRoot(HeroNode root) {
        this.root = root;
    }

    /**
     * 前序遍历
     */
    public void preOrder() {
        if (this.root != null) {
            this.root.preOrder();
        } else {
            System.out.println("二叉树为空，无法遍历");
        }
    }

    /**
     * 中序遍历
     */
    public void infixOrder() {
        if (this.root != null) {
            this.root.infixOrder();
        } else {
            System.out.println("二叉树为空，无法遍历");
        }
    }

    /**
     * 后序遍历
     */
    public void postOrder() {
        if (this.root != null) {
            this.root.postOrder();
        } else {
            System.out.println("二叉树为空，无法遍历");
        }
    }

    public void findNodeByPreOrder(int no){
        if(this.root.getNo()==no){
            System.out.println(this.toString());
            return;
        }else{
            this.root.preOrder();
        }
    }

}

class HeroNode {
    private int no;
    private String name;
    private HeroNode left;
    private HeroNode right;

    public HeroNode(int no,String name){
        this.no=no;
        this.name=name;
    }

    /**
     * 前序遍历
     */
    public void preOrder() {
        System.out.println(this);//先输出父节点
        //递归向左子树前序遍历
        if (this.left != null) {
            this.left.preOrder();
        }
        //递归向右前序遍历
        if (this.right != null) {
            this.right.preOrder();
        }
    }

    /**
     * 中序遍历
     */
    public void infixOrder() {
        if (this.left != null) {
            this.left.infixOrder();
        }
        System.out.println(this);
        if (this.right != null) {
            this.right.infixOrder();
        }
    }

    /**
     * 后序遍历
     */
    public void postOrder() {
        if (this.left != null) {
            this.left.postOrder();
        }
        if (this.right != null) {
            this.right.postOrder();
        }
        System.out.println(this);
    }


    @Override
    public String toString() {
        return "HeroNode{" +
                "no=" + no +
                ", name='" + name + '\'' +
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

    public HeroNode getLeft() {
        return left;
    }

    public void setLeft(HeroNode left) {
        this.left = left;
    }

    public HeroNode getRight() {
        return right;
    }

    public void setRight(HeroNode right) {
        this.right = right;
    }
}