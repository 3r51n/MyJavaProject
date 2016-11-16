package algorithms.linkedlistmanipulation;

import java.util.ArrayList;

// p.g. 214 of Cracking Coding Interview book
// sumIteratively two linked lists data
public class SumLists {
    public static void main(String[] args) {
        SinglyLinkedList linkedList1 = SinglyLinkedList.createLinkedListOfIntegers(new ArrayList<Integer>() {{
            add(7);
            add(1);
            add(6);
        }});
        SinglyLinkedList linkedList2 = SinglyLinkedList.createLinkedListOfIntegers(new ArrayList<Integer>() {{
            add(2);
            add(1);
            add(5);
        }});
        System.out.println(reverseAndSum(linkedList1, linkedList2));
    }

    /*
        7->1->6
        +
        2->1->5
        -------
        9->3->1

        you add 6 and 5=11. keep 1 and and pass 1 to next step.
        add 1 and 1 and passed 1 from first step=3
        add 7 and 2=9

        So to achieve this, you need to first reverse both the lists.
        start adding elements of lists from the beginning and do result%10 for each addition and pass remainder to next.


     */
    private static int reverseAndSum(SinglyLinkedList linkedList1, SinglyLinkedList linkedList2) {
        ReverseLinkedList.inPlaceReverse(linkedList1, linkedList1.head);
        ReverseLinkedList.inPlaceReverse(linkedList2, linkedList2.head);
        return Integer.valueOf(sum(linkedList1.head, linkedList2.head, ""));
    }

    private static String sum(Node headNode1, Node headNode2, String finalResult) {
        if(headNode1 != null) {
            // Recursive approach - reduce problem by 1
            // pre-order traversal (visit passed nodes first and make recursive calls to node1.next and node2.next)
                // Step 1 - sumIteratively head nodes first
                // Step 2 - call sumIteratively of head.next nodes recursively
                // Step 3 - add head nodes remainder and and to it.

            // Step 1
            int sumOfHeadNodes = headNode1.data + headNode2.data;

            int addToNextElementSum = 0;
            int lastDigitOfSumOfHeadNodes = sumOfHeadNodes;
            if(headNode1.next != null && sumOfHeadNodes >= 10) {
                lastDigitOfSumOfHeadNodes = sumOfHeadNodes % 10; // e.g if sumOfHeadNodes=15 then lastDigitOfSumOfHeadNodes=5
                addToNextElementSum = 1;
            }

            // Step 2
            String sum = sum(headNode1.next, headNode2.next, finalResult);
            if(sum.trim().length() == 0) {
                sum = "0";
            }

            // Step 3
            finalResult = finalResult +  (""+(addToNextElementSum + Integer.valueOf(sum))) + (""+lastDigitOfSumOfHeadNodes);
        }

        return finalResult;
    }
}