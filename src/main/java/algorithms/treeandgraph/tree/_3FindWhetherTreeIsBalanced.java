package algorithms.treeandgraph.tree;

import algorithms.treeandgraph.tree.baseclasses.BST;
import algorithms.treeandgraph.tree.baseclasses.TreeNode;

/*
Check Balanced:
Implement a function to check if a binary tree is balanced.
For the purpose of this question, a balanced tree is defended to be a tree such that the heights of the two subtrees of any node never different by more than one.

This algorithm doesn't check the tree as per the definition of actual balanced/almost balanced tree.
As per the definition of almost balanced tree, following is not an almost balanced tree.
        5
    3       9
  2        7
This one is

        5
    3       9
  2   7


But this algorithm doesn't take this difference into account because it just checks the height of the tree at each node.

IMPORTANT:
This algorithm is very important to understand runtime complexity of any binary tree algorithm.

Remember
                m(n)
    m(n/2)                  m(n/2)
m(n/4)  m(n/4)          m(n/4)  m(n/4)

This is how tree execution looks like when you use recursive method. When you recurse a method with left and right children of a tree node, basically you are recursing with n/2-n/2 nodes of a tree.

Now,

at each recursion of method 'm', 'gh' method is called. If you see at each level of a tree, 'gh' method visits n nodes.
So, time complexity of this kind of algorithm will be O(n log n). log n is the height of the tree (and so number of levels)

                                           m(n)
                                    gh(n/2) get(n/2)

                m(n/2)                                      m(n/2)
            gh(n/4) get(n/4)                            gh(n/4) get(n/4)

    m(n/4)              m(n/4)                  m(n/4)                  m(n/4)
 gh(n/8) get(n/8)    gh(n/8) get(n/8)        gh(n/8) get(n/8)        gh(n/8) get(n/8)


In below case, every node of m tree calls gh that visits p number of nodes. Unlike to above example, here gh is not reducing the number of nodes to visit to half for next level down.
You can think of like n belongs to tree T1 and p belongs to tree T2. n and p are number of nodes in T1 and T2 respectively. You are trying to find whether T2 is a subtree of T1.
So, when you see this kind of case, then its time complexity is O(np)
e.g. CheckSubTree.java

                                            m(n)
                                    gh(p/2) get(p/2)

                m(n/2)                                      m(n/2)
            gh(p/2) get(p/2)                            gh(p/2) get(p/2)

    m(n/4)              m(n/4)                  m(n/4)                  m(n/4)
 gh(p/2) get(p/2)    gh(p/2) get(p/2)        gh(p/2) get(p/2)        gh(p/2) get(p/2)


Making it simpler

               	 m(n)
    m(n/2)                 	 m(n/2)
m(n/4)  m(n/4)          m(n/4)  m(n/4)

At each node of this tree, nothing major is happening but node passed to the method (e.g. left node/right node), is just visited. It means that at each node of above recursive tree, O(1) operation is happening. So, time complexity will be O(1 * n).
e.g. You can see this example for isBalanced_Better method in this class.


 */

// http://www.programcreek.com/2013/02/leetcode-balanced-binary-tree-java/
// http://www.geeksforgeeks.org/how-to-determine-if-a-binary-tree-is-balanced/
public class _3FindWhetherTreeIsBalanced {

    /*

    This algorithm considers below trees as balanced


             5
        3        9
     2    1    7    6


             5
        3        9
     2    1    7


             5          ----- Surprisingly, this is also considered as balanced because it checks only the height of the tree at each level
        3        9
     2         7


    below trees as unbalanced


             5          --- if you see from the root, height of the tree is 2 on both left and right side. But when you go one level down height of a tree with node 3 is 1 but left of it is smaller than right.
        3        9
          1    7    6


             5
        3        9
     2    1         6


             5
        3
     2    1

     */

    public static void main(String[] args) {
        System.out.println("Testing Balanced Tree..............");

        {
            BST bst = BST.createBST();
            System.out.println("Testing Balanced Tree:" + isBalanced(bst.root));
            System.out.println(count);
            System.out.println("Testing Balanced Tree Better way: " + isBalanced_Better(bst.root));
            System.out.println(cnt);
            System.out.println("Wrong algorithm:" + isTreeAlmostBalanced(bst.root));
        }
        System.out.println("Testing UnBalanced Tree..............");
        {
            BST unBalancedBst = BST.createUnBalancedBST();
            System.out.println("Testing UnBalanced Tree:" + isBalanced(unBalancedBst.root));
            System.out.println("Testing UnBalanced Tree Better way: " + isBalanced_Better(unBalancedBst.root));
            System.out.println("Wrong algorithm:" + isTreeAlmostBalanced(unBalancedBst.root));

            BST anotherUnbalanced = BST.createAnotherUnBalancedBST();
            System.out.println("Testing another UnBalanced Tree: " + isBalanced(anotherUnbalanced.root));
            System.out.println("Testing another UnBalanced Tree Better way: " + isBalanced_Better(anotherUnbalanced.root));
        }

    }


