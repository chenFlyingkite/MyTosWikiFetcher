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
    private static final String craftPage = "https://tos.fandom.com/zh/wiki/%E9%BE%8D%E5%88%BB%E5%9C%96%E9%91%92";
    // 龍刻武裝圖鑒
    private static final String craftArmPage = "https://tos.fandom.com/zh/wiki/%E9%BE%8D%E5%88%BB%E6%AD%A6%E8%A3%9D%E5%9C%96%E9%91%92";

    private File armJson = mArmCraft.getFile().getFile();

    private List<String> getPeeks() {
        List<String> a = new ArrayList<>();
        //a.add("7002");
        //a.add("3083"); // #3083 : 龍王劍龍咒 -> https://tos.fandom.com/zh/wiki/%E9%BE%8D%E7%8E%8B%E5%8A%8D%E9%BE%8D%E5%92%92
        //a.add("3154"); // #3154 : 冥界寶杖龍符 -> https://tos.fandom.com/zh/wiki/%E5%86%A5%E7%95%8C%E5%AF%B6%E6%9D%96%E9%BE%8D%E7%AC%A6
        //a.add("3257"); // #3257 : 玄獸武刃龍咒 -> https://tos.fandom.com/zh/wiki/%E7%8E%84%E7%8D%B8%E6%AD%A6%E5%88%83%E9%BE%8D%E5%92%92
        //a.add("3258"); // #3258 : 玄獸武刃龍符 -> https://tos.fandom.com/zh/wiki/%E7%8E%84%E7%8D%B8%E6%AD%A6%E5%88%83%E9%BE%8D%E7%AC%A6
        a.clear();
        return a;
    }

    @Override
    public void run() {

        mLf.getFile().open(false);
        clock.tic();
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

        List<String> peeks = getPeeks();
        boolean hasPeek = peeks != null && peeks.size() > 0;

        Document doc;
        Elements item;
        mLf.setLogToFile(false);
        for (SimpleCraft s : simple) {
            if (hasPeek && !peeks.contains(s.getIdNorm())) {
                continue;
            }
            doc = getDocument(s.getLink());
            item = doc.getElementsByClass("wikitable");
            Element e = item.last();
            if (e != null) {
                mLf.log("%s", s);
                Craft cr = TosGet.me.getCraft(e, s);
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
