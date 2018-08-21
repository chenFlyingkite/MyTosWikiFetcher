package main.ptt;

import java.io.IOException;

import flyingkite.tool.TicTac2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public abstract class BasePPTFetcher {
    protected static final String pptCC = "https://www.ptt.cc";
    protected static final String pptBBS = pptCC + "/bbs";
    protected static TicTac2 ttClient = new TicTac2();

    public abstract String getPPTLink();

    public static Elements getHttp(final String link, String tag) {
        Document doc;
        try {
            ttClient.tic();
            doc = Jsoup.connect(link).timeout(40_000).maxBodySize(0).get();
            ttClient.tac("Jsoup connect");

            if (doc == null) return null;

            return doc.getElementsByClass(tag);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