    /*
        Although, this works, its not very efficient.
        On each node, we recurse through its entire subtree.
        This means that getHeight is called repeatedly on the same nodes.
        Time Complexity of this algorithm is O(n log n).

                                                                isBalanced(n)
                            isBalanced(n/2)	 		I	                                        sBalanced(n/2)
                    getHeight(n/4) getHeight(n/4) 	                          	          getHeight(n/4) getHeight(n/4)


                isBalanced(n/4)		        isBalanced(n/4)	                        isBalanced(n/4)		            isBalanced(n/4)
    getHeight(n/8) getHeight(n/8)   getHeight(n/8) getHeight(n/8)             getHeight(n/8) getHeight(n/8)      getHeight(n/8) getHeight(n/8)



    At each level, number of elements are halved and at each level getHeight method is called approx n times (for each node down the tree) and getHeight method visits only 1 node. So, time complexity is O(n log n).

    This is called BRUTE FORCE approach:
    in this approach, we just look at all possible paths. To do this, we traverse to each node.
    At each node, we recursively try all paths downwards, tracking result as we go.

    Brute Force concept is same for finding substring in a string or finding an element in an array etc.


    TIME COMPLEXITY OF THIS ALGORITHM CAN BE IMPROVED.
    see isBalanced_Better method.

    */
    static int count = 0;

    private static boolean isBalanced(TreeNode root) {
        if (root == null) return true;


        int leftSubTreeHeight = getHeight(root.left);
        int rightSubTreeHeight = getHeight(root.right);

        if (Math.abs(leftSubTreeHeight - rightSubTreeHeight) > 1) {
            return false;
        }
        return isBalanced(root.left) && isBalanced(root.right);
    }

    private static int getHeight(TreeNode root) {
        if (root == null) {
            return 0;
        }
        count++;
        int leftSubTreeHeight = getHeight(root.left);
        int rightSubTreeHeight = getHeight(root.right);

        if (leftSubTreeHeight > rightSubTreeHeight) return leftSubTreeHeight + 1;// adding root's height
        else return rightSubTreeHeight + 1;// adding root's height
    }
/*
                                     isBalanced(n)
                                            calls
                                      getHeight(n)
                getHeight(n/2)				                    getHeight(n/2)

         getHeight(n/4)	getHeight(n/4)		            getHeight(n/4)	getHeight(n/4)

    getHeight visits 1 node only on each method call.

    Remember, when each method call visits only 1 node, time complexity is O(n).

 */
    private static boolean isBalanced_Better(TreeNode root) {
        if (getHeight_Better(root) == -1) return false;

        return true;
    }

    static int cnt = 0;

    private static int getHeight_Better(TreeNode root) {
        if (root == null) {
            return 0;
        }
        //cnt++;
        int leftSubTreeHeight = getHeight_Better(root.left);
        if (leftSubTreeHeight == -1) return -1;

        int rightSubTreeHeight = getHeight_Better(root.right);
        if (rightSubTreeHeight == -1) return -1;

        if (leftSubTreeHeight > rightSubTreeHeight) {
            if (leftSubTreeHeight - rightSubTreeHeight > 1) return -1;
            else return leftSubTreeHeight + 1; // adding root's height
        } else {
            if (rightSubTreeHeight - leftSubTreeHeight > 1) return -1;
            else return rightSubTreeHeight + 1;// adding root's height
        }

    }

    /*

    This algorithm will return ALMOST_BALANCED for below tree. Correct answer should be UNBALANCED.

                    5

             3

          2     4

     */
    private static BALANCED_ENUM isTreeAlmostBalanced(TreeNode root) {
        if (root == null || (root.left == null && root.right == null)) return BALANCED_ENUM.TOTALLY_BALANCED;
        if (root.left != null && root.right == null) return BALANCED_ENUM.ALMOST_BALANCED;
        if (root.left == null && root.right != null) return BALANCED_ENUM.UNBALANCED;

        if (root.left != null) {
            BALANCED_ENUM leftTreeBalance = isTreeAlmostBalanced(root.left);
            if (leftTreeBalance == BALANCED_ENUM.TOTALLY_BALANCED) {

                if (root.right != null) {
                    BALANCED_ENUM rightTreeBalance = isTreeAlmostBalanced(root.left);

                    if (rightTreeBalance == BALANCED_ENUM.TOTALLY_BALANCED || rightTreeBalance == BALANCED_ENUM.ALMOST_BALANCED) {
                        return rightTreeBalance;
                    }
                }
            } else if (leftTreeBalance == BALANCED_ENUM.ALMOST_BALANCED) {
                if (root.right == null) {
                    return leftTreeBalance;
                }
            } else {
                return leftTreeBalance;
            }
        }
        return BALANCED_ENUM.TOTALLY_BALANCED;
    }

    private enum BALANCED_ENUM {
        TOTALLY_BALANCED,
        ALMOST_BALANCED,
        UNBALANCED;
    }


}
