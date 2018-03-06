package main.fetcher;

import main.card.IconInfo;
import main.card.TosGet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import util.data.Range;
import util.logging.L;
import util.logging.LF;
import util.tool.TicTac2;
import wikia.articles.UnexpandedArticle;

import java.io.IOException;

public class TosWikiIconFetcher extends TosWikiBaseFetcher {
    private TosWikiIconFetcher() {}
    public static final TosWikiIconFetcher me = new TosWikiIconFetcher();

    private final String iconsApi = "http://towerofsaviors.wikia.com/api/v1/Articles/List?category=icons&limit=250000";
    private final LF Lf = new LF("myIcons");

    @Override
    public String getAPILink() {
        return iconsApi;
    }

    @Override
    public LF getHttpLF() {
        return Lf;
    }

    public void run() {
        mFetchAll = true;

        ResultSet set = getApiResults();
        if (!hasResult(set)) return;

        Range rng = getRange(set, 0, 10);

        TicTac2 tt = new TicTac2();

        // For the category, ns = 14 (http://towerofsaviors.wikia.com/wiki/Category:Skill_Icons)
        // http://community.wikia.com/wiki/Help:Namespaces
        for (int i = rng.min; i < rng.max; i++) {
            UnexpandedArticle a = set.getItems()[i];
            String link = set.getBasePath() + "" + a.getUrl();
            L.log("#%s -> %s", i, link);

            if (a.getNameSpace() == 6) {
                tt.tic();
                IconInfo info = getIconInfo(link);
                downloadImage(info.getLink(), "myIcons", info.getName());
                tt.tac("OK, %s", info.getName());
            } else {
                L.log("Omit %s", link);
            }
        }
    }

    private IconInfo getIconInfo(String link) {
        boolean logTime = false;
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
        ts.tac("JSoup OK, %s", link);

        if (doc == null) {
            return new IconInfo();
        } else {
            return TosGet.me.getIcon(doc);
        }
    }
}
