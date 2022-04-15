package main.fetcher.data.thsr;

import flyingkite.tool.TextUtil;

import java.util.ArrayList;
import java.util.List;

public class THSRStation implements Comparable<THSRStation> {
    // "Nangang", "Taipei", "Banqiao", "Taoyuan", "Hsinchu", "Miaoli", "Taichung", "Changhua", "Yunlin", "Chiayi", "Tainan", "Zuoying"
    public static final String[] northToSouthEn = {"Nangang", "Taipei", "Banqiao", "Taoyuan", "Hsinchu", "Miaoli", "Taichung", "Changhua", "Yunlin", "Chiayi", "Tainan", "Zuoying"};
    // "南港", "台北", "板橋", "桃園", "新竹", "苗栗", "台中", "彰化", "雲林", "嘉義", "台南", "左營"
    public static final String[] northToSouthZh = {"南港", "台北", "板橋", "桃園", "新竹", "苗栗", "台中", "彰化", "雲林", "嘉義", "台南", "左營"};

    public String name;

    @Override
    public String toString() {
        return name;
    }

    // stations sorted in southbound
    // revert order is north bound
    public static final List<THSRStation> allStationSouthZh = new ArrayList<>();
    public static final List<THSRStation> allStationSouthEn = new ArrayList<>();
    static {
        for (int i = 0; i < northToSouthZh.length; i++) {
            THSRStation s;
            s = new THSRStation();
            s.name = northToSouthZh[i];
            allStationSouthZh.add(s);

            s = new THSRStation();
            s.name = northToSouthEn[i];
            allStationSouthEn.add(s);
        }
    }

    @Override
    public int compareTo(THSRStation o) {
        return compareZh(o);
    }

    // null, listed values, unlisted
    public int compareZh(THSRStation o) {
        if (o == null) return -1;
        int x = indexOfZh(name);
        int y = indexOfZh(o.name);
        return Integer.compare(x, y);
    }

    public int compareEn(THSRStation o) {
        if (o == null) return -1;
        int x = indexOfEn(name);
        int y = indexOfEn(o.name);
        return Integer.compare(x, y);
    }

    public int indexOfZh(String s) {
        return indexOf(s, northToSouthZh);
    }

    public int indexOfEn(String s) {
        return indexOf(s, northToSouthEn);
    }

    private int indexOf(String s, String[] a) {
        if (TextUtil.isEmpty(s) == false) {
            for (int i = 0; i < a.length; i++) {
                if (a[i].toLowerCase().contains(s.toLowerCase())) {
                    return i;
                }
            }
        }
        return a.length; // last one intentionally
    }
}