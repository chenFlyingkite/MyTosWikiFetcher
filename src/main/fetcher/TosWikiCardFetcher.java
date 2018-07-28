package main.fetcher;

import flyingkite.data.Range;
import flyingkite.log.L;
import flyingkite.log.LF;
import flyingkite.tool.TextUtil;
import flyingkite.tool.TicTac2;
import main.card.TosCard;
import main.card.TosCardCreator;
import main.card.TosCardCreator.CardInfo;
import main.fetcher.data.Anchors;
import main.kt.CardTds;
import main.kt.IconInfo;
import main.kt.SkillInfo;
import main.kt.TosGet;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import wikia.articles.UnexpandedArticle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Old card fetcher depends on API results, and api may lack of pages
 */
@Deprecated
public class TosWikiCardFetcher extends TosWikiBaseFetcher {
    private TosWikiCardFetcher() {}
    public static final TosWikiCardFetcher me = new TosWikiCardFetcher();
    private final LF Lf = new LF("mydata");
    private final String tosApi = "http://zh.tos.wikia.com/api/v1/Articles/List?limit=2500000";
    private final LF Lfc = new LF("mydata", "ca.json");
    private final LF LfPage = new LF("mydata", "pages.txt");
    private final LF LfCard = new LF("mydata", "cardList.json");
    public final String TOS_ALL_CARD = LfCard.getFile().toString();//"mydata/cardList.json";

    @Override
    public String getAPILink() {
        return tosApi;
    }

    @Override
    public LF getHttpLF() {
        return Lf;
    }

    // #1844 = 816 = 勇氣白羊 ‧ 波比
    // #0173 = 1063 = 鳴動威嚴 ‧ 摩迪與曼尼
    // #0023 = 24 = 青龍孟章神君
    // #0149 = 1041 = 九天應元 ‧ 聞仲
    private int from = 149; // 447 = #131
    private int prefetch = 1;
    private static final int CARD_END = 2500; // 2500 is safe end, raise value when new card added. Ended at #2239

    private boolean runChecker = false;

