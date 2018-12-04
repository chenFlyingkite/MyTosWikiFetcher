package main.fetcher;

import java.util.List;

import flyingkite.log.LF;
import main.kt.StageGroup;
import main.kt.TosGet;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class TosStoryStageFetcher extends TosWikiBaseFetcher {
    private TosStoryStageFetcher() {}
    public static final TosStoryStageFetcher me = new TosStoryStageFetcher();
    private static final String folder = "myStoryStage";
    private LF mLf = new LF(folder);
    private LF mStage = new LF(folder, "storyStage.json");

    private String getPage() {
        // 旅人的記憶 (故事模式) -> 故事模式
        return "http://zh.tos.wikia.com/wiki/%E6%95%85%E4%BA%8B%E6%A8%A1%E5%BC%8F";
    }

    @Override
    public void run() {
        mLf.getFile().open(false);
        clock.tic();
        // Start here

        List<StageGroup> li = getFlyTabs(getPage());
        mLf.log("%s pages", li.size());
        for (StageGroup s : li) {
            mLf.log("%s", s);
        }

        clock.tac("%s Done", tag());

        // Writing gson
        writeAsGson(li, mStage);

        mLf.getFile().close();
    }

    private List<StageGroup> getFlyTabs(String link) {
        Document doc = getDocument(link);

        Element main = doc.getElementById("mw-content-text");
        return TosGet.me.getStoryStages(main, wikiBaseZh);
    }
}
