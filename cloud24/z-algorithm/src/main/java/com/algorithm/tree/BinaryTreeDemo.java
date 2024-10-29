package com.algorithm.tree;

/**
 * @author QRH
 * @date 2024/10/28 22:02
 * @description 二叉树
 */
public class BinaryTreeDemo {
    public static void main(String[] args) {
        BinaryTree binaryTree = new BinaryTree();//空的二叉树
        HeroNode root = new HeroNode(1, "宋江");
        HeroNode node2 = new HeroNode(2, "无用");
        HeroNode node3 = new HeroNode(3, "卢俊义");
        HeroNode node4 = new HeroNode(4, "林冲");
        HeroNode node5 = new HeroNode(5, "关胜");

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

//        binaryTree.findNodeByPreOrder(5);

//        binaryTree.findNodeByInfixOrder(3);
        binaryTree.findNodeByPostOrder(3);

    }
}

class BinaryTree {
    private HeroNode root;


    public void setRoot(HeroNode root) {
        this.root = root;
    }

    /**
     * 删除节点
     * @param no
     */
    public void delNode(int no){
        if (this.root!=null){
            if (this.root.getNo()==no){
                this.root=null;
            }else{
                this.root.delNode(no);
            }
        }else{
            System.out.println("树空，无法删除！");
        }

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

    /**
     * 前序遍历查找指定no
     *
     * @param no 待查找的节点的no
     * @return 对应节点的数据
     */
    public void findNodeByPreOrder(int no) {
        HeroNode nodeByPreOrder = this.root.findNodeByPreOrder(no);
        System.out.println(nodeByPreOrder == null ? null : nodeByPreOrder.toString());
    }

    /**
     * 中序遍历查找指定no
     *
     * @param no 待查找的节点的no
     * @return 对应节点的数据
     */
    public void findNodeByInfixOrder(int no) {
        HeroNode nodeByPreOrder = this.root.findNodeByInfixOrder(no);
        System.out.println(nodeByPreOrder == null ? null : nodeByPreOrder.toString());
    }

    /**
     * 后序遍历查找指定no
     *
     * @param no 待查找的节点的no
     * @return 对应节点的数据
     */
    public void findNodeByPostOrder(int no) {
        HeroNode nodeByPreOrder = this.root.findNodeByPostOrder(no);
        System.out.println(nodeByPreOrder == null ? null : nodeByPreOrder.toString());
    }

}

class HeroNode {
    private int no;
    private String name;
    private HeroNode left;
    private HeroNode right;

    public HeroNode(int no, String name) {
        this.no = no;
        this.name = name;
    }

    /**
     * 删除节点
     * @param no
     */
    public void delNode(int no){
        if (this.left!=null&&this.getNo()==no){
            this.left=null;
            return ;
        }
        if (this.right!=null&&this.getNo()==no){
            this.right=null;
            return ;
        }
        if (this.left!=null){
            this.left.delNode(no);
        }
        if (this.right!=null){
            this.right.delNode(no);
        }

    }




    /**
     * 前序遍历查找指定no的节点
     *
     * @param no 待查找的节点的no
     * @return 对应节点的数据
     */
    public HeroNode findNodeByPreOrder(int no) {
        if (this.getNo() == no) {
            return this;
        }
        HeroNode resNode = null;
        if (this.left != null) {
            resNode = this.left.findNodeByPreOrder(no);
        }
        if (resNode != null) {
            return resNode;
        }
        if (this.right != null) {
            resNode = this.right.findNodeByPreOrder(no);
        }
        return resNode;
    }

    /**
     * 中序遍历查找指定no
     *
     * @param no 待查找的节点的no
     * @return 对应节点的数据
     */
    public HeroNode findNodeByInfixOrder(int no) {
        HeroNode resNode = null;
        if (this.left != null) {
            resNode = this.left.findNodeByInfixOrder(no);
        }
        if (resNode != null) {
            return resNode;
        }
        if (this.getNo() == no) {
            return this;
        }
        if (this.right != null) {
            resNode = this.right.findNodeByInfixOrder(no);
        }
        return resNode;
    }

    /**
     * 后序遍历查找指定no
     *
     * @param no 待查找的节点的no
     * @return 对应节点的数据
     */
    public HeroNode findNodeByPostOrder(int no) {
        HeroNode resNode = null;
        if (this.left != null) {
            resNode = this.left.findNodeByPostOrder(no);
        }
        if (resNode != null) {
            return resNode;
        }
        if (this.right != null) {
            resNode = this.right.findNodeByPostOrder(no);
        }
        if (resNode != null) {
            return resNode;
        }
        if (this.getNo() == no) {
            return this;
        }
        return null;
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