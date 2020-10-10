package com.exam0929;

import flyingkite.log.L;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MySol implements MaximumPop {
    // concrete data
    private List<Integer> list = new ArrayList<>();
    // list's positions as {ki = {ai1, ai2, ... aim}, ..}
    private Map<Integer, List<Integer>> position = new HashMap<>();
    // keys = keySet of position
    private List<Integer> keys = new ArrayList<>();
    private int hole = 0;

    // For v not exists in list (first new value), O(log(Key size))
    // For v exists in list, O(1)
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
            // update key;
            int x = Collections.binarySearch(keys, v);
            keys.add(-x-1, v);
        }
    }

    @Override
    public boolean canPop() {
        return list.size() > 0;
    }

    // For list has no other values of popped value, P(log(KeySize))
    // For list has entry for value popped, O(1)
    @Override
    public int popLast() {
        if (canPop()) {
            clearTail();
            // update position of remove position[v]
            int v = list.remove(list.size() - 1);
            List<Integer> li = position.get(v);
            li.remove(li.size() - 1);
            // last value to remove
            if (li.isEmpty()) {
                position.remove(v);
                int at = Collections.binarySearch(keys, v);
                keys.remove(at);
            }
            return v;
        } else {
            return Integer.MIN_VALUE;
        }
    }

    // O(1)
    @Override
    public int popMaximum() {
        if (canPop()) {
            int mx = keys.remove(keys.size() - 1);
            List<Integer> maxi = position.get(mx);
            hole += maxi.size();
            position.remove(mx);
            trimToSize();
            return mx;
        } else {
            return Integer.MIN_VALUE;
        }
    }

    @Override
    public String toString() {
        return _fmt("list(%s) = %s\nhole = %s, map = %s\nreal = %s keys = %s", list.size(), list, hole, position, list.size() - hole, keys);
    }

    // O(tail empty) <= O(list.size())
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

    // O(list.size() - hole)
    public void applyTrim() {
        if (hole == 0) {
            L.log("No trim");
            return;
        }
        // rebuild data
        List<Integer> newList = new ArrayList<>();
        List<Integer> newKeys = new ArrayList<>();
        TreeMap<Integer, List<Integer>> newPosition = new TreeMap<>();
        for (int i = 0; i < list.size(); i++) {
            int x = list.get(i);
            if (position.containsKey(x)) {
                int k = newList.size();
                newList.add(x);
                if (newPosition.containsKey(x)) {
                    newPosition.get(x).add(k);
                } else {
                    // log(k)
                    List<Integer> li = new ArrayList<>();
                    li.add(k);
                    newPosition.put(x, li);
                    // update key;
                    int at = Collections.binarySearch(newKeys, x);
                    newKeys.add(-at-1, x);
                }
            }
        }
        list = newList;
        keys = newKeys;
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
