package main.fetcher;

import java.util.ArrayList;
import java.util.List;

import flyingkite.log.LF;
import main.kt.NameLink;
import main.kt.RelicStage;
import main.kt.TosGet;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TosLostRelicPassFetcher extends TosWikiBaseFetcher {
    public static final TosLostRelicPassFetcher me = new TosLostRelicPassFetcher();
    private static final String folder = "myLostRelicPass";
    private LF mLf = new LF(folder);
    private LF mRelic = new LF(folder, "relicPass.json");

    private String getPage() {
        // 遺跡特許
        return "http://zh.tos.wikia.com/wiki/%E9%81%BA%E8%B7%A1%E7%89%B9%E8%A8%B1";
    }

    @Override
    public void run() {
        mLf.getFile().open(false);
        clock.tic();
        // Start here

        List<List<RelicStage>> allStages = getRelicPassStages(getPage());
        int n = allStages.size();
        mLf.log("%s pages", n);
        for (int i = 0; i < allStages.size(); i++) {
            mLf.log("#%s", i);
            List<RelicStage> stages = allStages.get(i);
            for (int j = 0; j < stages.size(); j++) {
                mLf.log("  -> #%2s  %s", j, stages.get(j));
            }
        }

        clock.tac("%s Done", tag());

        // Writing gson
        writeAsGson(allStages, mRelic);

        mLf.getFile().close();
    }

    private List<List<RelicStage>> getRelicPassStages(String link) {
        Document doc = getDocument(link);
        Elements tabs = doc.getElementsByClass("tabbertab");
        return TosGet.me.getRelicPassStages(tabs, wikiBaseZh);
    }
}
