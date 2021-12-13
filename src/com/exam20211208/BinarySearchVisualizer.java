package com.exam20211208;

import flyingkite.log.L;

import java.util.Arrays;
import java.util.Random;

public class BinarySearchVisualizer implements Runnable {

    @Override
    public void run() {
        int min = 0;
        int max = 19;
        int count = 15;
        int x = new Random().nextInt(max - min) + min;
        int[] a = random(min, max, count);
        String s1 = pretty(a); // String s1 = pretty(a, width);
        String s2 = locate(0, 5);
        L.log("sorted integer array = %s", s1);
        L.log("pointers for (0, 5)  = %s", s2);
        L.log("---------");
        binarySearch(a, x);
        L.log("---------");
        int[] b = { 0, 1, 1, 2, 2, 2, 3, 3, 3, 3, 6, 8, 8, 8, 8};
        //b = new int[]{0,1,2,3,4,5,6,5,4,3,2,1}; // increase and decrease
        binarySearch(b, 5);
    }
    /*
     Is this the formula?
     Make conditions to be
     Conditions can be
     - - - - - - - - - (all negative)
                       ^
     - - - - - + + + + (in middle)
               ^
     + + + + + + + + + (all positive)
     ^
     We want to find '^' at, we will find first + located at
     So we divide part as [L, M] and [M+1, R], now we are determine which part we want.

     int L, R = your range inclusively
     while (L < R) {
       int M = L + (R - L) / 2
       if (f(M) is left property, is negative) {
          L = M + 1; // uses (M + 1, R)
       } else {
          R = M; // uses (L, M), here is the positive
       }
     }
     // now L = R, and will stay on f(L = R) is positive

     or
     <code>
     int L, R = your range inclusively
     while (L < R) {
       int M = L + (R - L) / 2
       if (f(M) is right property, positive) {
          R = M; // uses (L, M) here is the positive
       } else {
          L = M + 1; // uses (M + 1, R)
       }
     }
     </code>
     // now L = R, and will stay on f(L = R) is positive
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
            L.log("         a = %s", pretty(a));
            L.log("#%2d(%2d~%2d) = %s", i, left, right, locate(left, right));
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
                L.log("#%2d(%2d~%2d) = %s", i, left, right, locate(left, right));
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

    public String pretty(int[] a) {
        return pretty(a, sym.empty.length() - 1);
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

    private final Symbol sym = new Symbol();
    // each length is k
    public String locate(int from, int end) {
        boolean same = from == end;
        String head = sym.head;
        String tail = sym.tail;
        String meet = sym.meet;
        String empty = sym.empty;
        StringBuilder sb = new StringBuilder(" "); // append space for start
        for (int i = 0; i <= end; i++) {
            String s = empty;
            if (i == from) {
                if (same) {
                    s = meet;
                } else {
                    s = head;
                }
            } else if (i == end) {
                s = tail; // meet case is taken in from
            }
            sb.append(s);
        }
        return sb.toString();
    }

    private class Symbol {
        //                           " 123,"
        private final String head  = " ^   ";
        private final String tail  = "   ^ ";
        private final String meet  = " ^ ^ ";
        private final String empty = "     ";
    }
}
