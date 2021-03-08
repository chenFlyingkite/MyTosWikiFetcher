package main;

import flyingkite.awt.Robot2;
import flyingkite.files.FileUtil;
import flyingkite.javaxlibrary.images.base.PngParam;
import flyingkite.javaxlibrary.images.create.PngCreateRequest;
import flyingkite.javaxlibrary.images.create.PngCreator;
import flyingkite.log.L;
import flyingkite.math.MathUtil;
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
        //PngCreator.me.standard16(null);

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
        //FileUtil.listImages("D:\\PhotoDirector_iOS\\Main_01"); // windows
        //FileUtil.listImages("/Users/ericchen/Desktop/SVNs/PHD_iOS/S_01"); // mac mini
        a();
    }
    private static void a() {

    }

    private static void matrixMul() {
        double[][] a1 = {
                {0.3811, 0.5783, 0.0402},
                {0.1967, 0.7244, 0.0782},
                {0.0241, 0.1288, 0.8444}
        };
        double[][] a2 = {
                {+4.4679, -3.5873, +0.1193},
                {-1.2186, +2.3809, -0.1624},
                {+0.0497, -0.2439, +1.2045}
        };
        // RGBtoYIQ
        double[][] b1 = {
                {+0.299, +0.587, +0.114},
                {+0.596, -0.274, -0.322},
                {+0.212, -0.523, +0.311},
        };
        // YIQtoRGB
        double[][] b2 = {
                {1.0, +0.956, +0.621},
                {1.0, -0.272, -0.647},
                {1.0, -1.105, +1.702},
        };
        L.log("a1 = %s", str(a1));
        L.log("a2 = %s", str(a2));
        double[][] a3 = mul(a1, a2);
        L.log("a3 = %s", str(a3));

        L.log("b1 = %s", str(b1));
        L.log("b2 = %s", str(b2));
        double[][] b3 = mul(b1, b2);
        L.log("b3 = %s", str(b3));
    }

    private static String str(double[][] a) {
        StringBuilder s = new StringBuilder("\n");
        for (int i = 0; i < a.length; i++) {
            s.append(i).append(" : ").append(Arrays.toString(a[i])).append("\n");
        }
        return s.toString();
    }

    private static double[][] mul(double[][] a, double[][] b) {
        int am = a.length;
        int an = a[0].length;
        int bm = b.length;
        int bn = b[0].length;
        L.log("Dim a = %dx%d, b = %dx%d", am, an, bm, bn);
        if (an != bm) {
            L.log("Wrong dim");
        }
        double[][] c = new double[am][bn];
        for (int i = 0; i < am; i++) {
            for (int j = 0; j < bn; j++) {
                double s = 0;
                for (int k = 0; k < an; k++) {
                    s += a[i][k] * b[k][j];
                }
                c[i][j] = s;
            }
        }
        return c;
    }

    private static void b() {
        String src;
        src = "D:\\PhotoDirector_iOS\\Main_01\\PhotoDirector\\PhotoDirector\\Resource\\SkyReplacementPacks\\b14cad52-3a61-4487-bc3b-745e2a376966\\ddfd8d19-176e-45c4-94cb-c6856c7c3f1e\\sky.jpg";
        String dst;
        dst = "D:\\ASD\\APNG Tests\\sky2.png";
        PngCreator.from(new PngParam(src)).replace(new PngCreateRequest.ColorSelector() {
            @Override
            public int drawAt(int x, int y, int w, int h, int c) {
                if (MathUtil.isInRange(x, w / 4, w * 3 / 4) && y < h * 3 / 4) {
                    return 0;
                }
                return c;
            }
        }).into(dst);
        dst = "D:\\ASD\\APNG Tests\\skyV.png";
        PngCreator.from(new PngParam(src)).replace(new PngCreateRequest.ColorSelector() {
            @Override
            public int drawAt(int x, int y, int w, int h, int c) {
                if (MathUtil.isInRange(y, h / 4, h * 3 / 4)) {
                    return 0;
                }
                return c;
            }
        }).into(dst);
        src = "D:\\ASD\\APNG Tests\\0225\\BG.jpg";
        dst = "D:\\ASD\\APNG Tests\\user.png";
        PngCreator.from(new PngParam(src)).replace(new PngCreateRequest.ColorSelector() {
            @Override
            public int drawAt(int x, int y, int w, int h, int c) {
                if (MathUtil.isInRange(y, h / 6, h * 5 / 6) && MathUtil.isInRange(x, w / 4, w * 3 / 4)) {
                    return c;
                }
                return 0;
            }
        }).into(dst);
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
