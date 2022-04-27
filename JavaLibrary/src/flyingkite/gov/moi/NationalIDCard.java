package flyingkite.gov.moi;

import flyingkite.log.L;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

// Taiwan's national ID card number
// https://zh.wikipedia.org/wiki/%E4%B8%AD%E8%8F%AF%E6%B0%91%E5%9C%8B%E5%9C%8B%E6%B0%91%E8%BA%AB%E5%88%86%E8%AD%89
// https://en.wikipedia.org/wiki/National_identification_card_(Taiwan)
public class NationalIDCard {

    // Family register
    // Household Registration,
    // 代碼 -> 數值 縣市 (停止賦配日期)
    private static final Map<Character, List<String>> houseRegion = new TreeMap<>();
    private static final Map<Character, Integer> regionNumber = new HashMap<>();
    private static final int[] weight = {1, 9, 8, 7, 6, 5, 4, 3, 2, 1, 1};

    public static void testCases() {
        String[] ss = {"A123456789", "a123456789", "10123456789", "a23456789", "a234567890"};
        for (int i = 0; i < ss.length; i++) {
            boolean ok = isValid(ss[i]);
            L.log("#%s = %s : %s", i, ss[i], ok);
        }
    }

    public static List<String> findPossible(String a1_3_5_7_9) {
        List<String> ans = new ArrayList<>();
        char[] cs = a1_3_5_7_9.toCharArray();
        int n = cs.length;
        for (int i = 0; i < cs.length; i++) {
            char x = cs[i];
            List<Character> keys = new ArrayList<>();
            if (i == 0) {
                boolean in = houseRegion.containsKey(x);
                if (in) {
                    find(i+1, cs, ans);
                } else {
                    // try on every possible value
                    keys = new ArrayList<>(houseRegion.keySet());
//                    for (int j = 0; j < keys.size(); j++) {
//                        char newK = keys.get(j);
//                        cs[i] = newK;
//                        find(i+1, cs, ans);
//                        cs[i] = x;
//                    }
                }
            } else {
                boolean in09 = '0' <= x && x < '9';
                if (in09) {
                    find(i+1, cs, ans);
                } else {
                    for (int j = 0; j < 10; j++) {
                        char c = (char)('0' + j);
                        keys.add(c);
                    }
                    // try on every possible value
                }
            }
            L.log("findPossible #%s : %s", i, keys);
            for (int j = 0; j < keys.size(); j++) {
                char newK = keys.get(j);
                cs[i] = newK;
                find(i+1, cs, ans);
                cs[i] = x;
            }
        }
        return ans;
    }

    private static void find(int k, char[] cs, List<String> now) {
        if (k >= cs.length) return;

        L.log("find %s, %s, %s", k, now.size(), Arrays.toString(cs));
        for (int i = 0; i < cs.length; i++) {
            // todo
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
    public static boolean isValid(String a123456789) {
        char[] cs = a123456789.toCharArray();
        return isValid(cs);
    }

    /**
     * @see #isValid(String)
     */
    public static boolean isValid(char[] a123456789) {
        if (a123456789 == null) return false;

        int n = a123456789.length;
        char[] cs = a123456789;
        char c0 = Character.toUpperCase(cs[0]);
        String s = String.valueOf(cs);
        int[] val = new int[n+1];
        Arrays.fill(val, -1);
        if (n == 10 && houseRegion.containsKey(c0)) {
            char[] vs = houseRegion.get(c0).get(0).toCharArray();
            val[0] = vs[0] - '0';
            val[1] = vs[1] - '0';
            for (int i = 1; i < n; i++) {
                int k = cs[i] - '0';
                val[i+1] = k;
            }
            // 10123456789
//        } else if (n == 11) {
//            for (int i = 0; i < n; i++) {
//                val[i] = cs[i] - '0';
//            }
        } else {
            return false;
        }

        // evaluation check value
        int check = getCheck(val);
        //L.log("check = %s, %s", s, check);
        return check % 10 == 0;
    }

    // return Integer.MIN_VALUE if invalid values
    private static int getCheck(int[] val) {
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
    }

    static {
        makeHouseRegion();
        makeEvalCode();
    }
}
