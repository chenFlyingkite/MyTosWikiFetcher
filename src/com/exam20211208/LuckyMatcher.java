package com.exam20211208;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LuckyMatcher {

    // 2021/12/08 21:12
    /*
     *
     * The function is expected to return an INTEGER.
     * The function accepts following parameters:
     *  1. STRING_ARRAY codeList
     *  2. STRING_ARRAY shoppingCart
     *  return 1 = match, 0 = miss
     */
    public int luckyMatcher(List<String> codeList, List<String> shoppingCart) {
        // Write your code here
        return sol1(codeList, shoppingCart);
    }
    // find the wanted order of substring in cart

    private int sol1(List<String> code, List<String> cart) {
        int ans = match(code, cart, 0, 0) ? 1 : 0;
        ln("code = /%s/", code);
        ln("cart = /%s/", cart);
        ln("ans = %s", ans);
        return ans;
    }

    private boolean match(List<String> code, List<String> cart, int codeAt, int cartAt) {
        int n = code.size();
        int m = cart.size();
        if (debug) {
            ln("run match [%s/%s] on %s/%s", codeAt, n, cartAt, m);
        }
        if (cartAt >= m && codeAt < n) {
            if (debug) {
                ln("ans:x1");
            }
            return false; // matched at end
        }
        if (codeAt == n) {
        //if (cartAt <= m && codeAt == n) {
            if (debug) {
                ln("ans:o1");
            }
            return true;
        }
        String[] each = code.get(codeAt).split(" ");
        int z = each.length;
        if (debug) {
            ln("z = %s, code = /%s/, each = /%s/", z, code, Arrays.toString(each));
        }
        for (int i = cartAt; i + z-1 < m; i++) { // *bug*z-1, not z
            boolean ok = pass(cart, each, i);
            if (debug) {
                ln("For %s, %s", i, ok ? "o" : "x");
            }
            if (ok) {
                boolean ans = match(code, cart, codeAt + 1, i + z); // matches each and try on next one
                if (debug) {
                    ln("ans match on %s = %s", i, ans);
                }
                return ans;
            }
        }
        if (debug) {
            ln("ans:o2");
        }
        return false;
    }

    private boolean pass(List<String> list, String[] want, int from) {
        int n = list.size();
        if (debug) {
            ln("from = %s, want = %s, list = %s", from, as(want), list);
        }
        if (from >= n) return false;
        for (int i = 0; i < want.length; i++) {
            if (from + i >= n) return false; // Out of bound
            String key = list.get(from + i);
            if (key.equals(want[i])) {
                // OK
            //} else if ("anything".equals(key)) { // *bug*
            } else if ("anything".equals(want[i])) { // *bug* is want[i], not key
                // OK
            } else {
                return false;// not wanted and not anything
            }
        }
        return true;
    }


    private boolean debug = 0 > 0;
    private String as(String[] s) {
        return Arrays.toString(s);
    }

    private void ln(String f, Object... p) {
        System.out.println(String.format(f, p));
    }

    public void testCase() {
//        test("orange, apple apple, banana anything apple, banana"
//            ,"orange, apple, apple, banana, kiwi, apple, banana");
//        test("orange"
//            ,"mango");
//        test("anything anything, anything anything anything, anything, anything anything anything anything, anything"
//            ,"orange, mango, banana, apricot, apricot, apple, Guava, Blueberry, Guava, Blueberry, Guava");
//        test("kiwi, pear, jackfruit, anything, anything, apricot, banana, orange, anything, watermelon"
//            ,"mango, jackfruit, kiwi, pear, jackfruit, orange, apple, apricot, banana, orange, pear, watermelon");
//        test("apple apple, apple anything apple, apple apple"
//            ,"kiwi, apple, apple, apple, apple, orange, apple, orange, apple, orange, apple, apple");
        test("orange apple apple banana orange apple banana kiwi pear orange jackfruit orange apple apple banana orange apple banana kiwi pear orange jackfruit, orange apple apple banana orange apple banana kiwi pear orange jackfruit, orange apple apple banana orange apple banana kiwi pear orange jackfruit orange apple apple banana orange apple banana kiwi pear orange jackfruit orange apple apple banana orange apple banana kiwi pear orange jackfruit, orange apple apple banana orange apple banana kiwi pear orange jackfruit, kiwi pear anything, jackfruit, anything, apple apple, banana anything apple, banana, orange apple apple banana orange apple banana kiwi pear orange jackfruit orange apple apple banana orange apple banana kiwi pear orange jackfruit, orange apple apple banana orange apple banana kiwi pear orange jackfruit, orange apple apple banana orange apple banana kiwi pear orange jackfruit orange apple apple banana orange apple banana kiwi pear orange jackfruit orange apple apple banana orange apple banana kiwi pear orange jackfruit, orange apple apple banana orange apple banana kiwi pear orange jackfruit, kiwi pear anything, jackfruit, anything, apple apple, banana anything apple, banana, orange apple apple banana orange apple banana kiwi pear orange jackfruit orange apple apple banana orange apple banana kiwi pear orange jackfruit, orange apple apple banana orange apple banana kiwi pear orange jackfruit, orange apple apple banana orange apple banana kiwi pear orange jackfruit orange apple apple banana orange apple banana kiwi pear orange jackfruit orange apple apple banana orange apple banana kiwi pear orange jackfruit, orange apple apple banana orange apple banana kiwi pear orange jackfruit, kiwi pear anything, jackfruit, anything, apple apple, banana anything apple, banana, orange apple apple banana orange apple banana kiwi pear orange jackfruit orange apple apple banana orange apple banana kiwi pear orange jackfruit, orange apple apple banana orange apple banana kiwi pear orange jackfruit, orange apple apple banana orange apple banana kiwi pear orange jackfruit orange apple apple banana orange apple banana kiwi pear orange jackfruit orange apple apple banana orange apple banana kiwi pear orange jackfruit, orange apple apple banana orange apple banana kiwi pear orange jackfruit, kiwi pear anything, jackfruit, anything, apple apple, banana anything apple, banana"
            ,"orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, kiwi, pear, kiwi, jackfruit, grapes, apple, apple, banana, apple, apple, banana, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, kiwi, pear, kiwi, jackfruit, grapes, apple, apple, banana, apple, apple, banana, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, kiwi, pear, kiwi, jackfruit, grapes, apple, apple, banana, apple, apple, banana, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, kiwi, pear, kiwi, jackfruit, grapes, apple, apple, banana, apple, apple, banana");
        test("apple apple"
            ,"apple, pear, orange, apple");
    }

    private void test(String co, String cr) {
        luckyMatcher(code(co), code(cr));
    }

    private List<String> code(String s) {
        String[] si = s.split(", ");
        List<String> li = new ArrayList<>();
        for (int i = 0; i < si.length; i++) {
            li.add(si[i]);
        }
        return li;
    }
//    code = /[orange, apple apple, banana anything apple, banana]/
//    cart = /[orange, apple, apple, banana, kiwi, apple, banana]/
//1
//    code = /[orange]/
//    cart = /[mango]/
//0
//    code = /[anything anything, anything anything anything, anything, anything anything anything anything, anything]/
//    cart = /[orange, mango, banana, apricot, apricot, apple, Guava, Blueberry, Guava, Blueberry, Guava]/
//1
//    code = /[kiwi, pear, jackfruit, anything, anything, apricot, banana, orange, anything, watermelon]/
//    cart = /[mango, jackfruit, kiwi, pear, jackfruit, orange, apple, apricot, banana, orange, pear, watermelon]/
//1
//    code = /[apple apple, apple anything apple, apple apple]/
//    cart = /[kiwi, apple, apple, apple, apple, orange, apple, orange, apple, orange, apple, apple]/
//1
//    code = /[orange apple apple banana orange apple banana kiwi pear orange jackfruit orange apple apple banana orange apple banana kiwi pear orange jackfruit, orange apple apple banana orange apple banana kiwi pear orange jackfruit, orange apple apple banana orange apple banana kiwi pear orange jackfruit orange apple apple banana orange apple banana kiwi pear orange jackfruit orange apple apple banana orange apple banana kiwi pear orange jackfruit, orange apple apple banana orange apple banana kiwi pear orange jackfruit, kiwi pear anything, jackfruit, anything, apple apple, banana anything apple, banana, orange apple apple banana orange apple banana kiwi pear orange jackfruit orange apple apple banana orange apple banana kiwi pear orange jackfruit, orange apple apple banana orange apple banana kiwi pear orange jackfruit, orange apple apple banana orange apple banana kiwi pear orange jackfruit orange apple apple banana orange apple banana kiwi pear orange jackfruit orange apple apple banana orange apple banana kiwi pear orange jackfruit, orange apple apple banana orange apple banana kiwi pear orange jackfruit, kiwi pear anything, jackfruit, anything, apple apple, banana anything apple, banana, orange apple apple banana orange apple banana kiwi pear orange jackfruit orange apple apple banana orange apple banana kiwi pear orange jackfruit, orange apple apple banana orange apple banana kiwi pear orange jackfruit, orange apple apple banana orange apple banana kiwi pear orange jackfruit orange apple apple banana orange apple banana kiwi pear orange jackfruit orange apple apple banana orange apple banana kiwi pear orange jackfruit, orange apple apple banana orange apple banana kiwi pear orange jackfruit, kiwi pear anything, jackfruit, anything, apple apple, banana anything apple, banana, orange apple apple banana orange apple banana kiwi pear orange jackfruit orange apple apple banana orange apple banana kiwi pear orange jackfruit, orange apple apple banana orange apple banana kiwi pear orange jackfruit, orange apple apple banana orange apple banana kiwi pear orange jackfruit orange apple apple banana orange apple banana kiwi pear orange jackfruit orange apple apple banana orange apple banana kiwi pear orange jackfruit, orange apple apple banana orange apple banana kiwi pear orange jackfruit, kiwi pear anything, jackfruit, anything, apple apple, banana anything apple, banana]/
//    cart = /[orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, kiwi, pear, kiwi, jackfruit, grapes, apple, apple, banana, apple, apple, banana, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, kiwi, pear, kiwi, jackfruit, grapes, apple, apple, banana, apple, apple, banana, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, kiwi, pear, kiwi, jackfruit, grapes, apple, apple, banana, apple, apple, banana, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, kiwi, pear, kiwi, jackfruit, grapes, apple, apple, banana, apple, apple, banana]/
//1 (too long for human...)
//    code = /[apple apple]/
//    cart = /[apple, pear, orange, apple]/
// 0

}
//--
//Compiled successfully.51/58 test cases passed
//Use print or log statements to debug why your hidden test cases are failing. Hidden test cases  are used to evaluate if your code can handle different scenarios, including corner cases.
//    code = /[orange, apple apple, banana anything apple, banana]/
//    cart = /[orange, apple, apple, banana, kiwi, apple, banana]/
//    code = /[orange]/
//    cart = /[mango]/
//    code = /[anything anything, anything anything anything, anything, anything anything anything anything, anything]/
//    cart = /[orange, mango, banana, apricot, apricot, apple, Guava, Blueberry, Guava, Blueberry, Guava]/
//    code = /[kiwi, pear, jackfruit, anything, anything, apricot, banana, orange, anything, watermelon]/
//    cart = /[mango, jackfruit, kiwi, pear, jackfruit, orange, apple, apricot, banana, orange, pear, watermelon]/
//    code = /[apple apple, apple anything apple, apple apple]/
//    cart = /[kiwi, apple, apple, apple, apple, orange, apple, orange, apple, orange, apple, apple]/
//    code = /[orange apple apple banana orange apple banana kiwi pear orange jackfruit orange apple apple banana orange apple banana kiwi pear orange jackfruit, orange apple apple banana orange apple banana kiwi pear orange jackfruit, orange apple apple banana orange apple banana kiwi pear orange jackfruit orange apple apple banana orange apple banana kiwi pear orange jackfruit orange apple apple banana orange apple banana kiwi pear orange jackfruit, orange apple apple banana orange apple banana kiwi pear orange jackfruit, kiwi pear anything, jackfruit, anything, apple apple, banana anything apple, banana, orange apple apple banana orange apple banana kiwi pear orange jackfruit orange apple apple banana orange apple banana kiwi pear orange jackfruit, orange apple apple banana orange apple banana kiwi pear orange jackfruit, orange apple apple banana orange apple banana kiwi pear orange jackfruit orange apple apple banana orange apple banana kiwi pear orange jackfruit orange apple apple banana orange apple banana kiwi pear orange jackfruit, orange apple apple banana orange apple banana kiwi pear orange jackfruit, kiwi pear anything, jackfruit, anything, apple apple, banana anything apple, banana, orange apple apple banana orange apple banana kiwi pear orange jackfruit orange apple apple banana orange apple banana kiwi pear orange jackfruit, orange apple apple banana orange apple banana kiwi pear orange jackfruit, orange apple apple banana orange apple banana kiwi pear orange jackfruit orange apple apple banana orange apple banana kiwi pear orange jackfruit orange apple apple banana orange apple banana kiwi pear orange jackfruit, orange apple apple banana orange apple banana kiwi pear orange jackfruit, kiwi pear anything, jackfruit, anything, apple apple, banana anything apple, banana, orange apple apple banana orange apple banana kiwi pear orange jackfruit orange apple apple banana orange apple banana kiwi pear orange jackfruit, orange apple apple banana orange apple banana kiwi pear orange jackfruit, orange apple apple banana orange apple banana kiwi pear orange jackfruit orange apple apple banana orange apple banana kiwi pear orange jackfruit orange apple apple banana orange apple banana kiwi pear orange jackfruit, orange apple apple banana orange apple banana kiwi pear orange jackfruit, kiwi pear anything, jackfruit, anything, apple apple, banana anything apple, banana]/
//    cart = /[orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, kiwi, pear, kiwi, jackfruit, grapes, apple, apple, banana, apple, apple, banana, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, kiwi, pear, kiwi, jackfruit, grapes, apple, apple, banana, apple, apple, banana, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, kiwi, pear, kiwi, jackfruit, grapes, apple, apple, banana, apple, apple, banana, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, orange, apple, apple, banana, orange, apple, banana, kiwi, pear, orange, jackfruit, kiwi, pear, kiwi, jackfruit, grapes, apple, apple, banana, apple, apple, banana]/
//    code = /[apple apple]/
//    cart = /[apple, pear, orange, apple]/
