package main;

import flyingkite.awt.Robot2;
import flyingkite.files.FileUtil;
import flyingkite.javaxlibrary.images.base.PngParam;
import flyingkite.javaxlibrary.images.create.PngCreateRequest;
import flyingkite.javaxlibrary.images.create.PngCreator;
import flyingkite.log.L;
import flyingkite.math.MathUtil;
import flyingkite.math.Matrix;
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

        //MyTosWikiFirebase.run();

        //-- testing area
        //PngCreator.me.standard(null);
        //PngCreator.me.standard16(null);

        //-- fix card
        //TosCardFetcher.me.run();


        //gold();
        //stock();
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
        //MainTest.main(null);
    }


    // https://www.khronos.org/registry/OpenGL/specs/gl/GLSLangSpec.1.20.pdf
    private static void matrixMul() {
        double[][] a1 = {
                {0.3811, 0.5783, 0.0402},
                {0.1967, 0.7244, 0.0782},
                {0.0241, 0.1288, 0.8444}
        };
        Matrix a1m = new Matrix(a1);
        double[][] a2 = {
                {+4.4679, -3.5873, +0.1193},
                {-1.2186, +2.3809, -0.1624},
                {+0.0497, -0.2439, +1.2045}
        };
        Matrix a2m = new Matrix(a2);

        double[][] a4 = {
                {+4.468669863496255542, -3.5886759034721264617, +0.11960436657860115646},
                {-1.2197166276177633275, +2.3830879129554568266, -0.16263011175140056616},
                {+0.058508476938545900071, -0.26107843902769373702, +1.2056659085256230519}
        };
        Matrix a4m = new Matrix(a4);
        // RGBtoYIQ
        double[][] b1 = {
                {+0.299, +0.587, +0.114},
                {+0.596, -0.274, -0.322},
                {+0.212, -0.523, +0.311},
        };
        Matrix b1m = new Matrix(b1);
        // YIQtoRGB
        double[][] b2 = {
                {1.0, +0.956, +0.621},
                {1.0, -0.272, -0.647},
                {1.0, -1.105, +1.702},
        };
        Matrix b2m = new Matrix(b2);
        Matrix a3m = Matrix.multiply(a1m, a2m);
        L.log("a3 = %s", a3m);
        Matrix a14 = Matrix.multiply(a1m, a4m);
        L.log("a14 = %s", a14);

        Matrix b3m = Matrix.multiply(b1m, b2m);
        L.log("b3 = %s", b3m);
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
        src = "D:\\ASD\\APNG Tests\\0225\\BG.jpg";
        dst = "D:\\ASD\\APNG Tests\\mask2.png";
        PngCreator.from(new PngParam(src)).replace(new PngCreateRequest.ColorSelector() {
            @Override
            public int drawAt(int x, int y, int w, int h, int c) {
                if (MathUtil.isInRange(y, 0, h / 2)) {
                    return 0xFFFFFFFF;
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
/*
82 items
": GPUImageFilter"
GPUImage3x3TextureSamplingFilter.h .h D:\Github\GPUImage\framework\Source 5 611 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImage3x3TextureSamplingFilter : GPUImageFilter
GPUImageAdaptiveThresholdFilter.h .h D:\Github\GPUImage\framework\Source 3 267 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageAdaptiveThresholdFilter : GPUImageFilterGroup
GPUImageAmatorkaFilter.h .h D:\Github\GPUImage\framework\Source 12 446 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageAmatorkaFilter : GPUImageFilterGroup
GPUImageAverageColor.h .h D:\Github\GPUImage\framework\Source 5 635 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageAverageColor : GPUImageFilter
GPUImageAverageLuminanceThresholdFilter.h .h D:\Github\GPUImage\framework\Source 3 309 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageAverageLuminanceThresholdFilter : GPUImageFilterGroup
GPUImageBrightnessFilter.h .h D:\Github\GPUImage\framework\Source 3 253 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageBrightnessFilter : GPUImageFilter
GPUImageBuffer.h .h D:\Github\GPUImage\framework\Source 3 189 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageBuffer : GPUImageFilter
GPUImageBulgeDistortionFilter.h .h D:\Github\GPUImage\framework\Source 4 616 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageBulgeDistortionFilter : GPUImageFilter
GPUImageCannyEdgeDetectionFilter.h .h D:\Github\GPUImage\framework\Source 22 2,792 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageCannyEdgeDetectionFilter : GPUImageFilterGroup
GPUImageCGAColorspaceFilter.h .h D:\Github\GPUImage\framework\Source 3 95 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageCGAColorspaceFilter : GPUImageFilter
GPUImageChromaKeyFilter.h .h D:\Github\GPUImage\framework\Source 3 1,017 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageChromaKeyFilter : GPUImageFilter
GPUImageClosingFilter.h .h D:\Github\GPUImage\framework\Source 9 565 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageClosingFilter : GPUImageFilterGroup
GPUImageColorInvertFilter.h .h D:\Github\GPUImage\framework\Source 3 99 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageColorInvertFilter : GPUImageFilter
GPUImageColorMatrixFilter.h .h D:\Github\GPUImage\framework\Source 5 510 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageColorMatrixFilter : GPUImageFilter
GPUImageColorPackingFilter.h .h D:\Github\GPUImage\framework\Source 3 194 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageColorPackingFilter : GPUImageFilter
GPUImageColourFASTFeatureDetector.h .h D:\Github\GPUImage\framework\Source 13 978 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageColourFASTFeatureDetector : GPUImageFilterGroup
GPUImageContrastFilter.h .h D:\Github\GPUImage\framework\Source 5 309 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageContrastFilter : GPUImageFilter
GPUImageCropFilter.h .h D:\Github\GPUImage\framework\Source 3 443 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageCropFilter : GPUImageFilter
GPUImageCrosshairGenerator.h .h D:\Github\GPUImage\framework\Source 3 751 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageCrosshairGenerator : GPUImageFilter
GPUImageCrosshatchFilter.h .h D:\Github\GPUImage\framework\Source 3 437 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageCrosshatchFilter : GPUImageFilter
GPUImageDirectionalNonMaximumSuppressionFilter.h .h D:\Github\GPUImage\framework\Source 3 836 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageDirectionalNonMaximumSuppressionFilter : GPUImageFilter
GPUImageExposureFilter.h .h D:\Github\GPUImage\framework\Source 3 247 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageExposureFilter : GPUImageFilter
GPUImageFalseColorFilter.h .h D:\Github\GPUImage\framework\Source 3 647 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageFalseColorFilter : GPUImageFilter
GPUImageFASTCornerDetectionFilter.h .h D:\Github\GPUImage\framework\Source 19 1,437 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageFASTCornerDetectionFilter : GPUImageFilterGroup
GPUImageGammaFilter.h .h D:\Github\GPUImage\framework\Source 3 232 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageGammaFilter : GPUImageFilter
GPUImageGaussianSelectiveBlurFilter.h .h D:\Github\GPUImage\framework\Source 7 1,201 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageGaussianSelectiveBlurFilter : GPUImageFilterGroup
GPUImageGrayscaleFilter.h .h D:\Github\GPUImage\framework\Source 7 310 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageGrayscaleFilter : GPUImageFilter
GPUImageHarrisCornerDetectionFilter.h .h D:\Github\GPUImage\framework\Source 23 2,431 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageHarrisCornerDetectionFilter : GPUImageFilterGroup
GPUImageHazeFilter.h .h D:\Github\GPUImage\framework\Source 15 659 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageHazeFilter : GPUImageFilter
GPUImageHighlightShadowFilter.h .h D:\Github\GPUImage\framework\Source 3 388 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageHighlightShadowFilter : GPUImageFilter
GPUImageHighlightShadowTintFilter.h .h D:\Github\GPUImage\framework\Source 11 1,057 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageHighlightShadowTintFilter : GPUImageFilter
GPUImageHighPassFilter.h .h D:\Github\GPUImage\framework\Source 5 521 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageHighPassFilter : GPUImageFilterGroup
GPUImageHistogramEqualizationFilter.h .h D:\Github\GPUImage\framework\Source 15 733 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageHistogramEqualizationFilter : GPUImageFilterGroup
GPUImageHistogramFilter.h .h D:\Github\GPUImage\framework\Source 5 832 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageHistogramFilter : GPUImageFilter
GPUImageHistogramGenerator.h .h D:\Github\GPUImage\framework\Source 3 135 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageHistogramGenerator : GPUImageFilter
GPUImageHoughTransformLineDetector.h .h D:\Github\GPUImage\framework\Source 25 3,106 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageHoughTransformLineDetector : GPUImageFilterGroup
GPUImageHueFilter.h .h D:\Github\GPUImage\framework\Source 4 175 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageHueFilter : GPUImageFilter
GPUImageiOSBlurFilter.h .h D:\Github\GPUImage\framework\Source 7 1,097 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageiOSBlurFilter : GPUImageFilterGroup
GPUImageJFAVoronoiFilter.h .h D:\Github\GPUImage\framework\Source 3 328 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageJFAVoronoiFilter : GPUImageFilter
GPUImageKuwaharaFilter.h .h D:\Github\GPUImage\framework\Source 5 677 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageKuwaharaFilter : GPUImageFilter
GPUImageKuwaharaRadius3Filter.h .h D:\Github\GPUImage\framework\Source 6 140 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageKuwaharaRadius3Filter : GPUImageFilter
GPUImageLevelsFilter.h .h D:\Github\GPUImage\framework\Source 14 1,715 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageLevelsFilter : GPUImageFilter
GPUImageLineGenerator.h .h D:\Github\GPUImage\framework\Source 3 692 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageLineGenerator : GPUImageFilter
GPUImageLowPassFilter.h .h D:\Github\GPUImage\framework\Source 5 479 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageLowPassFilter : GPUImageFilterGroup
GPUImageLuminanceRangeFilter.h .h D:\Github\GPUImage\framework\Source 3 286 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageLuminanceRangeFilter : GPUImageFilter
GPUImageLuminanceThresholdFilter.h .h D:\Github\GPUImage\framework\Source 5 422 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageLuminanceThresholdFilter : GPUImageFilter
GPUImageMissEtikateFilter.h .h D:\Github\GPUImage\framework\Source 12 464 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageMissEtikateFilter : GPUImageFilterGroup
GPUImageMonochromeFilter.h .h D:\Github\GPUImage\framework\Source 3 356 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageMonochromeFilter : GPUImageFilter
GPUImageMotionBlurFilter.h .h D:\Github\GPUImage\framework\Source 3 367 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageMotionBlurFilter : GPUImageFilter
GPUImageMotionDetector.h .h D:\Github\GPUImage\framework\Source 5 786 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageMotionDetector : GPUImageFilterGroup
GPUImageOpacityFilter.h .h D:\Github\GPUImage\framework\Source 3 241 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageOpacityFilter : GPUImageFilter
GPUImageOpeningFilter.h .h D:\Github\GPUImage\framework\Source 9 567 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageOpeningFilter : GPUImageFilterGroup
GPUImageParallelCoordinateLineTransformFilter.h .h D:\Github\GPUImage\framework\Source 9 838 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageParallelCoordinateLineTransformFilter : GPUImageFilter
GPUImagePerlinNoiseFilter.h .h D:\Github\GPUImage\framework\Source 3 330 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImagePerlinNoiseFilter : GPUImageFilter
GPUImagePinchDistortionFilter.h .h D:\Github\GPUImage\framework\Source 5 635 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImagePinchDistortionFilter : GPUImageFilter
GPUImagePixellateFilter.h .h D:\Github\GPUImage\framework\Source 3 386 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImagePixellateFilter : GPUImageFilter
GPUImagePixellatePositionFilter.h .h D:\Github\GPUImage\framework\Source 3 667 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImagePixellatePositionFilter : GPUImageFilter
GPUImagePolarPixellateFilter.h .h D:\Github\GPUImage\framework\Source 3 433 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImagePolarPixellateFilter : GPUImageFilter {
GPUImagePosterizeFilter.h .h D:\Github\GPUImage\framework\Source 5 442 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImagePosterizeFilter : GPUImageFilter
GPUImageRGBClosingFilter.h .h D:\Github\GPUImage\framework\Source 9 496 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageRGBClosingFilter : GPUImageFilterGroup
GPUImageRGBFilter.h .h D:\Github\GPUImage\framework\Source 3 427 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageRGBFilter : GPUImageFilter
GPUImageRGBOpeningFilter.h .h D:\Github\GPUImage\framework\Source 9 496 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageRGBOpeningFilter : GPUImageFilterGroup
GPUImageSaturationFilter.h .h D:\Github\GPUImage\framework\Source 5 340 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageSaturationFilter : GPUImageFilter
GPUImageSharpenFilter.h .h D:\Github\GPUImage\framework\Source 3 309 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageSharpenFilter : GPUImageFilter
GPUImageSkinToneFilter.h .h D:\Github\GPUImage\framework\Source 18 1,655 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageSkinToneFilter : GPUImageFilter
GPUImageSmoothToonFilter.h .h D:\Github\GPUImage\framework\Source 8 1,175 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageSmoothToonFilter : GPUImageFilterGroup
GPUImageSoftEleganceFilter.h .h D:\Github\GPUImage\framework\Source 13 546 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageSoftEleganceFilter : GPUImageFilterGroup
GPUImageSolarizeFilter.h .h D:\Github\GPUImage\framework\Source 5 388 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageSolarizeFilter : GPUImageFilter
GPUImageSolidColorGenerator.h .h D:\Github\GPUImage\framework\Source 7 722 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageSolidColorGenerator : GPUImageFilter
GPUImageSphereRefractionFilter.h .h D:\Github\GPUImage\framework\Source 3 580 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageSphereRefractionFilter : GPUImageFilter
GPUImageStretchDistortionFilter.h .h D:\Github\GPUImage\framework\Source 5 320 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageStretchDistortionFilter : GPUImageFilter {
GPUImageSwirlFilter.h .h D:\Github\GPUImage\framework\Source 5 593 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageSwirlFilter : GPUImageFilter
GPUImageTiltShiftFilter.h .h D:\Github\GPUImage\framework\Source 6 951 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageTiltShiftFilter : GPUImageFilterGroup
GPUImageToneCurveFilter.h .h D:\Github\GPUImage\framework\Source 3 1,174 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageToneCurveFilter : GPUImageFilter
GPUImageTransformFilter.h .h D:\Github\GPUImage\framework\Source 3 820 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageTransformFilter : GPUImageFilter
GPUImageTwoInputFilter.h .h D:\Github\GPUImage\framework\Source 5 649 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageTwoInputFilter : GPUImageFilter
GPUImageTwoPassFilter.h .h D:\Github\GPUImage\framework\Source 3 993 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageTwoPassFilter : GPUImageFilter
GPUImageUnsharpMaskFilter.h .h D:\Github\GPUImage\framework\Source 5 495 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageUnsharpMaskFilter : GPUImageFilterGroup
GPUImageVibranceFilter.h .h D:\Github\GPUImage\framework\Source 11 449 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageVibranceFilter : GPUImageFilter
GPUImageVignetteFilter.h .h D:\Github\GPUImage\framework\Source 5 821 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageVignetteFilter : GPUImageFilter
GPUImageWhiteBalanceFilter.h .h D:\Github\GPUImage\framework\Source 7 542 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageWhiteBalanceFilter : GPUImageFilter
GPUImageZoomBlurFilter.h .h D:\Github\GPUImage\framework\Source 3 355 2019/7/23 下午 02:30 2021/3/9 下午 01:56 2019/7/23 下午 02:30 @interface GPUImageZoomBlurFilter : GPUImageFilter

31 items
": GPUImageTwoInputFilter"
GPUImageAddBlendFilter.h .h D:\Github\GPUImage\framework\Source 3 106 2019/7/23 下午 02:30 2021/3/9 下午 12:12 2019/7/23 下午 02:30 @interface GPUImageAddBlendFilter : GPUImageTwoInputFilter
GPUImageAlphaBlendFilter.h .h D:\Github\GPUImage\framework\Source 3 277 2019/7/23 下午 02:30 2021/3/9 下午 12:12 2019/7/23 下午 02:30 @interface GPUImageAlphaBlendFilter : GPUImageTwoInputFilter
GPUImageChromaKeyBlendFilter.h .h D:\Github\GPUImage\framework\Source 5 1,118 2019/7/23 下午 02:30 2021/3/9 下午 12:12 2019/7/23 下午 02:30 @interface GPUImageChromaKeyBlendFilter : GPUImageTwoInputFilter
GPUImageColorBlendFilter.h .h D:\Github\GPUImage\framework\Source 3 108 2019/7/23 下午 02:30 2021/3/9 下午 12:12 2019/7/23 下午 02:30 @interface GPUImageColorBlendFilter : GPUImageTwoInputFilter
GPUImageColorBurnBlendFilter.h .h D:\Github\GPUImage\framework\Source 5 169 2019/7/23 下午 02:30 2021/3/9 下午 12:12 2019/7/23 下午 02:30 @interface GPUImageColorBurnBlendFilter : GPUImageTwoInputFilter
GPUImageColorDodgeBlendFilter.h .h D:\Github\GPUImage\framework\Source 5 171 2019/7/23 下午 02:30 2021/3/9 下午 12:12 2019/7/23 下午 02:30 @interface GPUImageColorDodgeBlendFilter : GPUImageTwoInputFilter
GPUImageColourFASTSamplingOperation.h .h D:\Github\GPUImage\framework\Source 10 1,078 2019/7/23 下午 02:30 2021/3/9 下午 12:12 2019/7/23 下午 02:30 @interface GPUImageColourFASTSamplingOperation : GPUImageTwoInputFilter
GPUImageDarkenBlendFilter.h .h D:\Github\GPUImage\framework\Source 3 115 2019/7/23 下午 02:30 2021/3/9 下午 12:12 2019/7/23 下午 02:30 @interface GPUImageDarkenBlendFilter : GPUImageTwoInputFilter
GPUImageDifferenceBlendFilter.h .h D:\Github\GPUImage\framework\Source 3 119 2019/7/23 下午 02:30 2021/3/9 下午 12:12 2019/7/23 下午 02:30 @interface GPUImageDifferenceBlendFilter : GPUImageTwoInputFilter
GPUImageDissolveBlendFilter.h .h D:\Github\GPUImage\framework\Source 3 297 2019/7/23 下午 02:30 2021/3/9 下午 12:12 2019/7/23 下午 02:30 @interface GPUImageDissolveBlendFilter : GPUImageTwoInputFilter
GPUImageDivideBlendFilter.h .h D:\Github\GPUImage\framework\Source 3 109 2019/7/23 下午 02:30 2021/3/9 下午 12:12 2019/7/23 下午 02:30 @interface GPUImageDivideBlendFilter : GPUImageTwoInputFilter
GPUImageExclusionBlendFilter.h .h D:\Github\GPUImage\framework\Source 3 118 2019/7/23 下午 02:30 2021/3/9 下午 12:12 2019/7/23 下午 02:30 @interface GPUImageExclusionBlendFilter : GPUImageTwoInputFilter
GPUImageHardLightBlendFilter.h .h D:\Github\GPUImage\framework\Source 3 118 2019/7/23 下午 02:30 2021/3/9 下午 12:12 2019/7/23 下午 02:30 @interface GPUImageHardLightBlendFilter : GPUImageTwoInputFilter
GPUImageHueBlendFilter.h .h D:\Github\GPUImage\framework\Source 3 106 2019/7/23 下午 02:30 2021/3/9 下午 12:12 2019/7/23 下午 02:30 @interface GPUImageHueBlendFilter : GPUImageTwoInputFilter
GPUImageLightenBlendFilter.h .h D:\Github\GPUImage\framework\Source 4 210 2019/7/23 下午 02:30 2021/3/9 下午 12:12 2019/7/23 下午 02:30 @interface GPUImageLightenBlendFilter : GPUImageTwoInputFilter
GPUImageLinearBurnBlendFilter.h .h D:\Github\GPUImage\framework\Source 3 113 2019/7/23 下午 02:30 2021/3/9 下午 12:12 2019/7/23 下午 02:30 @interface GPUImageLinearBurnBlendFilter : GPUImageTwoInputFilter
GPUImageLookupFilter.h .h D:\Github\GPUImage\framework\Source 3 1,530 2019/7/23 下午 02:30 2021/3/9 下午 12:12 2019/7/23 下午 02:30 @interface GPUImageLookupFilter : GPUImageTwoInputFilter
GPUImageLuminosityBlendFilter.h .h D:\Github\GPUImage\framework\Source 3 113 2019/7/23 下午 02:30 2021/3/9 下午 12:12 2019/7/23 下午 02:30 @interface GPUImageLuminosityBlendFilter : GPUImageTwoInputFilter
GPUImageMaskFilter.h .h D:\Github\GPUImage\framework\Source 3 102 2019/7/23 下午 02:30 2021/3/9 下午 12:12 2019/7/23 下午 02:30 @interface GPUImageMaskFilter : GPUImageTwoInputFilter
GPUImageMosaicFilter.h .h D:\Github\GPUImage\framework\Source 7 995 2019/7/23 下午 02:30 2021/3/9 下午 12:12 2019/7/23 下午 02:30 @interface GPUImageMosaicFilter : GPUImageTwoInputFilter {
GPUImageMultiplyBlendFilter.h .h D:\Github\GPUImage\framework\Source 3 117 2019/7/23 下午 02:30 2021/3/9 下午 12:12 2019/7/23 下午 02:30 @interface GPUImageMultiplyBlendFilter : GPUImageTwoInputFilter
GPUImageNormalBlendFilter.h .h D:\Github\GPUImage\framework\Source 6 155 2019/7/23 下午 02:30 2021/3/9 下午 12:12 2019/7/23 下午 02:30 @interface GPUImageNormalBlendFilter : GPUImageTwoInputFilter
GPUImageOverlayBlendFilter.h .h D:\Github\GPUImage\framework\Source 3 110 2019/7/23 下午 02:30 2021/3/9 下午 12:12 2019/7/23 下午 02:30 @interface GPUImageOverlayBlendFilter : GPUImageTwoInputFilter
GPUImageSaturationBlendFilter.h .h D:\Github\GPUImage\framework\Source 3 113 2019/7/23 下午 02:30 2021/3/9 下午 12:12 2019/7/23 下午 02:30 @interface GPUImageSaturationBlendFilter : GPUImageTwoInputFilter
GPUImageScreenBlendFilter.h .h D:\Github\GPUImage\framework\Source 3 115 2019/7/23 下午 02:30 2021/3/9 下午 12:12 2019/7/23 下午 02:30 @interface GPUImageScreenBlendFilter : GPUImageTwoInputFilter
GPUImageSoftLightBlendFilter.h .h D:\Github\GPUImage\framework\Source 3 118 2019/7/23 下午 02:30 2021/3/9 下午 12:12 2019/7/23 下午 02:30 @interface GPUImageSoftLightBlendFilter : GPUImageTwoInputFilter
GPUImageSourceOverBlendFilter.h .h D:\Github\GPUImage\framework\Source 3 113 2019/7/23 下午 02:30 2021/3/9 下午 12:12 2019/7/23 下午 02:30 @interface GPUImageSourceOverBlendFilter : GPUImageTwoInputFilter
GPUImageSubtractBlendFilter.h .h D:\Github\GPUImage\framework\Source 3 111 2019/7/23 下午 02:30 2021/3/9 下午 12:12 2019/7/23 下午 02:30 @interface GPUImageSubtractBlendFilter : GPUImageTwoInputFilter
GPUImageThreeInputFilter.h .h D:\Github\GPUImage\framework\Source 5 580 2019/7/23 下午 02:30 2021/3/9 下午 12:12 2019/7/23 下午 02:30 @interface GPUImageThreeInputFilter : GPUImageTwoInputFilter
GPUImageTwoInputCrossTextureSamplingFilter.h .h D:\Github\GPUImage\framework\Source 3 559 2019/7/23 下午 02:30 2021/3/9 下午 12:12 2019/7/23 下午 02:30 @interface GPUImageTwoInputCrossTextureSamplingFilter : GPUImageTwoInputFilter
GPUImageVoronoiConsumerFilter.h .h D:\Github\GPUImage\framework\Source 3 201 2019/7/23 下午 02:30 2021/3/9 下午 12:12 2019/7/23 下午 02:30 @interface GPUImageVoronoiConsumerFilter : GPUImageTwoInputFilter

": GPUImageThreeInputFilter"
GPUImageFourInputFilter.h .h D:\Github\GPUImage\framework\Source 5 588 2019/7/23 下午 02:30 2021/3/9 下午 12:12 2019/7/23 下午 02:30 @interface GPUImageFourInputFilter : GPUImageThreeInputFilter
*/
