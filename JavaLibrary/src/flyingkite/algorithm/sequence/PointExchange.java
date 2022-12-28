package flyingkite.algorithm.sequence;

import flyingkite.log.L;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PointExchange {
    // For example, in THSR we need to exchange all the remain points for it before 12/31
    // https://tgopoints.thsrc.com.tw/newProduct
    // So we use the recursive method to find all possible combinations, but may need to sort by items length and remains
    public static void main(String[] args) {
        testCases();
    }

    // 2022/12/28 12:13 ~ 12:21 ~ 12:44 debug ready
    private static List<List<Item>> getAllExchanges(int total, List<Item> items) {
         // sort from small to large, by points, and name
        Collections.sort(items, (x, y) -> {
            if (x.point != y.point) {
                return x.point - y.point;
            } else {
                return x.name.compareTo(y.name);
            }
        });
        List<List<Item>> ans = new ArrayList<>();
        find(0, total, new ArrayList<>(), ans, items);
        return ans;
    }

    // Try to find the next item to exchange by point
    private static void find(int at, int remain, List<Item> now, List<List<Item>> all, List<Item> data) {
        if (remain < data.get(at).point) {
            // no more to change
            List<Item> part = new ArrayList<>(now);
            if (remain > 0) {
                part.add(new Item(remain, "remain"));
            }
            all.add(part);
        } else {
            for (int i = at; i < data.size(); i++) {
                Item it = data.get(i);
                if (remain >= it.point) {
                    // take it
                    remain -= it.point;
                    now.add(it);
                    find(i, remain, now, all, data);
                    remain += it.point;
                    now.remove(now.size() - 1);
                }
            }
        }
    }

    private static void testCases() {
        List<Item> list = new ArrayList<>();
        for (int i = 2; i < 11; i++) {
            list.add(new Item(i, "#" + i));
        }
        list = getItems();
        L.log("list = %s", list);
        // Here is the point that you have
        int[] cases = {
                795
                //6, 7,
                //8, 9, 10, 11, 12, 13, 14, 15
        };
        for (int i = 0; i < cases.length; i++) {
            List<List<Item>> ans = getAllExchanges(cases[i], list);
            L.log("-----cases-------- %s", cases[i]);
            L.log("#%s : %s, %s", i, ans.size(), ans);
        }
    }

    //-- Data prepare
    private static List<Item> getItems() {
        List<Item> list = new ArrayList<>();
        add711(list);
        addFamilyMart(list);
        return list;
    }

    private static void addStarbucks(List<Item> list) {
        list.add(new Item( 710, "星巴克 大杯美式咖啡(兌換冰熱任選)"));
        list.add(new Item( 855, "星巴克 大杯那堤(兌換冰熱任選)"));
        list.add(new Item( 890, "星巴克 大杯巧克力可可碎片星冰樂"));
        list.add(new Item( 945, "星巴克 大杯醇濃抹茶那堤(兌換冰熱任選)"));
        list.add(new Item( 970, "星巴克 大杯焦糖瑪奇朵(兌換冰熱任選)"));
        list.add(new Item(1030, "星巴克 大杯醇濃抹茶星冰樂"));
    }

    private static void add711(List<Item> list) {
        list.add(new Item(  99, "統一超商 茶葉蛋"));
        list.add(new Item( 150, "高鐵聯名台塩海洋鹼性離子水 限高鐵門市兌換"));
        list.add(new Item( 200, "統一超商 御選肉鬆飯糰"));
        list.add(new Item( 210, "統一超商 雙蔬鮪魚飯糰"));
        list.add(new Item( 285, "CITY CAFÉ 美式冰/熱咖啡(大)"));
        list.add(new Item( 325, "CITY CAFÉ 拿鐵冰/熱咖啡(大)"));
        list.add(new Item( 410, "CITY PRIMA 精品冰/熱美式"));
        list.add(new Item( 460, "CITY PRIMA 精品冰/熱拿鐵"));
    }

    private static void addFamilyMart(List<Item> list) {
        list.add(new Item(  99, "Family mart 茶葉蛋"));
        list.add(new Item( 205, "Family mart 鹼性離子水-800ml"));
        list.add(new Item( 210, "Family mart 小分子氣泡水-500ml"));
        list.add(new Item( 270, "Family mart 霜淇淋"));
        list.add(new Item( 285, "Family mart 美式冰/熱咖啡(大)"));
        list.add(new Item( 325, "Family mart 拿鐵冰/熱咖啡(大)"));
    }

    private static void addLuisa(List<Item> list) {
        list.add(new Item( 385, "路易莎咖啡 (100%一番抹茶鮮奶茶/美式黑咖啡)（Ｍ／(冰/熱)）"));
        list.add(new Item( 455, "路易莎咖啡 莊園級美式（L／(熱/冰)）"));
        list.add(new Item( 505, "路易莎咖啡 咖啡拿鐵（L／(熱/冰)）"));
        list.add(new Item( 560, "路易莎咖啡 莊園級拿鐵（L／(熱/冰)）"));
    }

    private static void addCama(List<Item> list) {
        list.add(new Item( 380, "cama café 大杯高山錫蘭鮮奶茶(熱/冰)"));
        list.add(new Item( 555, "cama café 大杯法式可可歐蕾(熱/冰)"));
        list.add(new Item( 605, "cama café 大杯微韻輕拿鐵(熱/冰) 大杯黑絲絨拿鐵(冰)"));
        list.add(new Item( 665, "cama café 大杯蜂蜜拿鐵(熱/冰)"));
        list.add(new Item( 695, "cama café 大杯手沖-百蜜花園(熱/冰)"));
        list.add(new Item( 715, "cama café 大杯OATLY燕麥拿鐵(熱/冰)"));
    }

    public static class Item {
        public int point;
        public int cash;
        public String name;

        public Item() {
        }

        public Item(int _point, String _name) {
            point = _point;
            name = _name;
        }

        @Override
        public String toString() {
            return point + "=" + name;
        }
    }
}



