package com.basic;

import flyingkite.log.L;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

public class ParenthesisChecker {

    public static void main(String[] args) {
        // test cases
        String src = "()[]{}<>";
        src = "()";
        String[] ss = { null, "", "(", ")", "()", ")(", "((", "))", "[][]", "([)]",
                "(()", "))(", "(())",
        };
        for (int i = 0; i < ss.length; i++) {
            boolean sol = check(ss[i], src);
            L.log("#%s : %s for %s", i, sol, ss[i]);
        }
        Random r = new Random();
        for (int i = 0; i < 1000; i++) {
            int len = 2 * (1 + r.nextInt(5));
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < len; j++) {
                sb.append(src.charAt(r.nextInt(src.length())));
            }
            String q = sb.toString();
            boolean sol = check(q, src);
            L.log("#%s : %s for %s", i, sol, q);
        }
    }

    // 2022/10/28 16:18 ~ 16:32
    // pair = "()[]{}<>" , (head,tail)*n
    public static boolean check(String s, String pair) {
        if (s == null) return false;
        // build pair map of head -> tail & tail -> head
        Map<Character, Character> toHead = new HashMap<>();
        Map<Character, Character> toTail = new HashMap<>();
        char[] cs = pair.toCharArray();
        for (int i = 0; i < cs.length; i++) {
            char k = cs[i];
            if (i + 1 < cs.length) {
                char v = cs[i + 1];
                toTail.put(k, v);
                toHead.put(v, k);
                i++;
            }
        }
        cs = s.toCharArray();
        int n = s.length();
        Stack<Character> expect = new Stack<>();
        for (int i = 0; i < n; i++) {
            char x = cs[i];
            if (toTail.containsKey(x)) {
                // x == head
                // expect += tail
                expect.push(toTail.get(x));
            } else if (toHead.containsKey(x)) {
                // x = tail
                // pop if expect.peek = x
                if (expect.size() > 0 && expect.peek() == x) {
                    expect.pop();
                } else {
                    return false;
                }
            }
        }
        return expect.size() == 0;
    }
}
