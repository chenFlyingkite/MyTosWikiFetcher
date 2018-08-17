package main.fetcher;

import flyingkite.tool.GsonUtil;
import main.card.Skill;
import main.kt.SkillInfo;
import main.kt.TosGet;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import flyingkite.log.LF;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Deprecated
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
    private static List<SkillInfo> fetchedLeader;
    private static List<SkillInfo> fetchedActive;
    private File outActJson = mAmeAct.getFile().getFile();
    private File outLdrJson = mAmeLdr.getFile().getFile();

    @Override
    public void run() {
        mLf.getFile().open(false);

        clock.tic();
        fetchedActive = getTables(activeSkills, mAmeAct);
        fetchedLeader = getTables(leaderSkills, mAmeLdr);
        clock.tac("Amelioration Skills OK Done %s", tag());

        mLf.getFile().close();
    }

    private List<SkillInfo> getTables(String page, LF logFile) {
        Document doc = getDocument(page);
        List<SkillInfo> info = getSkillInfos(doc);

        // Print to console
        mLf.log("Page = %s", page);
        mLf.log("%s skills", info.size());
        for (SkillInfo a : info) {
            mLf.log("%s", a);
        }
        // Convert to String and write to file
        writeAsGson(info, logFile);
        return info;
    }

    private List<SkillInfo> getSkillInfos(Document doc) {
        Elements main = doc.getElementsByClass("wikitable");
        if (main == null) return new ArrayList<>();
        return TosGet.me.getAmeSkillTable(main, wikiBaseZh);
    }

    public Map<String, List<Skill>> getAllSkillsActive() {
        SkillInfo[] list = GsonUtil.loadFile(outActJson, SkillInfo[].class);
        return getAllSkills(Arrays.asList(list));
    }
    public Map<String, List<Skill>> getAllSkillsLeader() {
        SkillInfo[] list = GsonUtil.loadFile(outLdrJson, SkillInfo[].class);
        return getAllSkills(Arrays.asList(list));
    }

    public static Map<String, List<Skill>> getAllSkills(List<SkillInfo> skills) {
        Map<String, List<Skill>> map = new TreeMap<>();
        for (SkillInfo si : skills) {
            for (String s : si.getMonsters()) {
                // Get monster's list
                List<Skill> list = map.get(s);
                if (list == null) {
                    list = new ArrayList<>();
                }

                // Create Skill
                list.add(toSkill(si, s));
                map.put(s, list);
            }
        }
        return map;
    }

    private static Skill toSkill(SkillInfo si, String monsterId) {
        Skill s = new Skill();
        s.idNorm = monsterId;
        s.name = si.getSkillName();
        s.cdMin = si.getSkillCDMin();
        s.cdMax = si.getSkillCDMax();
        s.effect = si.getSkillDesc();
        s.wikiLink = si.getSkillLink();
        return s;
    }
}
