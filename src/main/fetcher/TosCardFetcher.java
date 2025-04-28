package main.fetcher;

import flyingkite.log.L;
import flyingkite.log.LF;
import flyingkite.tool.GsonUtil;
import flyingkite.tool.StringUtil;
import flyingkite.tool.TicTac2;
import main.card.Evolve;
import main.card.TosCard;
import main.card.TosCardCreator;
import main.card.TosCardCreator.CardInfo;
import main.fetcher.data.Anchors;
import main.kt.Awaken;
import main.kt.CardDetail;
import main.kt.CardItem;
import main.kt.CardTds;
import main.kt.NameLink;
import main.kt.SkillInfo;
import main.kt.TosGet;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class TosCardFetcher extends TosWikiBaseFetcher {
    public static final TosCardFetcher me = new TosCardFetcher();
    private static final String folder = "myCard";
    private LF mLf = new LF(folder);
    private LF mCardJson = new LF(folder, "cardList.json");
    private LF mEvolveJson = new LF(folder, "evolvePath.json");
    private String source = TosWikiCardsLister.me.VALAID_LINKS;

    // Records card types by metadata length
    private Map<String, String> cardKinds = new TreeMap<>();
    // Records card series
    private Set<String> cardSeries = new TreeSet<>();
    // Records card signatures of each count
    private Set<String> cardSigna = new TreeSet<>();
    //private Map<String, TosCard> allFetched = new TreeMap<>();
    //private List<TosCard> allSortedCard;

    // Nonempty = fixing card content
    private boolean fixing = 0 > 0;
    private List<String> getTests() {
        List<String> link = new ArrayList<>();
        Collections.addAll(link
                // 昇華
                , "https://tos.fandom.com/zh/wiki/004" // 水元素賢者莫莉
                // 合體
                , "https://tos.fandom.com/zh/wiki/724" // 鬼面魔刃 ‧ 源義經
                , "https://tos.fandom.com/zh/wiki/597" // 連肢機偶 · 格蕾琴與海森堡
                // 突破
                , "https://tos.fandom.com/zh/wiki/818" // 憶念雙子 ‧ 加斯陀與波魯克斯
                // 雙技能
                , "https://tos.fandom.com/zh/wiki/1166" // 冰花
                , "https://tos.fandom.com/zh/wiki/1063" // 鳴動威嚴 ‧ 摩迪與曼尼
                // 潛能解放
                , "https://tos.fandom.com/zh/wiki/230" // 白臉金毛 ‧ 妲己
                // 異空轉生
                , "https://tos.fandom.com/zh/wiki/595" // 傾世媚狐 ‧ 蘇妲己
                , "https://tos.fandom.com/zh/wiki/1082" // 孤高龍王 ‧ 敖廣
                , "https://tos.fandom.com/zh/wiki/1777" // 斯芬克斯
        );
        link.clear();
        //---
        // Card all possible cases
        Collections.addAll(link
                , "https://tos.fandom.com/zh/wiki/001" // TosCardCreator = 18
                , "https://tos.fandom.com/zh/wiki/024" // TosCardCreator = 28, // A = (超人貝利亞)  2162 : 10 = 主動技, 19 = 隊長技 22 = 昇華
                , "https://tos.fandom.com/zh/wiki/2162" // TosCardCreator = 28, // B = (青龍孟章神君)  24 : 10 = 主動技, 15 = 隊長技 18 = 昇華
                , "https://tos.fandom.com/zh/wiki/1017" // TosCardCreator = 22
                , "https://tos.fandom.com/zh/wiki/651" // TosCardCreator = 24
                , "https://tos.fandom.com/zh/wiki/656" // TosCardCreator = 31
                , "https://tos.fandom.com/zh/wiki/1001" // TosCardCreator = 16
                , "https://tos.fandom.com/zh/wiki/1063" // TosCardCreator = 32
                , "https://tos.fandom.com/zh/wiki/2425" // 29

        );
        link.clear(); // uncomment this if use test links
        //--
        // test cases
        //link.add("https://tos.fandom.com/zh/wiki/816");
        //link.add("https://tos.fandom.com/zh/wiki/651");
        //link.add("https://tos.fandom.com/zh/wiki/656");
        //link.add("https://tos.fandom.com/zh/wiki/1361"); // SupremeReckon
        //link.add("https://tos.fandom.com/zh/wiki/1021");
        //link.add("https://tos.fandom.com/zh/wiki/722"); // 合體
        //link.add("https://tos.fandom.com/zh/wiki/2344");
        //link.add("https://tos.fandom.com/zh/wiki/2345");
        //link.add("https://tos.fandom.com/zh/wiki/2414");
//        for (int i = 1; i < 100; i++) {
//            link.add("https://tos.fandom.com/zh/wiki/" + i);
//        }
        //link.add("https://tos.fandom.com/zh/wiki/2186");
        //link.add("https://tos.fandom.com/zh/wiki/2162");
        //link.add("https://tos.fandom.com/zh/wiki/1436");
        //link.add("https://tos.fandom.com/zh/wiki/6070"); // 妲己
        //link.add("https://tos.fandom.com/zh/wiki/6174");
        //link.add("https://tos.fandom.com/zh/wiki/595");
        //link.add("https://tos.fandom.com/zh/wiki/230");
        //link.add("https://tos.fandom.com/zh/wiki/2425"); // 29

        link.add("https://tos.fandom.com/zh/wiki/%E8%A9%A6%E7%85%89%E4%B9%8B%E7%9F%B3");
        if (!fixing) {
            link.clear();
        }
        return link;
    }

    private boolean isNoFix() {
        return getTests().isEmpty();
    }

    private boolean isFixing() {
        return !isNoFix();
    }

    @Override
    public void run() {
        mLf.getFile().open(false);
        mLf.setLogToL(isFixing());
        mLf.setLogToFile(!isFixing());

        clock.tic();
        // Start here
        List<String> pages = loadPages();
        mLf.log("%s cards in %s", pages.size(), source);

        clock.tic();
        List<String> failed = new ArrayList<>();
        List<TosCard> allCards = new ArrayList<>();
        for (int i = 0; i < pages.size(); i++) {
            String link = pages.get(i);
            L.log("For link[%s] %s", i, link);
            mLf.log("%s", link);
            // Fetch metadata from link
            CardInfo cInfo = getCardInfo(link);

            // Create as TosCard
            TosCard card = null;
            if (cInfo.cardTds != null) {
                card = TosCardCreator.me.asTosCard(cInfo);
            }

            if (card == null) {
                failed.add(cInfo.wikiLink);
                L.log("X_X, No card %s", cInfo.wikiLink);
            } else {
                allCards.add(card);
                cardSeries.add(card.series);
                if (isFixing()) {
                    L.log("#%s -> \n%s\n", i, mGson.toJson(card));
                }
                //allFetched.put(card.idNorm, card);
            }
        }
        clock.tac("Card parsed");
        //allSortedCard = allCards;

        mLf.log("%s metadata =", cardKinds.size());
        for (String k : cardKinds.keySet()) {
            String v = cardKinds.get(k);
            mLf.log("  %s -> %s", k, v);
        }
        mLf.log("%s series\n = %s", cardSeries.size(), cardSeries);
        mLf.log("%s signatures", cardSigna.size());
        for (String s : cardSigna) {
            mLf.log(" : %s", s);
        }

        if (isNoFix()) {
            saveCardsToGson(mCardJson, allCards);
        }

        long dur;
        dur = clock.tac("%s Done", tag());

        int no = failed.size();
        if (no > 0) {
            L.log("%s cards failed", no);
            for (int i = 0; i < no; i++) {
                L.log(" > #%2d = %s", i, failed.get(i));
            }
        }
        buildEvolveTree();

        mLf.getFile().close();
        L.log("time = %s     at %s", StringUtil.MMSSFFF(dur), new Date());
    }

    private List<String> loadPages() {
        List<String> pages = getTests();
        CardItem[] all;
        if (pages.isEmpty()) {
            clock.tic();
            all = GsonUtil.loadFile(new File(source), CardItem[].class);
            clock.tac("%s cards in %s", all.length, source);
            for (CardItem c : all) {
                pages.add(c.getLink());
            }
        }
        return pages;
    }

    // Main core
    private CardInfo getCardInfo(String link) {
        CardInfo info = new CardInfo();
        info.wikiLink = link;

        // Get document node from link by Jsoup
        Document doc = getDocument(link);
        if (doc == null) return info;

        Elements centers = doc.getElementsByTag("center");

        // Fill in icon & big image, in 1st & 2nd <center>
        info.bigImage = getImage(centers, 0);
        info.icon = getImage(centers, 1);
        // Fill in idNorm from mid of top 11 icons
        // ? using icon? why not number
        Elements rowCard = doc.getElementsByTag("table").get(0).getElementsByTag("td");
        Element spot = rowCard.get(rowCard.size() / 2);
        String xxi = TosGet.me.getImageTag(spot).attr("alt");
        info.idNorm = xxi.substring(0, xxi.lastIndexOf('i'));

        // Get main content table
        Element main = doc.getElementById("monster-data");
        CardTds cardTds = TosGet.me.getCardTds(main);
        if (cardTds == null) return info;
        // Get nodes of <td>
        info.cardTds = cardTds;
        List<String> tds = cardTds.getTds();

        // -- Start to fetching card's information --

        // Only take from 0 ~ "基本屬性", "主動技" to end (before "競技場 防守技能" or "來源")
        String[] anchor = Anchors.allNames();
        int[] anchors = findAnchors(tds, anchor);
        info.anchors = Arrays.copyOf(anchors, anchor.length);

        // Adding basic hp/exp info for card
        addBaseInfo(info, anchors, tds);
        addHpInfo(info, anchors, tds);
        addExpInfo(info, anchors, tds);
        addSkillInfo(info, anchors, tds);
        // Adding amelioration/awaken info for card
        addAmeAwkInfo(info, anchors, cardTds);

        String signa = info.signature();
        cardSigna.add(signa);

        // This just peek the cardInfo size, used for TosCardCreator
        String act = "Act_" + info.activeSkills.size() + "_";
        String ldr = "Ldr_" + info.leaderSkills.size() + "_";
        String ame = "Ame_" + info.ameStages.size() + "_";
        String kind = act + ldr + ame;
        if (!cardKinds.containsKey(kind)) {
            L.log("%s kind for card = %s", kind, link);
            cardKinds.put(kind, link);
        }

        // -- Finishing fetching card's information --
        // Get card details
        CardDetail a = TosGet.me.getCardDetails(doc);
        info.details = a.getDetail();
        info.sameSkills = a.getSameSkills();

        // -- Printing logs --
        if (isNoFix()) {
            L.log("get id = %s, sig = %s, Title = %s", info.idNorm, signa, doc.title());
        }
        return info;
    }

    private void addBaseInfo(CardInfo c, int[] anchors, List<String> tds) {
        c.basic.addAll(tds.subList(0, anchors[0]));
    }

    private void addHpInfo(CardInfo c, int[] anchors, List<String> tds) {
        int maxHp = anchors[0] + 1;
        c.hpValues.addAll(tds.subList(maxHp, maxHp + 3));
        int minHp = maxHp + 18; // 6*3
        c.hpValues.addAll(tds.subList(minHp, minHp + 3));
    }

    private void addExpInfo(CardInfo c, int[] anchors, List<String> tds) {
        c.expInfos.add(tds.get(8)); // Exp curve
        int sacExp = anchors[0] + 35; // 6*5 + 5
        c.expInfos.add(tds.get(sacExp)); // Sacrifice Exp Lv1
        c.expInfos.add(tds.get(sacExp + 6)); // Sacrifice Exp per Lv
    }

    private void addSkillInfo(CardInfo c, int[] anchors, List<String> tds) {
        c.activeSkills.addAll(tds.subList(anchors[1] + 1, anchors[2]));
        int min = getPositiveMin(anchors, Anchors.LeaderSkills.next().id(), anchors.length);
        c.leaderSkills.addAll(tds.subList(anchors[2] + 1, min));
    }

    private void addAmeAwkInfo(CardInfo info, int[] anchors, CardTds rawCard) {
        int ax;

        // Fetch if has 昇華關卡
        ax = Anchors.Amelioration.id();
        if (anchors[ax] >= 0) {
            int at = getPositiveMin(anchors, ax + 1, anchors.length);
            List<NameLink> links = rawCard.getAmelio();
            // Fill in each bonus, at most 4 amelioration = 4 * ( name + cost)
            int from = anchors[ax] + 1;
            int end = Math.min(at, from + 8);
            info.amelioSkills.addAll(info.cardTds.getTds().subList(from, end));
            // Add stage
            for (NameLink nk : links) {
                info.ameStages.add(nk.getName());
                info.ameStages.add(nk.getLink());
            }

            getSkillChange(info, rawCard.getRawTds(), from, end);
        }

        // Fetch if has 突破關卡
        ax = Anchors.AwakenRecall.id();
        if (anchors[ax] >= 0) {
            int at = getPositiveMin(anchors, ax + 1, anchors.length);
            List<Awaken> links = rawCard.getAwaken();
            int from = anchors[ax] + 1;
            int end = at;
            for (Awaken a : links) {
                info.awkStages.add(a.getSkill());
                info.awkStages.add(a.getName());
                info.awkStages.add(a.getLink());
            }

            getSkillChange(info, rawCard.getRawTds(), from, end);
        }

        // Fetch if has 潛能解放
        ax = Anchors.PowerRelease.id();
        if (anchors[ax] >= 0) {
            info.powStages.addAll(rawCard.getPowRel());
        }

        // Fetch if has 異空轉生
        ax = Anchors.VirtualRebirth.id();
        if (anchors[ax] >= 0) {
            List<NameLink> links = rawCard.getVirReb();
            for (NameLink nk : links) {
                info.virStages.add(nk.getName());
                info.virStages.add(nk.getLink());
            }
        }
    }

    private void saveCardsToGson(LF f, List<TosCard> cardList) {
        TicTac2 clk = new TicTac2();
        f.setLogToL(false);
        cardList.sort((c1, c2) -> {
            int n1 = Integer.parseInt(c1.idNorm);
            int n2 = Integer.parseInt(c2.idNorm);
            return Integer.compare(n1, n2);
        });

        // Write cardList to json file
        clk.tic();
        String msg = writeAsGson(cardList, f);
        clk.tac("%s cards written", cardList.size());

        // Try to parsing back to know its performance
        clk.tic();
        TosCard[] cardA = mGson.fromJson(msg, TosCard[].class);
        clk.tac("%s cards parsed back", cardA.length);
    }

    //-- Miscellaneous util functions - Start

    private void getSkillChange(CardInfo info, Elements raw, int from, int to) {
        // Check amelioration skills for skill change
        Elements delta;
        for (int i = from; i < to; i += 2) {
            Element ei = raw.get(i);
            boolean inStage = ei.parent().text().contains("關卡");
            if (inStage) {
                // Omit like 關卡 816i.png 星辰所拯救 ‧ 波比
            } else {
                delta = ei.getElementsByTag("a");
                for (Element dl : delta) {
                    SkillInfo si = new SkillInfo();
                    si.setSkillLink(wikiBaseZh + dl.attr("href"));
                    si.setSkillName(dl.attr("title"));
                    info.skillChange.add(si);
                }
            }
        }
    }

    // Find min = min(anchors[start : end]), and ignore -1 negative values,
    public static int getPositiveMin(int[] numbers, int start, int end) {
        int min = numbers[start];
        for (int i = start; i < end; i++) {
            if (numbers[i] >= 0) {
                if (min < 0) { // first value
                    min = numbers[i];
                } else {
                    min = Math.min(min, numbers[i]);
                }
            }
        }
        return min;
    }

    private NameLink getIconInfoByName(String name, List<NameLink> nameLink) {
        for (NameLink i : nameLink) {
            if (i.getName().equals(name)) {
                return i;
            }
        }
        return null;
    }

    private String getImage(Elements es, int index) {
        if (es == null || es.size() <= index) return "";

        Element e = es.get(index);
        return TosGet.me.getImage(e);
    }
    //-- Miscellaneous util functions - End


    private List<TosCard> loadAllCards() {
        // using List<TosCard> makes card be HashMap
        TosCard[] li = GsonUtil.loadFile(mCardJson.getFile().getFile(), TosCard[].class);
        return Arrays.asList(li);
    }

    private void buildEvolveTree() {
        List<TosCard> all = loadAllCards();
        Map<String, TosCard> pool = new HashMap<>();
        Map<String, String> edge = new HashMap<>();
        // Load cards and make our set, pool = 0001 -> card & edge = 0001 -> 0002
        int n = all.size();
        for (int i = 0; i < n; i++) {
            TosCard c = all.get(i);
            pool.put(c.idNorm, c);

            //-- Add the evolve info
            if (TosCardCreator.isSkinID(c.idNorm)) {
                // omit skin
            } else {
                for (int j = 0; j < c.evolveInfo.size(); j++) {
                    Evolve e = c.evolveInfo.get(j);
                    if (!edge.containsKey(e.evolveTo)) {
                        // Normally is To -> From,
                        // But for the VirRebirTrans 異力轉換 1217 -> 1824 -> 2732, 1823 -> 2732, 1823 <-> 1824
                        // We take the 1824 // 火巴比倫
                        String key = e.evolveFrom;
                        // For 1824 : rebirthFrom = "1217", rebirthChange = "1823"
                        // For 1823 : rebirthFrom =     "", rebirthChange = "1824"
                        if (c.rebirthFrom.isEmpty() && !c.rebirthChange.isEmpty()) {
                            key = c.rebirthChange;
                        }
                        edge.put(e.evolveTo, key);
                    }
                }
            }
        }
        List<TosCard> want = all;
        int pathLength = 0;
        List<List<TosCard>> answer = new ArrayList<>();
        // very fast to within 15 ms
        for (int i = 0; i < want.size(); i++) {
            TosCard x = want.get(i);
            if (TosCardCreator.isSkinID(x.idNorm)) {
                continue;
            }

            boolean valid = "神族 魔族 人類 獸類 龍類 妖精類 機械族".contains(x.race) && x.evolveInfo.isEmpty();
            // for 1821, not 1822
            boolean isRebirth = x.rebirthFrom.isEmpty() && !x.rebirthChange.isEmpty();


            List<TosCard> path = new ArrayList<>();
            path.add(x);
            if (valid) {
                String y = edge.get(x.idNorm);
                while (y != null) {
                    TosCard cy = pool.get(y);
                    path.add(cy);
                    y = edge.get(cy.idNorm);
                }
                pathLength = Math.max(pathLength, path.size());
                answer.add(path);
            } else if (isRebirth) {
                answer.add(path);
            }
        }
        // print answer of 0001 -> 0002 -> 0003 -> ...
        n = answer.size();
        String msg = String.format("Evolve tree : %s path, longest = %s", n, pathLength);
        L.log(msg);
        mLf.log(msg);
        TicTac2 t = new TicTac2();
        t.tic();
        boolean log = false;
        List<List<String>> idNorms = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            List<TosCard> li = answer.get(i);
            List<String> ki = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < li.size(); j++) {
                TosCard c = li.get(j);
                // append message
                if (log) {
                    if (j > 0) {
                        sb.append("  ->  ");
                    }
                    sb.append(c.idNorm).append(" ").append(c.name);
                }

                // for json
                ki.add(c.idNorm);
            }
            idNorms.add(ki);
            if (log) {
                L.log("#%4d = %s", i, sb.toString());
            }
        }
        t.tac("evolve tree ok");
        writeAsGson(idNorms, mEvolveJson);
    }

}
