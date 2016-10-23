package algorithms.treemanipulations;

import algorithms.treemanipulations.baseclasses.BST;
import algorithms.treemanipulations.baseclasses.TreeNode;

/*

    Given an array that contains numbers in ascending order, build a binary search tree with minimal height.

    // http://www.programcreek.com/2013/01/leetcode-convert-sorted-array-to-binary-search-tree-java/

    Create Minimal BST from an Array (p.g. 242 of Cracking Coding Interview Book)
    --------------------------------
    To convert an array into tree, imagine that you need to pull some array element up so that it becomes a root of a tree.
    If you pull first or last element up, then it may not result in a BST with minimal height.
    To create BST with minimal height, you need to pull a middle element to make it a root node and make left half of an array as left child tree and right half of an array as a right child tree.


    Here think of like you want to make
    - middle element of an array as a Parent Node of a BST
    - left half of an array as a Left Child Tree of that Parent Node
    - right half of an array as a Right Child Tree of that Parent Node


    Recursion Technique - Minimize the problem by one.
    -------------------

    - Reversing a linked nilList (ReverseLinkedList.java) was a perfect example of how to use Recursive Technique by considering first node of linkedlist as a helper node for Recursive Technique (minimizing problem by one). In this case you could pass the first node to a recursive method.
    - Merge Sort (Sorting.java), Creating Minimal BST (CreateMinimalBST.java) and Binary Search Algorithm (BinarySearch.java) are the perfect example how to use Recursive Technique (minimizing problem by one)by extracting middle element of an array.
      When you need to use array's in-between element as a helper element for 'minimizing a problem by one' technique, then it is always advisable to pass original array, start and end positions of original array to a recursive method and then retrieving that in between position inside the recursive method.
    - BST.java's get() method is an example of how to stop pushing method calls of other side of the tree node when match is found on one side. It shows the use of temporary variable 'found***' to stop pushing successive method calls after match is found by some method call.
      This is also an example of not combining extracted node (root - that is used for minimizing problem by one) with recursive method calls result.

    To make your life easier for recursion,
        If you are working with Array, always pass start and end. e.g. Sorting.java(mergeSort), FindSubsetsOfASet.java
        If you are working with binary tree, always pass root node. e.g. Any tree related algorithm (CreateMirrorImageOfATree.java)
        If you are working with linkedlist, always pass head node.  e.g. ReverseLinkedList.java, CreateMinimalBST.java

    Recursive method's return value is determined by exit condition.
    If exit condition is returning null, make sure you check for null before you deleteRootAndMergeItsLeftAndRight recursive call's result to root element. See CreateMinimalBST.java

    If recursive method is not returning anything, then its result has to be collected in a parameter passed to a method (e.g. outerList in FindSubsetsOfASet.java)

 */
public class CreateMinimalBST {
    public static void main(String[] args) {

        System.out.println("------------- Creating BST from unsorted array -------------- ");
        {
            // As it is unsorted BST, it will not create BST with minimum height and it will be unbalanced tree.
            // To create almost balanced tree with minimum height, you need sorted array.
            int[] someData = {1, 5, 3, 15, 6, 2, 7}; // unsorted array
            BST bst = new BST();
            for (int d : someData) {
                bst.put(d);
            }

            System.out.println();
            System.out.println("Created BST:");
            bst.printPreety();
            // O/P:
            /*
            	 1
				        5
			        3		15
		    	 2        6
		    	            7
             */

            System.out.println();
            System.out.println("InOrder Traversal");
            bst.inOrderTraversal(bst.root);// 1, 2, 3, 5, 6, 7, 15

            System.out.println();
            System.out.println("PreOrder Traversal");
            bst.preOrderTraversal(bst.root);// 1, 5, 3, 2, 15, 6, 7

            System.out.println();
            System.out.println("PostOrder Traversal");
            bst.postOrderTraversal(bst.root);// 2, 3, 7, 6, 15, 5, 1

            System.out.println();
            System.out.println("Picking middle element of an array as a root: This WILL NOT create a symmetric Binary Tree(BST)");
            BST resultingBST = createMinimalBSTAnotherWay(someData, 0 , someData.length - 1);
            resultingBST.printPreety();
            /*
                 15
              5     2
             1 3   6 7
             */

        }

        System.out.println();
        System.out.println("--------- Trying Sorted Array ----------");
        System.out.println();

        {
            int[] data = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};

            {
                BST bst = new BST();
                bst.root = createMinimalBST(data);

                System.out.println("Created BST:]n");
                bst.printPreety();
                System.out.println("\nInOrder Traversal:");
                bst.inOrderTraversal(bst.root);// In-Order traversal converts a tree back to original array - O/P: 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15
            }
            System.out.println();
            System.out.println();
            System.out.println();
            {
                BST bst = createMinimalBSTAnotherWay(data, 0, data.length - 1);

                System.out.println("Created BST Another Way:\n");
                bst.printPreety();
                System.out.println("\nInOrder Traversal:");
                bst.inOrderTraversal(bst.root);// In-Order traversal converts a tree back to original array - O/P: 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15
            }
        }


    }

    // p.g. 242 of Cracking Coding Interview Book.
    static TreeNode createMinimalBST(int array[]) {
        return createMinimalBST(array, 0, array.length - 1);
    }

    private static TreeNode createMinimalBST(int[] array, int start, int end) {
        if (end < start) {
            return null;
        }
        int mid = (end + start) / 2;

        TreeNode parentNode = new TreeNode(array[mid]);

        TreeNode leftNodeOfParentNode = createMinimalBST(array, start, mid - 1);
        TreeNode rightNodeOfParentNode = createMinimalBST(array, mid + 1, end);

        parentNode.left = leftNodeOfParentNode;
        parentNode.right = rightNodeOfParentNode;

        return parentNode;
    }

    private static BST createMinimalBSTAnotherWay(int[] array, int start, int end) { // Whenever you use array and it needs to be split then remember pass start and end of an array as method parameters

        if (array == null || array.length == 0) {
            return null;
        }

        if (end < start) { // Important exit condition -  remember to check end and start related exit condition when you pass them as parameters
            return null;
        }

        BST bst = new BST();
        int mid = (end + start) / 2;// or (end-start)/2 + start
        bst.root = new TreeNode(array[mid]);

        BST leftBST = createMinimalBSTAnotherWay(array, start, mid - 1); // if you see exit condition, then it can return null, so it means leftBST can be assigned null. So when you need to check for null when you attach it to bst.root.
        BST rightBST = createMinimalBSTAnotherWay(array, mid + 1, end); // if you see exit condition, then it can return null, so it means rightBST can be assigned null. So when you need to check for null when you attach it to bst.root.


        if (leftBST != null) // as leftBST can be assigned null due to exit conditions returning null, you need to check null
            bst.root.left = leftBST.root;
        if (rightBST != null) // as rightBST can be assigned null due to exit conditions returning null, you need to check null
            bst.root.right = rightBST.root;

        return bst;
    }
}
