package main;

import main.fetcher.TosActiveSkillFetcher;
import main.fetcher.TosAmeSkillFetcher;
import main.fetcher.TosPageArchiveFetcher;
import main.fetcher.TosWikiArticlesFetcher;
import main.fetcher.TosWikiCardFetcher;
import main.fetcher.TosWikiCardsLister;
import main.fetcher.TosWikiFilePeeker;
import main.fetcher.TosWikiHomeFetcher;
import main.fetcher.TosWikiIconFetcher;
import main.fetcher.TosWikiImageFileFetcher;
import main.fetcher.TosWikiPageFetcher;
import main.fetcher.TosWikiStageFetcher;
import main.fetcher.TosWikiSummonerLevelFetcher;
import flyingkite.log.L;
import flyingkite.tool.StringUtil;
import flyingkite.tool.TicTac;

public class Main {
    public static void main(String[] args) {
        long tic = System.currentTimeMillis();
        TicTac.tic();
        //-- Regular
        // 神魔主頁內容
        if (true) {
            TosWikiHomeFetcher.me.run();
        }
        // 技能內容
        if (true) {
            TosActiveSkillFetcher.me.run();
        }
        // 維基動態
        if (true) {
            // 最近動態
            TosWikiArticlesFetcher.me.run();
        }
        // 卡片內容
        // TosAmeSkillFetcher > TosWikiCardFetcher > TosWikiCardsLister
        if (true) {
            TosAmeSkillFetcher.me.run();
            TosWikiCardFetcher.me.run(); // Need to be run after AmeSkill & Active Skill fetchers
            TosWikiCardsLister.me.run();
        }

        //-- Seldom
        if (false) {
            TosWikiPageFetcher.me.run();
            TosPageArchiveFetcher.me.run();
            TosWikiIconFetcher.me.run();
            TosWikiSummonerLevelFetcher.me.run();
            TosWikiFilePeeker.me.run();
            TosWikiImageFileFetcher.me.run();
            TosWikiStageFetcher.me.run();
        }
        //MobileComm.run();
        //ASD.run();
        //ClusterMain.INSTANCE.main(args);
        //Statistics.run();
        TicTac.tac("Main ended");
        long tac = System.currentTimeMillis();
        L.log("time = %s", StringUtil.MMSSFFF(tac - tic));
    }

//    private static void parallel(Runnable... runs) {
//        for (Runnable r : runs) {
//            cache.submit(new Runnable() {
//                private TicTac2 clk = new TicTac2();
//                @Override
//                public void run() {
//                    clk.tic();
//                    r.run();
//                    clk.tac("%s Ended", r.getClass().getSimpleName());
//                }
//            });
//        }
//    }
//
//    private static final ExecutorService cache = Executors.newCachedThreadPool();

}
