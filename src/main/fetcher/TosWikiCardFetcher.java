package main.fetcher;

import main.card.CardTds;
import main.card.TosCard;
import main.card.TosCardCreator;
import main.card.TosCardCreator.CardInfo;
import main.card.TosGet;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.data.Range;
import util.logging.L;
import util.logging.LF;
import util.tool.TextUtil;
import util.tool.TicTac2;
import wikia.articles.UnexpandedArticle;

import java.io.IOException;
import java.util.*;

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

    //private boolean fetchAll = true;
    private int from = 0;
    private int prefetch = 200;
    private static final int CARD_END = 2500; // 2500 is safe end, raise value when new card added. Ended at #2239

    private boolean runChecker = false;

    public void run() {
        mFetchAll = true;
        ResultSet set = getApiResults();
        if (!hasResult(set)) return;

        Range rng = getRange(set, from, prefetch);

        TicTac2 tt = new TicTac2();
        tt.setLog(false);

        // Open logging files
        Lf.getFile().open();
        Lf.setLogToL(!mFetchAll);
        Lfc.getFile().delete().open();
        Lfc.setLogToL(false);

        // Required data
        int percent = 0;
        List<TosCard> cards = new ArrayList<>();
        Set<String> cardSet = new HashSet<>();
        List<TosCard> cardsNoDup = new ArrayList<>();

        tt.tic();
        for (int i = rng.min; i < rng.max; i++) {
            UnexpandedArticle a = set.getItems()[i];
            String link = set.getBasePath() + "" + a.getUrl();
            if (mFetchAll) {
                tt.tac("%s fetchCard ", i - 1);
                tt.tic();
                L.log("#%s -> %s", i, link);
            }

            boolean hasPercent = link.indexOf('%') >= 0;
            if (hasPercent) {
                percent++;
            } else {
                // Step 3: For valid links, get its card info
                Lf.log("#%04d -> %s, %s", i, link, set.getItems()[i]);
                CardInfo card = getCardInfo(link);
                TosCard tosCard = TosCardCreator.me.asTosCard(card);
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

        Lf.log("sizes are %s", itemsN);
        Lf.log("%s cards", cards.size());
        Lf.log("%s cards not duplicate", cardsNoDup.size());
        Lf.getFile().close();
        Lfc.getFile().close();

        writeToJson(cardsNoDup);
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
        final boolean logTime = false;
        CardInfo info = new CardInfo();

        // Step 1: Get the xml node from link by Jsoup
        Document doc = null;
        TicTac2 ts = new TicTac2();
        ts.setLog(logTime);
        ts.tic();
        try {
            doc = Jsoup.connect(link).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ts.tac("JSoup OK");
        if (doc == null) return info;

        // Step 2: Find the <center> nodes
        Lf.log("Title = %s, Children = %s", doc.title(), doc.getAllElements().size());
        Elements centers = doc.getElementsByTag("center");
        //Lf.log("%s centers", centers.size());

        // Step 3: And the icon & big image is in 1st & 2nd node
        info.wikiLink = link;
        info.bigImage = getImage(centers, 0);
        info.icon = getImage(centers, 1);

        List<String> cardInfo = info.data;
        List<String> cardValues = info.hpValues;
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
                        , "競技場 防守技能", "合體列表"
                        , "極限突破", "進化列表"
                        , "潛能解放", "異空轉生"
                        , "異力轉換", "來源"};
                int[] anchors = getAnchors(tds, anchor);

                int maxhpStart = anchors[0] + 1;
                cardValues.addAll(tds.subList(maxhpStart, maxhpStart + 3));
                int minhpStart = maxhpStart + 18; // 6*3
                cardValues.addAll(tds.subList(minhpStart, minhpStart + 3));

                // Find the end of card
                int min = getPositiveMin(anchors, 2, anchors.length);
                if (logAnchor) {
                    Lf.log("anchors => %s => %s", Arrays.toString(anchors), min);
                }

                if (cardTds.getEvolutions().size() == 0) {
                    Lf.log("No evolutions? %s", link);
                } else {
                    L.log("Evos = %s", cardTds.getEvolutions());
                }
                info.evolution.addAll(cardTds.getEvolutions());

                // Add name, color, stars, hp, attack, heal
                for (int i = 0; i < anchors[0]; i++) {
                    cardInfo.add(tds.get(i));
                }

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
        return info;
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

// English Wiki
// http://towerofsaviors.wikia.com/wiki/Tower_of_Saviors_Wiki

// Chinese Wiki
// http://zh.tos.wikia.com/wiki/%E7%A5%9E%E9%AD%94%E4%B9%8B%E5%A1%94_%E7%B9%81%E4%B8%AD%E7%B6%AD%E5%9F%BA
}
