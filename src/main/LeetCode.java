package main;

import flyingkite.tool.TicTac2;

import java.util.Arrays;

public class LeetCode implements Runnable {
    @Override
    public void run() {
        //int ans = coinChange(new int[]{186,419,83,408}, 6249);
        //ln("ans = %s", ans);

        int s = 0;
        int max = 100_000;
        //max = 100_000_000;
        //max = 2_147_483_647;
        // 100_000 = 9592 primes = 0.09592
        // 1000_000 = 78498 primes = 0.078498
        // [4928] 10_000_000 = 664579 primes = 0.0664579
        // [130492] 100_000_000 = 5761455 primes = 0.05761455
        TicTac2 t = new TicTac2();
        t.tic();
        for (int i = 0; i < max; i++) {
            if (p2(i)) {
                s++;
                //ln("Yes Prime V = %s", i);
                //ln("% 4d primes = %s", s, 1.0 * s / max);
            } else {
                //ln("No  Prime X = %s", i);
            }
        }
        t.tac("done");
        ln("% 4d primes = %s", s, 1.0 * s / max);

//        t.tic();
//        for (int i = 0; i < max; i++) {
//            if (p2(i)) {
//                s++;
//                //ln("Yes Prime V = %s", i);
//                //ln("% 4d primes = %s", s, 1.0 * s / max);
//            } else {
//                //ln("No  Prime X = %s", i);
//            }
//        }
//        t.tac("done");
    }

    // https://leetcode.com/problems/coin-change/
    // 11:24 starts read question, 11:30 pause, 11:55 finish first solution
    public int coinChange(int[] coins, int amount) {
        return sol1(coins, amount);
    }
// [186,419,83,408]
// 6249
// [2,4,6,8,10]
// 111
// [1,2,5]
// 11

    // Find solution of x = (x1, ...., xn)
    // with a1*x1 + ... + an * xn = b
    // return the min (x1 + ... + xn)
    private int sol1(int[] a, int b) {
        int n = a.length;
        // gcd of A shound be b
        // int gcd = gcdAll(a);
        // if (b % gcd != 0) {
        //     return -1;
        // }

        // Sort coins increasingly
        Arrays.sort(a);
        ln("Found %s from %s coints = %s", b, n, Arrays.toString(a));

        // Take largest m coins on a[k], m = n / a[n], and find S_(n % a[k])
        // and greedy try on 0 ~ m;
        // S_n = min(n/a[k] + S_(n % a[k])) for all k = 1, ... n
        int[] c = new int[n];
        //int m = b / a[n-1];
        boolean got = S(a, n-1, b, c);
        if (got) {
            // Sum up x1 + ... + xn
            int sum = 0;
            for (int i = 0; i < n; i++) {
                sum += c[i];
            }
            return sum;
        } else {
            return -1;
        }
    }

    private int sol2(int[] a, int b) {
        int n = a.length;
        int max = b+1;
        int[] x = new int[b+1];
        Arrays.fill(x, max);
        x[0] = 0;
        // Sn = S(n-a[j]) + 1
        for (int i = 0; i <= b; i++) {
            for (int j = 0; j < n; j++) {
                int k = i - a[j];
                if (k >= 0) {
                    x[i] = Math.min(x[i], x[k] + 1);
                }
            }
        }
        return x[b] > b ? -1 : x[b];
    }

    // a is sorted, return found or not
    // use coins a[0:k] find sn, with c coins
    private boolean S(int[] a, int k, int sn, int[] c) {
        ln("Try find % 4d with k = % 4d,    c = %s, a = %s", sn, k, Arrays.toString(c), Arrays.toString(a));
        if (sn == 0) {
            ln("found!");
            return true;
        }
        if (k < 0 || sn < 0) {
            ln("no coins");
            return false; // no coins to use
        }

        int n = a.length;
        int m = sn / a[k];
        for (int i = m; i >= 0; i--) {
            int taken = a[k] * i;
            ln("m = % 4d, Try a[%d] * i = %s * %s = %s", m, k, a[k], i, taken);
            c[k] += i;
            if (S(a, k-1, sn - taken, c)) {
                return true;
            }
            c[k] -= i;
            ln("failed");
        }
        return false;
    }

    private int gcdAll(int[] a) {
        int n = a.length;
        if (n < 2) return a[0];

        int g = gcd(a[0], a[1]);
        if (g == 1) return g;

        for (int i = 2; i < n; i++) {
            g = gcd(g, a[i]);
            if (g == 1) {
                return g;
            }
        }
        return g;
    }

    // a = bq + r, gcd(a, b) = gcd(b, r)
    private int gcd(int a, int b) {
        int x = a;
        int y = b;
        while (y > 0) {
            int r = x % y;
            x = y;
            y = r;
        }
        return y;
    }

    private static boolean isPrime(int k) {
        return p1(k);
    }

    private static boolean p2(int k) {
        if (k < 10) {
            return k == 2 || k == 3 || k == 5 || k == 7;
        }

        for (int i = 2; i * i <= k; i++) {
            if (k % i == 0) {
                return false;
            }
        }
        return true;
    }

    private static boolean p1(int k) {
        if (k < 10) {
            return k == 2 || k == 3 || k == 5 || k == 7;
        }

        if (k % 2 == 0) return false;

        for (int i = 3; i * i <= k; i += 2) {
            if (k % i == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Definition for singly-linked list.
     * public class ListNode {
     *     int val;
     *     ListNode next;
     *     ListNode(int x) { val = x; }
     * }
     */
    /*

    // Return li[k] if exists, or return at tail
    private ListNode get(ListNode head, int k) {
        int m = 0;
        ListNode p = head;
        while (p != null && m < k) {
            p = p.next;
            m++;
        }
        return p;
    }

    private int size(ListNode x) {
        int s = 0;
        ListNode p = x;
        while (p != null) {
            p = p.next;
            s++;
        }
        return s;
    }

     */
    /**
     * Definition for a binary tree node.
     * public class TreeNode {
     *     int val;
     *     TreeNode left;
     *     TreeNode right;
     *     TreeNode(int x) { val = x; }
     * }
     */
    /*

    private String ss(TreeNode x) {
        if (x == null) {
            return "null";
        } else {
            String l = (x.left == null) ? ("-") : ("" + x.left.val);
            String r = (x.right == null) ? ("-") : ("" + x.right.val);
            return l + " (" + x.val + ") " + r;
        }
    }

    */

    private static void ln(String fmt, Object... p) {
        System.out.println((p == null) ? fmt : String.format(fmt, p));
    }
}

