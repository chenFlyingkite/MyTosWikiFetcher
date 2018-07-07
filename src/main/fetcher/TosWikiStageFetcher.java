package main.fetcher;

import main.card.StageInfo;
import main.card.TosGet;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import util.logging.LF;

import java.util.ArrayList;
import java.util.List;

public class TosWikiStageFetcher extends TosWikiBaseFetcher {
    private TosWikiStageFetcher() {}
    public  static final TosWikiStageFetcher me = new TosWikiStageFetcher();

    private static final String enTosPage = "http://towerofsaviors.wikia.com/wiki/Summoner_Levels";
    private static final LF mLf = new LF("myStage");
    // 女王之塔
    private static final String link = "http://zh.tos.wikia.com/wiki/%E5%A5%B3%E7%8E%8B%E4%B9%8B%E5%A1%94";
    // 拜倒裙下
    // http://zh.tos.wikia.com/wiki/%E6%8B%9C%E5%80%92%E8%A3%99%E4%B8%8B
    // 法則的平衡
    // http://zh.tos.wikia.com/wiki/%E6%B3%95%E5%89%87%E7%9A%84%E5%B9%B3%E8%A1%A1
    // http://towerofsaviors.wikia.com/wiki/Balance_of_Order

    @Override
    public void run() {
        LF lf = mLf;
        lf.getFile().open(false);
        //lf.setLogToL(!mFetchAll);
        StageInfo info = getDoc();
        if (info != null) {
            List<String> list;
            // Headers
            list = info.getHeaders();
            lf.log("-------- T --------- ");
            lf.log(info.getTitle());
            lf.log("-------- H --------- " + list.size());
//            for (int i = 0; i < list.size(); i++) {
//                lf.log("#%2d = %s", i, list.get(i));
//            }
            lf.log("> %s ", list);

            list = info.getCells();
            lf.log("-------- C --------- " + list.size());
            List<String> s = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                //lf.log("#%3d = %s", i, list.get(i));
                s.add(list.get(i));
                int n = info.getHeaders().size();
                if (i % n == n - 1) {
                    lf.log("L %s", s);
                    lf.log("---");
                    s.clear();
                }
            }
        }
        lf.getFile().close();
    }

    private StageInfo getDoc() {
        Document doc = getDocument(link);
        if (doc == null) return null;

        Element stage = doc.getElementById("stage_table");
        return TosGet.me.getStageTable(stage);
    }
}
