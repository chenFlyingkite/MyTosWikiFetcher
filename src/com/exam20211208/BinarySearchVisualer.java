package com.exam20211208;

import flyingkite.log.L;

import java.util.Arrays;
import java.util.Random;

public class BinarySearchVisualer implements Runnable {

    private final String LL = " ^   ";
    private final String RR = "   ^ ";
    private final String BB = "     ";
    @Override
    public void run() {
        int min = 0;
        int max = 9;
        int count = 15;
        int width = BB.length() - 1;//(int) Math.log10(max - min + 1) + 1;
        int x = new Random().nextInt(max - min) + min;
        int[] a = random(min, max, count);
        String s1 = pretty(a, width);
        String s2 = locate(0, LL, 5, RR, BB);
        L.log("sorted integer array = %s", s1);
        L.log("pointers for (0, 5)  = %s", s2);
        L.log("---------");
        binarySearch(a, x);
        L.log("---------");
        int[] b = { 0, 1, 1, 2, 2, 2, 3, 3, 3, 3, 6, 8, 8, 8, 8};
        //b = new int[]{0,1,2,3,4,5,6,5,4,3,2,1}; // increase and decrease
        binarySearch(b, 3);
    }
    /*
     Is this the formula?
     int L, R = your range inclusively
     while (L < R) {
       int M = L + (R - L) / 2
       if (want) {
          R = M;
       } else {
          L = M + 1;
       }
     }
     // L and R will stay on wanted condition

     or
     <code>
     int L, R = your range inclusively
     while (L < R) {
       int M = L + (R - L) / 2
       if (not want) {
          L = M + 1;
       } else {
          R = M;
       }
     }
     </code>
     // L and R will stay on wanted condition
    */

    /*
    sorted integer array = [   0,   3,  13,  26,  32,  43,  61,  73,  73,  86,]
    pointers for (0, 5)  =   ^                          ^
    ---------
    Find k = 32
             a = [   0,   3,  13,  26,  32,  43,  61,  73,  73,  86,]
    # 0( 0~ 9) =   ^                                              ^
    # 1( 0~ 4) =   ^                     ^
    # 2( 3~ 4) =                  ^      ^
    # 3( 4~ 4) =                       ^ ^
    */
    private boolean visual = true;
    // find index a[m] >= k
    private int binarySearch(int[] a, int k) {
        int n = a.length;
        int left = 0;
        int right = n - 1;
        int i = 0;
        if (visual) {
            L.log("Find k = %s", k);
            L.log("         a = %s", pretty(a, BB.length() - 1));
            L.log("#%2d(%2d~%2d) = %s", i, left, right, locate(left, LL, right, RR, BB));
        }
        while (left < right) {
            int m = (left + right) / 2;

            if (a[m] > k) { // left most one meet condition // m stays at 6
            //if (a[m] >= k) { // >= k is the left most one of k // m stays at left most 3
            //if (a[m] > a[m+1]) { // stay at left most wanted condition
                right = m;
            } else { //
                left = m + 1; // stay at here
            }

            /*
            //if (a[m] < a[m+1]) {
            if (a[m] < k) { // negated condition
                left = m + 1;
            } else { // >= m
                right = m; // stay at here
            }
            */
            if (visual) {
                i++;
                L.log("#%2d(%2d~%2d) = %s", i, left, right, locate(left, LL, right, RR, BB));
            }
        }
        return left;
    }

    public int[] random(int min, int max, int length) {
        Random r = new Random();
        int[] ans = new int[length];
        for (int i = 0; i < length; i++) {
            int x = r.nextInt(max - min) + min;
            ans[i] = x;
        }
        Arrays.sort(ans);
        return ans;
    }

    // each element in k length
    // [" a[0], a[1], ..., a[n-1],"]
    public String pretty(int[] a, int k) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < a.length; i++) {
            String fmt = "%" + k + "d,";
            sb.append(String.format(fmt, a[i]));
        }
        sb.append("]");
        return sb.toString();
    }

    // each length is k
    public String locate(int from, String fromString, int end, String endString, String blank) {
        String merge = fromString;
        boolean meet = from == end;
        if (meet) {
            char[] fc = fromString.toCharArray();
            char[] ec = endString.toCharArray();
            char[] mc = new char[fc.length];
            for (int i = 0; i < mc.length; i++) {
                if (ec[i] == ' ' && fc[i] == ' ') {
                    mc[i] = ' ';
                } else {
                    if (ec[i] == ' ') {
                        mc[i] = fc[i];
                    } else {
                        mc[i] = ec[i];
                    }
                }
            }
            merge = new String(mc);
        }
        StringBuilder sb = new StringBuilder(" "); // append space for start
        for (int i = 0; i <= end; i++) {
            if (i == from) {
                if (meet) {
                    sb.append(merge);
                } else {
                    sb.append(fromString);
                }
            } else if (i == end) {
                sb.append(endString);
            } else {
                sb.append(blank);
            }
        }
        return sb.toString();
    }
}
