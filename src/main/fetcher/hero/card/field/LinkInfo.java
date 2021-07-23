package main.fetcher.hero.card.field;

import main.fetcher.hero.card.each.Heros;

public class LinkInfo {
    public String key;
    public String link;
    public Heros hero;

    public LinkInfo() {

    }

    public LinkInfo(Heros h, String l) {
        hero = h;
        key = h.nameEn;
        link = l;
    }

    @Override
    public String toString() {
        return key + " -> " + link;
    }
}
