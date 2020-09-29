package flyingkite.algorithm.sequence;

import java.util.Arrays;

/**
 *  We change it as int array
 * //--
 * https://leetcode.com/problems/maximal-rectangle/
 * Given a 2D binary matrix filled with 0's and 1's, find the largest rectangle containing only 1's and return its area.
 *
 * Example:
 *
 * Input:
 * [
 *   ["1","0","1","0","0"],
 *   ["1","0","1","1","1"],
 *   ["1","1","1","1","1"],
 *   ["1","0","0","1","0"]
 * ]
 * Output: 6
 */
public class MaximalRectangle {

    private boolean debug = 0 > 0;

    public int get(String[] a) {
        //-- Convert String array to char array
        int n = a.length;
        char[][] mx = new char[n][];
        for (int i = 0; i < n; i++) {
            mx[i] = a[i].toCharArray();
        }
        //--
        return sol1(mx);
    }

    private int sol1(char[][] a) {
        int m = a.length;
        if (m == 0) {
            return 0;
        }
        int n = a[0].length;
        int ans = 0;
        // h[j] = h[0:h] are all 1
        int[] height = new int[n];
        // left[j] = x, a[x] = 1 and a[x-1] = 0 (left most 1 of a[i]])
        int[] left = new int[n];
        // right[i][j] = x, a[x][j] = 1 and a[x+1][j] = 0 right most 1 of a[i][j]
        int[] right = new int[n];
        Arrays.fill(right, n-1);

        if (debug) {
            ln("a = ");
            pc(a);
        }
        for (int i = 0; i < m; i++) {
            // update right
            int rb = n-1;
            for (int j = n-1; j >= 0; j--) {
                char x = a[i][j];
                if (x == '1') {
                    right[j] = Math.min(right[j], rb);
                } else if (x == '0') {
                    right[j] = n-1;
                    rb = j-1;
                }
            }
            // update left
            int lb = 0;
            for (int j = 0; j < n; j++) {
                char x = a[i][j];
                if (x == '1') {
                    // height
                    height[j]++;
                    // left
                    left[j] = Math.max(left[j], lb);
                } else if (x == '0') {
                    // height
                    height[j] = 0;
                    // left
                    left[j] = 0;
                    lb = j+1;
                }
            }
            if (debug) {
                ln("-----");
                ln("#row %s : ", i);
                ln("height = %s", as(height));
                ln("left   = %s", as(left));
                ln("right  = %s", as(right));
            }
            for (int j = 0; j < n; j++) {
                int w = right[j] - left[j] + 1;
                int h = height[j];
                int area = w * h;
                ans = Math.max(ans, area);
                if (debug) {
                    ln("  h---");
                    ln("at (%s, %s) = %s x %s = %s", i, j, w, h, area);
                    ln("ans = %s", ans);
                }
            }
        }
        return ans;
    }

    private static void pc(char[][] a) {
        int n = a.length;
        for (int i = 0; i < n; i++) {
            ln("#%s : %s", i, cs(a[i]));
        }
    }

    private static String cs(char[] a) {
        return Arrays.toString(a);
    }

    private static void pa(int[][] a) {
        int n = a.length;
        for (int i = 0; i < n; i++) {
            ln("#%s : %s", i, as(a[i]));
        }
    }

    private static String as(int[] a) {
        return Arrays.toString(a);
    }

    private static void ln(String f, Object... p) {
        System.out.println((p == null) ? f :String.format(f, p));
    }
/*
[["0","1","1","0","1"],["1","1","0","1","0"],["0","1","1","1","0"],["1","1","1","1","0"],["1","1","1","1","1"],["0","0","0","0","0"]]
[["1","0","1","0","0"],["1","0","1","1","1"],["1","1","1","1","1"],["1","0","0","1","0"]]
*/
}
