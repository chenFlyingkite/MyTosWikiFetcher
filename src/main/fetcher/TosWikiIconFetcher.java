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

public class TosWikiIconFetcher extends TosWikiBaseFetcher implements Runnable {
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

    @Override
    public void run() {
        // It is about 18 sec
        mFetchAll = true;

        ResultSet set = getApiResults();
        if (!hasResult(set)) return;
        Lf.getFile().open();
        Lf.setLogToL(true);

        Range rng = getRange(set, 0, 10);

        TicTac2 tt = new TicTac2();

        // For the category, ns = 14 (http://towerofsaviors.wikia.com/wiki/Category:Skill_Icons)
        // http://community.wikia.com/wiki/Help:Namespaces
        for (int i = rng.min; i < rng.max; i++) {
            UnexpandedArticle a = set.getItems()[i];
            String link = set.getBasePath() + "" + a.getUrl();
            Lf.log("#%s -> %s", i, link);

            if (a.getNameSpace() == 6) {
                tt.tic();
                IconInfo info = getIconInfo(link);
                downloadImage(info.getLink(), "myIcons", info.getName());
                tt.tac("OK, %s", info.getName());
            } else {
                Lf.log("Omit %s", link);
            }
        }
        Lf.getFile().close();
    }
}
