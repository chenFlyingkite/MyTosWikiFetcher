package main;

import flyingkite.awt.Robot2;
import flyingkite.files.FileUtil;
import flyingkite.functional.Projector;
import flyingkite.javaxlibrary.images.base.PngParam;
import flyingkite.javaxlibrary.images.create.PngCreateRequest;
import flyingkite.javaxlibrary.images.create.PngCreator;
import flyingkite.javaxlibrary.images.resize.PngResizer;
import flyingkite.log.L;
import flyingkite.math.MathUtil;
import flyingkite.tool.StringUtil;
import flyingkite.tool.TaskMonitorUtil;
import flyingkite.tool.ThreadUtil;
import flyingkite.tool.TicTac2;
import main.fetcher.BotGoldPassbook;
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
import main.fetcher.thsr.THSRTGoFetcher;
import main.fetcher.web.WebFetcher;
import main.kt.CopyInfo;
import main.twse.TWSEStockFetcher;

import java.awt.AWTException;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutorService;

public class Main {
    private static final Random random = new Random();
    private static final Robot2 robot = Robot2.create();
    public static final SimpleDateFormat formatISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);

    public static void main(String[] args) {
        L.log("Main, %s", now());
        clock.tic();
        //-- Main work
        //-- Tos related
        //fetch(0); // x <= 0 : run TosCardExtras
        //copyToMyTosWiki();
        //enterUID();
        //-- Robot2 Automations
        //HPMain.main(null);
        //-- Other Misc
        //taiwanHighSpeedRail();
        //ForeignExchangeRateFetcher.main(null);
        //Covariance.test();

        //LiveAHeroMain.main(null);
        //MyTosWikiFirebase.run();
        //-- testing area
        //PngCreator.me.standard(null);
        //PngCreator.me.standard16(null);
        //PngCreator.me.standardGrey1(null);

        //gold(); // Annually
        stock();
        //robotKeyboard();

        //XliffParser.me.addStringsToIos();

        //ASD.run(); // testing on filter cards
        //print();

        //TosCardInfos.me.run(); // creating evolution info
        //new LeetCode().run();
        //PngCreator.me.moveImage();
        //FileUtil.listImages("D:\\PhotoDirector_iOS\\Main_01"); // windows
        //FileUtil.listImages("/Users/ericchen/Desktop/SVNs/PHD_iOS/PHD_M03"); // mac mini
        //ExcelParser.main(null);
        //MainTest.main(null);
        //new LeetCode().run();
        //addJpg();
        //getIP();
        // 199215954 360302
        // 150372202 690139
        // https://gift4u.tosgame.com/tutorial
        // https://jumbodraw.tosgame.com/

        //NationalIDCard.me.testCases();
        //TaxCalculator.me.test();

        //FaceMeAuto.replaceFintechNile();
        //FaceMeAuto.faceMeFintechBuild();
        //FaceMeAuto.organizeCommitLog(new File("D:\\Github\\sample.txt"));
        //FileUtil.seeFiles("G:\\我的雲端硬碟\\台南的房屋訴訟\\陳建志台南房屋買賣", FileUtil.OMIT_FILE_INI, FileUtil.RELATIVE_PATH);
        //FileUtil.seeFiles("D:\\_\\光碟燒錄\\李睿巃傷害", FileUtil.RELATIVE_PATH);
        //FileUtil.seeFiles("D:\\_李睿巃\\CVC", FileUtil.RELATIVE_PATH);
        //JudgementSearch.run();
        //new BinarySearchVisualizer().run();
        //PointExchange.main(null);
        //new BinarySearchVisualizer().run();
        //PointExchange.main(null);
        //temp();
        //robotMouse();
        //YouTubeHelper.publicYTVideo(YouTubeHelper.Audience_private);
        //shopeeShrinkUrl();
        //autoRenameFiles();
        clock.tac("Done");
        L.log("Done, %s", now());
    }

    private static void pdfSpecs() {
        // 1. set/unset password
        // 2. round robin pages of pdfs
    }

    private static void autoRenameFiles() {
        for (int i = 1; i <= 18; i++) {
            robot.keyClick(KeyEvent.VK_F2);
            robot.paste();
            robot.enter(String.format(Locale.US, "%02d", i));
            // next
            robot.keyClick(KeyEvent.VK_RIGHT);
            robot.delay(200);
        }
    }

    private static void shopeeShrinkUrl() {
        String src;
        src = "https://shopee.tw/%E8%BD%89%E6%8E%A5%E9%A0%AD-%E8%90%AC%E7%94%A8%E8%BD%89%E6%8E%A5%E9%A0%AD-%E8%90%AC%E7%94%A8-%E4%BD%8E%E5%8A%9F%E7%8E%87-%E9%AB%98%E5%8A%9F%E7%8E%87-%E8%90%AC%E7%94%A8%E6%8F%92%E9%A0%AD-%E8%BD%89%E6%8F%9B%E9%A0%AD%E6%8F%92%E5%BA%A7%E9%9B%BB%E6%BA%90%E8%BD%89%E6%8F%9B%E6%8F%92%E9%A0%AD%E6%97%85%E9%81%8A%E6%97%85%E8%A1%8C%E8%90%AC%E7%94%A8%E8%BD%89%E6%8F%9B%E6%8F%92%E9%A0%AD-%E8%BD%89%E6%8E%A5%E6%8F%92%E9%A0%AD-%E5%85%85%E9%9B%BB%E9%A0%AD-i.232991858.21652256132?sp_atk=e506997b-c909-4685-a689-344a96bade96&xptdk=e506997b-c909-4685-a689-344a96bade96";
        src = "https://shopee.tw/%E6%96%B0%E5%93%81%E5%8F%AF%E6%A8%82%E6%A9%9F%E6%BF%83%E7%B8%AE%E6%BC%BF%E5%95%86%E7%94%A8%E5%8F%AF%E6%A8%82%E7%A2%B3%E9%85%B8%E9%A3%B2%E6%96%99%E6%A9%9F%E6%BC%A2%E5%A0%A1%E5%BA%97%E5%B0%88%E7%94%A8print0602-i.150866167.25775645119?sp_atk=00cb08c3-d5d6-4807-b027-fb4cd02d09c7&xptdk=00cb08c3-d5d6-4807-b027-fb4cd02d09c7";
//        # 0 : shopee.tw
//        # 1 : shopee.tw
//        # 2 : null
//        # 3 : null
//        # 4 : /轉接頭-萬用轉接頭-萬用-低功率-高功率-萬用插頭-轉換頭插座電源轉換插頭旅遊旅行萬用轉換插頭-轉接插頭-充電頭-i.232991858.21652256132
//        # 5 : /%E8%BD%89%E6%8E%A5%E9%A0%AD-%E8%90%AC%E7%94%A8%E8%BD%89%E6%8E%A5%E9%A0%AD-%E8%90%AC%E7%94%A8-%E4%BD%8E%E5%8A%9F%E7%8E%87-%E9%AB%98%E5%8A%9F%E7%8E%87-%E8%90%AC%E7%94%A8%E6%8F%92%E9%A0%AD-%E8%BD%89%E6%8F%9B%E9%A0%AD%E6%8F%92%E5%BA%A7%E9%9B%BB%E6%BA%90%E8%BD%89%E6%8F%9B%E6%8F%92%E9%A0%AD%E6%97%85%E9%81%8A%E6%97%85%E8%A1%8C%E8%90%AC%E7%94%A8%E8%BD%89%E6%8F%9B%E6%8F%92%E9%A0%AD-%E8%BD%89%E6%8E%A5%E6%8F%92%E9%A0%AD-%E5%85%85%E9%9B%BB%E9%A0%AD-i.232991858.21652256132
//        # 6 : shopee.tw
//        # 7 : sp_atk=e506997b-c909-4685-a689-344a96bade96&xptdk=e506997b-c909-4685-a689-344a96bade96
//        # 8 : sp_atk=e506997b-c909-4685-a689-344a96bade96&xptdk=e506997b-c909-4685-a689-344a96bade96
//        # 9 : https
//        #10 : //shopee.tw/轉接頭-萬用轉接頭-萬用-低功率-高功率-萬用插頭-轉換頭插座電源轉換插頭旅遊旅行萬用轉換插頭-轉接插頭-充電頭-i.232991858.21652256132?sp_atk=e506997b-c909-4685-a689-344a96bade96&xptdk=e506997b-c909-4685-a689-344a96bade96
//        #11 : //shopee.tw/%E8%BD%89%E6%8E%A5%E9%A0%AD-%E8%90%AC%E7%94%A8%E8%BD%89%E6%8E%A5%E9%A0%AD-%E8%90%AC%E7%94%A8-%E4%BD%8E%E5%8A%9F%E7%8E%87-%E9%AB%98%E5%8A%9F%E7%8E%87-%E8%90%AC%E7%94%A8%E6%8F%92%E9%A0%AD-%E8%BD%89%E6%8F%9B%E9%A0%AD%E6%8F%92%E5%BA%A7%E9%9B%BB%E6%BA%90%E8%BD%89%E6%8F%9B%E6%8F%92%E9%A0%AD%E6%97%85%E9%81%8A%E6%97%85%E8%A1%8C%E8%90%AC%E7%94%A8%E8%BD%89%E6%8F%9B%E6%8F%92%E9%A0%AD-%E8%BD%89%E6%8E%A5%E6%8F%92%E9%A0%AD-%E5%85%85%E9%9B%BB%E9%A0%AD-i.232991858.21652256132?sp_atk=e506997b-c909-4685-a689-344a96bade96&xptdk=e506997b-c909-4685-a689-344a96bade96
//        #12 : -1
//        #13 : null
        try {
            URI u = new URI(src);
            String[] ss = {
                    u.getAuthority(), u.getRawAuthority(),
                    u.getFragment(), u.getRawFragment(),
                    u.getPath(), u.getRawPath(),
                    u.getHost(),
                    u.getQuery(), u.getRawQuery(),
                    u.getScheme(), u.getSchemeSpecificPart(), u.getRawSchemeSpecificPart(),
                    "" + u.getPort(), u.getUserInfo()
            };
            String[] focus = u.getPath().split("\\x2E"); // = . (period)

            int n = focus.length;
            if (n >= 3) {
                String newPath = String.format(Locale.US, "/product/%s/%s", focus[n-2], focus[n-1]);
                URI u2 = new URI(u.getScheme(), u.getHost(), newPath, u.getFragment());
                L.log("shrunk = %s", u2);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        String link = "https://teams.microsoft.com/dl/launcher/launcher.html?url=%2F_%23%2Fl%2Fmeetup-join%2F19%3Ameeting_NWFhYThlMTMtYWQ0ZS00MzQ0LWE2YzktNWQxM2M2ZWVlOTY0%40thread.v2%2F0%3Fcontext%3D%257b%2522Tid%2522%253a%252286254269-e4d3-4c53-bc98-640752566d31%2522%252c%2522Oid%2522%253a%2522cfe4575f-ec58-44cb-81c5-df1a5a65196e%2522%257d%26anon%3Dtrue&type=meetup-join&deeplinkId=7727539f-f958-4252-b3f0-35fcd0038e22&directDl=true&msLaunch=true&enableMobilePage=true&suppressPrompt=true";

    }

    private static void temp() {
        String path;
        path = "D:\\政府法律訴訟\\法院訴訟\\1120328陳建志提告許䕒元妨害自由_Z112039AV4O1XUX";
        path = "D:\\___PlayGround\\XZP SD\\DCIM\\100ANDRO";
        path = "D:\\三星 A52s";
        //FileUtil.seeFiles(path, FileUtil.OMIT_FILE_INI, FileUtil.RELATIVE_PATH);

        //renameFiles();
        //path = "D:\\地籍謄本 - 複製"; // y
        //s = "D:\\地籍謄本 - 複製\\2023 - 複製"; // H
        //s = "D:\\地籍謄本 - 複製\\2023 - 複製"; // Q
        //path = "D:\\地籍謄本 - 複製\\2023 - 複製"; // M
        //s = "D:\\地籍謄本 - 複製\\2023H01"; // D
        //FileUtil.wrapFileInFolder(path, 'M');
        path = "E:\\20230730車禍\\ToPolice";
        List<File> fs = FileUtil.listAllFiles(path);
        L.log("%4d files in %s", fs.size(), path);
        for (int i = 0; i < fs.size(); i++) {
            L.log("#%4d : %s", i, fs.get(i));
        }
    }

    private static void robotMouse() {
        ThreadUtil.sleep(5_000);
        for (int i = 0; i < 20000; i++) {
            robot.mouseClickLeft();
            //ThreadUtil.sleep(100);
        }
    }

    private static void seeFiles() {
//        listFiles(path, RELATIVE_PATH);
        String path = "C:\\Users\\chener\\Documents\\HP\\GitHub\\CMITSW\\WinPVT03c";
        //path = "\\\\tdc-cmit-sw.auth.hpicorp.net\\share\\external\\WinPVT\\WinPVT_11.10.1_Verifying\\Scripts\\X86_X64";
        Projector<File, Boolean> cppFilter = (file) -> {
            if (file == null) return false;
            boolean nonHidden = file.isHidden();
            //Regex regex = new Regex(".*[\\x2e](c|cpp|h)");

            L.log("-> %s", file.getName());
            //boolean cpp_h = regex.matches(file.getName());
            boolean cpp_h = file.getName().matches(".*[\\x2e](c|cpp|h)");
            boolean valid = nonHidden && cpp_h;
            return valid;
        };
        //FileUtil.listAllFiles(path, cppFilter);
    }

    // 2147483647          = 2.1 * 10^9  = Integer.MAX_VALUE = 2^31 - 1 = 0x7fffffff
    // 9223372036854775807 = 9.2 * 10^18 = Long.MAX_VALUE    = 2^63 - 1 = 0x7fffffffffffffffL
    // 0         1         2
    // 012345678901234567890

    // Used for auto enter the UID giveaway
    // Usage: Change the more text if you have answers,
    // and run to web page make cursor stays at it, now it will make things done!
    private static void enterUID() {
        // We need to place cursor on the input position for typing automatically
        robot.delay(3_000);
        // true = ids + more + (ctrl+v), false = ids + more
        boolean paste = 0 > 0;
        String more = ""; // " A"

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
                "512617371",
                "2170312",
        };
        for (String s : ids) {
            String t = s + more;
            if (paste) {
                robot.type(t + " ");
                robot.paste();
                robot.keyClick(KeyEvent.VK_ENTER);
            } else {
                robot.enter(t);
            }
            // mimic as human to pass fraud detection
            robot.delay(1500 + random.nextInt(500));
        }
    }

    private static void autoScreenCapture() {
        // Parameters
        long intervalMS = 30 * 1_000;
        int captures = 1000;

        // Implementation
        ThreadUtil.sleep(3_000);
        for (int i = 0; i < captures; i++) {
            robot.keyPressRelease(new int[]{KeyEvent.VK_WINDOWS, KeyEvent.VK_PRINTSCREEN});
            ThreadUtil.sleep(intervalMS);
        }
    }

    // AppleID cl.shaomai@gmail.com / Cl23829868
    // https://developer.android.com/studio/command-line/adb

    private static void robotKeyboard() {
        try {
            Robot2 r = new Robot2();
            r.delay(5_000);
            for (int i = 0; i < 30; i++) {
                r.keyClick(KeyEvent.VK_RIGHT);
                r.keyClick(KeyEvent.VK_SPACE);
            }
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private static void taiwanHighSpeedRail() {
        THSRTGoFetcher.main(null);
    }

    // https://stackoverflow.com/questions/23157653/drawviewhierarchyinrectafterscreenupdates-delays-other-animations

    // Fast Contour-Tracing Algorithm Based on a Pixel-Following Method for Image Sensors
    // https://www.ncbi.nlm.nih.gov/pmc/articles/PMC4813928/
    //
    // https://limnu.com/premultiplied-alpha-primer-artists/
    // https://www.khronos.org/registry/OpenGL/specs/gl/GLSLangSpec.1.20.pdf

    // https://davidmz.github.io/apng-js/
    private static void x() {
        String src;
        src = "D:\\ASD\\APNG Tests\\U-2021-03-11.jpg";
        String dst;
        dst = "D:\\ASD\\APNG Tests\\U-2021-03-11_2.png";
        PngCreator.from(new PngParam(src)).replace(new PngCreateRequest.ColorSelector() {
            @Override
            public int drawAt(int x, int y, int w, int h, int c) {
                if (MathUtil.isInRange(x, w / 4, w * 3 / 4) && MathUtil.isInRange(y, h / 5, h * 3 / 5)) {
                    return 0;
                }
                return c;
            }
        }).into(dst);
    }
    // 台北捷運 單一車站至所有車站
    // https://web.metro.taipei/pages/tw/ticketroutetimesingle/036

    // resize
    private static void make1x2x() {
        // make Photo director images,
        // given 3x image, make it of 1x, 2x
        String root = "/Users/ericchen/Desktop/SVNs/PHD_iOS/PHD_M03/PhotoDirector/PhotoDirector/";
        //root += "/Images.xcassets/";
        root += "/Images/edit/";
        // images path to from 3x
        String[] path3x = {
                "btn_2lv_font_n@3x.png",
                "btn_2lv_font_n@3x.png",
                "btn_2lv_font_n@3x.png",
                "btn_2lv_font_p@3x.png",
                "btn_2lv_font_p@3x.png",
                "btn_2lv_font_p@3x.png",
                "btn_2lv_font_s@3x.png",
                "btn_2lv_font_s@3x.png",
                "btn_2lv_font_s@3x.png",
        };
        for (int i = 0; i < path3x.length; i++) {
            String src = root + path3x[i];
            File f = new File(src);
            if (f.exists()) {
                String dst;
                if (i % 3 == 0) {
                    dst = src.replace("font_", "font2_");
                    dst = dst.replace("@3x", "@1x");
                    PngResizer.from(new PngParam(f).size(33, 33)).into(dst);
                } else if (i % 3 == 1) {
                    dst = src.replace("font_", "font2_");
                    dst = dst.replace("@3x", "@2x");
                    PngResizer.from(new PngParam(f).size(65, 65)).into(dst);
                } else {
                    dst = src.replace("font_", "font2_");
                    PngResizer.from(new PngParam(f).size(110, 110)).into(dst);
                }
                ln("OK : %s", dst);
            }
        }
    }

    private static void fetch(int extra) {
        if (extra <= 0) {
            //TosCardExtras.me.run(); // Almost 200ms * 3300 cards ~= 15min
        }
        //fetchMisc();
        fetchCards();
    }

    private static void ln(String fmt, Object... p) {
        System.out.println((p == null) ? fmt : String.format(fmt, p));
    }

    private static void fetchCards() {
        boolean fullRun = 0 > 0;
        clock.tic();
        if (fullRun) {
            // 卡片內容
            // Skill change + Craft + Card List -> Card Fetcher
            List<Runnable> beforeCard = Arrays.asList(
                    TosWikiCardsLister.me
                    , TosSkillFetcher.me
                    , TosCraftFetcher.me
            );
            Runnable endCard = () -> {
                TosCardFetcher.me.run();
            };

            TaskMonitorUtil.join(beforeCard, endCard);
        } else {
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
        //YahooStockFetcher.me.parse();
        TWSEStockFetcher.main(null);
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
        // macOS or unix is /, while windows = \\
        String asset = "../MyTosWiki/app/src/main/assets/";

        paths.add(as(         "myCard/",     "cardList.json", asset));
        paths.add(as(         "myCard/",   "evolvePath.json", asset));
        paths.add(as(        "myCraft/",       "crafts.json", asset));
        paths.add(as(        "myCraft/",    "armCrafts.json", asset));
        paths.add(as("myLostRelicPass/",    "relicPass.json", asset));
        //paths.add(as(    "myMainStage/",    "mainStage.json", asset));
        paths.add(as(   "myStoryStage/",   "storyStage.json", asset));
        paths.add(as(    "myVoidRealm/",    "voidRealm.json", asset));
        paths.add(as("myUltimateStage/","ultimateStage.json", asset));

        for (CopyInfo i : paths) {
            String source = i.getSourceName();
            String target = i.getTargetName();
            FileUtil.copy(source, target);
            L.log("copy : %s\n to -> %s", source, target);
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

    // https://stackoverflow.com/questions/3387132/how-to-load-and-display-image-in-opengl-es-for-iphone
    // (Nekojishi) A Sunny Day is Watching over You
    // https://www.youtube.com/watch?v=egAwcvzseeM
    // https://mega.nz/file/UOZ1wJrC#-eCtj4DlZoIyKOI5Ss0fG2d1uWDbUTDOaK6nDOwz1Xo

    //https://tos.fandom.com/zh/api.php?format=json&action=expandtemplates&text=%7B%7B1234%7CfullstatsMax}}
    private static final ExecutorService cache = ThreadUtil.newFlexThreadPool(50, 60);

    private static final TicTac2 clock = new TicTac2();

    private static void getIP() {
        //https://stackoverflow.com/questions/391979/how-to-get-clients-ip-address-using-javascript
        String[] src = {
                "https://api.db-ip.com/v2/free/self",
                "http://ip-api.com/json"
        };

        WebFetcher w = new WebFetcher();
        for (int i = 0; i < src.length; i++) {
            String link = src[i];
            String s = w.sendRequest(link, null);
            L.log("#%s :\n%s", i, s);
        }
    }

    // For each file in {$source}, if file is source/a, rename to be source/a{want}
    // usually used for Line's internal file folder to make all files as image
    private static void renameFiles() {
        // add jpg
        final String source = "C:\\Users\\User\\Downloads\\LINE_Android-backup-chat301366571b8\\linebackup\\image"; // source path, 1-depth
        final String want = ".jpg";

        File f = new File(source);
        File[] fs = f.listFiles();
        int n = 0;
        if (fs != null) {
            for (int i = 0; i < fs.length; i++) {
                File z = fs[i];
                boolean ok = z.getName().endsWith(want);
                if (ok) {
                } else {
                    n++;
                    File fx = new File(z.getAbsolutePath() + want);
                    z.renameTo(fx);
                    L.log("#%d : %s", n, z);
                }
            }
        }
        L.log("%s files renamed", n);
    }

}
/*
https://docs.google.com/presentation/d/15mSXudFsWko1Cmzdg6O__z1AX_Po4YpcauqPq6T7-xU/edit#slide=id.p
https://docs.google.com/presentation/d/14rKfhX5e6PvEs4OsdXpwv0DM6rQ97BWTkxrn6tg2Kq8/edit#slide=id.p
https://docs.google.com/presentation/d/1MMhbJhNe217dmKJmmYr2qxpxfGlxgG-P8IuxevrLC2c/edit#slide=id.p
*/
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
