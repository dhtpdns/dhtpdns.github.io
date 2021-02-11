package Codility.lession2;


import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class OddOccurrencesInArray {
    public static final String IMPOSSIBLE = "IMPOSSIBLE";
    public static final String DOT = ".";

    public static void main(String[] args) {
        int[] A = {9, 3, 9, 3, 9, 7, 9} ;

        System.out.println(new Solution().solution(A));
    }

    static class Solution {
        public int solution(int[] A) {
            // write your code in Java SE 8
            int answer;
            Map<Integer,Integer> items = makeCountMap(A);

            answer = findUnpairedElement(items);
            return answer;
        }


        public Map<Integer,Integer> makeCountMap(int[] A){
            Map<Integer,Integer> items = new HashMap<>();
            for(int num : A){
                items.put(num , items.getOrDefault(num,0) +1);
            }
            return items;
        }

        public int findUnpairedElement(Map<Integer,Integer> items){
            int unpairedElementVlaue = 0;
            for(int key : items.keySet()){
                if(items.get(key) == 1 || items.get(key) % 2 !=0  ){
                    unpairedElementVlaue = key;
                    break;
                }
            }

            return unpairedElementVlaue;
        }
    }
}
