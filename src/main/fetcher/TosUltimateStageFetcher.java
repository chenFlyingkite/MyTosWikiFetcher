package main.fetcher;

import flyingkite.log.LF;
import main.kt.Stage;
import main.kt.StageGroup;
import main.kt.TosGet;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;

public class TosUltimateStageFetcher  extends TosWikiBaseFetcher {
    public static final TosUltimateStageFetcher me = new TosUltimateStageFetcher();
    private static final String folder = "myUltimateStage";
    private LF mLf = new LF(folder);
    private LF mStage = new LF(folder, "ultimateStage.json");

    private String getPage() {
        // 地獄級任務
        //return "https://tos.fandom.com/zh/wiki/地獄級任務";
        return "https://tos.fandom.com/zh/wiki/%E5%9C%B0%E7%8D%84%E7%B4%9A%E4%BB%BB%E5%8B%99";
    }

    @Override
    public void run() {
        mLf.getFile().open(false);
        clock.tic();
        // Start here

        List<Stage> li = getFlyTabs(getPage());
        mLf.log("%s ultimate stages", li.size());
        for (Stage s : li) {
            mLf.log("%s", s);
        }

        clock.tac("%s Done", tag());

        StageGroup ulti = new StageGroup();
        ulti.setGroup("ultimate");
        ulti.getStages().addAll(li);
        // Writing gson
        writeAsGson(ulti, mStage);

        mLf.getFile().close();
    }

    private List<Stage> getFlyTabs(String link) {
        Document doc = getDocument(link);

        Element main = doc.getElementById("mw-content-text");
        return TosGet.me.getUltimateStages(main, wikiBaseZh);
    }
}
