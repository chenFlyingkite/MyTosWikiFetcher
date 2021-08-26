package main.fetcher.hero.card.field;

import main.fetcher.hero.card.each.Heros;

public class LinkInfo {
    public String link;
    public Heros hero;
    public String moegirlLink;

    public LinkInfo() {

    }

    public LinkInfo(Heros h, String l, String m) {
        hero = h;
        link = l;
        moegirlLink = m;
    }

    @Override
    public String toString() {
        return hero.nameEn + " -> " + link;
    }
}
