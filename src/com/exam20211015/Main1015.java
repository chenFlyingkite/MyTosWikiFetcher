package com.exam20211015;

import flyingkite.log.L;
import flyingkite.tool.TicTac2;

import java.util.HashMap;
import java.util.Map;

public class Main1015 implements Runnable {

    // This is for 2021/10/15 interview
    @Override
    public void run() {
        int[] ns = {
                2,4,8
                //,16,32 // reasonable time within 3 minutes
                //,64,128 // is nearly OOM
        };
        TicTac2 clk = new TicTac2();
        for (int n : ns) {
            clk.tic();
            for (int i = 0; i < n; i++) {
                clk.tic();
                String s = win(i, 0, n);
                //L.log("win(%s, %s, %s) = %s", i, 0, n, s);
                clk.tac("win(%s, 0, %s) ok", i, n);
            }
            clk.tac("win(all, 0, %s) ok", n);
        }
    }

    // win(0, 0, 8) =
    // ((P[0, 1]) * (P[0, 2] * P[2, 3] + P[0, 3] * P[3, 2])) *
    // (P[0, 4] * (P[4, 5]) * (P[4, 6] * P[6, 7] + P[4, 7] * P[7, 6])
    // + P[0, 5] * (P[5, 4]) * (P[5, 6] * P[6, 7] + P[5, 7] * P[7, 6])
    // + P[0, 6] * (P[6, 7]) * (P[6, 4] * P[4, 5] + P[6, 5] * P[5, 4])
    // + P[0, 7] * (P[7, 6]) * (P[7, 4] * P[4, 5] + P[7, 5] * P[5, 4]))
    private Map<String, String> winCache = new HashMap<>();// x_i_j : string
    // Return calculation of player x to win in game[i : j) like
    // player 1 to win in first 4 people game of [0, 1, 2, 3],
    // -> called win(1, 0, 4), i inclusive, j exclusive
    private String win(int x, int i, int j) {
        boolean valid = i <= x && x < j;
        if (!valid) return "";
        String key = String.format("%s_%s_%s", x, i, j);
        if (winCache.containsKey(key)) {
            return winCache.get(key);
        }
        L.log("For (%s, %s, %s)", x, i, j);

        String value = "";
        int len = j - i;
        if (len == 1) {
            value = String.format("P[%s, %s]", x, x); // returned Here
        } else if (len == 2) {
            value = String.format("P[%s, %s]", x, x == i ? (i+1) : i); // returned Here
        } else {
            // ---------- // if x falls in left part, then x wins in left part and beats every lead y in right part
            // i   m    j // and same as y
            //   x    y
            int m = (i + j) / 2;
            if (x < m) {
                // x wins in left part
                String win_xim = win(x, i, m);
                StringBuilder s = new StringBuilder();
                // and x wins in right part each one
                for (int k = m; k < j; k++) {
                    String win_kmj = win(k, m, j);
                    if (s.length() > 0) {
                        s.append(" + ");
                    }
                    s.append(String.format("P[%s, %s] * %s", x, k, win_kmj));
                }
                value = String.format("(%s) * (%s)", win_xim, s); // returned Here
            } else {
                // x wins in right part
                String win_xmj = win(x, m, j);
                // and x wins in left part each one
                StringBuilder s = new StringBuilder();
                for (int k = i; k < m; k++) {
                    String win_kim = win(k, i, m);//
                    if (s.length() > 0) {
                        s.append(" + ");
                    }
                    s.append(String.format("P[%s, %s] * %s", x, k, win_kim));
                }
                value = String.format("(%s) * (%s)", win_xmj, s); // returned Here
            }
        }
        winCache.put(key, value);
        return value;
    }
}
