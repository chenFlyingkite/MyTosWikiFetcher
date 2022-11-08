package flyingkite.gov.interior;

import flyingkite.log.L;
import flyingkite.math.MathUtil;
import flyingkite.tool.TextUtil;
import flyingkite.tool.TicTac2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Taiwan's national ID card number
// https://zh.wikipedia.org/wiki/%E4%B8%AD%E8%8F%AF%E6%B0%91%E5%9C%8B%E5%9C%8B%E6%B0%91%E8%BA%AB%E5%88%86%E8%AD%89
// https://en.wikipedia.org/wiki/National_identification_card_(Taiwan)
// test
// http://120.105.184.250/peiyuli/lesson-40.htm
public class NationalIDCard {
    public static final NationalIDCard me = new NationalIDCard();

    // Family register
    // Household Registration,
    // 代碼 -> 數值 縣市 (停止賦配日期)
    private static final Map<Character, List<String>> houseRegion = new HashMap<>();
    private static final Map<Character, Integer> regionNumber = new HashMap<>();
    // Digit key spaces
    private static final List<Character> keysAZ = new ArrayList<>();
    private static final List<Character> keys12 = new ArrayList<>();
    private static final List<Character> keys09 = new ArrayList<>();
    private static final int[] weight = {1, 9, 8, 7, 6, 5, 4, 3, 2, 1, 1};
    private boolean log = false;

    public void testCases() {
//        testPossible();
//        testIsValid();
    }

    private void testPossible() {
        // no log time, solution/space
        String[] ss = {
                "*123456789", // 3/26
                "A*23456789", // 1/2
                "**23456789", // 0ms, 6/52
                "a1_3_5_789", // 30ms, 100/1000
                //"a1_3_5_7_9", // 6ms, 10^3 / 10^4
                //"a12345____", // 6ms, 10^3 / 10^4
                //"a1234_____", // 6ms, 10^4/10^5
                //"a123______", // 178ms, 10^5/10^6
                //"a12_______", // 1413ms, 10^6/10^7
                //"_12_______", // 19514ms, 26*10^6 / 26*10^7
                //"_1________", // OOM
        };
        //testPossible: #2 : **23456789, 6 in ans = [A123456789, J223456789, M123456789, V223456789, W123456789, X223456789]
        TicTac2 clock = new TicTac2();
        for (int i = 0; i < ss.length; i++) {
            String s = ss[i];
            clock.tic();
            List<String> ans = findPossible(s);
            clock.tac("testPossible: #%s : %s, ans.size = %s", i, s, ans.size());
            L.log("testPossible: #%s : %s, ans.size = %s", i, s, ans.size());
            L.log("ans = %s", ans);
        }
    }

    private void testIsValid() {
        String[] ss = {
                "A123456789", "a123456789", "10123456789", "a23456789", "a234567890", "A987654321",
        };
        for (int i = 0; i < ss.length; i++) {
            boolean ok = isValid(ss[i]);
            L.log("#%s = %s : %s", i, ss[i], ok);
        }
    }

    public List<String> findPossible(String a1_3_5_7_9) {
        String id = normalize(a1_3_5_7_9);
        List<String> ans = new ArrayList<>();
        List<String> visit = new ArrayList<>();
        char[] cs = id.toCharArray();
        int at = nextUnknown(-1, cs);
        L.log("at = %s", at);
        find(at, cs, ans, visit);
        L.log("visit = %s items", visit.size());
        return ans;
    }

    private int nextUnknown(int k, char[] cs) {
        int n = cs.length;
        int begin = Math.max(k+1, 0);
        for (int i = begin; i < n; i++) {
            char c = cs[i];
            if (isUnknown(c)) {
                return i;
            }
        }
        return n;
    }

    private boolean is09(char c) {
        return MathUtil.isInRangeInclusive(c, '0', '9');
    }

    private boolean isAZaz(char c) {
        if (MathUtil.isInRangeInclusive(c, 'a', 'z')) {
            return true;
        }
        if (MathUtil.isInRangeInclusive(c, 'A', 'Z')) {
            return true;
        }
        return false;
    }

    private String normalize(String s) {
        if (isNormalized(s)) {
            return s;
        }

        char[] cs = s.toCharArray();
        cs = normalize(cs);
        return String.valueOf(cs);
    }

    private char[] normalize(char[] cs) {
        if (isNormalized(cs)) {
            return cs;
        }
        cs[0] = Character.toUpperCase(cs[0]);
        return cs;
    }

    private boolean isNormalized(String s) {
        if (TextUtil.isEmpty(s)) return false;

        return Character.isUpperCase(s.charAt(0));
    }

    private boolean isNormalized(char[] cs) {
        if (cs == null || cs.length < 1) return false;

        return Character.isUpperCase(cs[0]);
    }

    private boolean isKnown(char c) {
        return is09(c) || houseRegion.containsKey(Character.toUpperCase(c));
    }

    private boolean isUnknown(char c) {
        return !isKnown(c);
    }

    private List<Character> getKeys(int n) {
        if (n == 0) {
            return keysAZ;
        } else if (n == 1) {
            // sex is 1/2 or all?
            //return keys09;
            return keys12;
        } else {
            return keys09;
        }
    }

