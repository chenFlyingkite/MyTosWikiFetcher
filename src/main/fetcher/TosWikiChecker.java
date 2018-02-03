package main.fetcher;

import com.google.gson.Gson;
import main.card.TosCard;
import util.logging.L;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TosWikiChecker {
    private TosWikiChecker() {}
    public static final TosWikiChecker me = new TosWikiChecker();
    private Gson gson = new Gson();

    public List<TosCard> check(List<TosCard> cards) {
        checkDuplicate(cards);
        return cards;
    }

    // 20180203
    // Change : BigBang => id & image links
    // Change : Disney  => wikiLink
    private void checkDuplicate(List<TosCard> cards) {
        if (cards == null) return;

        Map<String, String> map = new HashMap<>();

        for (TosCard card : cards) {
            String key = card.name;
            String json = gson.toJson(card, TosCard.class);

            if (map.containsKey(key)) {
                String oldJson = map.get(key);
                LinkResult result = sameExcludeLinks(oldJson, json);
                if (result.same) {
                    L.log("%s : -> properties OK, link excluded", key);
                } else {
                    L.log("%s : x different\nold = %s\nnew = %s", key, oldJson, json);
                }
            }
            map.put(key, json);
        }
    }

    private class LinkResult {
        private boolean same;
        private String older = "";
        private String newer = "";
    }

    private LinkResult sameExcludeLinks(String json1, String json2) {
        LinkResult r = new LinkResult();
        r.older = eraseLinks(json1);
        r.newer = eraseLinks(json2);
        r.same = r.older.equals(r.newer);
        return r;
    }

    private String eraseLinks(String json) {
        TosCard card = gson.fromJson(json, TosCard.class);
        eraseLinks(card);
        return gson.toJson(card, TosCard.class);
    }

    private void eraseLinks(TosCard c) {
        c.wikiLink = "";
        c.icon = "";
        c.bigImage = "";
    }
}
