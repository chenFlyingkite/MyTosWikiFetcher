package main.fetcher;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import flyingkite.log.L;
import flyingkite.tool.GsonUtil;
import flyingkite.tool.TextUtil;
import flyingkite.tool.TicTac2;
import main.card.Evolve;
import main.card.TosCard;

public class TosWikiChecker {
    private TosWikiChecker() {}
    public static final TosWikiChecker me = new TosWikiChecker();
    private Gson gson = new Gson();

    private Map<String, TosCard> allmap = new HashMap<>();

    public List<TosCard> check() {
        TicTac2 clk = new TicTac2();
        File fc = new File("myCard", "cardList.json");
        // Try to parsing back to know its performance
        clk.tic();
        TosCard[] allCards = GsonUtil.loadFile(fc, TosCard[].class);
        clk.tac("%s cards loaded", allCards.length);
        List<TosCard> cards = Arrays.asList(allCards);

        createMap(cards);
        L.log("--- Check missing");
        checkMissingCard(cards);
        L.log("--- Check Duplicate");
        checkDuplicate(cards);
        return cards;
    }

    private void createMap(List<TosCard> all) {
        allmap.clear();
        for (TosCard c : all) {
            if (allmap.containsKey(c.idNorm)) {
                L.log("X_X Duplicate on %s", c.idNorm);
            }
            allmap.put(c.idNorm, c);
        }
    }

    private void checkMissingCard(List<TosCard> all) {
        for (TosCard c : all) {
            TosCard d = allmap.get(c.idNorm);
            if (d == null) {
                L.log("Missing card %s", c.idNorm);
            }
            if (d != null) {
                // Rebirth
                logIfNull("Rebirth Null on from", c, c.rebirthFrom);
                logIfNull("Rebirth Null on change", c, c.rebirthChange);

                // Combine
                for (String s : c.combineFrom) {
                    logIfNull("Combine Null on from", c, s);
                }
                for (String s : c.combineTo) {
                    logIfNull("Combine Null on to", c, s);
                }

                // Evolve
                for (Evolve ev : c.evolveInfo) {
                    logIfNull("Evolve Null on from", c, ev.evolveFrom);
                    logIfNull("Evolve Null on to", c, ev.evolveTo);
                    for (String s : ev.evolveNeed) {
                        logIfNull("Evolve Null on need", c, s);
                    }
                }

                // Same Skills
                for (String s : c.sameSkills) {
                    logIfNull("SameSkills Null on", c, s);
                }
            }
        }
    }

    private void logIfNull(String prefix, TosCard c, String idNorm) {
        if (TextUtil.isEmpty(idNorm)) return;

        if (allmap.get(idNorm) == null) {
            L.log("%s on %s, %s", prefix, c.idNorm, idNorm);
        }
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