    private void find(int k, char[] cs, List<String> now, List<String> visit) {
        if (log) {
            L.log("find cs[%s], now = %s, %s", k, now.size(), Arrays.toString(cs));
        }
        int n = cs.length;
        if (k < 0 || n <= k) { // out of bound
            boolean isValid = isValid(cs);
            String ss = String.valueOf(cs);
            if (log) {
                L.log("valid = %s for %s", isValid, ss);
            }
            visit.add(ss);
            if (isValid) {
                now.add(ss);
            } else {

            }
        } else {
            List<Character> keys = getKeys(k);
            for (int i = 0; i < keys.size(); i++) {
                char ki = keys.get(i);
                char x = cs[k];
                cs[k] = ki;
                int next = nextUnknown(k, cs);
                find(next, cs, now, visit);
                cs[k] = x;
            }
        }
    }

    /**
     * ID = "A123456789" or "a123456789"
     * A轉換為數值是10，將每一碼拆開後分別編號：
     * ID1 = [n1, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11]
     * ID1 = [ 1,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9]
     * Mul = [ 1, 9, 8, 7, 6, 5, 4, 3, 2, 1, 1]
     * true if ID1.*Mul % 10 == 0
     *
     * Valid input = upper/lower case OK, like ID
     * or decoded as ID1
     */
    public boolean isValid(String a123456789) {
        char[] cs = a123456789.toCharArray();
        return isValid(cs);
    }

    /**
     * @see #isValid(String)
     */
    public boolean isValid(char[] a123456789) {
        if (a123456789 == null) {
            return false;
        }

        char[] cs = normalize(a123456789);
        String s = String.valueOf(cs);
        // make cs as input
        int n = cs.length;
        // validate number space
        for (int i = 0; i < n; i++) {
            char x = cs[i];
            boolean ok;
            if (i == 0) {
                ok = isAZaz(x);
            } else {
                ok = is09(x);
            }
            if (!ok) {
                return false;
            }
        }

        int[] val = new int[n+1];
        Arrays.fill(val, -1);
        char c0 = cs[0];
        boolean ok = n == 10 && houseRegion.containsKey(c0);
        if (!ok) {
            return false;
        }

        char[] vs = houseRegion.get(c0).get(0).toCharArray();
        val[0] = vs[0] - '0';
        val[1] = vs[1] - '0';
        for (int i = 1; i < n; i++) {
            int k = cs[i] - '0';
            val[i+1] = k;
        }

        // evaluation check value
        int check = getCheck(val);
        //L.log("check = %s, %s", s, check);
        return check % 10 == 0;
    }

    private boolean validateRegex(String s) {
        return s.matches("[A-Za-z][12](\\d){8}");
    }

    // return Integer.MIN_VALUE if invalid values
    private int getCheck(int[] val) {
        final int[] mi = weight;

        int len = val.length;
        // evaluation check value
        int check = 0;
        for (int i = 0; i < len; i++) {
            int v = val[i];
            boolean ok = 0 <= v && v <= 9;
            if (!ok) {
                return Integer.MIN_VALUE;
            }
            check += mi[i] * v;
        }
        return check;
    }

    private static void makeEvalCode() {
        String[] code0 = { // 0~4, 5~9
                "B N Z", "A M W", "K L Y", "J V X", "H U",
                "G T", "F S", "E R", "D O Q", "C I P",};
        for (int i = 0; i < code0.length; i++) {
            numberAdd(i, code0[i]);
        }
    }

    private static void numberAdd(int v, String chars) {
        String[] ss = chars.split(" ");
        for (String s : ss) {
            char c = s.charAt(0);
            regionNumber.put(c, v);
        }
    }

    private static void houseRegionPut(Character c, String... msg) {
        houseRegion.put(c, Arrays.asList(msg));
    }

    private static void makeHouseRegion() {
        if (houseRegion.isEmpty()) {
            houseRegionPut('A', "10", "臺北市");
            houseRegionPut('B', "11", "臺中市");
            houseRegionPut('C', "12", "基隆市");
            houseRegionPut('D', "13", "臺南市");
            houseRegionPut('E', "14", "高雄市");
            houseRegionPut('F', "15", "新北市");
            houseRegionPut('G', "16", "宜蘭縣");
            houseRegionPut('H', "17", "桃園市");
            houseRegionPut('I', "34", "嘉義市");
            houseRegionPut('J', "18", "新竹縣");
            houseRegionPut('K', "19", "苗栗縣");
            houseRegionPut('M', "21", "南投縣");
            houseRegionPut('N', "22", "彰化縣");
            houseRegionPut('O', "35", "新竹市");
            houseRegionPut('P', "23", "雲林縣");
            houseRegionPut('Q', "24", "嘉義縣");
            houseRegionPut('T', "27", "屏東縣");
            houseRegionPut('U', "28", "花蓮縣");
            houseRegionPut('V', "29", "臺東縣");
            houseRegionPut('W', "32", "金門縣");
            houseRegionPut('X', "30", "澎湖縣");
            houseRegionPut('Z', "33", "連江縣");
            houseRegionPut('L', "20", "臺中縣", "2010/12/25");
            houseRegionPut('R', "25", "臺南縣", "2010/12/25");
            houseRegionPut('S', "26", "高雄縣", "2010/12/25");
            houseRegionPut('Y', "31", "陽明山管理局", "1974/1/1");
        }
        if (keysAZ.isEmpty()) {
            List<Character> li = new ArrayList<>(houseRegion.keySet());
            Collections.sort(li);
            keysAZ.addAll(li);
        }
        if (keys09.isEmpty()) {
            for (int j = 0; j <= 9; j++) {
                char c = (char)('0' + j);
                keys09.add(c);
            }
        }
        if (keys12.isEmpty()) {
            keys12.add('1');
            keys12.add('2');
        }
    }

    static {
        makeHouseRegion();
        makeEvalCode();
    }
}
