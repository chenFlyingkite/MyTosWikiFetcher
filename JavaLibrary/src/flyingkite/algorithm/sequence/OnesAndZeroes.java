package flyingkite.algorithm.sequence;

/**
 * https://leetcode.com/problems/ones-and-zeroes/
 * In the computer world, use restricted resource you have to generate maximum benefit is what we always want to pursue.
 *
 * For now, suppose you are a dominator of m 0s and n 1s respectively. On the other hand, there is an array with strings consisting of only 0s and 1s.
 *
 * Now your task is to find the maximum number of strings that you can form with given m 0s and n 1s. Each 0 and 1 can be used at most once.
 *
 * Note:
 *
 * The given numbers of 0s and 1s will both not exceed 100
 * The size of given string array won't exceed 600.
 *
 *
 * Example 1:
 *
 * Input: Array = {"10", "0001", "111001", "1", "0"}, m = 5, n = 3
 * Output: 4
 *
 * Explanation: This are totally 4 strings can be formed by the using of 5 0s and 3 1s, which are “10,”0001”,”1”,”0”
 *
 *
 * Example 2:
 *
 * Input: Array = {"10", "0", "1"}, m = 1, n = 1
 * Output: 2
 *
 * Explanation: You could form "10", but then you'd have nothing left. Better form "0" and "1".
 * */
public class OnesAndZeroes {

    public int findMaxForm(String[] strs, int m, int n) {
        return sol1(strs, m, n);
    }

    private int sol1(String[] s, int m, int n) {
        int len = s.length;

        // counting a[0:i] has how many zeros and ones, as z[i][0] & z[i][1]
        int[][] z01s = new int[len][2];
        int m0 = 0, m1 = 0;
        for (int i = 0; i < len; i++) {
            String si = s[i];
            int[] z = get01(si);
            m0 += z[0];
            m1 += z[1];
            z01s[i] = z;
        }
        // Returns len if sufficient to form all s[0:len]
        if (m0 >= m && m1 >= n) {
            return len;
        }

        // dp[i][j][k] = using j zeros & k ones forms s[0:i]
        // for s[i][j][k] it has 0 * z0 + 1 * z1,
        //   can form it if s[i-1][j-z0][k-z1] forms + 1
        // dp[i][j][k]
        int[][] dp = new int[m+1][n+1];

        for (int i = 0; i < len; i++) {
            int[] z01 = z01s[i];
            int z0 = z01[0];
            int z1 = z01[1];
            for (int j = m; j >= 0; j--) {
                for (int k = n; k >= 0; k--) {
                    if (j >= z0 && k >= z1) {
                        int next = 1 + dp[j-z0][k-z1];
                        dp[j][k] = Math.max(dp[j][k], next);
                    }
                }
            }
        }
        return dp[m][n];
    }

    // For string s, returns the 0 chars and 1 chars count as ans = {m, n},
    // where s has m zeros and n ones
    private int[] get01(String s) {
        int[] ans = new int[2];
        int n = s.length();
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            if (c == '0') {
                ans[0]++;
            } else if (c == '1') {
                ans[1]++;
            }
        }
        return ans;
    }

    private static void ln(String f, Object... p) {
        System.out.println((p == null) ? f : String.format(f, p));
    }
}
