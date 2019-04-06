package flyingkite.algorithm.sequence;

/**
 * Implementation of Longest Common Subsequence
 * https://en.wikipedia.org/wiki/Longest_common_subsequence_problem
 */
public class LCS implements Runnable {

    // These are "constants" which indicate a direction in the backtracking array.
    private static final int NEITHER     = 0;
    private static final int UP          = 1;
    private static final int LEFT        = 2;
    private static final int UP_AND_LEFT = 3;

    private String strX;
    private String strY;
    private String strLcs;
    private boolean done;

    // Table of LCS's length
    private int[][] S;
    // Table of LCS's direction
    private int[][] R;

    public LCS(String x, String y) {
        if (x == null) {
            throw new NullPointerException("x is null");
        }
        if (y == null) {
            throw new NullPointerException("y is null");
        }
        strX = x;
        strY = y;
    }

    @Override
    public void run() {
        if (done) return;

        String a = strX;
        String b = strY;
        int n = a.length();
        int m = b.length();

        S = new int[n+1][m+1];
        R = new int[n+1][m+1];

        // It is important to use <=, not <.  The next two for-loops are initialization
        for (int i = 0; i <= n; i++) {
            S[i][0] = 0;
            R[i][0] = UP;
        }
        for (int j = 0; j <= m; j++) {
            S[0][j] = 0;
            R[0][j] = LEFT;
        }

        // This is the main dynamic programming loop that computes the score and
        // backtracking arrays.
        for (int i = 1; i <= n; ++i) {
            for (int j = 1; j <= m; ++j) {
                if (a.charAt(i-1) == b.charAt(j-1)) {
                    S[i][j] = S[i-1][j-1] + 1;
                    R[i][j] = UP_AND_LEFT;
                } else {
                    S[i][j] = S[i-1][j-1];
                    R[i][j] = NEITHER;
                }

                if (S[i-1][j] >= S[i][j]) {
                    S[i][j] = S[i-1][j];
                    R[i][j] = UP;
                }

                if (S[i][j-1] >= S[i][j]) {
                    S[i][j] = S[i][j-1];
                    R[i][j] = LEFT;
                }
            }
        }
        done = true;
    }

    public int getLength() {
        run();
        // The length of the longest substring is S[n][m]
        int n = strX.length();
        int m = strY.length();
        return S[n][m];
    }

    public String getString() {
        run();
        if (strLcs != null) return strLcs;

        String a = strX;
        String b = strY;
        int n = a.length();
        int m = b.length();

        // The length of the longest substring is S[n][m]
        int i = n;
        int j = m;
        int pos = S[i][j] - 1;
        char[] lcs = new char[pos + 1];

        // Trace the backtracking matrix.
        while (i > 0 || j > 0) {
            switch (R[i][j]) {
                case UP:
                    i--;
                    break;
                case LEFT:
                    j--;
                    break;
                case UP_AND_LEFT:
                    i--;
                    j--;
                    lcs[pos--] = a.charAt(i);
                    break;
            }
        }
        strLcs = new String(lcs);
        return strLcs;
    }
}
