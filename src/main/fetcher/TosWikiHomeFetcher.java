package main.fetcher;

import flyingkite.log.L;
import main.kt.HomeRow;
import main.kt.HomeTable;
import main.kt.SealEventItem;
import main.kt.SealEventTable;
import main.kt.TosGet;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import flyingkite.log.LF;

public class TosWikiHomeFetcher extends TosWikiBaseFetcher {
    public static final TosWikiHomeFetcher me = new TosWikiHomeFetcher();
    private static final String folder = "myHome";
    private LF mLf = new LF(folder);

    private String getPage() {
        // 神魔之塔_繁中維基
        return "https://tos.fandom.com/zh/wiki/%E7%A5%9E%E9%AD%94%E4%B9%8B%E5%A1%94_%E7%B9%81%E4%B8%AD%E7%B6%AD%E5%9F%BA";
    }

    @Override
    public void run() {
        mLf.getFile().open(false);

        Document doc = getDocument(getPage());
        clock.tic();
        getMain(doc);
        getSealEvent(doc);
        clock.tac("TosWikiHomeFetcher OK");

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
            if (row.isChallenge()) {
                mLf.log(mGson.toJson(row.toStage()) + ",");
            }
        }
    }

    private void getSealEvent(Document doc) {
        Element main = doc.getElementById("SealEvent");
        if (main == null) return;
        SealEventTable st = TosGet.me.getTosSealEvent(main, wikiBaseZh);

        for (int i = 0; i < st.getRows().size(); i++) {
            SealEventItem x = st.getRows().get(i);
            L.log("#%s = %s", i, x);
        }


    }
}
