package main.ptt;

import main.fetcher.TosWikiBaseFetcher;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.logging.L;
import util.tool.TicTac2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class BasePPTFetcher {
    protected static final String pptCC = "https://www.ptt.cc";
    protected static final String pptBBS = pptCC + "/bbs";
    protected static TicTac2 ttClient = new TicTac2();

    public abstract String getPPTLink();

    public static Elements getHttp(final String link, String tag) {
        Document doc;
        try {
            ttClient.tic();
            doc = Jsoup.connect(link).timeout(40_000).get();
            ttClient.tac("Jsoup connect");

            if (doc == null) return null;

            return doc.getElementsByClass(tag);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
