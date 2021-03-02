package main;

import flyingkite.awt.Robot2;
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

import java.awt.AWTException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class Main {
    public static void main(String[] args) {
        //-- Main work
        //fetch();
        //copyToMyTosWiki();
        //enterUID();

        //-- testing area
        //PngCreator.me.standard(null);

        //-- fix card
        //TosCardFetcher.me.run();


        //gold();
        //stock();
        //MyTosWikiFirebase.run();
        //TosUserPackFetcher.me.run();
        //TosWikiHomeFetcher.me.run();
        //TosCardExtras.me.run();

        //XliffParser.me.addStringsToIos();

        //ASD.run(); // testing on filter cards
        //print();


        //TosCardInfos.me.run(); // creating evolution info
        L.log("now = %s", now());
        //new LeetCode().run();
        //PngCreator.me.moveImage();
        //FileUtil.listImages("D:\\PhotoDirector_iOS\\Main_01");
        a();
    }

    private static void a() {
    }


    private static void fetch() {
        TosCardExtras.me.run(); // Almost 460ms * 2500 cards = 20min
        fetchMisc();
        fetchCards();
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
    #define Spaces "                                                                                "
    #define qq(Format) printf(Format"\n")
    #define qw(Format, ...) printf(Format"\n", __VA_ARGS__)
    // __FILE__ gives full path from /Users/ericchen/Desktop/SVNs/PHD_iOS/....
    //#define qwe(Format, ...) printf(Format"\n"Spaces"L #%u %s\n", __VA_ARGS__, __LINE__, __func__)

    #define qq(Format)

    ----

    #define Spaces "                                                                                "
    #define qq(Format) printf(""Format"\n")
    #define qw(Format, ...) printf(""Format"\n", __VA_ARGS__)
    // __FILE__ gives full path from /Users/ericchen/Desktop/SVNs/PHD_iOS/....
    //#define qwe(Format, ...) printf(""Format"\n"Spaces"L #%u %s\n", __VA_ARGS__, __LINE__, __func__)

    #define qq(Format)

    // Apple Developer Program
    // https://ephrain.net/ios-%E8%A8%BB%E5%86%8A%E6%88%90%E7%82%BA-apple-developer-%E4%BA%86%EF%BC%81/
    */

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
    private static void enterUID() {
        try {
            Robot2 r = new Robot2();
            r.delay(5_000);
            String[] ids = {
                    "199215954",
                    "150372202",
                    "192291028",
                    "8397957",
                    "58402658",
                    "195014910",
                    "237475591",
                    "176874774",
                    "200172730",
            };

            for (String s : ids) {
                r.enter(s);
            }
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

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
    // 家有大貓 (Nekojishi) 「舉頭三尺天氣晴」主題曲 完成版 (Fanmade)
    // https://www.youtube.com/watch?v=egAwcvzseeM
    // https://mega.nz/file/UOZ1wJrC#-eCtj4DlZoIyKOI5Ss0fG2d1uWDbUTDOaK6nDOwz1Xo

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
