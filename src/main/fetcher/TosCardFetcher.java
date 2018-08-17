package main.fetcher;

import flyingkite.collection.ListUtil;
import flyingkite.log.L;
import flyingkite.log.LF;
import flyingkite.tool.GsonUtil;
import flyingkite.tool.StringUtil;
import flyingkite.tool.TicTac2;
import main.card.TosCard;
import main.card.TosCardCreator;
import main.card.TosCardCreator.CardInfo;
import main.fetcher.data.Anchors;
import main.kt.CardDetail;
import main.kt.CardItem;
import main.kt.CardTds;
import main.kt.IconInfo;
import main.kt.SkillInfo;
import main.kt.TosGet;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TosCardFetcher extends TosWikiBaseFetcher {
    private TosCardFetcher() {}
    public static final TosCardFetcher me = new TosCardFetcher();
    private static final String folder = "myCard";
    private LF mLf = new LF(folder);
    private LF mCardJson = new LF(folder, "cardList.json");
    private String source = TosWikiCardsLister.me.VALAID_LINKS;

    private List<String> getTests() {
        List<String> link = new ArrayList<>();
        Collections.addAll(link
                // 昇華
                , "http://zh.tos.wikia.com/wiki/004" // 水元素賢者莫莉
                // 合體
                , "http://zh.tos.wikia.com/wiki/724" // 鬼面魔刃 ‧ 源義經
                , "http://zh.tos.wikia.com/wiki/597" // 連肢機偶 · 格蕾琴與海森堡
                // 突破
                , "http://zh.tos.wikia.com/wiki/818" // 憶念雙子 ‧ 加斯陀與波魯克斯
                // 雙技能
                , "http://zh.tos.wikia.com/wiki/1166" // 冰花
                , "http://zh.tos.wikia.com/wiki/1063" // 鳴動威嚴 ‧ 摩迪與曼尼
                // 潛能解放
                , "http://zh.tos.wikia.com/wiki/230" // 白臉金毛 ‧ 妲己
                // 異空轉生
                , "http://zh.tos.wikia.com/wiki/595" // 傾世媚狐 ‧ 蘇妲己
                , "http://zh.tos.wikia.com/wiki/1082" // 孤高龍王 ‧ 敖廣
                , "http://zh.tos.wikia.com/wiki/1777" // 斯芬克斯
        );
        //---
        Collections.addAll(link
                , "http://zh.tos.wikia.com/wiki/001" // TosCardCreator = 18
                , "http://zh.tos.wikia.com/wiki/024" // TosCardCreator = 28
                , "http://zh.tos.wikia.com/wiki/1001" // TosCardCreator = 16
                , "http://zh.tos.wikia.com/wiki/1017" // TosCardCreator = 22
                , "http://zh.tos.wikia.com/wiki/1063" // TosCardCreator = 32
                , "http://zh.tos.wikia.com/wiki/651" // TosCardCreator = 24
                , "http://zh.tos.wikia.com/wiki/656" // TosCardCreator = 31
        );
        link.clear(); // uncomment this if use test links
//        link.add("http://zh.tos.wikia.com/wiki/1380");
//        link.add("http://zh.tos.wikia.com/wiki/1381");
//        link.add("http://zh.tos.wikia.com/wiki/6070"); // 妲己
//        link.add("http://zh.tos.wikia.com/wiki/6174");
        //link.add("http://zh.tos.wikia.com/wiki/595");
        //link.add("http://zh.tos.wikia.com/wiki/230");
        return link;
    }

    // Records card types by metadata length
    private Set<Integer> cardDataN = new HashSet<>();
    // Records card series
    private Set<String> cardSeries = new HashSet<>();

    @Override
    public void run() {
        mLf.getFile().open(false);
        clock.tic();
        // Start here
        List<String> pages = getTests();
        boolean test = !pages.isEmpty();
        CardItem[] all;
        if (pages.isEmpty()) {
            clock.tic();
            all = GsonUtil.loadFile(new File(source), CardItem[].class);
            clock.tac("%s cards in %s", all.length, source);
            for (CardItem c : all) {
                pages.add(c.getLink());
            }
        }
        mLf.log("%s cards in %s", pages.size(), source);

        mLf.setLogToL(test);
        clock.tic();
        List<TosCard> allCards = new ArrayList<>();
        for (int i = 0; i < pages.size(); i++) {
            String link = pages.get(i);
            L.log("#%s -> %s", i, link);
            mLf.log("%s", link);
            // Fetch metadata from link
            CardInfo cInfo = getCardInfo(link);

            // Create as TosCard
            TosCard card = TosCardCreator.me.asTosCard(cInfo);

            if (card == null) {
                L.log("X_X, No card %s", cInfo.wikiLink); // For 龍刻
            } else {
                allCards.add(card);
                cardSeries.add(card.series);
            }
        }
        clock.tac("Card parsed");

        mLf.log("%s metadata = %s", cardDataN.size(), cardDataN);
        mLf.log("%s series = %s", cardSeries.size(), cardSeries);
        mLf.getFile().close();
        saveCardsToGson(mCardJson, allCards);

        long dur;
        dur = clock.tac("%s Done", tag());
        L.log("time = %s", StringUtil.MMSSFFF(dur));
    }

    private CardInfo getCardInfo(String link) {
        CardInfo info = new CardInfo();

        // Get document node from link by Jsoup
        Document doc = getDocument(link);
        if (doc == null) return info;

        if (getTests().isEmpty()) {
            L.log("Title = %s", doc.title());
        }
        Elements centers = doc.getElementsByTag("center");

        // Fill in icon & big image, in 1st & 2nd <center>
        info.wikiLink = link;
        info.bigImage = getImage(centers, 0);
        info.icon = getImage(centers, 1);
        // Fill in idNorm from mid of top 11 icons
        Elements rowCard = doc.getElementsByTag("table").get(0).getElementsByTag("td");
        Element spot = rowCard.get(rowCard.size() / 2);
        info.idNorm = TosGet.me.getImageTag(spot).attr("alt");

        // Get main content table
        Element main = doc.getElementById("monster-data");
        CardTds cardTds = TosGet.me.getCardTds(main);
        if (cardTds == null) return info;
        // Get nodes of <td>
        List<String> tds = cardTds.getTds();

        // -- Start to fetching card's information --

        // Only take from 0 ~ "基本屬性", "主動技" to end (before "競技場 防守技能" or "來源")
        String[] anchor = Anchors.allNames();
        int[] anchors = findAnchors(tds, anchor);
        info.anchors = Arrays.copyOf(anchors, anchor.length);

        // Adding basic hp/exp info for card
        addHpInfo(info, anchors, tds);
        addExpInfo(info, anchors, tds);
        // Adding amelioration/awaken info for card
        List<IconInfo> ameInfo = TosGet.me.getCardImagedLink(doc);
        addAmeAwkInfo(info, ameInfo, anchors, tds, cardTds);

        // Find the end of card
        int min = getPositiveMin(anchors, Anchors.AwakenRecall.id(), anchors.length);
        L.log("Evos = %s", cardTds.getImages());
        //info.anchors = Arrays.copyOf(anchors, anchors.length); // Unused variable
        info.cardTds = cardTds;

        // Abbreviation
        List<String> data = info.data;

        // Add name, color, stars, hp, attack, heal
        data.addAll(tds.subList(0, anchors[0]));

        // Add skill of active & leader
        for (int i = anchors[1]; i < min; i++) {
            data.add(tds.get(i));
        }

        // This just peek the cardInfo size, used for TosCardCreator
        int num = data.size();
        if (!cardDataN.contains(num)) {
            L.log("%s data for card = %s", num, link);
        }
        cardDataN.add(num);

        // -- Finishing fetching card's information --
        // Get card details
        CardDetail a = TosGet.me.getCardDetails(doc);
        info.details = a.getDetail();
        info.sameSkills = a.getSameSkills();

        return info;
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

    private void addAmeAwkInfo(CardInfo info, List<IconInfo> iconInfo, int[] anchors, List<String> tds, CardTds rawCard) {
        if (ListUtil.isEmpty(iconInfo)) return;
        int ax;

        // Fetch if has 昇華關卡
        ax = Anchors.Amelioration.id();
        if (anchors[ax] >= 0) {
            int at = getPositiveMin(anchors, ax + 1, anchors.length);
            String name = tds.get(at - 1);
            IconInfo icf = getIconInfoByName(name, iconInfo);
            if (icf != null) {
                info.ameStages.add(name);
                info.ameStages.add(wikiBaseZh + icf.getLink());
            }

            getSkillChange(info, rawCard.getRawTds(), anchors[ax] + 1, at - 1);
        }

        // Fetch if has 突破關卡
        ax = Anchors.AwakenRecall.id();
        if (anchors[ax] >= 0) {
            int at = getPositiveMin(anchors, ax + 1, anchors.length);
            String name = tds.get(at - 1);
            IconInfo icf = getIconInfoByName(name, iconInfo);
            if (icf != null) {
                info.awkStages.add(tds.get(anchors[ax] + 1)); // Skill name
                info.awkStages.add(tds.get(anchors[ax] + 2)); // = icf.getName(), stage name
                info.awkStages.add(wikiBaseZh + icf.getLink()); // battle link
            }
        }

        // Fetch if has 潛能解放
        ax = Anchors.PowerRelease.id();
        if (anchors[ax] >= 0) {
            int at = getPositiveMin(anchors, ax + 1, anchors.length);
            String name = tds.get(at - 1);
            IconInfo icf = getIconInfoByName(name, iconInfo);
            if (icf != null) {
                //info.powStages.add(tds.get(anchors[ax] + 1)); // Skill name
                info.powStages.add(name); // = icf.getName(), stage name
                info.powStages.add(wikiBaseZh + icf.getLink()); // battle link
            }
        }

        // Fetch if has 異空轉生
        ax = Anchors.VirtualRebirth.id();
        if (anchors[ax] >= 0) {
            int at = getPositiveMin(anchors, ax + 1, anchors.length);
            String name = tds.get(at - 1);
            IconInfo icf = getIconInfoByName(name, iconInfo);
            if (icf != null) {
                info.virStages.add(name); // Stage name
                info.virStages.add(wikiBaseZh + icf.getLink()); // Battle link
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
            delta = raw.get(i).getElementsByTag("a");
            for (Element dl : delta) {
                SkillInfo si = new SkillInfo();
                si.setSkillLink(wikiBaseZh + dl.attr("href"));
                si.setSkillName(dl.attr("title"));
                info.skillChange.add(si);
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

    private IconInfo getIconInfoByName(String name, List<IconInfo> iconInfo) {
        for (IconInfo i : iconInfo) {
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
}
