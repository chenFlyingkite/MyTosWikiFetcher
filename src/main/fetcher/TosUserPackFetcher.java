package main.fetcher;

import com.google.gson.Gson;
import flyingkite.log.L;
import flyingkite.log.LF;
import flyingkite.math.MathUtil;
import flyingkite.tool.GsonUtil;
import flyingkite.tool.TicTac2;
import main.card.TosCard;
import main.fetcher.data.mypack.PackResponse;
import org.jsoup.nodes.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class TosUserPackFetcher extends TosWikiBaseFetcher {
    public static final TosUserPackFetcher me = new TosUserPackFetcher();
    private static final String folder = "myAAA";
    private LF mLf = new LF(folder);
    private TicTac2 clk = new TicTac2();
    private Map<String, TosCard> allCards = new HashMap<>();

    private String getPage() {
        // 神魔健檢中心 : http://review.towerofsaviors.com/
        //return "https://review.towerofsaviors.com/199215954";
        // new 神魔健檢中心 = https://checkup.tosgame.com/
        //return "https://checkupapi.tosgame.com/user/login";
        return "https://checkupapi.tosgame.com/api/inventoryReview/getUserProfile";
    }
    // {"token":"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOjE5OTIxNTk1NCwibmFtZSI6IkZseWluZ2tpdGUyIiwiY2FtcGFpZ25Mb2dpbkRheXMiOjMyLCJsZXZlbCI6NTI3LCJyb2xlIjowLCJpYXQiOjE1ODgyNDM5OTQsImV4cCI6MTU4ODMzMDM5NCwiaXNzIjoibWFkaGVhZCJ9.U9OE0heZ_pD8Y1rHwkS9iHZVQjMzUaZdPRRD1RgugoWljhFvk7l3WE6iV4-6KKKfMy4XfZ43CfN0DltAjbRd1A"
    // ,"user":{"uid":199215954,"name":"Flyingkite2","campaignLoginDays":32,"level":527,"role":0},"isSuccess":1}

    private Map<String, String> params() {
        Map<String, String> m = new HashMap<>();
        //m.put("token", "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOjE5OTIxNTk1NCwibmFtZSI6IkZseWluZ2tpdGUyIiwiY2FtcGFpZ25Mb2dpbkRheXMiOjMyLCJsZXZlbCI6NTI3LCJyb2xlIjowLCJpYXQiOjE1ODgyNDM5OTQsImV4cCI6MTU4ODMzMDM5NCwiaXNzIjoibWFkaGVhZCJ9.U9OE0heZ_pD8Y1rHwkS9iHZVQjMzUaZdPRRD1RgugoWljhFvk7l3WE6iV4-6KKKfMy4XfZ43CfN0DltAjbRd1A");
        m.put("token", "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOjE5OTIxNTk1NCwibmFtZSI6IkZseWluZ2tpdGUyIiwiY2FtcGFpZ25Mb2dpbkRheXMiOjMyLCJsZXZlbCI6NTI3LCJyb2xlIjowLCJpYXQiOjE1ODgyNDM5OTQsImV4cCI6MTU4ODMzMDM5NCwiaXNzIjoibWFkaGVhZCJ9.U9OE0heZ_pD8Y1rHwkS9iHZVQjMzUaZdPRRD1RgugoWljhFvk7l3WE6iV4-6KKKfMy4XfZ43CfN0DltAjbRd1A");
        m.put("aid", "408798");
        m.put("uid", "199215954");

//        m.put("aid", "489224");
//        m.put("uid", "150372202");
        //m.put("labels", "{\"serviceType\":\"tosCampaign\"}");
        //m.put("serviceType", "tosCampaign");
        m.put("includeInventory", "true");
        return m;
    }

    @Override
    public void run() {
        loadAllCards();
        clk.tic();
        Document d = getByOkHttp(getPage(), params());

        clk.tac("TAC get doc");
        String data = d.toString();
        L.log("data = %s", data);

        //albums(data);

        inventory(data);
        //inventoryOld(data);

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

    private void inventory(String data) {
        String userData = find(data, 0, "<body>", "</body>").trim();
        L.log("use data (len = %s) = \n%s\n", userData.length(), userData);
        Gson gson = new Gson();
        PackResponse pr = gson.fromJson(userData, PackResponse.class);
        if (pr != null) {
            L.log("pr = %s cards", pr.card.size());
        }
    }

    private void albums(String data) {
        String album = find(data, 0, "album_str : '", "'.split(\",\")");
        L.log("album = \n%s\n", album);
        String[] albs = album.split(",");
        print(albs);
        L.log("\n\n");
        statistics("album", albs);
    }

    @Deprecated
    // This is old way
    private void inventoryOld(String data) {
        String inventory = find(data, 0, "inventory_str : '", "'.split(\",\")");
        L.log("inventory_str  = \n%s\n", inventory);
        String[] invs = inventory.split(",");
        print(invs);
        for (int i = 0; i < invs.length; i++) {
            String[] a = invs[i].split("[|]");
        }
        L.log("\n\n");
        statistics("inventoryOld", invs);
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
