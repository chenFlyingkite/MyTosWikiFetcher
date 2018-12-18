package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;

import flyingkite.files.FileUtil;
import flyingkite.log.L;
import flyingkite.tool.StringUtil;
import flyingkite.tool.TaskMonitorUtil;
import flyingkite.tool.ThreadUtil;
import flyingkite.tool.TicTac;
import flyingkite.tool.TicTac2;
import kotlin.Pair;
import main.fetcher.TosCardFetcher;
import main.fetcher.TosCraftFetcher;
import main.fetcher.TosEnemySkillFetcher;
import main.fetcher.TosLostRelicPassFetcher;
import main.fetcher.TosMainStageFetcher;
import main.fetcher.TosPageArchiveFetcher;
import main.fetcher.TosSkillFetcher;
import main.fetcher.TosStoryStageFetcher;
import main.fetcher.TosWikiArticlesFetcher;
import main.fetcher.TosWikiCardsLister;
import main.fetcher.TosWikiFilePeeker;
import main.fetcher.TosWikiHomeFetcher;
import main.fetcher.TosWikiIconFetcher;
import main.fetcher.TosWikiImageFileFetcher;
import main.fetcher.TosWikiPageFetcher;
import main.fetcher.TosWikiStageFetcher;
import main.fetcher.TosWikiSummonerLevelFetcher;

public class Main {
    public static void main(String[] args) {
        fetch();
        //copyToMyTosWiki();
    }

    private static void fetch() {
        L.log("" + new Date());
        long tic = System.currentTimeMillis();
        TicTac.tic();
        boolean fixCard = 0 > 0;
        //-- Regular
        boolean regl = 1 > 0 && !fixCard; // Regular
        boolean parl = true; // Parallel
        // 維基動態
        if (regl) {
            // 最近動態
            runParallel(parl, TosWikiArticlesFetcher.me);
        }
        //TosWikiArticlesFetcher.me.run();
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
            // 遺跡特許
            runParallel(parl, TosLostRelicPassFetcher.me);
            // 主線關卡
            runParallel(parl, TosMainStageFetcher.me);
            // 旅人的記憶
            runParallel(parl, TosStoryStageFetcher.me);
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

        // Fetch cards only
        if (fixCard) {
            TicTac2 c = new TicTac2();
            c.tic();
            //TosCraftFetcher.me.run();
            //TosSkillFetcher.me.run();
            TosCardFetcher.me.run();
            c.tac("Partly fetch OK");
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
            TosMainStageFetcher.me.run();
            TosStoryStageFetcher.me.run();
        }
        //MobileComm.run();
        // Others misc
        if (1 > 0) { // left value 1 = yes, 0 = no
            //ASD.run();
            QWE.run(); // Firebase comments
        }
        //ClusterMain.INSTANCE.main(args);
        //Statistics.run();
        TicTac.tac("Main ended");
        long tac = System.currentTimeMillis();
        L.log("time = %s", StringUtil.MMSSFFF(tac - tic));
        L.log("" + new Date());
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

    private static void copyToMyTosWiki() {
        List<Pair<String, String>> paths = new ArrayList<>();
        String asset = "..\\MyTosWiki\\app\\src\\main\\assets\\A\\";
        paths.add(as("myCard/cardList.json", asset + "cardList.json"));
        paths.add(as("myCraft/crafts.json", asset + "crafts.json"));
        paths.add(as("myCraft/armCrafts.json", asset + "armCrafts.json"));
        paths.add(as("myLostRelicPass/relicPass.json", asset + "relicPass.json"));
        paths.add(as("myMainStage/mainStage.json", asset + "mainStage.json"));
        paths.add(as("myStoryStage/storyStage.json", asset + "storyStage.json"));

        for (Pair<String, String> pair : paths) {
            FileUtil.copy(pair.getFirst(), pair.getSecond());
        }
    }

    private static <M, N> Pair<M, N> as(M m, N n) {
        return new Pair<>(m, n);
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
        = ThreadUtil.newFlexThreadPool(Integer.MAX_VALUE, 60);

}
