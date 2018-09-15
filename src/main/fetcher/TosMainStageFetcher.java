package main.fetcher;

import java.util.List;

import flyingkite.log.LF;
import main.kt.MainStage;
import main.kt.TosGet;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class TosMainStageFetcher extends TosWikiBaseFetcher {
    private TosMainStageFetcher() {}
    public static final TosMainStageFetcher me = new TosMainStageFetcher();
    private static final String folder = "myMainStage";
    private LF mLf = new LF(folder);
    private LF mStage = new LF(folder, "mainStage.json");

    private String getPage() {
        // 主線任務
        return "http://zh.tos.wikia.com/wiki/%E4%B8%BB%E7%B7%9A%E4%BB%BB%E5%8B%99";
    }

    @Override
    public void run() {
        mLf.getFile().open(false);

        // Fetch
        clock.tic();
        List<MainStage> groups = getMainStages(getPage());
        int n = groups.size();
        clock.tac("Fetched %s Main stages", n);

        // Printing stages
        mLf.log("%s main stages", n);
        for (int i = 0; i < n; i++) {
            mLf.log("#%s : %s", i, groups.get(i));
        }

        // to gson
        clock.tic();
        writeAsGson(groups, mStage);
        clock.tac("%s written Done OK %s", n, tag());
        mLf.getFile().close();
    }

    private List<MainStage> getMainStages(String link) {
        Document doc = getDocument(link);

        Element main = doc.getElementById("mw-content-text");
        return TosGet.me.getMainStages(main, wikiBaseZh);
    }
}