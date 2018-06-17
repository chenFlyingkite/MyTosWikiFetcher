package main.fetcher;

import main.card.AmeSkillInfo;
import main.card.TosGet;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import util.logging.LF;

import java.util.List;

public class TosAmeSkillFetcher extends TosWikiBaseFetcher {
    private TosAmeSkillFetcher() {}
    public static final TosAmeSkillFetcher me = new TosAmeSkillFetcher();
    private static final String folder = "myAmeSkill";
    private LF mLf = new LF(folder);
    private LF mLfSkills = new LF(folder, "ameSkills");

    private String getPage() {
        // http://zh.tos.wikia.com/wiki/主動技列表/昇華技能
        return "http://zh.tos.wikia.com/wiki/%E4%B8%BB%E5%8B%95%E6%8A%80%E5%88%97%E8%A1%A8/%E6%98%87%E8%8F%AF%E6%8A%80%E8%83%BD";
    }

    public void run() {
        mLf.getFile().open(false);

        Document doc = getDocument(getPage());
        clock.tic();
        getTables(doc);
        clock.tac("Image OK");
        //mLf.log(getPoem(doc));

        mLf.getFile().close();
    }

    private void getTables(Document doc) {
        Elements main = doc.getElementsByClass("wikitable");
        if (main == null) return;
        List<AmeSkillInfo> info = TosGet.me.getAmeSkillTable(main, wikiBaseZh);

        mLf.log("%s skills", info.size());
        for (AmeSkillInfo a : info) {
            mLf.log("%s", a);
        }
        AmeSkillInfo[] ainfo = info.toArray(new AmeSkillInfo[info.size()]);
        String msg = mGson.toJson(ainfo, AmeSkillInfo[].class);
        mLfSkills.setLogToL(false);
        mLfSkills.getFile().delete().open(false);
        mLfSkills.log(msg);
        mLfSkills.getFile().close();
    }
}
