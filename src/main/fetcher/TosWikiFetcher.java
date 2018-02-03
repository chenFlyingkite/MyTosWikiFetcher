package main.fetcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import util.logging.L;
import util.logging.LF;
import util.tool.TextUtil;
import util.tool.TicTac2;
import wikia.articles.UnexpandedArticle;
import wikia.articles.UnexpandedListArticleResultSet;

import java.io.IOException;
import java.util.*;

public class TosWikiFetcher {
    private TosWikiFetcher() {}
    public static final TosWikiFetcher me = new TosWikiFetcher();
    private final LF Lf = new LF("mydata");
    private final String tosApi = "http://zh.tos.wikia.com/api/v1/Articles/List?limit=2500000";
    private final LF Lfc = new LF("mydata", "ca.json");

    private TicTac2 tt = new TicTac2();
    private Gson mGson
            = new GsonBuilder().setPrettyPrinting().create();
            //= new Gson();

    private boolean fetchAll = true;
    private int from = 0;
    private int prefetch = 10;

    private boolean runChecker = false;

    public void run() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(tosApi).build();
        Lf.getFile().delete().open();
        Lf.setLogToL(!fetchAll);
        Lfc.getFile().delete().open();
        Lfc.setLogToL(false);

        Response response;
        ResponseBody body;
        List<TosCard> cards = new ArrayList<>();
        Set<String> cardSet = new HashSet<>();
        List<TosCard> cardsNoDup = new ArrayList<>();
        try {
            Lf.log("Linking %s", tosApi);
            tt.tic();
            response = client.newCall(request).execute();
            tt.tac("response = %s", response);
            tt.tic();
            body = response.body();
            tt.tac("body = %s", body);
            if (body == null) return;

            tt.tic();
            ResultSet set = mGson.fromJson(body.string(), ResultSet.class);
            //UnexpandedListArticleResultSet set = gson.fromJson(body.string(), UnexpandedListArticleResultSet.class);

            tt.tac("from gson, %s", set);

            if (set != null && set.getItems() != null) {
                int min = 0, max = set.getItems().length;
                if (!fetchAll) {
                    min = Math.max(min, from);
                    max = Math.min(max, from + prefetch);
                }
                int percent = 0;
                tt.setLog(false);
                tt.tic();
                for (int i = min; i < max; i++) {
                    UnexpandedArticle a = set.getItems()[i];
                    String link = set.getBasePath() + "" + a.getUrl();
                    if (fetchAll) {
                        tt.tac("%s fetchCard ", i - 1);
                        tt.tic();
                        L.log("#%s -> %s", i, link);
                    }
                    boolean hasPercent = link.indexOf('%') >= 0;
                    if (hasPercent) {
                        percent++;
                    } else {
                        Lf.log("#%04d -> %s, %s", i, link, set.getItems()[i]);
                        CardInfo card = getCardInfo(link);
                        TosCard tosCard = TosCardCreator.me.asTosCard(card);
                        if (tosCard == null) {
                            //Lfc.log("X_X %s", card.wikiLink); // For 龍刻
                        } else {
                            cards.add(tosCard);
                            // Add to non-duplicated
                            String key = tosCard.name;
                            if (!cardSet.contains(key)) {
                                cardSet.add(key);
                                cardsNoDup.add(tosCard);
                                String json = mGson.toJson(tosCard, TosCard.class);
                                Lfc.log(json);
                            }
                        }
                    }
                }
                tt.tac("%s fetchCard", max - 1);
                tt.setLog(true);

                if (runChecker) {
                    Lf.log("------ Checker Start -------");
                    TosWikiChecker.me.check(cards);
                    Lf.log("------ Checker End -------");
                }
            }

            //Lf.log("--------------------- xxxx -----");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Lf.setLogToL(true);
        Lf.getFile().close();
        Lfc.getFile().close();

        L.log("sizes are %s", itemsN);
        L.log("%s cards", cards.size());
        L.log("%s cards not duplicate", cardsNoDup.size());

        List<TosCard> cardList = cardsNoDup;
        LF lf2 = new LF("mydata", "dataList.json");
        lf2.setLogToL(false);
        lf2.getFile().delete().open();
        TosCard[] arr = cardList.toArray(new TosCard[cardList.size()]);
        tt.tic();
        String json = mGson.toJson(arr, TosCard[].class);
        tt.tac("All to json %s", arr.length);
        lf2.log(json);
        tt.tic();
        TosCard[] cardA = mGson.fromJson(json, TosCard[].class);
        tt.tac("Parsing back %s", cardA.length);
        lf2.getFile().close();

    }

    private Set<Integer> itemsN = new HashSet<>();

    private CardInfo getCardInfo(String link) {
        final boolean logTime = false;
        CardInfo info = new CardInfo();

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

        Lf.log("Title = %s, Children = %s", doc.title(), doc.getAllElements().size());
        Elements centers = doc.getElementsByTag("center");
        //Lf.log("%s centers", centers.size());

        info.wikiLink = link;
        info.bigImage = getImage(centers, 0);
        info.icon = getImage(centers, 1);
        List<String> cardInfo = info.data;
        boolean logNode = false;
        if (centers.size() > 2) {
            List<String> s = TosGet.me.getTd(centers.get(1));
            if (s != null) {
                // Only take from 0 ~ "基本屬性", "主動技" to end (before "競技場 防守技能" or "來源")
                String[] anchor = {"基本屬性", "主動技"
                        , "競技場 防守技能", "合體列表", "極限突破", "進化列表", "潛能解放", "異空轉生", "異力轉換", "來源"};
                int[] anchors = getAnchors(s, anchor);

                // Find the end of card
                int min = getPositiveMin(anchors, 2, anchors.length);
                //Lf.log("anchors => %s => %s", Arrays.toString(anchors), min);

                // Add name, color, stars, hp, attack, heal
                for (int i = 0; i < anchors[0]; i++) {
                    cardInfo.add(s.get(i));
                }

                // Add skill of active & leader
                for (int i = anchors[1]; i < min; i++) {
                    cardInfo.add(s.get(i));
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
        Lf.log("---------------------");
        return info;
    }

    private <T> int[] getAnchors(List<T> list, T[] anchor) {
        int n = list.size();
        int an = anchor.length;
        int[] anchors = new int[an];
        // init as -1
        for (int i = 0; i < an; i++) {
            anchors[i] = -1;
        }
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

    // class abbreviation
    private class ResultSet extends UnexpandedListArticleResultSet {
    }


// English Wiki
// http://towerofsaviors.wikia.com/wiki/Tower_of_Saviors_Wiki

// Chinese Wiki
// http://zh.tos.wikia.com/wiki/%E7%A5%9E%E9%AD%94%E4%B9%8B%E5%A1%94_%E7%B9%81%E4%B8%AD%E7%B6%AD%E5%9F%BA
}
