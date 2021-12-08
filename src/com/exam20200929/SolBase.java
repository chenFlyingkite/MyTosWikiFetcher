package com.exam20200929;

import java.util.ArrayList;
import java.util.List;

public class SolBase implements MaximumPop {
    private List<Integer> list = new ArrayList<>();

    @Override
    public void push(int v) {
        list.add(v);
    }

    @Override
    public boolean canPop() {
        return list.size() > 0;
    }

    @Override
    public int popLast() {
        if (canPop()) {
            int t = list.remove(list.size() - 1);
            return t;
        } else {
            return Integer.MIN_VALUE;
        }
    }

    @Override
    public int popMaximum() {
        if (canPop()) {
            int max = list.get(0);
            for (int i = 1; i < list.size(); i++) {
                max = Math.max(max, list.get(i));
            }
            List<Integer> li = new ArrayList<>();
            for (int x : list) {
                if (x != max) {
                    li.add(x);
                }
            }
            list = li;
            return max;
        } else {
            return Integer.MIN_VALUE;
        }
    }

    @Override
    public String toString() {
        return _fmt("list(%s) = %s", list.size(), list);
    }
}
