package main.fetcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.card.TosCard;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private int prefetch = 100;

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
                int min, max;
                if (fetchAll) {
                    min = 0;
                    max = set.getItems().length;
                } else {
                    min = from;
                    max = from + prefetch;
                }
                int percent = 0;
                for (int i = min; i < max; i++) {
                    UnexpandedArticle a = set.getItems()[i];
                    String link = set.getBasePath() + "" + a.getUrl();
                    L.log("#%s -> %s", i, link);
                    boolean hasPercent = link.indexOf('%') >= 0;
                    if (hasPercent) {
                        percent++;
                    } else {
                        Lf.log("#%04d -> %s, %s", i, link, set.getItems()[i]);
                        CardInfo card = getCardInfo(link);
                        TosCard tosCard = asTosCard(card.data, card.icon, card.bigImage);
                        cards.add(tosCard);
                        String json = mGson.toJson(tosCard, TosCard.class);
                        Lfc.log(json);

                        Lfc.log("------");
                    }
                }
                //Lf.log(" percent = %s", percent);
            }

            //Lf.log("--------------------- xxxx -----");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Lf.setLogToL(true);
        Lf.getFile().close();
        Lfc.getFile().close();

        L.log("sizes are %s", itemsN);
    }


    private Set<Integer> itemsN = new HashSet<>();
    private Set<String> cardNames = new HashSet<>();

    private class CardInfo {
        private List<String> data = new ArrayList<>();
        private String icon = "";
        private String bigImage = "";
    }

    private CardInfo getCardInfo(String link) {
        //Lf.log("jsoup link = %s", link);
        CardInfo info = new CardInfo();
        Document doc = null;
        try {
            doc = Jsoup.connect(link).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (doc == null) return info;

        Lf.log("Title = %s, Children = %s", doc.title(), doc.getAllElements().size());
        Elements centers = doc.getElementsByTag("center");
        //Lf.log("%s centers", centers.size());

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
                //L.log("%s items", cardInfo.size());
                int num = cardInfo.size();
                if (!itemsN.contains(num)) {
                    L.log("%s, card = %s", num, link);
                }
                itemsN.add(cardInfo.size());
                String name = s.get(0);
                if (cardNames.contains(name)) {
                    L.log("%s, collision %s, %s", num, name, link);
                }
                cardNames.add(name);
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


//  node length, page
//18, card = http://zh.tos.wikia.com/wiki/001
//28, card = http://zh.tos.wikia.com/wiki/024
//16, card = http://zh.tos.wikia.com/wiki/1001
//22, card = http://zh.tos.wikia.com/wiki/1017
//32, card = http://zh.tos.wikia.com/wiki/1063
//24, card = http://zh.tos.wikia.com/wiki/651
//31, card = http://zh.tos.wikia.com/wiki/656

    private TosCard asTosCard(List<String> list, String icon, String bigImg) {
        int n = list.size();
        switch (n) {
            case 18: return asTosCard_18(list, icon, bigImg);
            case 28: return asTosCard_28(list, icon, bigImg);
            case 16: return asTosCard_16(list, icon, bigImg);
            case 22: return asTosCard_22(list, icon, bigImg);
            case 32: return asTosCard_32(list, icon, bigImg);
            case 24: return asTosCard_24(list, icon, bigImg);
            case 31: return asTosCard_31(list, icon, bigImg);
        }
        return null;
    }


    private TosCard asTosCard_18(List<String> list, String icon, String bigImg) {
        TosCard c = new TosCard();

        fillImage(c, icon, bigImg);
        fillBasic(c, list.subList(0, 10));
//        c.name = list.get(0);
//        c.attribute = list.get(1);
//        c.id = list.get(2);
//        c.rarity = Integer.parseInt(list.get(3).substring(0, 1));
//        c.cost = Integer.parseInt(list.get(4));
//        c.race = list.get(5);
//        c.series = list.get(6);
//        c.LvMax = Integer.parseInt(list.get(7));
//        //-- Exp curve #8
//        c.ExpMax = Long.parseLong(list.get(9));
        //-- Skill Active name #10
        fillSkillActive(c, list.subList(11, 15));
//        c.skillName = list.get(11);
//        c.skillCDMin = Integer.parseInt(list.get(12));
//        c.skillCDMax = Integer.parseInt(list.get(13));
//        c.skillDesc = list.get(14);
        //-- Skill Leader name #15
        fillSkillLeader(c, list.subList(16, 18));
//        c.skillLeaderName = list.get(16);
//        c.skillLeaderDesc = list.get(17);

        return c;
    }

    private TosCard asTosCard_28(List<String> list, String icon, String bigImg) {
        TosCard c = new TosCard();

        fillImage(c, icon, bigImg);
        fillBasic(c, list.subList(0, 10));
        //-- Skill Active name #10
        fillSkillActive(c, list.subList(11, 15));
        //-- Skill Leader name #15
        fillSkillLeader(c, list.subList(16, 18));
        fillAmelioration(c, list.subList(19, 27));

        return c;
    }

    private TosCard asTosCard_16(List<String> list, String icon, String bigImg) {
        TosCard c = new TosCard();

        fillImage(c, icon, bigImg);
        fillBasic(c, list.subList(0, 10));
        //-- Skill Active name #10
        c.skillName = list.get(11);
        c.skillCDMin = 0;
        c.skillCDMax = 0;
        c.skillDesc = list.get(12);
        //-- Skill Leader name #15
        fillSkillLeader(c, list.subList(14, 16));

        return c;
    }

    private TosCard asTosCard_22(List<String> list, String icon, String bigImg) {
        TosCard c = new TosCard();

        fillImage(c, icon, bigImg);
        fillBasic(c, list.subList(0, 10));
        //-- Skill Active name #10
        fillSkillActive(c, list.subList(11, 15));
        fillSkillActive2(c, list.subList(15, 19));
        //-- Skill Leader name #19
        fillSkillLeader(c, list.subList(20, 22));

        return c;
    }

    private TosCard asTosCard_32(List<String> list, String icon, String bigImg) {
        TosCard c = new TosCard();
        fillImage(c, icon, bigImg);

        fillBasic(c, list.subList(0, 10));
        //-- Skill Active name #10
        fillSkillActive(c, list.subList(11, 15));
        fillSkillActive2(c, list.subList(15, 19));
        //-- Skill Leader name #19
        fillSkillLeader(c, list.subList(20, 22));
        //-- Amelioration # 22
        fillAmelioration(c, list.subList(23, 31));

        return c;
    }

    private TosCard asTosCard_24(List<String> list, String icon, String bigImg) {
        TosCard c = new TosCard();
        fillImage(c, icon, bigImg);

        fillBasic(c, list.subList(0, 10));
        //-- Skill Active name #10
        fillSkillActive(c, list.subList(11, 15));
        //-- Skill Leader name #15
        fillSkillLeader(c, list.subList(16, 18));
        fillAmelioration(c, list.subList(19, 23));

        return c;
    }

    private TosCard asTosCard_31(List<String> list, String icon, String bigImg) {
        TosCard c = new TosCard();

        fillImage(c, icon, bigImg);
        fillBasic(c, list.subList(0, 10));
        //-- Skill Active name #10
        fillSkillActive(c, list.subList(11, 15));
        fillSkillActive2(c, list.subList(15, 19));
        //-- Skill Leader name #19
        fillSkillLeader(c, list.subList(20, 22));
        fillAmelioration(c, list.subList(23, 31));

        return c;
    }

    private void fillImage(TosCard c, String icon, String bigImg) {
        c.icon = icon;
        c.bigImage = bigImg;
    }

    private void fillBasic(TosCard c, List<String> list) {
        c.name = list.get(0);
        c.attribute = list.get(1);
        c.id = list.get(2);
        c.rarity = Integer.parseInt(list.get(3).substring(0, 1));
        c.cost = Integer.parseInt(list.get(4));
        c.race = list.get(5);
        c.series = list.get(6);
        c.LvMax = Integer.parseInt(list.get(7));
        //-- Exp curve #8
        c.ExpMax = Long.parseLong(list.get(9));
    }

    private void fillSkillActive(TosCard c, List<String> list) {
        //-- Skill Active name #10
        c.skillName = list.get(0);
        c.skillCDMin = Integer.parseInt(list.get(1));
        c.skillCDMax = Integer.parseInt(list.get(2));
        c.skillDesc = list.get(3);
    }

    private void fillSkillActive2(TosCard c, List<String> list) {
        //-- Skill Active name #10
        c.skillName2 = list.get(0);
        c.skillCDMin2 = Integer.parseInt(list.get(1));
        c.skillCDMax2 = Integer.parseInt(list.get(2));
        c.skillDesc2 = list.get(3);
    }

    private void fillSkillLeader(TosCard c, List<String> list) {
        //-- Skill Leader name #15
        c.skillLeaderName = list.get(0);
        c.skillLeaderDesc = list.get(1);
    }

    private void fillAmelioration(TosCard c, List<String> list) {
        //-- Skill Leader name #15
        c.skillAmeliorationName1 = list.get(0);
        c.skillAmeliorationCost1 = Integer.parseInt(list.get(1));
        c.skillAmeliorationName2 = list.get(2);
        c.skillAmeliorationCost2 = Integer.parseInt(list.get(3));
        if (list.size() <= 4) return;

        c.skillAmeliorationName3 = list.get(4);
        c.skillAmeliorationCost3 = Integer.parseInt(list.get(5));
        c.skillAmeliorationName4 = list.get(6);
        c.skillAmeliorationCost4 = Integer.parseInt(list.get(7));
    }

// English Wiki
// http://towerofsaviors.wikia.com/wiki/Tower_of_Saviors_Wiki

// Chinese Wiki
// http://zh.tos.wikia.com/wiki/%E7%A5%9E%E9%AD%94%E4%B9%8B%E5%A1%94_%E7%B9%81%E4%B8%AD%E7%B6%AD%E5%9F%BA
}