    @Override
    protected List<String> getTestLinks() {
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
                //---
                , "http://zh.tos.wikia.com/wiki/001" // TosCardCreator = 18
                , "http://zh.tos.wikia.com/wiki/024" // TosCardCreator = 28
                , "http://zh.tos.wikia.com/wiki/1001" // TosCardCreator = 16
                , "http://zh.tos.wikia.com/wiki/1017" // TosCardCreator = 22
                , "http://zh.tos.wikia.com/wiki/1063" // TosCardCreator = 32
                , "http://zh.tos.wikia.com/wiki/651" // TosCardCreator = 24
                , "http://zh.tos.wikia.com/wiki/656" // TosCardCreator = 31
        );
        link.clear(); // uncomment this if use test links
        return link;
    }

    @Override
    public void run() {
        // About 5 min 36 sec
        // Parameters setting
        mFetchAll = 0 < 3;

        // Get the range sets
        Source source = getSource(from, prefetch);
        if (source == null) return;
        ResultSet set = source.results;
        Range rng = source.range;
        boolean useTest = useTest();

        // Start to fetch
        TicTac2 tt = new TicTac2();
        tt.setLog(false);

        // Open log files
        Lf.getFile().open();
        Lf.setLogToL(!mFetchAll);
        Lfc.getFile().delete().open();
        Lfc.setLogToL(false);
        LfPage.getFile().delete().open();
        LfPage.setLogToL(false);

        // Required data
        int percent = 0, crafts = 0, mvps = 0;
        Set<String> cardSet = new HashSet<>();
        Set<String> cardSeriesSet = new TreeSet<>();
        List<TosCard> cards = new ArrayList<>();
        List<TosCard> cardsNoDup = new ArrayList<>();

        tt.tic();
        for (int i = rng.min; i < rng.max; i++) {
            // Get link
            String link = getSourceLinkAt(source, i);

            if (mFetchAll) {
                tt.tac("%s fetchCard ", i - 1);
                tt.tic();
                L.log("#%s -> %s", i, link);
                LfPage.log("%s", link);
            }

            boolean hasPercent = link.contains("%");
            boolean isCraft = link.matches(".+/wiki/C\\d+");
            boolean isSkin = link.matches(".+/wiki/S\\d+"); // 動態造型
            boolean isM = link.matches(".+/wiki/M\\d+"); // 72柱神
            boolean isV = link.matches(".+/wiki/V\\d+"); // 迪士尼
            boolean isP = link.matches(".+/wiki/P\\d+"); // 討伐戰
            if (hasPercent) {
                percent++;
            } else if (isCraft) {
                crafts++;
                //L.log("Craft #%s -> %s", i, link);
            } else if (isSkin || isM || isP || isV) {
                mvps++;
                //L.log("MVPS #%s -> %s", i, link);
            } else {
                // Step 3: For valid links, get its card info
                Lf.log("#%04d -> %s, %s", i, link, useTest ? "" : set.getItems()[i]);

                CardInfo card = getCardInfo(link);
                TosCard tosCard = TosCardCreator.me.asTosCard(card);
                TosCardCreator.me.inspectCard(tosCard, Lfc);
                if (tosCard == null) {
                    Lfc.log("X_X, No card %s", card.wikiLink); // For 龍刻
                } else {
                    cards.add(tosCard);
                    cardSeriesSet.add(tosCard.series);
                    // Use id as key, Add to non-duplicated
                    // For BigBang series, it has same name but different id
                    // http://zh.tos.wikia.com/wiki/611
                    // http://zh.tos.wikia.com/wiki/676
                    String key = tosCard.idNorm;
                    boolean hasKey = cardSet.contains(key);
                    if (hasKey) {
                        Lf.log("%s exists key = %s", i, key);
                    } else {
                        cardSet.add(key);
                        cardsNoDup.add(tosCard);
                        String json = mGson.toJson(tosCard, TosCard.class);
                        Lfc.log(json);
                    }
                }
            }
            Lf.log("---------------------");
        }
        tt.tac("%s fetchCard", rng.max - 1);
        tt.setLog(true);

        if (runChecker) {
            Lf.log("------ Checker Start -------");
            TosWikiChecker.me.check(cards);
            Lf.log("------ Checker End -------");
        }

        Lf.setLogToL(true);

        Lf.log("links: %s has %%, %s are crafts", percent, crafts);
        Lf.log("sizes are %s", itemsN);
        Lf.log("%s series => %s", cardSeriesSet.size(), cardSeriesSet);
        Lf.log("%s cards", cards.size());
        Lf.log("%s cards not duplicate", cardsNoDup.size());
        Lf.getFile().close();
        Lfc.getFile().close();
        LfPage.getFile().close();

        saveCardsToGson(LfCard, cardsNoDup);
        L.log("Tos Card OK Done %s", tag());
    }

    @Deprecated
    private void downloadToLocal() {
        // Download is too big file, > 1g pages
        L.log("downloadToLocal");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(tosApi).build();
        Response response;
        ResponseBody body = null;

        LF wikia = new LF("mydata/wikia", "a_wikia.txt");
        wikia.setLogToL(false);
        wikia.getFile().delete().open();
        Lf.getFile().delete().open();
        TicTac2 tt = new TicTac2();

        // Step 1: Fetch all links from Wikia API
        try {
            Lf.log("Linking %s", tosApi);
            tt.tic();
            response = client.newCall(request).execute();
            tt.tac("response = %s", response);

            if (response != null) {
                tt.tic();
                body = response.body();
                tt.tac("body = %s", body);
            }
            if (body == null) return;

            String s = body.string();
            wikia.log(s);
            wikia.getFile().close();

            tt.tic();
            ResultSet set = mGson.fromJson(s, ResultSet.class);
            tt.tac("from gson, %s", set);

            if (set != null && set.getItems() != null) {
                // Step 2: Determine the range of parsing
                int min = 0, max = set.getItems().length;
                tt.setLog(false);
                tt.tic();
                for (int i = min; i < max; i++) {
                    UnexpandedArticle a = set.getItems()[i];
                    String link = set.getBasePath() + "" + a.getUrl();
                    tt.tac("%s fetchCard ", i - 1);
                    tt.tic();
                    Lf.log("#%s -> %s", i, link);
                    String filename = String.format("p%05d.txt", i);
                    LF page = new LF("mydata/wikia", filename);
                    page.setLogToL(false);
                    page.getFile().delete().open();

                    request = request.newBuilder().url(link).build();
                    tt.tic();
                    response = client.newCall(request).execute();
                    tt.tac("response = %s", response);
                    tt.tic();
                    body = response.body();
                    tt.tac("body = %s", body);
                    if (body == null) {
                        page.getFile().close();
                        return;
                    }

                    page.log(body.string());
                    page.getFile().close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Lf.getFile().close();
    }

    private void saveCardsToGson(LF f, List<TosCard> cardList) {
        TicTac2 clk = new TicTac2();
        f.setLogToL(false);
        cardList.sort((c1, c2) -> {
            int n1 = Integer.parseInt(c1.idNorm);
            int n2 = Integer.parseInt(c2.idNorm);
            return Integer.compare(n1, n2);
        });

        // Step 1: Write cardList to json file
        clk.tic();
        String msg = writeAsGson(cardList, f);
        clk.tac("%s cards written", cardList.size());

        // Step 2: Try to parsing back to know its performance
        clk.tic();
        TosCard[] cardA = mGson.fromJson(msg, TosCard[].class);
        clk.tac("%s cards parsed back", cardA.length);
    }

    private Set<Integer> itemsN = new HashSet<>();

    private CardInfo getCardInfo(String link) {
        CardInfo info = new CardInfo();

        // Step 1: Get the xml node from link by Jsoup
        Document doc = getDocument(link);
        if (doc == null) return info;

        // Step 2: Find the <center> nodes
        Lf.log("Title = %s", doc.title());
        Elements centers = doc.getElementsByTag("center");
        //Lf.log("%s centers", centers.size());

        // Step 3: And the icon & big image is in 1st & 2nd node
        info.wikiLink = link;
        info.bigImage = getImage(centers, 0);
        info.icon = getImage(centers, 1);

        List<String> cardInfo = info.data;
        boolean logNode = false;
        boolean logAnchor = true;
        // Step 4 : Get the card info from 3rd node, in <td>
        if (centers.size() > 2) {
            CardTds cardTds = TosGet.me.getCardTds(centers.get(1));
            List<String> tds = null;
            if (cardTds != null) {
                tds = cardTds.getTds();
            }
            if (tds != null) {
                // Only take from 0 ~ "基本屬性", "主動技" to end (before "競技場 防守技能" or "來源")
                String[] anchor = Anchors.allNames();
                int[] anchors = findAnchors(tds, anchor);

                // Adding basic hp/exp info for card
                addHpInfo(info, anchors, tds);
                addExpInfo(info, anchors, tds);
                // Adding amelioration/awaken info for card
                List<IconInfo> ameInfo = TosGet.me.getCardImagedLink(doc);
                addAmeAwkInfo(info, ameInfo, anchors, tds, cardTds);

                // Find the end of card
                int min = getPositiveMin(anchors, Anchors.AwakenRecall.id(), anchors.length);
                if (logAnchor) {
                    Lf.log("anchors => %s => %s", Arrays.toString(anchors), min);
                }

                if (cardTds.getImages().size() == 0) {
                    Lf.log("No evolutions? %s", link);
                } else {
                    L.log("Evos = %s", cardTds.getImages());
                    info.anchors = Arrays.copyOf(anchors, anchors.length);
                    info.cardTds = cardTds;
                    //info.evolution.addAll(cardTds.getImages());
                }

                // Add name, color, stars, hp, attack, heal
                cardInfo.addAll(tds.subList(0, anchors[0]));

                // Add skill of active & leader
                for (int i = anchors[1]; i < min; i++) {
                    cardInfo.add(tds.get(i));
                }

                // TODO : node info
                //printList(cardInfo);
                // This just peek the cardInfo size, used for TosCardCreator
                int num = cardInfo.size();
                if (!itemsN.contains(num)) {
                    L.log("%s, card = %s", num, link);
                }
                itemsN.add(cardInfo.size());
                // Original list
                //Lf.log("---------------------");
                //printList(s);
            }

            // Original xml
            if (logNode) {
                Lf.log("---------------------");
                Lf.log("center(1) = \n%s", centers.get(1));
            }
        }

        // Step 5 : Get the card details
        info.detailsHtml = TosGet.me.getCardDetailsNormed(doc);

        return info;
    }

    private void addHpInfo(CardInfo info, int[] anchors, List<String> tds) {
        int maxhpStart = anchors[0] + 1;
        info.hpValues.addAll(tds.subList(maxhpStart, maxhpStart + 3));
        int minhpStart = maxhpStart + 18; // 6*3
        info.hpValues.addAll(tds.subList(minhpStart, minhpStart + 3));
    }

    private void addExpInfo(CardInfo info, int[] anchors, List<String> tds) {
        info.expInfos.add(tds.get(8)); // Exp curve
        int sacrifyExpStart = anchors[0] + 35; // 6*5 + 5
        info.expInfos.add(tds.get(sacrifyExpStart)); // Sacrifice Exp Lv1
        info.expInfos.add(tds.get(sacrifyExpStart + 6)); // Sacrifice Exp per Lv
    }

    private void addAmeAwkInfo(CardInfo info, List<IconInfo> iconInfo, int[] anchors, List<String> tds, CardTds rawCard) {
        boolean empty = iconInfo == null || iconInfo.size() == 0;

        if (empty) return;
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
            } else {
                // Missing the 潛能解放關卡, it is added in previous monster
                //L.log("ERROR!!!!! Missing 潛能解放關卡 : %s -> %s ", tds.get(0), info.wikiLink);
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

    private IconInfo getIconInfoByName(String name, List<IconInfo> iconInfo) {
        for (IconInfo i : iconInfo) {
            if (i.getName().equals(name)) {
                return i;
            }
        }
        return null;
    }

    private int getPositiveMin(int[] numbers, int start, int end) {
        int min = numbers[start];
        // min = min(anchors[2 : an]), and ignore -1 value
        for (int i = start; i < end; i++) {
            if (numbers[i] > 0) {
                if (min < 0) { // first value
                    min = numbers[i];
                } else {
                    min = Math.min(min, numbers[i]);
                }
            }
        }
        return min;
    }

    private void printList(List<String> list) {
        if (list == null) return;

        int n = list.size();
        for (int i = 0; i < n; i++) {
            String s = list.get(i);
            Lf.log("#%s -> /%s/", i, s);
        }
    }

    private String getImage(Elements elements, int index) {
        if (elements == null || elements.size() <= index) return "";

        Element e = elements.get(index);

        String img = TosGet.me.getImage(e);
        if (!TextUtil.isEmpty(img)) {
            // TODO : Image link
            //Lf.log("image #%s = %s", index + 1, img);
        }
        return img;
    }

// English Wiki
// http://towerofsaviors.wikia.com/wiki/Tower_of_Saviors_Wiki

// Chinese Wiki
// http://zh.tos.wikia.com/wiki/%E7%A5%9E%E9%AD%94%E4%B9%8B%E5%A1%94_%E7%B9%81%E4%B8%AD%E7%B6%AD%E5%9F%BA

// API Documentation
// http://towerofsaviors.wikia.com/api/v1
// http://zh.tos.wikia.com/api/v1

// For the category, ns = 14 (http://towerofsaviors.wikia.com/wiki/Category:Skill_Icons)
// http://community.wikia.com/wiki/Help:Namespaces

// Main : 神魔之塔_繁中維基
// http://zh.tos.wikia.com/wiki/%E7%A5%9E%E9%AD%94%E4%B9%8B%E5%A1%94_%E7%B9%81%E4%B8%AD%E7%B6%AD%E5%9F%BA

// URL Encoding Functions
// https://www.w3schools.com/tags/ref_urlencode.asp
}
