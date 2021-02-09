package Codility.lession2;


import java.util.ArrayDeque;
import java.util.Deque;

public class CyclicRotation {
    public static final String IMPOSSIBLE = "IMPOSSIBLE";
    public static final String DOT = ".";

    public static void main(String[] args) {
        int[] A = {3, 8, 9, 7, 6} ;
        int K = 3;
        new Solution().solution(A,K);
    }

    static class Solution {
        public int[] solution(int[] A, int K) {
            // write your code in Java SE 8
            int[] answer ={};
            Deque<Integer> items = getBound(A);
            rotationItems(items,K);
            //items.stream().forEach(s-> System.out.print(s + "," ));
            answer = items.stream().mapToInt(x -> x).toArray();
            return answer;
        }

        public Deque<Integer> getBound(int[] A){
            Deque<Integer> items = new ArrayDeque<>();
            for(int num : A){
                items.add(num);
            }
            items.stream().forEach(s-> System.out.print(s+","));
            return items;
        }

        public void rotationItems(Deque<Integer> items,int K){

            if(!items.isEmpty()){
                int count =0;
                while(count < K){
                    items.addFirst(items.pollLast());
                    count++;
                }
            }
        }
    }
}
