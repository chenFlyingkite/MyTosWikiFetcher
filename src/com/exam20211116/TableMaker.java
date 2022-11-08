package com.exam20211116;

import flyingkite.log.L;
import flyingkite.tool.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Given an array of string, s, and width w,
// return the maximum column that can fit in the column table
// vertical line of '|' '-' is not taken into the table width
// s = ["cat", "dog", "elephant", "bow", "zoo", "rainbows"]
// w = 14
public class TableMaker {
    public boolean debug = false;

    private class Cases {
        List<String> fields;
        int width;

        Cases(int w, String... s) {
            width = w;
            fields = Arrays.asList(s);
        }

        @Override
        public String toString() {
            return width + " for " + fields;
        }
    }

    public void test() {
        int ans;
        List<Cases> test = new ArrayList<>();
        test.add(new Cases(14, "cat", "dog", "elephant", "bow", "zoo", "rainbows"));
        test.add(new Cases(14, "elephant", "bow", "zoo", "rainbows"));
        test.add(new Cases(7, "abc", "bow", "z", "b", "zaas"));
        TableMaker tm = new TableMaker();
        for (int i = 0; i < test.size(); i++) {
            Cases c = test.get(i);
            L.log("#%d : %s", i, c);
            ans = tm.getMaximumFitColumn(c.fields, c.width);
            L.log("ans = %s", ans);
            tm.printTableMostColumn(c.fields, c.width);
            for (int j = 1; j < c.fields.size(); j++) {
                L.log("table as col %s", j);
                tm.printTable(c.fields, j);
            }
        }
    }

    public void printTableMostColumn(List<String> fields, int width) {
        int col = getMaximumFitColumn(fields, width);
        int n = fields.size();
        int[] len = new int[n];
        for (int i = 0; i < n; i++) {
            len[i] = fields.get(i).length();
        }
        if (col <= 0) {
            L.log("Cannot print table as %s", col);
            return;
        }
        int[] ws = getFitWidth(len, col);
        int row = (int) Math.ceil(1.0F * n / col);
        int now = 0;
        String div = repeat("-", width + col + 1);
        L.log(div);
        for (int i = 0; i < row; i++) {
            StringBuilder sb = new StringBuilder("|");
            for (int j = 0; j < col; j++) {
                if (now < n) {
                    sb.append(String.format("%-" + ws[j] + "s|", fields.get(now)));
                } else {
                    sb.append(repeat(" ", ws[j])).append("|");
                }
                now++;
            }
            L.log(sb.toString());
            L.log(div);
        }
    }

    // Print the table with given column
    public void printTable(List<String> fields, int col) {
        if (col <= 0) {
            L.log("Cannot print table as %s", col);
            return;
        }
        int n = fields.size();
        int[] len = new int[n];
        for (int i = 0; i < n; i++) {
            len[i] = fields.get(i).length();
        }
        int[] ws = getFitWidth(len, col);
        int width = sum(ws);
        int row = (int) Math.ceil(1.0F * n / col);
        int now = 0;
        String div = repeat("-", width + col + 1);
        L.log(div);
        for (int i = 0; i < row; i++) {
            StringBuilder sb = new StringBuilder("|");
            for (int j = 0; j < col; j++) {
                if (now < n) {
                    sb.append(String.format("%-" + ws[j] + "s|", fields.get(now)));
                } else {
                    sb.append(repeat(" ", ws[j])).append("|");
                }
                now++;
            }
            L.log(sb.toString());
            L.log(div);
        }
    }

    // return Regex of "(s){n}"
    private String repeat(String s, int n) {
        return StringUtil.repeat(s, n);
    }

    /**
     * Time complexity = O(N^2), space = O(N)
     * N = fields.length()
     */
    public int getMaximumFitColumn(List<String> fields, int width) {
        if (fields == null) return 0;

        // collect the length of strings, omit its content
        int n = fields.size();
        int[] len = new int[n];
        int end = 0; // start to find from this value
        int sum = 0; // cumulated length = a[0] + .. + a[i]
        for (int i = 0; i < n; i++) {
            len[i] = fields.get(i).length();
            sum += len[i];
            if (sum <= width) {
                end = i+1;
            }
        }
        if (debug) {
            L.log("len = %s", Arrays.toString(len));
        }

        // perform linear search
        for (int i = end; i >= 1; i--) {
        //for (int i = n; i >= 1; i--) {
            if (canFit(len, width, i)) {
                return i;
            }
        }
        return 0;
    }

    // getFitWidth for strings lengths are in <len>, and with column number is <col>
    // [1 2 3 4 5] in 3 => [max(1, 4), max(2, 5), max(3)]
    // Time complexity = O(len.length), space = O(1)
    private int[] getFitWidth(int[] len, int col) {
        int n = len.length;
        int[] max = new int[n];
        for (int i = 0; i < n; i++) {
            max[i % col] = Math.max(max[i % col], len[i]);
        }
        return max;
    }

    private int sum(int[] a) {
        int sum = 0;
        for (int x : a) {
            sum += x;
        }
        return sum;
    }

    private boolean canFit(int[] len, int width, int col) {
        int[] max = getFitWidth(len, col);
        int sum = sum(max);
        return sum <= width;
    }
}
