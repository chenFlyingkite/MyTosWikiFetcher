package main.fetcher;

import flyingkite.log.LF;
import main.kt.MainStage;
import main.kt.RelicStage;
import main.kt.TosGet;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;

public class TosVoidRealmFetcher extends TosWikiBaseFetcher {
    public static final TosVoidRealmFetcher me = new TosVoidRealmFetcher();
    private static final String folder = "myVoidRealm";
    private LF mLf = new LF(folder);
    private LF mRealm = new LF(folder, "voidRealm.json");

    private String getPage() {
        // 虛影世界
        return "http://zh.tos.wikia.com/wiki/虛影世界";
    }

    @Override
    public void run() {
        mLf.getFile().open(false);
        clock.tic();
        // Start here
        List<MainStage> stages = getVoidRealmStages(getPage());
        for (int j = 0; j < stages.size(); j++) {
            mLf.log("  -> #%s %s", j, stages.get(j));
        }

        clock.tac("%s Done", tag());

        // Writing gson
        writeAsGson(stages, mRealm);

        mLf.getFile().close();
    }

    private List<MainStage> getVoidRealmStages(String link) {
        Document doc = getDocument(link);

        Element main = doc.getElementById("mw-content-text");
        return TosGet.me.getVoidRealmStages(main, wikiBaseZh);
    }
}
