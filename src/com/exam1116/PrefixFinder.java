package com.exam1116;

import flyingkite.log.L;

import java.util.Arrays;

public class PrefixFinder {
    public boolean debug = true;

    public void test() {
        String[] s = {"a", "a", "b", "b", "b", "c", "c", "c", "d", "d"};
        String[] ps = {"a", "b", "c"};
        Arrays.sort(s);
        L.log("For s = %s", Arrays.toString(s));
        for (int i = 0; i < ps.length; i++) {
            int n = getCountsContainsOfPrefix(s, ps[i]);
            L.log("%s for prefix = %s", n, ps[i]);
        }
    }

    // The list should be sorted lexicographically
    public int getCountsContainsOfPrefix(String[] list, String prefix) {
        int n = list.length;
        // find begin of prefix as begin
        // inclusive
        int leftLeft = 0;
        int leftRight = n-1;
        while (leftLeft < leftRight) {
            int mid = (leftLeft + leftRight) / 2;
            int cmp = list[mid].compareTo(prefix);
            if (cmp < 0) {
                leftLeft = mid + 1;
            } else { // cmp >= 0
                leftRight = mid;
            }
        }
        if (debug) {
            L.log("prefix = %s, list = %s", prefix, Arrays.toString(list));
            L.log("left = %s -> %s", leftLeft, list[leftLeft]);
        }
        if (list[leftLeft].startsWith(prefix) == false) {
            return 0; // every string are smaller than prefix
        }

        // find last prefix position as rightLeft+1
        // exclusive
        int rightLeft = leftLeft;
        int rightRight = n;
        // 0 ~ 3 for a, a, a on a
        while (rightLeft < rightRight) { //
            int mid = (rightLeft + rightRight) / 2; // 1
            boolean cmp = list[mid].startsWith(prefix);
            if (cmp) {
                rightLeft = mid + 1; // 2
            } else { //
                rightRight = mid;
            }
        }
        if (debug) {
            L.log("right = %s -> %s", rightLeft, rightLeft < n ? list[rightLeft] : "  ");
        }
        return rightLeft - leftLeft;
    }
}
