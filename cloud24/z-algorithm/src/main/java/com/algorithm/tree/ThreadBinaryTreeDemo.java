package com.algorithm.tree;

/**
 * @author QRH
 * @date 2024/10/29 14:00
 * @description 线索化二叉树
 */
public class ThreadBinaryTreeDemo {
    public static void main(String[] args) {
        //中序遍历结果：8, 3, 10, 1, 14, 6
        HeroNode2 root = new HeroNode2(1, "tom");
        HeroNode2 node2 = new HeroNode2(3, "jack");
        HeroNode2 node3 = new HeroNode2(6, "smith");
        HeroNode2 node4 = new HeroNode2(8, "mary");
        HeroNode2 node5 = new HeroNode2(10, "king");
        HeroNode2 node6 = new HeroNode2(14, "dim");

        //手动创建二叉树
        root.setLeft(node2);
        root.setRight(node3);
        node2.setLeft(node4);
        node2.setRight(node5);
        node3.setLeft(node6);

        ThreadBinaryTree threadBinaryTree = new ThreadBinaryTree();
        threadBinaryTree.setRoot(root);

//        System.out.println("===========前序遍历===========");
//        threadBinaryTree.threadNodeByPreOrder(root);
//
//        HeroNode2 node5Left1 = node5.getLeft();
//        HeroNode2 node5Right1 = node5.getRight();
//        System.out.println("node5的前驱结点为：" + node5Left1.toString());
//        System.out.println("node5的后继结点为：" + node5Right1.toString());
//        threadBinaryTree.threadListByPreOrder(); //1 3 8 10 6 14

        //////////////////////////////////////////////////////////////////
//        System.out.println("===========中序遍历===========");
//        threadBinaryTree.threadNodeByInfixOrder(root);
//
//        HeroNode2 node5Left = node5.getLeft();
//        HeroNode2 node5Right = node5.getRight();
//        System.out.println("node5的前驱结点为：" + node5Left.toString());
//        System.out.println("node5的后继结点为：" + node5Right.toString());
//
//        threadBinaryTree.threadListByInfixOrder(); //8, 3, 10, 1, 14, 6

        System.out.println("===========后序遍历===========");
        threadBinaryTree.threadNodeByPostOrder(root);

        HeroNode2 node5Left1 = node5.getLeft();
        HeroNode2 node5Right1 = node5.getRight();
        System.out.println("node5的前驱结点为：" + node5Left1.toString());
        System.out.println("node5的后继结点为：" + node5Right1.toString());
//        threadBinaryTree.threadListByPostOrder(); //8 10 3 14 6 1

    }
}

class ThreadBinaryTree {
    private HeroNode2 root;
    private HeroNode2 pre = null;//在递归线索化是，pre总是保存当前节点的前驱结点

    public void setRoot(HeroNode2 root) {
        this.root = root;
    }


    /**
     * 前序遍历-线索化二叉树
     *
     * @param node
     */
    public void threadNodeByPreOrder(HeroNode2 node) {
        //如果 node==null, 不能线索化
        if (node == null) {
            return;
        }
        //处理当前节点
        if (node.getLeft() == null) {
            node.setLeft(pre);
            node.setLeftType(1);
        }
        if (pre != null && pre.getRight() == null) {
            pre.setRight(node);
            pre.setRightType(1);
        }
        //更新前一个访问节点
        pre = node;

        //线索化左子树
        if (node.getLeftType() != 1) {
            threadNodeByPreOrder(node.getLeft());
        }
        //线索化右子树
        if (node.getRightType() != 1) {
            threadNodeByPreOrder(node.getRight());
        }

    }

