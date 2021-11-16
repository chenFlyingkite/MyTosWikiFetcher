package com.exam1116;

import flyingkite.log.L;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main1116 {
    public static void main(String[] args) {
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

    private static class Cases {
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

}
