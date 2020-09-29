package com.exam0929;

import flyingkite.log.L;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class MySol implements MaximumPop {
    private List<Integer> list = new ArrayList<>();
    private TreeMap<Integer, List<Integer>> position = new TreeMap<>();
    private int hole = 0;

    @Override
    public void push(int v) {
        // v is possibly be popped
        if (!position.containsKey(v)) {
            applyTrim();
        }
        int k = list.size();
        list.add(v);
        if (position.containsKey(v)) {
            position.get(v).add(k);
        } else {
            List<Integer> li = new ArrayList<>();
            li.add(k);
            position.put(v, li);
        }
    }

    @Override
    public boolean canPop() {
        return list.size() > 0;
    }

    @Override
    public int popLast() {
        if (canPop()) {
            clearTail();
            // update position of remove position[v]
            int v = list.remove(list.size() - 1);
            List<Integer> li = position.get(v);
            li.remove(li.size() - 1);
            if (li.isEmpty()) {
                position.remove(v);
            }
            return v;
        } else {
            return Integer.MIN_VALUE;
        }
    }

    @Override
    public int popMaximum() {
        if (canPop()) {
            int mx = position.lastKey();
            List<Integer> maxi = position.get(mx);
            hole += maxi.size();
            position.remove(mx);
            clearTail();
            trimToSize();
            return mx;
        } else {
            return Integer.MIN_VALUE;
        }
    }

    @Override
    public String toString() {
        return _fmt("list(%s) = %s\nhole = %s, map = %s\nreal = %s", list.size(), list, hole, position, list.size() - hole);
    }

    private void clearTail() {
        for (int i = list.size() - 1; i >= 0; i--) {
            int x = list.get(i);
            if (position.containsKey(x)) {
                return;
            } else {
                list.remove(i);
                hole--;
            }
        }
    }

    private void check() {
        if (1 > 0) return;
        int ln = list.size() - hole;
        int sum = 0;
        for (int x : position.keySet()) {
            sum += position.get(x).size();
        }
        if (ln != sum) {
            L.log("---- Wrong ----");
            L.log("ln = %s, sum = %s", ln, sum);
            L.log("%s", toString());
            L.log("----       ----");
            try {
                throw new IllegalStateException();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void applyTrim() {
        if (hole == 0) {
            L.log("No trim");
            return;
        }
        List<Integer> newList = new ArrayList<>();
        TreeMap<Integer, List<Integer>> newPosition = new TreeMap<>();
        for (int i = 0; i < list.size(); i++) {
            int x = list.get(i);
            if (position.containsKey(x)) {
                int k = newList.size();
                newList.add(x);
                if (newPosition.containsKey(x)) {
                    newPosition.get(x).add(k);
                } else {
                    List<Integer> li = new ArrayList<>();
                    li.add(k);
                    newPosition.put(x, li);
                }
            }
        }
        list = newList;
        position = newPosition;
        hole = 0;
    }

    private void trimToSize() {
        boolean shouldTrim = hole >= list.size() / 2;
        if (shouldTrim) {
            applyTrim();
        }
    }

}
