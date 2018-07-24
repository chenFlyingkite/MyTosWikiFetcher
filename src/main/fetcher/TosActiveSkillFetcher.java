package main.fetcher;

import main.card.SkillInfo;
import main.card.TosGet;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import flyingkite.data.Range;
import flyingkite.logging.LF;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TosActiveSkillFetcher extends TosWikiBaseFetcher {
    private TosActiveSkillFetcher() {}
    public static final TosActiveSkillFetcher me = new TosActiveSkillFetcher();
    private static final String folder = "myActSkill";
    private LF mLf = new LF(folder);
    private LF mLfSkills = new LF(folder, "actSkills");
    private final String tosApi = "http://zh.tos.wikia.com/api/v1/Articles/List?limit=2500000&category=%E4%B8%BB%E5%8B%95%E6%8A%80";

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
        List<String> list = Arrays.asList(
                // 不倒之志
                "http://zh.tos.wikia.com/wiki/%E4%B8%8D%E5%80%92%E4%B9%8B%E5%BF%97"
        );
        //return list;
        return super.getTestLinks();
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
        mLf.getFile().open();
        mLf.setLogToL(true);
        clock.tic();
        for (int i = rng.min; i < rng.max; i++) {
            String link = getSourceLinkAt(source, i);
            Document doc = getDocument(link);
            SkillInfo a = getSkillInfo(doc);
            if (a != null) {
                skills.add(a);
                mLf.log("%s => %s", a.getSkillName(), a.getSkillLink());
            }
        }

        clock.tac("Active Skill OK");

        mLf.log("%s skills", skills.size());
        SkillInfo[] ainfo = skills.toArray(new SkillInfo[skills.size()]);
        String msg = mGson.toJson(ainfo, SkillInfo[].class);
        mLfSkills.setLogToL(false);
        mLfSkills.getFile().delete().open(false);
        mLfSkills.log(msg);
        mLfSkills.getFile().close();

        mLf.getFile().close();
    }

    private SkillInfo getSkillInfo(Document doc) {
        Elements main = doc.getElementsByClass("wikitable");
        if (main == null) return null;
        return TosGet.me.getActiveSkillTable(main, wikiBaseZh);
    }
}