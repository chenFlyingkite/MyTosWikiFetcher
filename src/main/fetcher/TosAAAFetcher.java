package main.fetcher;

import flyingkite.log.L;
import flyingkite.log.LF;
import flyingkite.math.MathUtil;
import flyingkite.tool.GsonUtil;
import flyingkite.tool.TicTac;
import flyingkite.tool.TicTac2;
import main.card.TosCard;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class TosAAAFetcher extends TosWikiBaseFetcher {
    private TosAAAFetcher() {}
    public static final TosAAAFetcher me = new TosAAAFetcher();
    private static final String folder = "myAAA";
    private LF mLf = new LF(folder);
    private TicTac2 clk = new TicTac2();
    private Map<String, TosCard> allCards = new HashMap<>();

    private String getPage() {
        // 關卡敵人技能/敵人技能列表
        //return "http://zh.tos.wikia.com/wiki/%E9%97%9C%E5%8D%A1%E6%95%B5%E4%BA%BA%E6%8A%80%E8%83%BD/%E6%95%B5%E4%BA%BA%E6%8A%80%E8%83%BD%E5%88%97%E8%A1%A8";
        return "https://review.towerofsaviors.com/199215954";
    }

    @Override
    public void run() {
        loadAllCards();
        clk.tic();
        Document d = getDocument(getPage());
        clk.tac("TAC get doc");
        String data = d.toString();

        //albums(data);

        inventory(data);

//        let temp = {
//                id : parseInt(c[0]),
//                cardId : parseInt(c[1]),
//                exp : parseInt(c[2]),
//                level : parseInt(c[3]),
//                skLevel : parseInt(c[4]),
//                createdAt : parseInt(c[5]),
//                soul : parseInt(c[6]), // soul gain if sell
//                unknown : parseInt(c[7]), // added soul to amelioration
//                refineLevel : parseInt(c[8]), // refine 5 = 突破
//                skinId : parseInt(c[9]),
//                skExp : parseInt(c[10]),
//                normalSkillCd : parseInt(c[11]),
//            };
//


        //L.log("d = \n%s", d);
//        mLf.getFile().open(false);
//        clock.tic();
//        // Start here
//
//
//
//        clock.tac("%s Done", tag());
//        mLf.getFile().close();
    }

    private void albums(String data) {
        String album = find(data, 0, "album_str : '", "'.split(\",\")");
        L.log("album = \n%s\n", album);
        String[] albs = album.split(",");
        print(albs);
        L.log("\n\n");
        statistics("album", albs);
    }

    private void inventory(String data) {
        String inventory = find(data, 0, "inventory_str : '", "'.split(\",\")");
        L.log("inventory_str  = \n%s\n", inventory);
        String[] invs = inventory.split(",");
        print(invs);
        for (int i = 0; i < invs.length; i++) {
            String[] a = invs[i].split("[|]");
            int x = Integer.parseInt(a[7]);
            if (x != 0) {
                L.log("QWE x = %s", x);
            }
        }
        L.log("\n\n");
        statistics("inventory", invs);
    }

    private <T> void print(T[] d) {
        if (d == null) return;

        L.log("%s items", d.length);
        for (int i = 0; i < d.length; i++) {
            L.log("#%04d : %s", i, d[i]);
        }
    }

    private void statistics(String prefix, String[] data) {
        // n records
        int n = data.length;
        if (n == 0) return;

        // m columns
        int m = data[0].split("[|]").length;
        List<Set<Integer>> sets = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            sets.add(new TreeSet<>());
        }

        for (int i = 0; i < n; i++) {
            String[] di = data[i].split("[|]");
            for (int j = 0; j < m; j++) {
                int xij = Integer.parseInt(di[j]);
                Set<Integer> sj = sets.get(j);
                sj.add(xij);
                if (j == 2 && xij == 10) {
                    L.log("data = %s", data[i]);
                }
            }

            String idNorm = idNorm(di[1]);
            int slv = Integer.parseInt(di[4]);
            TosCard c = allCards.get(idNorm);
            // can train
            boolean sk = MathUtil.isInRange(1, slv, c.skillCDMax1);
            if (c.sameSkills.size() > 2 && sk) {
                if (c.rarity >= 5) {
                    L.log("Skills = %s\n  %s\n", data[i], sc(c));
                }
            }
        }

        L.log("For %s", prefix);
        for (int i = 0; i < m; i++) {
            Set<Integer> s = sets.get(i);
            L.log("#%s has %s items = %s", i, s.size(), s);
        }
        //---
    }

    private String find(String src, int pos, String head, String tail) {
        int a = src.indexOf(head, pos);
        if (a < 0) return "";
        int b = src.indexOf(tail, a);

        return src.substring(a + head.length(), b);
    }

    private static String sc(TosCard c) {
        return "#" + c.idNorm + "," + c.name
                + "\n  " + c.skillDesc1 + "," + c.skillDesc2
                + "\n  " + c.skillLeaderDesc
                ;
        //return String.format("#%4s,%s\n      %s\n      %s", c.idNorm, c.name, c.skillDesc1 + "," + c.skillDesc2, c.skillLeaderDesc);
    }

    private void loadAllCards() {
        List<TosCard> all = load();
        allCards.clear();
        for (TosCard c : all) {
            allCards.put(c.idNorm, c);
        }
    }


    private List<TosCard> load() {
        File source = new File("myCard", "cardList.json");
        TosCard[] allCards = GsonUtil.loadFile(source, TosCard[].class);
        return Arrays.asList(allCards);
    }

    private String idNorm(String s) {
        int n = Integer.parseInt(s);
        return String.format(java.util.Locale.US, "%04d", n);
    }
}
