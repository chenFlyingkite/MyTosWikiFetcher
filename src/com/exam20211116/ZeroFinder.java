package com.exam20211116;

public class ZeroFinder {

    // Return the count of sub array of all zero, a[i]~ a[j] are zeros
    // a is an integer array containing only zero or one, a[i] = 0 or 1
    public int getSubArrayOfAllZero(int[] a) {
        int n = a.length;
        int now = 0;
        int ans = 0;
        for (int i = 0; i < n; i++) {
            if (a[i] == 0) {
                now++;
            } else {
                now = 0;
            }
            ans += now;
        }
        return ans;
    }

    // Returns the submatrix count of all zero in a, maybe we can use the maximum rectangle or previous height one solution
    private int getSubArrayOfAllZero(int[][] a) {
        int n = a.length;
        int m = a.length;
        return 0;
    }
}
