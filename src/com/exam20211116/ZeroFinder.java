package com.exam20211116;

import flyingkite.log.L;
import flyingkite.math.MathUtil;
import flyingkite.tool.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * Let a = m*n matrix
 * w*h 0-matrix : matrix with all zero of width w, height h
 * Let L0 = k*1 0-rect count, k = 1 ~ m
 * Let H0 = 1*k 0-rect count, k = 1 ~ n
 * rectangle count = all possible size rectangle counts = (L1 + L2 + ... + Lm) * (H1 + H2 + ... + Hn)
 * prefixSum(i, j) = sum(a[0:i-1][0:j-1]) , sum up matrix left-top = (0,0), right-bottom = (i-1, j-1)
 * For a  = [1,0,0], H0 = [0,1,1], H1 = [0,1,1], H2 = [0,0,0], prefixSum = [0,0,0,0]
 *          [0,0,0]       [1,2,2]       [1,1,1]       [0,1,1]              [0,0,1,2]
 * and L0 = [0,1,2], L1 = [0,1,1], L2 = [0,0,1], L3 = [0,0,0],             [0,1,3,5]
 *          [1,2,3]       [1,1,1]       [0,1,1]       [0,0,1],
 *
 * We using prefixSum for knowing if a[t:b][l:r] is a 0-rect or not,
 * Complexity :
 * Checking : space = O(1), time = O(1)
 * Build prefixSum : space = O(N*M), time = O(N*M)
 * So totally run by "check each possible size rect at each position"
 * needs O(N*M) space and O(N^2*M^2) time,
 *
 * Can we have a better solution of faster to time = O(N*M), space = O(N^2*M^2) ?
 */
public class ZeroFinder {

    // Return the count of sub array of all zero, a[i]~ a[j] are zeros
    // a is an 2-D integer array containing only zero or one, a[i] = 0 or 1
    // Time = O(N), Space = O(1)
    public int getSubArrayCountOfAllZero(int[] a) {
        if (a == null) return -1;

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

    // space = O(1), time = O(N)
    // Return L0, L0(i, j) = count of height 1 of 0-rectangle whose right bottom is at (i, j)
    public int[] getRowZero(int[] a) {
        if (a == null) return null;

        int n = a.length;
        int[] ans = new int[n];
        int now = 0;
        for (int i = 0; i < n; i++) {
            if (a[i] == 0) {
                now++;
            } else {
                now = 0;
            }
            ans[i] = now;
        }
        return ans;
    }

    // space = O(1), time = O(N*M)
    // Return H0, H0(i, j) = count of width 1 of 0-rectangle whose right bottom is at (i, j)
    public int[][] getColumnZero(int[][] a) {
        if (a == null) return null;

        // if a[i][j] = 1, ans[i+1][j] = 0
        // if a[i][j] = 0, ans[i+1][j] = ans[i][j] + 1 for i > 0
        // if a[i][j] = 0, ans[i+1][j] = 1 for i = 0 // first row
        int n = a.length;
        int m = a[0].length;
        int[][] ans = new int[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                int prev = 0;
                if (i > 0) {
                    prev = ans[i-1][j];
                }
                ans[i][j] = a[i][j] == 0 ? (prev + 1) : 0;
            }
        }
        return ans;
    }

