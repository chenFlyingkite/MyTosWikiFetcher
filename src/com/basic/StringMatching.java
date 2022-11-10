package com.basic;

import flyingkite.log.L;

import java.util.Arrays;

public class StringMatching {

    public static void main(String[] args) {
        findByKMP("ABCDABD", "ABC ABCDAB ABCDABCDABDE");
    }

    public static void findByKMP(String want, String pool) {
        int n = pool.length();
        // the partial matching
        int[] T = new int[n];
        int pos = 2; // the current position we are computing in T
        int cnd = 0; // the zero-based index in W of the next character of the current candidate substring
        T[0] = -1;
        T[1] = 0;
        while (pos < pool.length()) {
            if (pool.charAt(pos - 1) == want.charAt(cnd)) {
                // or if (pool.charAt(pos - 1) == pool.charAt(cnd)) {?
                // (first case: the substring continues)
                cnd = cnd + 1;
                T[pos] = cnd;
                pos = pos + 1;
            } else if (cnd > 0) {
                // (second case: it doesn't, but we can fall back)
                cnd = T[cnd];
            } else {
                // (third case: we have run out of candidates.  Note cnd = 0)
                T[pos] = 0;
                pos = pos + 1;
            }
        }
        L.log("want = %s, pool = %s", want, pool);
        L.log("T = %s", Arrays.toString(T));
    }
}
