package Codility.lession3;


import java.util.ArrayDeque;
import java.util.Deque;

public class FrogJump {
    public static final String IMPOSSIBLE = "IMPOSSIBLE";
    public static final String DOT = ".";

    public static void main(String[] args) {
        int  X = 10 ;
        int Y =  85;
        int D= 30;
        new Solution().solution(X,Y,D);
    }

    static class Solution {
        public int solution(int X, int Y , int D) {

            int answer = 0;
            if( ((double) Y-(double) X) / (double) D == (Y-X) / D )
                return  (Y-X) / D;

            return ((Y-X) / D ) + 1;

        }


    }
}
