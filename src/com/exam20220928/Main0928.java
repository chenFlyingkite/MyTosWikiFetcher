package com.exam20220928;

import flyingkite.log.L;

import java.util.Arrays;

public class Main0928 {
    private static final IntervalOperation allen = new IntervalOperation();
    public static void main(String[] args) {
        int[][] a = {{5, 25}, {20, 30}, {35, 55}};
        int ans = allen.unionLength(a);
        L.log("ans = %s for %s", ans, as(a));
    }

    private static String as(int[][] a) {
        if (a == null) return "null";
        StringBuilder ans = new StringBuilder("[");
        for (int i = 0; i < a.length; i++) {
            if (i > 0) {
                ans.append(", ");
            }
            ans.append(as(a[0]));
        }
        ans.append("]");
        return ans.toString();
    }

    private static String as(int[] a) {
        return Arrays.toString(a);
    }
}
