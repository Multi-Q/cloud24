package com.algorithm.tree;

/**
 * @author QRH
 * @date 2024/11/24 12:51
 * @description TODO
 */
public class AVLTreeDemo {
    public static void main(String[] args) {
        int[] arr = {4, 3, 6, 5, 7, 8};

        AVLTree avlTree = new AVLTree();
        for (int i = 0; i < arr.length; i++) {
            avlTree.add(new Node4(arr[i]));
        }
        //中序遍历
        avlTree.infixOrder();

        System.out.println("\n没平衡之前，根节点的高度为：" + avlTree.getRoot().height());
        System.out.println("没平衡之前，根节点左子树的高度为：" + avlTree.getRoot().leftHeight());
        System.out.println("没平衡之前，根节点右子树的高度为：" + avlTree.getRoot().rightHeight());

    }
}

class AVLTree {
    private Node4 root;

    /**
     * 获取根节点
     *
     * @return
     */
    public Node4 getRoot() {
        if (root != null) {
            return root;
        } else {
            throw new NullPointerException("根节点为空！");
        }
    }

    /**
     * 寻找待删除的节点
     *
     * @param value
     * @return
     */
    public Node4 search(int value) {
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
    public Node4 searchParent(int value) {
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
    public int delRightTreeMin(Node4 node) {
        Node4 target = node;
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
            Node4 targetNode = this.search(value);//先去找到要删除的结点
            if (targetNode == null) return;//没有找到要删除的节点

            //如果这棵树只有一个节点
            if (root.left == null && root.right == null) {
                root = null;
                return;
            }

            //找到targetNode的父节点
            Node4 parent = this.searchParent(value);
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

    public void add(Node4 node) {
        if (root == null) root = node;
        else root.add(node);
    }

    public void infixOrder() {
        if (root == null) return;
        root.infixOrder();
    }


}


class Node4 {
    int value;
    Node4 left;
    Node4 right;

    Node4() {
    }

    Node4(int value) {
        this.value = value;
    }


    /**
     * 左旋转
     */
    public void leftRotate() {
        //创建新的节点，以当前根节点的值
        Node4 newNode = new Node4(value);
        //把新的节点的左子树设置成当前节点的左子树
        newNode.left = left;
        //把新的节点的右子树设置成当前节点的右子树的左子树
        newNode.left = right.left;
        //把当前节点的值替换成右子节点的值
        value = right.value;
        //把当前结点的右子树设置成右子树的右子树
        right = right.right;
        //把当前节点的左子结点设置成新的结点
        left = newNode;

    }

    /**
     * 右旋转
     */
    public void rightRotate() {
        Node4 newNode = new Node4(value);
        newNode.right = right;
        newNode.left = left.right;
        value = left.value;
        left = left.left;
        right = newNode;
    }


    /**
     * 左子树的高度
     *
     * @return 左子树的高度
     */
    public int leftHeight() {
        if (left == null) {
            return 0;
        }
        return left.height();
    }

    /**
     * 右子树的高度
     *
     * @return 右子树的高度
     */
    public int rightHeight() {
        if (right == null) {
            return 0;
        }
        return right.height();
    }

    /**
     * 返回当前节点的高度，该节点为根节点的高度
     *
     * @return
     */
    public int height() {
        return Math.max(left == null ? 0 : left.height(), right == null ? 0 : right.height()) + 1;
    }

    /**
     * 寻找要删除的节点
     *
     * @param value 要删除节点的值
     * @return 要删除的结点
     */
    public Node4 search(int value) {
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
    public Node4 searchParent(int value) {
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

    public void add(Node4 node) {
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
        //当添加完一个节点后，如果右子树的高度-左子树的高度>1,左旋转
        if (rightHeight() - leftHeight() > 1) {
            //如果他的右子树的左子树的高度大于他的右子树的高度
            //先对右子树进行右旋转，再对当前节点进行左旋转
            if (right != null && right.leftHeight() > right.rightHeight()) {
                right.rightRotate();
                leftRotate();
            } else {
                leftRotate();
            }
            return;//必须要
        }
        //当添加完一个节点后，如果左子树的高度->右子树的高度1,右旋转
        if (leftHeight() - rightHeight() > 1) {
            //如果它的左子树的右子树的高度大于他的左子树的高度
            if (left != null && left.rightHeight() > left.leftHeight()) {
                //先对当前节点的左节点 左旋转
                left.leftRotate();
                rightRotate();
            } else {
                //直接进行右旋转
                rightRotate();
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
        return "Node4{" +
                "value=" + value +
                '}';
    }
}