    /**
     * 中序遍历-线索化二叉树
     *
     * @param node
     */
    public void threadNodeByInfixOrder(HeroNode2 node) {
        //如果 node==null, 不能线索化
        if (node == null) {
            return;
        }
        threadNodeByInfixOrder(node.getLeft());
        //(二)线索化当前结点[有难度]
        //处理当前结点的前驱结点
        if (node.getLeft() == null) {
            //让当前结点的左指针指向前驱结点
            node.setLeft(pre);
            //修改当前结点的左指针的类型,指向前驱结点
            node.setLeftType(1);
        }
        //处理后继结点
        if (pre != null && pre.getRight() == null) {
            //让前驱结点的右指针指向当前结点
            pre.setRight(node);
            //修改前驱结点的右指针类型
            pre.setRightType(1);
        }
        //!!! 每处理一个结点后，让当前结点是下一个结点的前驱结点
        pre = node;
        //(三)在线索化右子树
        threadNodeByInfixOrder(node.getRight());
    }

    /**
     * 后序遍历-线索化二叉树
     *
     * @param node
     */
    public void threadNodeByPostOrder(HeroNode2 node) {
        if (node == null) {
            return;
        }

        //线索化左子树
        if (node.getLeftType() != 1) {
            threadNodeByPostOrder(node.getLeft());
        }
        //线索化右子树
        if (node.getRightType() != 1) {
            threadNodeByPostOrder(node.getRight());
        }

        //处理当前节点
        if (node.getLeft() == null) {
            node.setLeft(pre);
            node.setLeftType(1);
        }
        if (pre != null && pre.getRight() == null) {
            pre.setRight(node);
            pre.setRightType(1);
        }
        pre = node;
    }

    /* ************************************************************  */

    /**
     * 前序遍历-遍历线索化二叉树
     */
    public void threadListByPreOrder() {
        //定义一个变量，存储当前遍历的节点，从root开始
        HeroNode2 node = root;
        while (node != null) {
            //当node不是叶子结点时，进入while
            while (node.getLeftType() == 0) {
                System.out.println(node);
                node = node.getLeft();
            }

            //当node.getLeft()是叶子结点时，输出这个叶子结点
            System.out.println(node);

            //当node有后继结点时，进入while，当最后一个也直接点没有后继节点时，退出循环
            while (node.getRightType() == 1) {
                //获取当前节点的后继节点
                node = node.getRight();
                System.out.println(node);
            }

            //最后的叶子结点getRight()==null，大循环结束
            node = node.getRight();
        }
    }

    /**
     * 中序遍历-遍历线索化二叉树
     */
    public void threadListByInfixOrder() {
        //定义一个变量，存储当前遍历的节点，从root开始
        HeroNode2 node = root;
        while (node != null) {
            //循环找到leftType==1的节点，第一个找到就是8节点
            //后面随着遍历而变化，因为当leftType==1时，说明该节点是按照线索化
            //处理后面有效结点
            while (node.getLeftType() == 0) {
                node = node.getLeft();
            }
            //打印当前节点
            System.out.println(node);
            //吐过当前节点的有指针指向的就是后继节点，就一直输出
            while (node.getRightType() == 1) {
                //获取当前节点的后继节点
                node = node.getRight();
                System.out.println(node);
            }
            //替换这个遍历的结点
            node = node.getRight();
        }
    }

    /**
     * 后序遍历-遍历线索化二叉树
     */
    public void threadListByPostOrder() {
        //写不出来。。。。。
    }
}


class HeroNode2 {
    private int no;
    private String name;
    private HeroNode2 left;
    private HeroNode2 right;

    //如果leftType==0表示指向左子树，1则为指向前驱结点
    private int leftType;
    //如果rightType==0表示指向后子树，1则为指向后继结点
    private int rightType;

    public HeroNode2(int no, String name) {
        this.no = no;
        this.name = name;
    }


    @Override
    public String toString() {
        return "HeroNode2{" +
                "no=" + no +
                ", name='" + name + '\'' +
                '}';
    }

    public int getLeftType() {
        return leftType;
    }

    public void setLeftType(int leftType) {
        this.leftType = leftType;
    }

    public int getRightType() {
        return rightType;
    }

    public void setRightType(int rightType) {
        this.rightType = rightType;
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

    public HeroNode2 getLeft() {
        return left;
    }

    public void setLeft(HeroNode2 left) {
        this.left = left;
    }

    public HeroNode2 getRight() {
        return right;
    }

    public void setRight(HeroNode2 right) {
        this.right = right;
    }
}