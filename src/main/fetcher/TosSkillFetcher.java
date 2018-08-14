package main.fetcher;

import flyingkite.data.Range;
import flyingkite.log.L;
import flyingkite.log.LF;
import flyingkite.tool.GsonUtil;
import main.card.Skill;
import main.kt.SkillInfo;
import main.kt.SkillInfo2;
import main.kt.TosGet;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TosSkillFetcher  extends TosWikiBaseFetcher {
    private TosSkillFetcher() {}
    public static final TosSkillFetcher me = new TosSkillFetcher();
    private static final String folder = "myAllSkill";
    private LF mLf = new LF(folder);
    private LF mLfSkills = new LF(folder, "allSkills.json");
    // 技能
    private final String tosApi = "http://zh.tos.wikia.com/api/v1/Articles/List?limit=2500000&category=%E6%8A%80%E8%83%BD&namespaces=0";
    private static List<SkillInfo> allSkills = new ArrayList<>();
    private File outJson = mLfSkills.getFile().getFile();

    @Override
    public String getAPILink() {
        return tosApi;
    }

    @Override
    public LF getHttpLF() {
        return mLf;
    }

    @Override
    protected List<String> getTestLinks() {
        List<String> list = new ArrayList<>();
        Collections.addAll(list,
                // 不倒之志
                //"http://zh.tos.wikia.com/wiki/%E4%B8%8D%E5%80%92%E4%B9%8B%E5%BF%97"
                "http://zh.tos.wikia.com/wiki/%E4%B8%89%E5%8E%9F%E5%88%A9%E5%88%83_%E2%80%A7_%E4%BA%BA%E6%97%8F"
        );
        list.clear();
        return list;
    }

    @Override
    public void run() {
        // About 1 min 30 sec
        mFetchAll = 0 < 3;

        // Get the range sets
        Source source = getSource(0, 100);
        if (source == null) return;
        ResultSet set = source.results;
        Range rng = source.range;

        List<SkillInfo> skills = new ArrayList<>();
        Set<String> types = new HashSet<>();
        mLf.getFile().open();
        mLf.setLogToL(true);
        clock.tic();
        for (int i = rng.min; i < rng.max; i++) {
            String link = getSourceLinkAt(source, i);
            //mLf.log("%s", link);
            Document doc = getDocument(link);
            List<SkillInfo2> ss = getSkillInfo(doc);
            if (ss != null) {
                for (SkillInfo2 a : ss) {
                    if (a != null) {
                        skills.add(a);
                        types.add(a.getType());
                        mLf.log("%s => %s", a.getSkillName(), a.getSkillLink());
                    }
                }
            }
        }

        clock.tac("All Skill Done %s", tag());
        allSkills = skills;
        L.log("%s Types = %s", types.size(), types);

        mLf.log("%s skills", skills.size());
        writeAsGson(skills, mLfSkills);
        mLf.getFile().close();
    }

    private List<SkillInfo2> getSkillInfo(Document doc) {
        Elements main = doc.getElementsByClass("wikitable");
        if (main == null) return null;
        return TosGet.me.getAllSkillTable(main);
    }

    public Map<String, List<Skill>> getActiveSkills() {
        SkillInfo[] list = GsonUtil.loadFile(outJson, SkillInfo[].class);
        return TosAmeSkillFetcher.getAllSkills(Arrays.asList(list));
    }
}