    // sum([0,0 : i,j]) = ans[i+1][j+1]
    // Time = O(N*M)
    public int[][] getPrefixSum(int[][] a) {
        int n = a.length;
        int m = a[0].length;
        int[][] sum = new int[n+1][m+1];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                // using 1-a[i][j] is counting zero
                sum[i+1][j+1] = (1-a[i][j]) + sum[i][j+1] + sum[i+1][j] - sum[i][j];
            }
        }
        return sum;
    }

    // return true if a[l:r,t:b] are all zero, the zero matrix, l, t, r, b, inclusive
    // space = O(1), time = O(N*M), precisely, the 1st non-zero at k = O(k)
    public boolean isAllZero(int[][] a, int l, int t, int r, int b) {
        if (a == null) return false;
        int n = a.length;
        int m = a[0].length;
        if (l < 0 || m < r) return false;
        if (t < 0 || n < b) return false;
        for (int i = t; i <= b; i++) {
            for (int j = l; j <= r; j++) {
                if (a[i][j] != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    // sum([0,0 : i,j]) = ans[i+1][j+1]
    // decide by prefix sum : space = O(1), time = O(1)
    private boolean isAllZeroPrefixSum(int[][] sum, int l, int t, int r, int b) {
        if (sum == null) return false;
        int n = sum.length-1;
        int m = sum[0].length-1;
        if (l < 0 || m < r) return false;
        if (t < 0 || n < b) return false;
        int part = sum[b+1][r+1] - sum[b+1][l] - sum[t][r+1] + sum[t][l];
        int w = r - l + 1, h = b - t + 1;
        //L.log("part = %s, %s,%s-%s,%s, wh=%s,%s", part, l, t, r, b, w, h);
        boolean ans = part == w*h;
        return ans;
    }

    // 2022/06/28 17:37
    // Returns the matrix count of all zero (zero-matrix) in a, maybe we can use the maximum rectangle or previous height one solution
    public int getSubArrayCountOfAllZero(int[][] a) {
        int n = a.length;
        int m = a[0].length;

        int[][] zeroL = new int[n][];
        for (int i = 0; i < n; i++) {
            zeroL[i] = getRowZero(a[i]);
        }
        int[][] zeroH = getColumnZero(a);
        int[][] prevS = getPrefixSum(a);
        L.log("a, L, H, Px =");
        ln(print("a = ", a), print("Px = ", prevS), print("L = ", zeroL), print("H = ", zeroH));
        int ans = 0;
        int[][] zeroE = new int[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                int now = 0;
                if (i == 0) {
                    now = zeroL[i][j];
                } else if (j == 0) {
                    now = zeroH[i][j];
                } else {
                    // w, x
                    // y, z
                    int w = a[i-1][j-1], x = a[i-1][j], y = a[i][j-1], z = a[i][j];
                    if (z == 0) {
                        if (w+x+y == 0) { // all zero
                            //    h1 + w1 - self + left,top +w1h1
                            // todo, how to count w*h sizes faster, where w >= 2, h >= 2 ?
//                            a[4,7]
//                            # 0 : [1, 2, 0, 0, 1, 2, 0]
//                            # 1 : [2, 4, 0, 1, 0, 2, 2]
//                            # 2 : [3, 0, 1, 3, 0, 3, 4]
//                            # 3 : [4, 2, 4, 7, 5, 9, 10] // a[3][6] should be 11 instead of 10, since it did not add the 2*3 0-matrix,
                            now = zeroL[i][j] + zeroH[i][j] - 1 + MathUtil.mins(zeroL[i-1][j-1], zeroH[i-1][j-1]);
                            //now = zeroL[i][j] + zeroH[i][j] - 1 + zeroE[i-1][j-1];
                            //L.log("wxy0 (%s, %s)", i, j);
                        } else if (x == 1 && y == 0) { // x = 1, w = 0,1
                            now = zeroL[i][j];
                            //L.log("w.y0 (%s, %s)", i, j);
                        } else if (x == 0 && y == 1) { // y = 1
                            now = zeroH[i][j];
                            //L.log("wx.0 (%s, %s)", i, j);
                        } else if (w == 1 && x+y == 0) { // w = 1
                            now = zeroL[i][j] + zeroH[i][j] - 1;
                            //L.log(".xy0 (%s, %s)", i, j);
                        } else {
                            // x+y=2
                            now = 1;
                        }
                    }
                }
                zeroE[i][j] = now;
                ans += now;
            }
        }
        ln(print("ans = Sum(E), E =", zeroE), print("a =", a));
        return ans;
    }

    private String[] print(String title, int[][] a) {
        if (a == null) return null;

        int n = a.length;
        String[] ans = new String[n+1];
        ans[0] = title;
        for (int i = 0; i < n; i++) {
            String s;
            s = StringUtil.toWidthString(a[i], 2);
            ans[i+1] = String.format("#%2d : %s", i, s);
        }
        return ans;
    }

    private void ln(String[]... a) {
        if (a == null) return;
        String[] ms = StringUtil.concatenateAligned(a, " | ");
        for (int i = 0; i < ms.length; i++) {
            L.log(ms[i]);
        }
    }

    // split the String into characters to be int binary array
    public void single() {
        String[] singleRow = {"10100100010000101101111", null, "000110101001"};
        single(singleRow);
    }

//    for (A; B; C) {
//        D
//        if (U) continue; // A,B,(D,(U|(U,K)),C,B,)*,E // continue execute C
//        if (K) break;    // A,B,D,U,K,E // break will not execute C
//    }
//    E

    public void single(String[] row) {
        if (row == null) return;

        for (int i = 0; i < row.length; i++) {
            String src = row[i];
            L.log("#%s : src = %s", i, src);
            if (src == null) continue;

            int[] input = StringUtil.parse01(src);
            int ans = getSubArrayCountOfAllZero(input);
            int[] zs = getRowZero(input);
            L.log("#%s : ans = %s, src = %s (%s)", i, ans, src.length(), src);
            L.log("  zeros = %s", Arrays.toString(zs));
        }
    }

    public void multi() {
        String[][] multiRow = {
                {"000", "000"}, // ans = 18 = 1+2+3+2+4+6 // x =17
                {"100", "000"}, // ans = 12 = 1+2+1+3+5
                {"010", "000"}, // ans = 10 = 1+1+2+2+4
                {"001", "000"}, // ans = 12 = 1+2+2+4+3
                {"110","000","100",}, // ans = 15 = (6+3+1+3+1+0+1+0+0) = 0+0+1+1+2+4+2+5
                {"0011001", "0010100", "0100100", "0000000",}, // x
        };
        for (int i = 0; i < multiRow.length; i++) {
            String[] x = multiRow[i];
            L.log("multi #%s : ", i);
            multi(x);
        }
        L.log("multiRows =");
        ln(multiRow);
        L.log("---");
    }

    // Using navigate on all possible (l, t, r, b) and check zero by navigate all
    // method 0 = run both 1 and 2
    // method 1 =
    // Space = O(1), Time = O(N^3*M^3) for prefixSum = false
    // method 2 =
    // Space = O(N*M), Time = O(N^2*M^2) for prefixSum = true
    // Do we have faster way for finding width & height?
    public int getSubMatrixOfAllZeroBase(int[][] a, int method) {
        if (a == null) return -1;

        List<Boolean> flags = new ArrayList<>();
        int[][] prefixSum = null;
        if (method == 0 || method == 2) {
            prefixSum = getPrefixSum(a);
        }
        //
        int n = a.length;
        int m = a[0].length;
        int ans = 0;
        // For each possible left, top and valid width, height to examine
        for (int t = 0; t < n; t++) {
            for (int l = 0; l < m; l++) {
                for (int b = t; b < n; b++) {
                    for (int r = l; r < m; r++) {
                        // reset
                        flags.clear();

                        int w = r - l + 1, h = b - t + 1;
                        // sol 1
                        boolean is0 = false;
                        if (method == 0 || method == 1) {
                            is0 = isAllZero(a, l, t, r, b);
                            flags.add(is0);
                        }
                        //L.log("is0  = %s for wh = %sx%s, a[%s,%s]-[%s,%s]", is0 ? "o" : "x", w, h, t, l, b, r);

                        boolean is0P = false;
                        if (method == 0 || method == 2) {
                            is0P = isAllZeroPrefixSum(prefixSum, l, t, r, b);
                            flags.add(is0P);
                        }
                        //L.log("is0P = %s for wh = %sx%s, a[%s,%s]-[%s,%s]", is0P ? "o" : "x", w, h, t, l, b, r);
                        if (is0 != is0P) {
                            L.log("X_X Error by base = %s, prefixSum = %s, for wh = (%s, %s), a[%s,%s]-[%s,%s]", is0, is0P, w, h, t, l, b, r);
                        }

                        boolean decide = flags.size() > 0 ? flags.get(0) : false;
                        if (decide) {
                            ans++;
                        }
                    }
                }
            }
        }
        return ans;
    }

    public void multi(String[] s) {
        int[][] input = StringUtil.parse01(s);
        int ans = getSubArrayCountOfAllZero(input);
        L.log("ans = %s", ans);
        int ans2 = getSubMatrixOfAllZeroBase(input, 0);
        L.log("ans2 = %s", ans2);
    }
}
