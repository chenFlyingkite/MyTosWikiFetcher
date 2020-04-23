package flyingkite.algorithm.sequence.intervals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * https://leetcode.com/problems/interval-list-intersections/
 * https://leetcode.com/problems/merge-intervals/
 *
 * https://en.wikipedia.org/wiki/Allen's_interval_algebra
 */
public class IntervalAlgebra {
    /**
     * https://leetcode.com/problems/interval-list-intersections/
     * O(m+n), m = |A|, n = |B|
     */
    public List<int[]> intersection(List<int[]> a, List<int[]> b) {
        int an = a.size();
        int bn = b.size();

        // a[i] & b[j] try intersects, add it if not null, and ahead i or j
        int i = 0, j = 0;
        List<int[]> ans = new ArrayList<>();
        while (i < an && j < bn) {
            int[] ai = a.get(i);
            int[] bj = b.get(j);
            int[] z = inter(ai, bj);
            if (z != null) {
                ans.add(z);
            }
            if (ai[1] < bj[1]) {
                i++;
            } else {
                j++;
            }
        }
        return ans;
    }

    // Return null for no intersection
    private int[] inter(int[] a, int[] b) {
        //ln("inter a = (%s, %s), b = (%s, %s)", a[0], a[1], b[0], b[1]);
        // a =    -----   | -----
        // b = ------     | ------

        int m = Math.max(a[0], b[0]);
        int M = Math.min(a[1], b[1]);
        if (m > M) {
            return null;
        }
        return new int[]{m, M};
    }

    // Space = O(n)
    // Time = best = O(n), worst = O(n^2)
    public int[][] merge(int[][] intervals) {
        return mergeSol1(intervals);
    }

    private int[][] mergeSol1(int[][] a) {
        int n = a.length;
        List<int[]> all = new ArrayList<>();
        // For each interval, extend the existing intervals by contains/overlaps and merge as ab
        for (int i = 0; i < n; i++) {
            int m = all.size();
            int[] ai = a[i];
            // Merge ai, bj as ab
            int[] ab = ai;
            // Loop from end sinve we will remove
            for (int j = m-1; j >= 0; j--) {
                int[] bj = all.get(j);
                boolean over = overlaps(ab, bj);
                boolean cont = contains(ab, bj);
                if (cont || over) {
                    all.remove(j);
                    ab = merge(ab, bj);
                }
            }
            all.add(ab);
        }

        // convert to array of int[]
        int[][] ans = new int[all.size()][2];
        return all.toArray(ans);
    }

    //
    private boolean contains(int[] a, int[] b) {
        if (b[0] <= a[0] && a[1] <= b[1]) {
            // a = ---
            // b = -----
            return true;
        } else if (a[0] <= b[0] && b[1] <= a[1]) {
            // a = ------
            // b =  ----
            return true;
        } else {
            return false;
        }
    }

    private boolean overlaps(int[] a, int[] b) {
        // a, b, is sorted
        if (b[0] <= a[1] && a[1] <= b[1]) {
            // a = -----
            // b =  ------
            return true;
        } else if (a[0] <= b[1] && b[1] <= a[1]) {
            // a =   -----
            // b = ------
            return true;
        }
        return false;
    }

    private int[] merge(int[] a, int[] b) {
        int l = Math.min(a[0], b[0]);
        int r = Math.max(a[1], b[1]);
        return new int[]{l, r};
    }

    // https://leetcode.com/problems/merge-intervals/

    private static void ln(String f, Object... p) {
        System.out.println((p == null) ? f : String.format(f, p));
    }
}
// Test cases
/*
[[0,2],[5,10],[13,23],[24,25]]
[[1,5],[8,12],[15,24],[25,26]]
[[0,2],[13,23],[24,25]]
[[1,5],[8,12],[15,24],[25,26]]
[[0,1],[2,3],[4,5],[10,12]]
[[1,2],[3,5],[6,11]]
[[1,3],[5,9]]
[]
*/