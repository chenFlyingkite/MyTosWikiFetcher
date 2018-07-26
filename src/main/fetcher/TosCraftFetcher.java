package main.fetcher;

import flyingkite.log.LF;
import main.card.Craft;
import main.card.SimpleCraft;
import main.card.TosGet;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class TosCraftFetcher extends TosWikiBaseFetcher {
    private TosCraftFetcher() {}
    public static final TosCraftFetcher me = new TosCraftFetcher();
    private static final String folder = "myCraft";
    private LF mLf = new LF(folder);
    private LF mLite = new LF(folder, "craftLite.json");
    private LF mCraft = new LF(folder, "crafts.json");
    // 龍刻圖鑒
    private static final String craftPage = "http://zh.tos.wikia.com/wiki/%E9%BE%8D%E5%88%BB%E5%9C%96%E9%91%92";
    // 龍刻武裝圖鑒
    private static final String craftArmPage = "http://zh.tos.wikia.com/wiki/%E9%BE%8D%E5%88%BB%E6%AD%A6%E8%A3%9D%E5%9C%96%E9%91%92";

    @Override
    public void run() {
        mLf.getFile().open(false);
        clock.tic();
        // Start here

        List<SimpleCraft> lite = getSimpleCraft(craftPage);
        mLf.log("%s crafts in page", lite.size());
        writeAsGson(lite, mLite);
        List<Craft> crafts = getCraft(lite);
        printList(crafts, mLf, "crafts");
        writeAsGson(crafts, mCraft);

        clock.tac("%s Done", tag());
        mLf.getFile().close();
    }

    private List<SimpleCraft> getSimpleCraft(String link) {
        Document doc = getDocument(link);

        Element main = doc.getElementById("mw-content-text");
        return TosGet.me.getSimpleCraftItems(main, wikiBaseZh);
    }

    private List<Craft> getCraft(List<SimpleCraft> simple) {
        List<Craft> crafts = new ArrayList<>();

        Document doc;
        Elements item;
        mLf.setLogToFile(false);
        for (SimpleCraft s : simple) {
            doc = getDocument(s.getLink());
            item = doc.getElementsByClass("wikitable");
            if (item.size() > 1) {
                mLf.log("%s", s);
                Craft cr = TosGet.me.getCraft(item.get(1), s, wikiBaseZh);
                crafts.add(cr);
            }
        }
        mLf.setLogToFile(true);

        return crafts;
    }
}
