package com.exam20221108;

import java.util.ArrayList;
import java.util.List;

// source code
public class Expand {
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

    String expand(String s) {
        if (s == null || s.length() == 0) return s;

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
        if (findNum) return null;
        // not same length
        if (nums.size() != text.size()) return null;

        // build the answer
        sb = new StringBuilder();
        for (int i = 0; i < nums.size(); i++) {
            for (int j = 0; j < nums.get(i); j++) {
                sb.append(text.get(i));
            }
        }
        return sb.toString();
    }
}
