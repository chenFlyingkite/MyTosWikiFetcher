package main.fetcher;

import flyingkite.log.LF;
import flyingkite.tool.GsonUtil;
import main.kt.Craft;
import main.kt.SimpleCraft;
import main.kt.TosGet;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TosCraftFetcher extends TosWikiBaseFetcher {
    private TosCraftFetcher() {}
    public static final TosCraftFetcher me = new TosCraftFetcher();
    private static final String folder = "myCraft";
    private LF mLf = new LF(folder);
    private LF mLite = new LF(folder, "craftLite.json");
    private LF mCraft = new LF(folder, "crafts.json");
    private LF mArmLite = new LF(folder, "armCraftLite.json");
    private LF mArmCraft = new LF(folder, "armCrafts.json");
    // 龍刻圖鑒
    private static final String craftPage = "http://zh.tos.wikia.com/wiki/%E9%BE%8D%E5%88%BB%E5%9C%96%E9%91%92";
    // 龍刻武裝圖鑒
    private static final String craftArmPage = "http://zh.tos.wikia.com/wiki/%E9%BE%8D%E5%88%BB%E6%AD%A6%E8%A3%9D%E5%9C%96%E9%91%92";

    private File armJson = mArmCraft.getFile().getFile();

    @Override
    public void run() {
        mLf.getFile().open(false);
        clock.tic();
        // Start here
        List<SimpleCraft> lite;
        List<Craft> craft;

        // Fetch normal crafts
        loadAllCrafts(craftPage, mLite, mCraft);

        // Fetch armed crafts
        loadAllCrafts(craftArmPage, mArmLite, mArmCraft);

        clock.tac("Crafts & Armed craft OK Done %s", tag());
        mLf.getFile().close();
    }

    private List<Craft> loadAllCrafts(String page, LF lfLite, LF lfFull) {
        return loadCrafts(loadSimpleCraft(page, lfLite), lfFull);
    }

    private List<SimpleCraft> loadSimpleCraft(String page, LF lfLite) {
        List<SimpleCraft> lite = getSimpleCraft(page);
        mLf.log("%s crafts in page %s", lite.size(), page);
        writeAsGson(lite, lfLite);
        return lite;
    }

    private List<Craft> loadCrafts(List<SimpleCraft> lite, LF lfFull) {
        List<Craft> crafts = getCraft(lite);
        printList(crafts, mLf, "crafts");
        writeAsGson(crafts, lfFull);
        return crafts;
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

    public Map<String, List<Craft>> getArmCrafts() {
        Craft[] list = GsonUtil.loadFile(armJson, Craft[].class);
        return toMap(Arrays.asList(list));
    }

    private static Map<String, List<Craft>> toMap(List<Craft> crafts) {
        Map<String, List<Craft>> map = new TreeMap<>();
        for (Craft c : crafts) {
            for (String s : c.getCardLimit()) {
                // Get monster's list
                List<Craft> list = map.get(s);
                if (list == null) {
                    list = new ArrayList<>();
                }

                // Create Skill
                list.add(c);
                map.put(s, list);
            }
        }
        return map;
    }
}
