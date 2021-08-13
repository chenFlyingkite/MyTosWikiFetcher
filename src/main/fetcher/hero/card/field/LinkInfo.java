package main.fetcher.hero.card.field;

import main.fetcher.hero.card.each.Heros;

public class LinkInfo {
    public String key;
    public String link;
    public Heros hero;
    public String moegirlLink;

    public LinkInfo() {

    }

    public LinkInfo(Heros h, String l, String m) {
        hero = h;
        key = h.nameEn;
        link = l;
        moegirlLink = m;
    }

    @Override
    public String toString() {
        return key + " -> " + link;
    }
}
