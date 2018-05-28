package main.fetcher;

import main.card.CardTds;
import main.card.IconInfo;
import main.card.TosCard;
import main.card.TosCardCreator;
import main.card.TosCardCreator.CardInfo;
import main.card.TosGet;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.MathUtil;
import util.data.Range;
import util.logging.L;
import util.logging.LF;
import util.tool.TextUtil;
import util.tool.TicTac2;
import wikia.articles.UnexpandedArticle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TosWikiCardFetcher extends TosWikiBaseFetcher {
    private TosWikiCardFetcher() {}
    public static final TosWikiCardFetcher me = new TosWikiCardFetcher();
    private final LF Lf = new LF("mydata");
    private final String tosApi = "http://zh.tos.wikia.com/api/v1/Articles/List?limit=2500000";
    private final LF Lfc = new LF("mydata", "ca.json");

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

    private List<String> getTestLinks() {
        List<String> list = Arrays.asList(
                "http://zh.tos.wikia.com/wiki/003"

        );
        //return list;
        return new ArrayList<>();
    }

    public void run() {
        // About 5 min 36 sec
        // Parameters setting
        mFetchAll = 0 < 3;

        // Get the range sets
        Source source = getSource();
        if (source == null) return;
        ResultSet set = source.results;
        Range rng = source.range;
        boolean useTest = useTest();

        // Start to fetch
        TicTac2 tt = new TicTac2();
        tt.setLog(false);

        // Open logging files
        Lf.getFile().open();
        Lf.setLogToL(!mFetchAll);
        Lfc.getFile().delete().open();
        Lfc.setLogToL(false);

        // Required data
        int percent = 0, crafts = 0;
        List<TosCard> cards = new ArrayList<>();
        Set<String> cardSet = new HashSet<>();
        List<TosCard> cardsNoDup = new ArrayList<>();

        tt.tic();
        for (int i = rng.min; i < rng.max; i++) {
            // Get link
            String link = "";
            if (useTest) {
                link = source.links.get(i);
            } else {
                UnexpandedArticle a = set.getItems()[i];
                link = set.getBasePath() + "" + a.getUrl();
            }

            if (mFetchAll) {
                tt.tac("%s fetchCard ", i - 1);
                tt.tic();
                L.log("#%s -> %s", i, link);
            }

            boolean hasPercent = link.contains("%");
            boolean isCraft = link.contains("/wiki/C");
            if (hasPercent) {
                percent++;
            } else if (isCraft) {
                crafts++;
                //L.log("Craft #%s -> %s", i, link);
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
                    // Use name + id as key, Add to non-duplicated
                    // For BigBang series, it has same name but different id
                    // http://zh.tos.wikia.com/wiki/611
                    // http://zh.tos.wikia.com/wiki/676
                    String key = tosCard.idNorm + "_" + tosCard.name;
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
        Lf.log("%s cards", cards.size());
        Lf.log("%s cards not duplicate", cardsNoDup.size());
        Lf.getFile().close();
        Lfc.getFile().close();

        writeToJson(cardsNoDup);
    }

    private boolean useTest() {
        return getTestLinks().size() > 0;
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
//                if (!fetchAll) {
//                    min = Math.max(min, from);
//                    max = Math.min(max, from + prefetch);
//                }
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

    private void writeToJson(List<TosCard> cardList) {
        TicTac2 clk = new TicTac2();
        LF f = new LF("mydata", "cardList.json");
        f.setLogToL(false);
        cardList.sort((c1, c2) -> {
            int n1 = Integer.parseInt(c1.idNorm);
            int n2 = Integer.parseInt(c2.idNorm);
            return Integer.compare(n1, n2);
        });

        // Step 1: Write cardList to json file
        f.getFile().delete().open();
        TosCard[] arr = cardList.toArray(new TosCard[cardList.size()]);
        clk.tic();
        String json = mGson.toJson(arr, TosCard[].class);
        clk.tac("%s cards written", arr.length);
        f.log(json);
        f.getFile().close();

        // Step 2: Try to parsing back to know its performance
        clk.tic();
        TosCard[] cardA = mGson.fromJson(json, TosCard[].class);
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
                String[] anchor = {"基本屬性", "主動技"
                        , "競技場 防守技能"
                        , "昇華" // Amelioration
                        , "極限突破" // Awaken recall
                        , "進化列表" // Evolve
                        , "合體列表" // Combination
                        , "潛能解放" // Power release
                        , "異空轉生" // Virtual rebirth
                        , "異力轉換", "來源"};
                int[] anchors = getAnchors(tds, anchor);

                // Adding basic hp/exp info for card
                addHpInfo(info, anchors, tds);
                addExpInfo(info, anchors, tds);
                // Adding amelioration/awaken info for card
                List<IconInfo> ameInfo = TosGet.me.getCardImagedLink(doc);
                addAmeAwkInfo(info, ameInfo, anchors, tds);

                // Find the end of card
                int min = getPositiveMin(anchors, 4, anchors.length);
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
                    // There exists 競技場 elements (81 cards, 莫莉&伊登&希臘), we omit it & include 昇華
                    if (anchors[2] >= 0 && isInRange(i, anchors[2], anchors[3] >= 0 ? anchors[3] : min)) {
                        //L.log("oooomit: anchor = %s, %s @ %s", anchors[2], i, tds.get(i));
                    } else {
                        cardInfo.add(tds.get(i));
                    }
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

    private boolean isInRange(long value, long min, long max) {
        return MathUtil.isInRange(value, min, max);
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

    private void addAmeAwkInfo(CardInfo info, List<IconInfo> iconInfo, int[] anchors, List<String> tds) {
        boolean empty = iconInfo == null || iconInfo.size() == 0;

        if (empty) return;
        int ax;

        // Fetch if has 昇華關卡
        ax = 3;
        if (anchors[ax] >= 0) {
            IconInfo icf = iconInfo.get(0);
            int at = getPositiveMin(anchors, ax + 1, anchors.length);
            if (icf.getName().equals(tds.get(at - 1))) {
                info.ameStages.add(icf.getName());
                info.ameStages.add(wikiBaseZh + icf.getLink());
            }
        }

        // Fetch if has 突破關卡
        ax = 4;
        if (anchors[ax] >= 0) {
            IconInfo icf = iconInfo.get(1);
            int at = getPositiveMin(anchors, ax + 1, anchors.length);
            if (icf.getName().equals(tds.get(at - 1))) {
                info.awkStages.add(tds.get(anchors[ax] + 1)); // Skill name
                info.awkStages.add(tds.get(anchors[ax] + 2)); // = icf.getName(), stage name
                info.awkStages.add(wikiBaseZh + icf.getLink()); // battle link
            }
        }

        // Fetch if has 潛能解放
        ax = 7;
        if (anchors[ax] >= 0) {
            IconInfo icf = iconInfo.get(iconInfo.size() - 1);
            int at = getPositiveMin(anchors, ax + 1, anchors.length);
            if (icf.getName().equals(tds.get(at - 1))) {
                //info.powStages.add(tds.get(anchors[ax] + 1)); // Skill name
                info.powStages.add(tds.get(at - 1)); // = icf.getName(), stage name
                info.powStages.add(wikiBaseZh + icf.getLink()); // battle link
            } else {
                // Missing the 潛能解放關卡, it is added in previous monster
                //L.log("ERROR!!!!! Missing 潛能解放關卡 : %s -> %s ", tds.get(0), info.wikiLink);
            }
        }
    }

    private <T> int[] getAnchors(List<T> list, T[] anchor) {
        int n = list.size();
        int an = anchor.length;
        int[] anchors = new int[an];
        // init as -1
        Arrays.fill(anchors, -1);

        for (int i = 0; i < n; i++) {
            T si = list.get(i);
            for (int j = 0; j < an; j++) {
                if (anchors[j] < 0 && anchor[j].equals(si)) {
                    anchors[j] = i;
                }
            }
        }
        return anchors;
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


    private Source getSource() {
        // Get the range sets
        List<String> tests = getTestLinks();
        boolean useTest = useTest();
        Source src = new Source();
        if (useTest) {
            src.results = new ResultSet();
            src.range = new Range(0, tests.size());
            src.links = tests;
        } else {
            src.results = getApiResults();
            if (!hasResult(src.results)) return null;

            src.range = getRange(src.results, from, prefetch);
        }
        return src;
    }

    private class Source {
        private ResultSet results;
        private Range range;
        private List<String> links = new ArrayList<>();
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
