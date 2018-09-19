package main.fetcher.data;

public enum Anchors {
    BasicProperty ("基本屬性"),
    ActiveSkills  ("主動技"),
    Amelioration  ("昇華"),
    AwakenRecall  ("極限突破"),
    Evolution     ("進化列表"),
    PowerRelease  ("潛能解放"),
    Combination   ("合體列表"),
    VirtualRebirth("異空轉生"),
    VirRebirTrans ("異力轉換"),
    Dragonware    ("武裝龍刻"),
    Origin        ("來源"),
    ;

    final String name;
    Anchors(String s) {
        name = s;
    }

    public int id() {
        return ordinal();
    }

    public static String[] allNames() {
        Anchors[] cs = values();
        String[] ns = new String[cs.length];

        for (int i = 0; i < cs.length; i++) {
            ns[i] = cs[i].name;
        }
        return ns;
    }
}