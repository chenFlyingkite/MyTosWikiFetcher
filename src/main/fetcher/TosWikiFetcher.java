package main.fetcher;

import com.google.gson.Gson;
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
import java.util.List;

public class TosWikiFetcher {
    private TosWikiFetcher() {}
    private static final LF Lf = new LF("D:\\GitHub\\MyTosWikiFetcher\\mydata");
    private static final String tosApi = "http://zh.tos.wikia.com/api/v1/Articles/List?limit=2500000";

    private static TicTac2 tt = new TicTac2();
    private static Gson mGson = new Gson();

    private static boolean fetchAll = true;

    public static void run() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(tosApi).build();
        Lf.getFile().delete().open();
        Lf.setLogToL(!fetchAll);

        Response response;
        ResponseBody body;
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
                int max = 10;
                if (fetchAll) {
                    max = set.getItems().length;
                }
                int percent = 0;
                for (int i = 0; i < max; i++) {
                    L.log("#%s", i);
                    UnexpandedArticle a = set.getItems()[i];
                    String link = set.getBasePath() + "" + a.getUrl();
                    boolean hasPercent = link.indexOf('%') >= 0;
                    if (hasPercent) {
                        percent++;
                    } else {
                        Lf.log("#%02d -> %s, %s", i, link, set.getItems()[i]);
                        getImage(link);
                    }
                }
                Lf.log(" percent = %s", percent);
            }

            Lf.log("--------------------- xxxx -----");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Lf.setLogToL(true);
        Lf.getFile().close();
    }

    private static void getImage(String link) {
        //Lf.log("jsoup link = %s", link);
        Document doc = null;
        try {
            doc = Jsoup.connect(link).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (doc == null) return;

        //Lf.log("Title = %s, Children = %s", doc.title(), doc.getAllElements().size());
        Elements centers = doc.getElementsByTag("center");
        //Lf.log("%s centers", centers.size());

        getImage(centers, 0);
        getImage(centers, 1);
        if (centers.size() > 2) {
            List<String> s = TosGet.me.getTd(centers.get(1));
            Lf.log("%s items, %s", s == null ? 0 : s.size(), s);
        }
        Lf.log("---------------------");
    }

    private static void getImage(Elements elements, int index) {
        if (elements == null || elements.size() <= index) return;

        Element e = elements.get(index);

        String img = TosGet.me.getImage(e);
        if (!TextUtil.isEmpty(img)) {
            Lf.log("image #%s = %s", index + 1, img);
        }
    }

    private String getCover(Element e) {
        Elements nos = e.getElementsByTag("noscript");
        Element x;
        Elements imgs;
        if (nos != null && nos.size() > 0) {
            x = nos.get(0);
            imgs = x.getElementsByTag("img");
            if (imgs != null && imgs.size() > 0) {
                Element en = imgs.get(0);
                if (en != null) {
                    String ig = en.attr("src");
                    Lf.log("image #1 = %s", ig);
                }
            }
        }
        return "";
    }

    // class abbreviation
    private class ResultSet extends UnexpandedListArticleResultSet {
    }
}
