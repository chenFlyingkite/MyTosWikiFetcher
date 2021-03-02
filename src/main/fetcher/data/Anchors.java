package main.fetcher.data;

public enum Anchors {
    BasicProperty ("基本屬性"),
    ActiveSkills  ("主動技"),
    LeaderSkills  ("隊長技"),
    Amelioration  ("昇華"),
    AwakenRecall  ("極限昇華"),
    Evolution     ("進化列表"),
    PowerRelease  ("潛能解放"),
    Combination   ("合體列表"),
    VirtualRebirth("異空轉生"),
    VirRebirTrans ("異力轉換"),
    Dragonware    ("武裝龍刻"),
    SupremeReckon ("究極融煉"),
    Origin        ("來源"),
    ;

    public final String key;
    Anchors(String s) {
        key = s;
    }

    public int id() {
        return ordinal();
    }

    public static String[] allNames() {
        Anchors[] cs = values();
        String[] ns = new String[cs.length];

        for (int i = 0; i < cs.length; i++) {
            ns[i] = cs[i].key;
        }
        return ns;
    }

    public Anchors next() {
        Anchors[] cs = values();
        if (this == cs[cs.length - 1]) {
            return cs[0];
        } else {
            return cs[this.id() + 1];
        }
    }
}