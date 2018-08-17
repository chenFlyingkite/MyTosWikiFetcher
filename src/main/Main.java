package main;

import flyingkite.log.L;
import flyingkite.math.GammaFunction;
import flyingkite.math.Math2;
import flyingkite.tool.StringUtil;
import flyingkite.tool.TaskMonitorUtil;
import flyingkite.tool.ThreadUtil;
import flyingkite.tool.TicTac;
import flyingkite.tool.TicTac2;
import main.fetcher.TosCardFetcher;
import main.fetcher.TosCraftFetcher;
import main.fetcher.TosEnemySkillFetcher;
import main.fetcher.TosPageArchiveFetcher;
import main.fetcher.TosSkillFetcher;
import main.fetcher.TosWikiArticlesFetcher;
import main.fetcher.TosWikiCardsLister;
import main.fetcher.TosWikiFilePeeker;
import main.fetcher.TosWikiHomeFetcher;
import main.fetcher.TosWikiIconFetcher;
import main.fetcher.TosWikiImageFileFetcher;
import main.fetcher.TosWikiPageFetcher;
import main.fetcher.TosWikiStageFetcher;
import main.fetcher.TosWikiSummonerLevelFetcher;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class Main {
    public static void main(String[] args) {
        long tic = System.currentTimeMillis();
        TicTac.tic();
        //-- Regular
        boolean regl = true; // Regular
        boolean parl = true; // Parallel
        // 神魔主頁內容
        if (regl) {
            runParallel(parl, TosWikiHomeFetcher.me);
        }
        // 技能內容
        if (regl) {
            // 敵人技能
            runParallel(parl, TosEnemySkillFetcher.me);
            // 龍刻
            //runParallel(parl, TosCraftFetcher.me);
            // 全部技能
            //runParallel(parl, TosSkillFetcher.me);
        }
        // 維基動態
        if (regl) {
            // 最近動態
            runParallel(parl, TosWikiArticlesFetcher.me);
        }
        // 卡片內容
        // Skill change + Craft + Card List -> Card Fetcher
        List<Runnable> beforeCard = Arrays.asList(
                TosWikiCardsLister.me
                , TosSkillFetcher.me
                , TosCraftFetcher.me
        );
        Runnable endCard = TosCardFetcher.me;
        if (regl) {
            TaskMonitorUtil.join(beforeCard, endCard);
        }
        //TosSkillFetcher.me.run();
        //TosCardFetcher.me.run();
        if (!regl && false) {
            if (true) {

            }
            if (true) {
                TicTac2 c = new TicTac2();

                for (int i = 0; i < 50; i++) {
                    BigInteger x = GammaFunction.gammaN(i);
                    BigInteger y = Math2.factorial(i);
                    BigInteger z = x.subtract(y);
                    if (z.compareTo(BigInteger.ZERO) != 0) {
                        L.log("x = %s\ny = %s\nz = %s", x, y, z);
                    }
                }
            }
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
        // Others misc
        if (false) {
            ASD.run();
        }
        //ClusterMain.INSTANCE.main(args);
        //Statistics.run();
        TicTac.tac("Main ended");
        long tac = System.currentTimeMillis();
        L.log("time = %s", StringUtil.MMSSFFF(tac - tic));
    }

    private static void runParallel(boolean parallel, Runnable... rs) {
        for (Runnable r : rs) {
            if (parallel) {
                cache.submit(r);
            } else {
                r.run();
            }
        }
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
    private static final ExecutorService cache
    //    = Executors.newCachedThreadPool();
        = ThreadUtil.newFlexThreadPool(Integer.MAX_VALUE, 20);

}
