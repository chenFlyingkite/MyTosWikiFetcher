package main.fetcher;

import java.util.ArrayList;
import java.util.List;

import flyingkite.log.LF;
import main.kt.EnemySkill;
import main.kt.TosGet;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class TosEnemySkillFetcher extends TosWikiBaseFetcher {
    private TosEnemySkillFetcher() {}
    public static final TosEnemySkillFetcher me = new TosEnemySkillFetcher();
    private static final String folder = "myEnemySkill";
    private LF mLf = new LF(folder);
    private LF mEsj = new LF(folder, "enemySkill.json");

    private String getPage() {
        // 關卡敵人技能/敵人技能列表
        return "http://zh.tos.wikia.com/wiki/%E9%97%9C%E5%8D%A1%E6%95%B5%E4%BA%BA%E6%8A%80%E8%83%BD/%E6%95%B5%E4%BA%BA%E6%8A%80%E8%83%BD%E5%88%97%E8%A1%A8";
    }

    @Override
    public void run() {
        mLf.getFile().open(false);

        clock.tic();
        List<String> groups = getSkillGroups(getPage());
        mLf.log("%s skill groups", groups.size());
        List<EnemySkill> allItems = new ArrayList<>();
        int n = 0;
        for (int i = 0; i < groups.size(); i++) {
            String gi = groups.get(i);
            mLf.log("%s", gi);
            List<EnemySkill> skills = getSkillsInLink(gi);
            allItems.addAll(skills);
            n += skills.size();
            // Print out all skills
            boolean print = true;
            if (print) {
                mLf.log("-   %s skills", skills.size());
                for (EnemySkill s : skills) {
                    mLf.log("--  %s", s);
                }
            }
        }
        clock.tac("Fetched %s Enemy Skills", n);
        clock.tic();
        writeAsGson(allItems, mEsj);
        clock.tac("%s Enemy skill written Done OK %s", n, tag());
        mLf.getFile().close();
    }

    private List<String> getSkillGroups(String link) {
        Document doc = getDocument(link);

        Element main = doc.getElementById("mw-content-text");
        return TosGet.me.getLiAHref(main, wikiBaseZh);
    }

    private List<EnemySkill> getSkillsInLink(String link) {
        Document doc = getDocument(link);

        Element main = doc.getElementById("mw-content-text");

        return TosGet.me.getSkillItems(main, wikiBaseZh);
    }
}
