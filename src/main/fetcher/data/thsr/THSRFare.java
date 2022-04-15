package main.fetcher.data.thsr;

import flyingkite.tool.TextUtil;

import java.util.Map;
import java.util.TreeMap;

public class THSRFare {
    public String name;

    // {"Nangang-Taipei", "130"}
    // {"南港站-台北站", "20"}
    public Map<String, String> prices = new TreeMap<>();
    //public Map<String, String> prices = new HashMap<>();

    public int price(String s) {
        boolean exist = prices.containsKey(s);
        if (exist) {
            String t = prices.get(s);
            if (isEmpty(t)) {
            } else {
                t = t.replaceAll("[,*]", "");
                return Integer.parseInt(t);
            }
        }
        return -1;
    }

    // "-" = same station
    public boolean isEmpty(String key) {
        return TextUtil.isEmpty(key) || "-".equals(key);
    }

    @Override
    public String toString() {
        String s = String.format("%s, %s prices, %s", name, prices.size(), prices);
        return s;
    }
}
