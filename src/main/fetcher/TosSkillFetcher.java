package main.fetcher;

import flyingkite.data.Range;
import flyingkite.log.L;
import flyingkite.log.LF;
import flyingkite.tool.GsonUtil;
import main.kt.SkillInfo;
import main.kt.SkillInfo2;
import main.kt.TosGet;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TosSkillFetcher extends TosWikiBaseFetcher {
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
        Collections.addAll(list
                // 隊長技 : 護甲金身
                //, "http://zh.tos.wikia.com/wiki/%E8%AD%B7%E7%94%B2%E9%87%91%E8%BA%AB"
                // 	隊長技 ‧ 昇華 : 護甲金身 ‧ 強
                //, "http://zh.tos.wikia.com/wiki/%E8%AD%B7%E7%94%B2%E9%87%91%E8%BA%AB_%E2%80%A7_%E5%BC%B7"
                // 主動技 : 金睛火眼 ‧ 凝煉
                //, "http://zh.tos.wikia.com/wiki/%E9%87%91%E7%9D%9B%E7%81%AB%E7%9C%BC_%E2%80%A7_%E5%87%9D%E7%85%89"
                // 主動技 ‧ 昇華 : 金睛真火 ‧ 凝煉
                //, "http://zh.tos.wikia.com/wiki/%E9%87%91%E7%9D%9B%E7%9C%9F%E7%81%AB_%E2%80%A7_%E5%87%9D%E7%85%89"
                // 組合技 : 三原獵刃
                , "http://zh.tos.wikia.com/wiki/%E4%B8%89%E5%8E%9F%E7%8D%B5%E5%88%83"
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
            String li = decodeUrl(link);
            mLf.log("%s -> %s", li.substring(li.lastIndexOf("/") + 1), link);
            Document doc = getDocument(link);
            List<SkillInfo2> ss = getSkillInfo(doc);
            if (ss != null) {
                for (SkillInfo2 a : ss) {
                    if (a != null) {
                        skills.add(a);
                        types.add(a.getType());
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

    public AllSkill getAllTypedSkills() {
        SkillInfo2[] list = GsonUtil.loadFile(outJson, SkillInfo2[].class);

        int N = 0;
        if (list != null) {
            N = list.length;
            L.log("%s skills in %s", N, outJson);
        }

        int n = 0;
        // Transfer skillInfo as typed
        // idNorm.[ame|active/leader] -> {skill1, skill2, ..., skillN}
        AllSkill as = new AllSkill();
        String key, type;
        Map<String, List<SkillInfo2>> li;
        List<SkillInfo2> si;
        for (SkillInfo2 s : list) {
            type = s.getType();
            List<String> mons = s.getMonsters();
            for (String mi : mons) {
                if (mi.length() != 4) continue;
            }


            key = s.getSkillName();
            //key = mi;
            switch (type) {
                case "主動技":
                    li = as.active;
                    break;
                case "隊長技":
                    li = as.leader;
                    break;
                case "主動技 ‧ 昇華":
                    li = as.ameActive;
                    break;
                case "隊長技 ‧ 昇華":
                    li = as.ameLeader;
                    break;
                default:
                    li = null;
            }
            if (li != null) {
                si = li.get(key);
                if (si == null) {
                    si = new ArrayList<>();
                }
                si.add(s);
                li.put(key, si);
                n++;
            }
        }
        L.log("%s monsters added in %s skills", n, N);
        return as;
    }

    public class AllSkill {
        public Map<String, List<SkillInfo2>> ameLeader = new HashMap<>();
        public Map<String, List<SkillInfo2>> ameActive = new HashMap<>();
        public Map<String, List<SkillInfo2>> leader = new HashMap<>();
        public Map<String, List<SkillInfo2>> active = new HashMap<>();
    }
}