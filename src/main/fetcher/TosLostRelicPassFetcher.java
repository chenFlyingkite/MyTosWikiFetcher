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
    private TosLostRelicPassFetcher() {}
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

        List<NameLink> li = getFlyTabs(getPage());
        mLf.log("%s pages", li.size());
        List<RelicStage> stages = new ArrayList<>();
        for (int i = 0; i < li.size(); i++) {
            NameLink n = li.get(i);
            mLf.log("#%s %s", i, n);
            stages = getRelicPassStages(n.getLink());
            for (int j = 0; j < stages.size(); j++) {
                mLf.log("  -> #%s %s", j, stages.get(j));
            }
        }

        clock.tac("%s Done", tag());

        // Writing gson
        writeAsGson(stages, mRelic);

        mLf.getFile().close();
    }

    private List<NameLink> getFlyTabs(String link) {
        Document doc = getDocument(link);

        Element fly = doc.getElementById("flytabs_0");
        Elements as = fly.getElementsByTag("a");
        List<NameLink> pages = new ArrayList<>();
        for (int i = 0; i < as.size(); i++) {
            Element e = as.get(i);
            NameLink a = new NameLink();
            a.setName(e.text());
            a.setLink(wikiLink(e.attr("href")));
            pages.add(a);
        }
        return pages;
    }

    private List<RelicStage> getRelicPassStages(String link) {
        Document doc = getDocument(link);

        Element main = doc.getElementById("mw-content-text");
        return TosGet.me.getRelicPassStages(main, wikiBaseZh);
    }
}
