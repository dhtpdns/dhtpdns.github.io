package Codility.lession1;


public class BinaryGap {
    public static final String IMPOSSIBLE = "IMPOSSIBLE";
    public static final String DOT = ".";

    public static void main(String[] args) {
        int N = 1041 ;
        System.out.println(new Solution().solution(N));
    }

    static class Solution {
        public static final char COMPARE_CHAR = '1';
        public int solution(int N) {
            // write your code in Java SE 8
            int answer =0;
            if(N > Integer.MAX_VALUE){
                throw new IllegalArgumentException("Invalid parameter.");
            }
            String binaryStr =  getBinaryStrValue(N);

            if(isBinaryGap(binaryStr)){
                answer = getMaxBinaryGap(binaryStr);
            }
            return answer;
        }

        public int getMaxBinaryGap(String binaryStr){
            int maxBinaryGap = 0;
            int zeroCount =0;
            for(int i =0; i<binaryStr.length(); i++){
                if(COMPARE_CHAR ==  binaryStr.charAt(i)){
                    maxBinaryGap = Math.max(maxBinaryGap,zeroCount);
                    zeroCount=0;
                }else{
                    zeroCount++;
                }
            }
            return maxBinaryGap;
        }

        public boolean isBinaryGap (String binaryStr){
            int i=0;
            int count =0;
            while(i<binaryStr.length()){
                if(COMPARE_CHAR == binaryStr.charAt(i)){
                    count++;
                }
                i++;
            }
            return (binaryStr.length() > 2 && count >= 2 ) ? true : false;
        }

        public String getBinaryStrValue(int N){
            String binaryStr = Integer.toBinaryString(N);
            return binaryStr;
        }
    }
}
