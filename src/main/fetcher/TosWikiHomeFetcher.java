package main.fetcher;

import main.card.HomeRow;
import main.card.HomeTable;
import main.card.TosGet;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import flyingkite.log.LF;

public class TosWikiHomeFetcher extends TosWikiBaseFetcher {
    private TosWikiHomeFetcher() {}
    public static final TosWikiHomeFetcher me = new TosWikiHomeFetcher();
    private static final String folder = "myHome";
    private LF mLf = new LF(folder);

    private String getPage() {
        // 神魔之塔_繁中維基
        return "http://zh.tos.wikia.com/wiki/%E7%A5%9E%E9%AD%94%E4%B9%8B%E5%A1%94_%E7%B9%81%E4%B8%AD%E7%B6%AD%E5%9F%BA";
    }

    @Override
    public void run() {
        mLf.getFile().open(false);

        Document doc = getDocument(getPage());
        clock.tic();
        getMain(doc);
        clock.tac("Main OK");

        mLf.getFile().close();
    }

    private void getMain(Document doc) {
        Element main = doc.getElementById("Timetable");
        if (main == null) return;
        int n = main.children().size();
        if (n < 4) return;
        HomeTable table = TosGet.me.getTosMainInfo(main, wikiBaseZh);
        mLf.log("" + table.getTitle());
        for (HomeRow row : table.getRows()) {
            mLf.log("" + row);
        }
    }
}
