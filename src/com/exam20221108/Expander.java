package com.exam20221108;

import flyingkite.log.L;
import flyingkite.tool.TicTac2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Expander {

    // https://codeshare.io/gLkP0B
    // https://leetcode.com/problems/decode-string/
    public static void main(String[] args) {
        L.log("Hello world");
        test();
    }

    private static void test() {
        TicTac2 clock = new TicTac2();
        String[] in = {"2", "2[a]", "123", "1[a]2[b]3[c]", "10[x]5[y]7", "[", "]", "-1[5]", "2[[]", "9999999[]", "10000[a]"};
        clock.tic();
        for (int i = 0; i < in.length; i++) {
            String input = in[i];
            clock.tic();
            String ans = expand(input);
            clock.tac("#%s : %s = %s", i, input, ans);
        }
        clock.tac("Stage 1 done : try specified test cases");

        String s = "1[a]2[5]3[c]4[d]5[e]6[[]";
        clock.tic();
        for (int i = 0; i < s.length(); i++) {
            for (int j = i+1; j <= s.length(); j++) {
                String input = s.substring(i, j);
                clock.tic();
                String ans = expand(input);
                clock.tac("#(%s, %s) : %s = %s", i, j, input, ans);
                clock.tic();
                String ans2 = sol2(input);
                clock.tac("#(%s, %s) : %s = %s", i, j, input, ans2);
                boolean ok = false;
                ok |= ans == null && ans2 == null;
                if (ans != null) {
                    ok |= ans.equals(ans2);
                }
                if (!ok) {
                    L.log("X_X ans = %s, ans2 = %s", ans, ans2);
                }
            }
        }
        clock.tac("Stage 2 done: Try each substrings in one");
        L.log("-------");
        String pool = "0123456789[][][][][]";
        Random r = new Random();
        int max = 6 + r.nextInt(5);
        StringBuilder sb = null;
        clock.tic();
        for (int i = 0; i < 10; i++) {
            // make string
            sb = new StringBuilder();
            for (int j = 0; j < max; j++) {
                int at = r.nextInt(pool.length());
                sb.append(pool.charAt(at));
            }
            String input = sb.toString();

            clock.tic();
            String ans = expand(input);
            clock.tac("#%s : %s = %s", i, input, ans);
            clock.tic();
            String ans2 = sol2(input);
            clock.tac("#%s : %s = %s", i, input, ans2);
            boolean ok = false;
            ok |= ans == null && ans2 == null;
            if (ans != null) {
                ok |= ans.equals(ans2);
            }
            if (!ok) {
                L.log("X_X ans = %s, ans2 = %s", ans, ans2);
            }
        }
        clock.tac("Stage 3 done : random test");
    }

    // 2022/11/08 15:55 ~ 16:05
    static String sol2(String s) {
        if (s == null || s.length() == 0) return null;

        int n = s.length();
        int now = 0;
        int l = s.indexOf('[', now);
        int r = s.indexOf(']', now);
        List<Integer> nums = new ArrayList<>();
        List<String> text = new ArrayList<>();
        while (0 <= l && l < r && r <= n) {
            String vx = s.substring(now, l);
            int x = 0;
            try {
                x = Integer.parseInt(vx);
            } catch (NumberFormatException e) {
                return null;
            }
            String tx = s.substring(l+1, r);
            nums.add(x);
            text.add(tx);
            now = r + 1;
            l = s.indexOf('[', now);
            r = s.indexOf(']', now);
            if (l*r < 0) {
                return null;
            } else {
                if (l < 0 && r < 0 && now > s.length()) {
                    return null;
                }
            }
        }
        if (nums.size() != text.size()) return null;
        if (nums.size() == 0) return null;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nums.size(); i++) {
            for (int j = 0; j < nums.get(i); j++) {
                sb.append(text.get(i));
            }
        }
        return sb.toString();
    }

    /*
    [1-9][abc]
            13[acc]2[b]
    accaccaccbb
    Null :
            2 => null
            2[[]]
            -2[]
    Valid :
            2[a] = "aa"
            2[a]3[b] = "aabbb"
            2[2] = "22"
    number = [1-9]+
            2[asd]
            2[] = ""
            (number[text])*
// String s, parse
    */

    static String expand(String s) {
        if (s == null || s.length() == 0) return null;

        int n = s.length();
        List<Integer> nums = new ArrayList<>();
        List<String> text = new ArrayList<>();
        int v = -1;
        StringBuilder sb = null;
        boolean findNum = true;
        // 1[]
        // 2[a] => [2] & [a]
        // 102[abs]
        // 3[v]4[qwer]
        // 4
        for (int i = 0; i < n; i++) {
            char x = s.charAt(i);
            if (findNum) {
                // num
                if ('0' <= x && x <= '9') {
                    if (v == -1) {
                        v = 0;
                    }
                    v = 10 * v + (x - '0');
                } else if (x == '[' && v >= 0) { // "["
                    nums.add(v);  // nums = [1]
                    findNum = false;
                    sb = new StringBuilder();
                    v = -1; // reset
                } else {
                    return null; // "["
                }
            } else {
                // text
                if (x == '[') { // "2[a]"
                    return null;
                } else if (x == ']') { // "1[]"
                    if (sb == null) return null;
                    text.add(sb.toString());
                    sb = null;
                    findNum = true;
                } else { //
                    if (sb == null) return null;
                    sb.append(x);
                }
            }
        }

        if (v >= 0) return null; // "1"
        // not same length
        if (nums.size() != text.size()) return null; // "1["

        // build the answer
        sb = new StringBuilder();
        for (int i = 0; i < nums.size(); i++) {
            String each = text.get(i);
            int times = nums.get(i);
            sb.append(repeat(each, times));
        }
        return sb.toString();
    }

    // s^n
    private static StringBuilder repeat(String s, int n) {
        if (s == null) return null;
        StringBuilder ans = new StringBuilder();
        if (s.length() == 0) return ans;
        StringBuilder pow = new StringBuilder(s);
        int now = n;
        while (now > 0) {
            if (now % 2 == 1) {
                ans.append(pow);
            }
            pow.append(pow);
            now /= 2;
        }
        return ans;
    }
}
