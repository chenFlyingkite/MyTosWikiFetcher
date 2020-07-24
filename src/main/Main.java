package main;

import flyingkite.files.FileUtil;
import flyingkite.log.L;
import flyingkite.tool.StringUtil;
import flyingkite.tool.TaskMonitorUtil;
import flyingkite.tool.ThreadUtil;
import flyingkite.tool.TicTac2;
import main.fetcher.BotGoldPassbook;
import main.fetcher.TosCardExtras;
import main.fetcher.TosCardFetcher;
import main.fetcher.TosCraftFetcher;
import main.fetcher.TosEnemySkillFetcher;
import main.fetcher.TosLostRelicPassFetcher;
import main.fetcher.TosMainStageFetcher;
import main.fetcher.TosPageArchiveFetcher;
import main.fetcher.TosSkillFetcher;
import main.fetcher.TosStoryStageFetcher;
import main.fetcher.TosUltimateStageFetcher;
import main.fetcher.TosVoidRealmFetcher;
import main.fetcher.TosWikiCardsLister;
import main.fetcher.TosWikiChecker;
import main.fetcher.TosWikiFilePeeker;
import main.fetcher.TosWikiHomeFetcher;
import main.fetcher.TosWikiIconFetcher;
import main.fetcher.TosWikiImageFileFetcher;
import main.fetcher.TosWikiPageFetcher;
import main.fetcher.TosWikiStageFetcher;
import main.fetcher.TosWikiSummonerLevelFetcher;
import main.fetcher.YahooStockFetcher;
import main.kt.CopyInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class Main {
    public static void main(String[] args) {
        // Common case : load all cards, all cards only
        // , load other miscs, copy to TosWiki
        //gold();
        //stock();

        //MyTosWikiFirebase.run();
        fetch();
        //copyToMyTosWiki();
        //TosLostRelicPassFetcher.me.run();
        //TosMainStageFetcher.me.run();
        //TosVoidRealmFetcher.me.run();
        //TosUserPackFetcher.me.run();
        //TosCardFetcher.me.run();
        //TosWikiHomeFetcher.me.run();

        //XliffParser.me.addStringsToIos();

        //TosUserPackFetcher.me.run();
        //ASD.run(); // testing on filter cards
        //print();

        //TosCardInfos.me.run(); // creating evolution info
        L.log("now = %s", now());
        //new LeetCode().run();
        //PngCreator.me.moveImage();
        a();
    }

    private static void a() {

    }

    private static void fetch() {
        TosCardExtras.me.run(); // Almost 460ms * 2500 cards = 20min
        fetchMisc();
        fetchCards();
        //TosWikiCardsLister.me.run();
        //TosCardFetcher.me.run();
    }

    private static void ln(String fmt, Object... p) {
        System.out.println((p == null) ? fmt : String.format(fmt, p));
    }

    private static void fetchCards() {
        boolean fullRun = 1 > 0;
        clock.tic();
        if (fullRun) {
            // 卡片內容
            // Skill change + Craft + Card List -> Card Fetcher
            List<Runnable> beforeCard = Arrays.asList(
                    TosWikiCardsLister.me
                    , TosSkillFetcher.me
                    , TosCraftFetcher.me
            );
            Runnable endCard = TosCardFetcher.me;

            TaskMonitorUtil.join(beforeCard, endCard);
        } else {
            //TosCraftFetcher.me.run();
            //TosSkillFetcher.me.run();
            TosCardFetcher.me.run();
        }
        clock.tac("Card fetched at %s", now());
    }

    private static void fetchMisc() {
        L.log("fetchMisc : Start at %s", now());
        clock.tic();
        clock.tic();
        //-- Regular
        boolean regl = 1 > 0; // Regular

        List<Runnable> run = new ArrayList<>();
        if (regl) {
            MyTosWikiFirebase.run();
            // 神魔主頁內容
            run.add(TosWikiHomeFetcher.me);
            // 技能內容 - 卡片內容 (4 min if fast)
            // Skill change + Craft + Card List -> Card Fetcher
            // -  龍刻
            //run.add(TosCraftFetcher.me);
            // -  全部技能
            //run.add(TosSkillFetcher.me);
            // 敵人技能
            run.add(TosEnemySkillFetcher.me);
            // 遺跡特許
            run.add(TosLostRelicPassFetcher.me);
            // 主線關卡
            run.add(TosMainStageFetcher.me);
            // 旅人的記憶
            run.add(TosStoryStageFetcher.me);
            // 虛影世界
            run.add(TosVoidRealmFetcher.me);
            // 地獄級關卡
            run.add(TosUltimateStageFetcher.me);


            Runnable end = () -> {
                long t = clock.tacL();
                L.log("fetchMisc : Done %s", StringUtil.MMSSFFF(t));
                L.log("now = %s", now());
            };
            TaskMonitorUtil.join(run, end);
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
        clock.tac("fetchMisc : End at %s", now());
    }

    private static void misc() {
        ASD.run();
        TosWikiChecker.me.cardStatistics();
    }

    private static void gold() {
        new BotGoldPassbook().run();
    }

    private static void stock() {
        YahooStockFetcher.me.parse();
    }

    private static Date now() {
        return new Date();
    }

    private static void runs(Runnable... rs) {
        runs(true, rs);
    }
    private static void runs(boolean parallel, Runnable... rs) {
        for (Runnable r : rs) {
            if (parallel) {
                cache.submit(r);
            } else {
                r.run();
            }
        }
    }

    private static void copyToMyTosWiki() {
        List<CopyInfo> paths = new ArrayList<>();
        String asset = "..\\MyTosWiki\\app\\src\\main\\assets\\";
        paths.add(as(         "myCard/",     "cardList.json", asset));
        paths.add(as(        "myCraft/",       "crafts.json", asset));
        paths.add(as(        "myCraft/",    "armCrafts.json", asset));
        paths.add(as("myLostRelicPass/",    "relicPass.json", asset));
        //paths.add(as(    "myMainStage/",    "mainStage.json", asset));
        paths.add(as(   "myStoryStage/",   "storyStage.json", asset));
        paths.add(as(    "myVoidRealm/",    "voidRealm.json", asset));
        paths.add(as("myUltimateStage/","ultimateStage.json", asset));

        for (CopyInfo i : paths) {
            String source = i.getSrcName();
            String target = i.getDstName();
            FileUtil.copy(source, target);
            L.log("copy : %s\n to  -> %s", source, target);
        }
    }

    private static CopyInfo as(String srcFolder, String name, String dstFolder) {
        return new CopyInfo(srcFolder, srcFolder + name, dstFolder, dstFolder + name);
    }

    /*
    UID 199215954 中文版
    UID 150372202 中文版
    UID 192291028 中文版
    UID 8397957   中文版
    UID 58402658  中文版
    UID 195014910 中文版
    UID 237475591 中文版
    UID 176874774 中文版
    UID 200172730 中文版
    */

    private static String[] envDirs() {
        return new String[] {
                "Environment.DIRECTORY_ALARMS"
                , "Environment.DIRECTORY_DCIM"
                , "Environment.DIRECTORY_DOCUMENTS"
                , "Environment.DIRECTORY_DOWNLOADS"
                , "Environment.DIRECTORY_MOVIES"
                , "Environment.DIRECTORY_MUSIC"
                , "Environment.DIRECTORY_NOTIFICATIONS"
                , "Environment.DIRECTORY_PICTURES"
                , "Environment.DIRECTORY_PODCASTS"
                , "Environment.DIRECTORY_RINGTONES"
        };
    }

    private static void p(String[] keys) {
        for (String s : keys) {
            s = s.trim();
            L.log("logE(\"%-85s = %%s\", %-85s);", s, s);
        }
    }

    private static void print() {
        String methods = "\n" +
                "        Environment.getRootDirectory();\n" +
                "        Environment.getDataDirectory();\n" +
                "        Environment.getDownloadCacheDirectory();\n" +
                "        Environment.getExternalStorageDirectory();\n" +
                "        Environment.getExternalStorageState();"
                ;
        String[] ms = methods.split("[;]");

        p(envDirs());
        p(ms);

        String[] dirs = envDirs();
        for (int i = 0; i < dirs.length; i++) {
            dirs[i] = String.format("Environment.getExternalStoragePublicDirectory(%s)", dirs[i]);
        }
        p(dirs);

        dirs = envDirs();
        for (int i = 0; i < dirs.length; i++) {
            dirs[i] = String.format("c.getExternalFilesDir(%s)", dirs[i]);
        }
        p(dirs);
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
    //https://tos.fandom.com/zh/api.php?format=json&action=expandtemplates&text=%7B%7B1234%7CfullstatsMax}}
    private static final ExecutorService cache
    //    = Executors.newCachedThreadPool();
        = ThreadUtil.newFlexThreadPool(Integer.MAX_VALUE, 60);

    private static final TicTac2 clock = new TicTac2();
}
