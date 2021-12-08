package com.exam20211208;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PrefixGetter {

    /*
     * Complete the 'searchSuggestions' function below.
     *
     * The function is expected to return a 2D_STRING_ARRAY.
     * The function accepts following parameters:
     *  1. STRING_ARRAY repository
     *  2. STRING customerQuery
     */
    //public List<List<String>> searchSuggestions(List<String> repository, String customerQuery) {
    // 2021/12/08 21:03 1st version
    public List<List<String>> searchSuggestions(List<String> repository, String query) {
        Collections.sort(repository);
        List<List<String>> ans = new ArrayList<>();
        int n = query.length();
        for (int i = 2; i <= n; i++) {
            String key = query.substring(0, i);
            List<String> li = getPrefixOf(repository, key);
            if (debug) {
                ln("#%s : key = %s, %s", i, key, li.size());
            }
            ans.add(li);
        }
        return ans;
    }

    private List<String> getPrefixOf(List<String> list, String prefix) {
        int[] range = getRangeOfPrefix(list, prefix);
        if (debug) {
            ln("range = %s, %s, on %s", range[0], range[1], prefix);
        }
        int head = range[0];
        int tail = Math.min(head + 3, range[1]); //  // take at most 3
        //ans = list.sublist(range[0], range[1]); // ?
        List<String> ans = sublist(list, head, tail);
        return ans;
    }

    private List<String> sublist(List<String> list, int from, int to) {
        List<String> ans = new ArrayList<>();
        for (int i = from; i < to; i++) {
            ans.add(list.get(i).toLowerCase());
        }
        return ans;
    }

    // 2021/12/08 20:37
    // need to sort the list of
    private int[] getRangeOfPrefix(List<String> list, String query) {
        int n = list.size();
        int leftLeft = 0;
        int leftRight = n - 1;
        while (leftLeft < leftRight) {
            int mid = (leftLeft + leftRight) / 2;
            int cmp = list.get(mid).compareTo(query);
            if (cmp < 0) {
                leftLeft = mid + 1;
            } else {
                leftRight = mid;
            }
        }
        if (list.get(leftLeft).startsWith(query) == false) {
            return new int[]{-1, -1};
        }
        int rightLeft = leftLeft;
        int rightRight = n;
        while (rightLeft < rightRight) {
            int mid = (rightLeft + rightRight) / 2;
            boolean cmp = list.get(mid).startsWith(query);
            if (cmp) {
                rightLeft = mid + 1;
            } else {
                rightRight = mid;
            }
        }
        return new int[]{leftLeft, rightLeft};
    }

    private boolean debug = 0 > 0;
    private void ln(String f, Object... p) {
        System.out.println(String.format(f, p));
    }
}
