package main.fetcher;

import main.card.SkillInfo;
import main.card.TosGet;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import util.logging.LF;

import java.util.ArrayList;
import java.util.List;

public class TosAmeSkillFetcher extends TosWikiBaseFetcher {
    private TosAmeSkillFetcher() {}
    public static final TosAmeSkillFetcher me = new TosAmeSkillFetcher();
    private static final String folder = "myAmeSkill";
    private LF mLf = new LF(folder);
    private LF mAmeAct = new LF(folder, "ameActiveSkills.json");
    private LF mAmeLdr = new LF(folder, "ameLeaderSkills.json");
    // http://zh.tos.wikia.com/wiki/隊長技列表/昇華技能
    private static final String leaderSkills = "http://zh.tos.wikia.com/wiki/%E9%9A%8A%E9%95%B7%E6%8A%80%E5%88%97%E8%A1%A8/%E6%98%87%E8%8F%AF%E6%8A%80%E8%83%BD";
    // http://zh.tos.wikia.com/wiki/主動技列表/昇華技能
    private static final String activeSkills = "http://zh.tos.wikia.com/wiki/%E4%B8%BB%E5%8B%95%E6%8A%80%E5%88%97%E8%A1%A8/%E6%98%87%E8%8F%AF%E6%8A%80%E8%83%BD";

    @Override
    public void run() {
        mLf.getFile().open(false);

        clock.tic();
        getTables(activeSkills, mAmeAct);
        getTables(leaderSkills, mAmeLdr);
        clock.tac("Amelioration Skills OK");

        mLf.getFile().close();
    }

    private void getTables(String page, LF logFile) {
        Document doc = getDocument(page);
        List<SkillInfo> info = getSkillInfos(doc);

        // Print to console
        mLf.log("Page = %s", page);
        mLf.log("%s skills", info.size());
        for (SkillInfo a : info) {
            mLf.log("%s", a);
        }
        // Convert to String and write to file
        SkillInfo[] ainfo = info.toArray(new SkillInfo[info.size()]);
        String msg = mGson.toJson(ainfo, SkillInfo[].class);
        // Write to file
        logFile.setLogToL(false);
        logFile.getFile().delete().open(false);
        logFile.log(msg);
        logFile.getFile().close();
    }

    private List<SkillInfo> getSkillInfos(Document doc) {
        Elements main = doc.getElementsByClass("wikitable");
        if (main == null) return new ArrayList<>();
        return TosGet.me.getAmeSkillTable(main, wikiBaseZh);
    }
}
