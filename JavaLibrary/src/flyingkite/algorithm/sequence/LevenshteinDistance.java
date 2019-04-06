package flyingkite.algorithm.sequence;

/**
 * Two string's edit distance.
 *
 * Informally, the Levenshtein distance between two words is the minimum number
 * of single-character edits (insertions, deletions or substitutions) required
 * to change one word into the other.
 * https://en.wikipedia.org/wiki/Levenshtein_distance
 */
public class LevenshteinDistance implements Runnable {

    private String s1;
    private String s2;
    private boolean done;
    private int[][] d;

    public LevenshteinDistance(String x, String y) {
        if (x == null) {
            throw new NullPointerException("x is null");
        }
        if (y == null) {
            throw new NullPointerException("y is null");
        }
        s1 = x;
        s2 = y;
    }

    @Override
    public void run() {
        if (done) return;

        int n = s1.length();
        int m = s2.length();
        d = new int[n+1][m+1];

        for (int i = 0; i <= n; i++) {
            d[i][0] = i;
        }
        for (int j = 0; j <= m; j++) {
            d[0][j] = j;
        }

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int modi;
                if (s1.charAt(i-1) == s2.charAt(j-1)) {
                    modi = 0;
                } else {
                    modi = 1;
                }
                d[i][j] = Math.min(Math.min(
                        d[i-1][j  ] + 1, // Delete
                        d[i  ][j-1] + 1), // Insert
                        d[i-1][j-1] + modi // Change
                );
            }
        }
        done = true;
    }

    public int get() {
        run();

        int n = s1.length();
        int m = s2.length();
        return d[n][m];
    }
}
