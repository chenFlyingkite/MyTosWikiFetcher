package main;

import flyingkite.data.Rect2;
import flyingkite.functional.MeetSS;
import flyingkite.javaxlibrary.images.base.PngParam;
import flyingkite.javaxlibrary.images.create.PngCreator;
import flyingkite.javaxlibrary.images.diff.PngDiffer;
import flyingkite.javaxlibrary.images.resize.PngResizer;
import flyingkite.log.L;
import flyingkite.math.ChiSquarePearson;
import flyingkite.math.ChiSquareTable;
import flyingkite.math.DiscreteSample;
import flyingkite.math.Statistics;
import flyingkite.tool.GsonUtil;
import flyingkite.tool.IOUtil;
import flyingkite.tool.TextUtil;
import flyingkite.tool.TicTac2;
import main.card.TosCard;
import main.kt.Craft;
import main.kt.CraftSkill;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class ASD {
    private static final TicTac2 tt = new TicTac2();

    public static void run() {
        tt.reset();
        tt.tic();
        //getLogos();
        //getPlusMinus();
        //getMissions();
        //getNums();
        //getActiveIcons();
        //getMonsterEat();
        //getPrizeIcons();
        //getCrop();
        //new RunInvokeMethod().run();
        //Jsoner.json();
        //getCrafts();
        //getMainIcons();
        //chiTest();
        //test();
        //getFriendPointsCards();
        //formatting();

        //scale();
        //scaleAllImage("D:\\PMP_Android_Face\\Amber", new String[]{"original"}, 640);
        //diff();
        //getStones();

        loadAllCards();
        //loadAllCrafts();

        //getV16Icons();
        //getVoidRealm();
        //PngInvert.me.invertImages("Logos/A", "Logos/B");
        //getEnochianTower();
        //getStoryDragon();
        //scaleImage("D:\\GitHub\\MyTosWikiFetcher\\Logos\\Output\\enochian", 400);
        //scaleAllImage("D:\\GitHub\\MyTosWiki\\app\\src\\main\\res\\drawable-xxxhdpi", new String[]{"x"}, 100);
        tt.tac("Done");
    }

    private static void loadAllCards() {
        TicTac2 clk = new TicTac2();

        File fc = new File("myCard", "cardList.json");
        // Try to parsing back to know its performance
        clk.tic();
        TosCard[] allCards = GsonUtil.loadFile(fc, TosCard[].class);
        clk.tac("%s cards loaded", allCards.length);

        // Put cards as map
        List<TosCard> noSkin = new ArrayList<>();
        Map<String, TosCard> map = new HashMap<>();
        for (TosCard c : allCards) {
            if (map.containsKey(c.idNorm)) {
                L.log("X_X Duplicate on %s", c.idNorm);
            }

            int id = Integer.parseInt(c.idNorm);
            if (6000 <= id && id < 7000) {
                // omit skin
                continue;
            }
            noSkin.add(c);
            map.put(c.idNorm, c);
        }
        // allCards to no skin
        allCards = noSkin.toArray(new TosCard[0]);

        // Find cards
        // Normal
        //findCardSkill("電擊符石", allCards);
        //findCardSkill("指定形狀", allCards);
        //findCardSkill("解除黑白符石的狀態", allCards);
        L.log("--**--\n\n--**--");
        //findCardSkill("水(.{0,30})火(.{0,30})木(.{0,30})光(.{0,30})暗(.{0,30})", true, allCards);

        L.log("--**--\n\n--**--");
        // Regex
        //findCardSkill(new String[]{"水","火","木","光","暗"}, true, allCards);
        //findCardSkill("增加(.{0,20})連擊", true, allCards);
        //findCardSkill("五屬", true, allCards);
        findCardSkill("位置", true, allCards);
        //findCardSkill("(水|火|木|光|暗)((、|及)(水|火|木|光|暗)){4}", true, allCards);
        //findCardSkill("生命力(.{0,30})提升(.{0,20})(倍|點)", true, allCards);
        L.log("--**--\n\n--**--");
        //findCardAme("召喚獸技能冷卻回合", allCards);
        //findCardSkill("解除黑白符石的狀態", allCards);
    }

    private static void loadAllCrafts() {
        File fc = new File("myCraft", "crafts.json");
        Craft[] craftsNorm = GsonUtil.loadFile(fc, Craft[].class);
        L.log("%s crafts", craftsNorm.length);
        Set<String> skillsNorm = new LinkedHashSet<>();
        for (Craft c : craftsNorm) {
            for (CraftSkill s : c.getCraftSkill()) {
                skillsNorm.add(s.getDetail());
            }
        }
        L.log("%s skillsNorm", skillsNorm.size());
//        for (String s : skillsNorm) {
//            L.log("%s", s);
//        }
        L.log("\n");


        File fa = new File("myCraft", "armCrafts.json");
        Craft[] craftsArm = GsonUtil.loadFile(fa, Craft[].class);
        L.log("%s arm crafts", craftsArm.length);

        Set<String> skillsArm = new HashSet<>();
        for (Craft c : craftsArm) {
            for (CraftSkill s : c.getCraftSkill()) {
                skillsArm.add(s.getDetail());
            }
        }
        L.log("%s skillsArm", skillsArm.size());
//        for (String s : skillsArm) {
//            L.log("%s", s);
//        }
    }


    private static void chiTest() {
        int runs = 50;
        int draws = 20;
        DiscreteSample s = new DiscreteSample(8);
        double[] pdf = new double[]{0.025, 0.1, 0.1, 0.155, 0.155, 0.155, 0.155, 0.155};
        List<Double> w = new ArrayList<>();
        for (double d : pdf) {
            w.add(d);
        }
        //w.clear();
        //w.addAll(Arrays.asList(1.0, 5.0, 10.0, 10.0, 5.0, 1.0));
        double skewPDF = Statistics.skewness(w);
        s.setPdf(pdf);
        for (int i = 0; i < runs; i++) {
            s.clearSample();
            s.drawSample(draws);
            s.evalObservePdf();
            boolean acc = ChiSquarePearson.acceptH0(s, ChiSquareTable.getChiTailArea(7, ChiSquareTable._100));
            List<Double> v = new ArrayList<>();
            for (double d : s.observePdf) {
                v.add(d);
            }

            double skew = Statistics.skewness(v);

            double euro = draws * s.cdf[2];
            double got = s.observe[0] + s.observe[1] + s.observe[2];
            boolean isEu = got >= euro;

            L.log(" euro = %s, get = %s => isEuro = %s", euro, got, isEu);
            L.log("#%s ->\n%s\n => Chi %s\n Skew = %s\n SPdf = %s-----\n", i, s, acc ? "accept" : "qwe reject", skew, skewPDF);
        }
    }

    private static void test() {
        List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 1));

        for (int i = 2; i < 21; i++) {
            double sku = Statistics.skewness(list);
            L.log("skew = %s\n : %s\n---", sku, list);

            list.add(1);
            for (int j = list.size() - 2; j > 0; j--) {
                int x = list.get(j) + list.get(j - 1);
                list.set(j, x);
            }
        }
    }

    private static void findCardAme(String key, TosCard[] allCards) {
        int nc = 0;
        for (TosCard c : allCards) {
            boolean hasAme = !c.skillAmeName1.isEmpty();
            if (hasAme) {
                char[] cs = new char[4];
                cs[0] = exist(key, c.skillAmeName1);
                cs[1] = exist(key, c.skillAmeName2);
                cs[2] = exist(key, c.skillAmeName3);
                cs[3] = exist(key, c.skillAmeName4);

                String s = String.valueOf(cs);
                if (s.contains("O")) {
                    nc++;

                    String t = "";
                    t += cs[0] + " : " + c.skillAmeName1 + "\n";
                    t += cs[1] + " : " + c.skillAmeName2 + "\n";
                    t += cs[2] + " : " + c.skillAmeName3 + "\n";
                    t += cs[3] + " : " + c.skillAmeName4 + "\n";

                    L.log("%s\n%s\n", sc(c), t);
                }
            }
        }

        L.log("%s in Amelioration, key = %s", nc, key);
    }

    private static char exist(String key, String content) {
        if (content == null || content.isEmpty()) {
            return 'N';
        } else if (content.contains(key)) {
            return 'O';
        } else {
            return 'X';
        }
    }

    private static void findCardSkill(String key, boolean regex, TosCard[] allCards) {
        // Define match condition
        MeetSS<String, Boolean> cond;
        if (regex) {
            cond = (a, b) -> {
                return Pattern.compile(b).matcher(a).find();
            };
        } else {
            cond = (a, b) -> {
                return a.contains(b);
            };
        }

        // Major part
        int sn = 0, tn = 0, un = 0;
        L.log("--**--");
        //L.log("Find %s of %s", regex ? "regex" : "text", key);
        int n = 0, m = 0;
        for (TosCard c : allCards) {
            boolean inDetail = false;
            String s = c.skillDesc1 + " & " + c.skillDesc2 + " & " + c.skillAwkName;
            String t = c.cardDetails;
            String u = c.skillLeaderDesc;
            String exist = "";
            if (cond.meet(s, key)) {
                sn++;
                exist += " Skill,";
            }
            if (cond.meet(t, key)) {
                inDetail = true;
                exist += " Detail,";
                tn++;
            }
            if (cond.meet(u, key)) {
                exist += " Leader";
                un++;
            }
            if (!exist.isEmpty()) {
                L.log(exist + "\n" + sc(c));
                if (inDetail) {
                    L.log(c.cardDetails);
                }

                if (c.race.contains("素材")) {
                    m++;
                } else {
                    n++;
                }
            }
        }

        L.log("%s in skill, %s in detail, %s in leader, key = %s", sn, tn, un, key);
        L.log("Found %s in normal, %s in 素材", n, m);
    }

    private static void findCardSkill(String[] keys, boolean regex, TosCard[] allCards) {
        // Define match condition
        MeetSS<String, Boolean> cond;
        if (regex) {
            cond = (a, b) -> {
                return Pattern.compile(b).matcher(a).find();
            };
        } else {
            cond = (a, b) -> {
                return a.contains(b);
            };
        }

        // Major part
        int sn = 0, tn = 0, un = 0;
        L.log("--**--");
        //L.log("Find %s of %s", regex ? "regex" : "text", key);
        int n = 0, m = 0;
        for (TosCard c : allCards) {
            boolean inDetail = false;
            String s = c.skillDesc1 + " & " + c.skillDesc2 + " & " + c.skillAwkName;
            String t = c.cardDetails;
            String u = c.skillLeaderDesc;
            String exist = "";
            boolean skYes = true;
            for (String k : keys) {
                skYes &= cond.meet(s, k);
            }
            if (skYes) {
                sn++;
                exist += " Skill,";
            }

            boolean dtYes = true;
            for (String k : keys) {
                dtYes &= cond.meet(t, k);
            }
            if (dtYes) {
                inDetail = true;
                exist += " Detail,";
                tn++;
            }

            boolean ldYes = true;
            for (String k : keys) {
                ldYes &= cond.meet(u, k);
            }
            if (ldYes) {
                exist += " Leader";
                un++;
            }
            if (!exist.isEmpty()) {
                L.log(exist + "\n" + sc(c));
                if (inDetail) {
                    L.log(c.cardDetails);
                }

                if (c.race.contains("素材")) {
                    m++;
                } else {
                    n++;
                }
            }
        }

        L.log("%s in skill, %s in detail, %s in leader, key = %s", sn, tn, un, Arrays.toString(keys));
        L.log("Found %s in normal, %s in 素材", n, m);
    }

    private static void findCardSkill(String key, TosCard[] allCards) {
        findCardSkill(key, false, allCards);
    }

    private static String sc(TosCard c) {
        return "#" + c.idNorm + "," + c.name
            + "\n  " + c.skillDesc1 + "," + c.skillDesc2
            + "\n  " + c.skillLeaderDesc
            //+ "\n  " + c.cardDetails
        ;
        //return String.format("#%4s,%s\n      %s\n      %s", c.idNorm, c.name, c.skillDesc1 + "," + c.skillDesc2, c.skillLeaderDesc);
    }

    private static void formatting() {
        String data = "1 0.000 0.000 0.001 0.004 0.016 2.706 3.841 5.024 6.635 7.879\n" +
                "        2 0.010 0.020 0.051 0.103 0.211 4.605 5.991 7.378 9.210 10.597\n" +
                "        3 0.072 0.115 0.216 0.352 0.584 6.251 7.815 9.348 11.345 12.838\n" +
                "        4 0.207 0.297 0.484 0.711 1.064 7.779 9.488 11.143 13.277 14.860\n" +
                "        5 0.412 0.554 0.831 1.145 1.610 9.236 11.070 12.833 15.086 16.750\n" +
                "        6 0.676 0.872 1.237 1.635 2.204 10.645 12.592 14.449 16.812 18.548\n" +
                "        7 0.989 1.239 1.690 2.167 2.833 12.017 14.067 16.013 18.475 20.278\n" +
                "        8 1.344 1.646 2.180 2.733 3.490 13.362 15.507 17.535 20.090 21.955\n" +
                "        9 1.735 2.088 2.700 3.325 4.168 14.684 16.919 19.023 21.666 23.589\n" +
                "        10 2.156 2.558 3.247 3.940 4.865 15.987 18.307 20.483 23.209 25.188\n" +
                "        11 2.603 3.053 3.816 4.575 5.578 17.275 19.675 21.920 24.725 26.757\n" +
                "        12 3.074 3.571 4.404 5.226 6.304 18.549 21.026 23.337 26.217 28.300\n" +
                "        13 3.565 4.107 5.009 5.892 7.042 19.812 22.362 24.736 27.688 29.819\n" +
                "        14 4.075 4.660 5.629 6.571 7.790 21.064 23.685 26.119 29.141 31.319\n" +
                "        15 4.601 5.229 6.262 7.261 8.547 22.307 24.996 27.488 30.578 32.801\n" +
                "        16 5.142 5.812 6.908 7.962 9.312 23.542 26.296 28.845 32.000 34.267\n" +
                "        17 5.697 6.408 7.564 8.672 10.085 24.769 27.587 30.191 33.409 35.718\n" +
                "        18 6.265 7.015 8.231 9.390 10.865 25.989 28.869 31.526 34.805 37.156\n" +
                "        19 6.844 7.633 8.907 10.117 11.651 27.204 30.144 32.852 36.191 38.582\n" +
                "        20 7.434 8.260 9.591 10.851 12.443 28.412 31.410 34.170 37.566 39.997\n" +
                "        21 8.034 8.897 10.283 11.591 13.240 29.615 32.671 35.479 38.932 41.401\n" +
                "        22 8.643 9.542 10.982 12.338 14.041 30.813 33.924 36.781 40.289 42.796\n" +
                "        23 9.260 10.196 11.689 13.091 14.848 32.007 35.172 38.076 41.638 44.181\n" +
                "        24 9.886 10.856 12.401 13.848 15.659 33.196 36.415 39.364 42.980 45.559\n" +
                "        25 10.520 11.524 13.120 14.611 16.473 34.382 37.652 40.646 44.314 46.928\n" +
                "        26 11.160 12.198 13.844 15.379 17.292 35.563 38.885 41.923 45.642 48.290\n" +
                "        27 11.808 12.879 14.573 16.151 18.114 36.741 40.113 43.195 46.963 49.645\n" +
                "        28 12.461 13.565 15.308 16.928 18.939 37.916 41.337 44.461 48.278 50.993\n" +
                "        29 13.121 14.256 16.047 17.708 19.768 39.087 42.557 45.722 49.588 52.336\n" +
                "        30 13.787 14.953 16.791 18.493 20.599 40.256 43.773 46.979 50.892 53.672\n" +
                "        40 20.707 22.164 24.433 26.509 29.051 51.805 55.758 59.342 63.691 66.766\n" +
                "        50 27.991 29.707 32.357 34.764 37.689 63.167 67.505 71.420 76.154 79.490\n" +
                "        60 35.534 37.485 40.482 43.188 46.459 74.397 79.082 83.298 88.379 91.952\n" +
                "        70 43.275 45.442 48.758 51.739 55.329 85.527 90.531 95.023 100.425 104.215\n" +
                "        80 51.172 53.540 57.153 60.391 64.278 96.578 101.879 106.629 112.329 116.321\n" +
                "        90 59.196 61.754 65.647 69.126 73.291 107.565 113.145 118.136 124.116 128.299\n" +
                "        100 67.328 70.065 74.222 77.929 82.358 118.498 124.342 129.561 135.807 140.169";

        String[] lines = data.split("\n");
        for (String line : lines) {
            String ln = line.trim();
            String[] cell = ln.split(" ");
            StringBuilder sk = new StringBuilder("");
            for (int i = 1; i < cell.length; i++) {
                if (i > 1) {
                    sk.append(", ");
                }
                sk.append(String.format("%7s", cell[i]));
            }
            L.log("table.put(%3s, new Double[]{%s});", cell[0], sk);
        }
    }

    private static void getVoidRealm() {
        Rect2 r;
        r = Rect2.atLTWH(1560, 2560, 600, 600);
        getImageR("realm", "Screenshot_20181225-161727.png", r, "0");
        scaleImage("D:\\GitHub\\MyTosWikiFetcher\\Logos\\Output\\realm", 400);
    }

    private static void getV16Icons() {
        Rect2 r;
        r = Rect2.atXYWH(1991, 2817, 100, 100);
        getImageR("v16", "Screenshot_20190115-170235.png", r, "0");
        r = Rect2.atXYWH(2025, 1789, 155, 86);
        getImageR("v16", "Screenshot_20190115-170321.png", r, "1");
        r = Rect2.atXYWH( 135, 1789, 155, 86);
        getImageR("v16", "Screenshot_20190115-170333.png", r, "2");
    }

    private static void getStoryDragon() {
        Rect2 r;
        r = Rect2.atLTWH(1426, 660, 600, 600);
        getImageR("story", "Screenshot_20181204-033329.png", r, "0");
        scaleImage("D:\\GitHub\\MyTosWikiFetcher\\Logos\\Output\\story", 400);
    }

    private static void getEnochianTower() {
        Rect2 r;
        r = Rect2.atLTWH(680, 1480, 800, 800);
        getImageR("enochian", "Screenshot_20180915-225351.png", r, "0");
        r = Rect2.atLTWH(0, 700, 600, 600);
        getImageR("enochian", "Screenshot_20180915-225351.png", r, "1");
        r = Rect2.atLTWH(1050, 1200, 600, 600);
        getImageR("enochian", "Screenshot_20181120-191640.png", r, "2");

        scaleImage("D:\\GitHub\\MyTosWikiFetcher\\Logos\\Output\\enochian", 400);
    }

    private static void scaleImage(String folder, int wide) {
        TicTac2 t = new TicTac2();
        File src = new File(folder);
        File[] images = src.listFiles((dir, name) -> isImage(name));
        int n = images == null ? 0 : images.length;
        L.log("Scale %s images in %s", n, folder);

        if (n == 0) return;

        File dstFolder = new File(folder, "scaled");
        t.tic();
        for (int i = 0; i < n; i++) {
            File f = images[i];
            File dst = new File(dstFolder, f.getName());
            PngParam p = new PngParam(f);
            t.tic();
            PngResizer.from(p).longSide(wide).into(dst);
            t.tac("OK #%s -> %s", i, dst);
        }
        t.tac("All images scaled OK, see %s", n, dstFolder);
    }

    private static void getFriendPointsCards() {
        int sx = 191;
        int sy = 1491;
        int ny = 1885; // next y
        getFriendCards("friend", "Screenshot_20180816-145053.png", sx, sy, "a", 5);// x502 y1799 555
        getFriendCards("friend", "Screenshot_20180816-145053.png", sx, ny, "b", 5);// x502 y1799 555
        getFriendCards("friend", "Screenshot_20180817-014006.png", sx, sy, "c", 5);// x502 y1799 555
        getFriendCards("friend", "Screenshot_20180817-014006.png", sx, ny, "d", 5);// x502 y1799 555
    }

    // Diff the image in folder
    private static void diff() {
        int wide = 640;
        PngParam p = new PngParam("D:\\PMP_Android_Face\\Dal" + wide + "\\2013PowerStar6");
        PngDiffer.from(p).diff();
    }

    private static void scaleAllImage(String folder, String[] subs, int wide) {
        TicTac2 t = new TicTac2();
        String main = folder;
        String[] sub = subs;
        t.tic();
        for (int z = 0; z < sub.length; z++) {
            t.tic();
            // Crop icon
            File src = new File(main + "\\" + sub[z]);
            File[] images = src.listFiles((dir, name) -> {
                return isImage(name);
            });
            t.tac("List imgs in %s", src);

            printThem(images);
            final String dstFolder = main + "\\" + sub[z] + wide;
            if (images != null) {
                t.tic();

                for (int i = 0; i < images.length; i++) {
                    File f = images[i];
                    File dst = new File(dstFolder, f.getName());
                    PngParam p = new PngParam(f);
                    t.tic();
                    PngResizer.from(p).longSide(wide).into(dst);
                    t.tac("Image OK -> %s", dst);
                }
                t.tac("All %s images OK in %s", images.length, sub[z]);
            }
        }
        t.tac("All images OK in %s", main);
    }

    private static boolean isImage(String file) {
        int lastDot = file.lastIndexOf(".");
        if (lastDot < 0) return false;

        String ext = file.substring(lastDot);

        String[] mime = {".jpg", ".bmp", ".png"};
        for (String s : mime) {
            if (s.equalsIgnoreCase(ext)) {
                return true;
            }
        }
        return false;
    }

    // Conform all the images in folder with same long side
    private static void scale() {
        TicTac2 t = new TicTac2();
        String main = "D:\\PMP_Android_Face\\Dal";
        String[] sub = {"2013PowerStar6", "2012Year End Party"};
        t.tic();
        for (int z = 0; z < sub.length; z++) {
            t.tic();
            // Crop icon
            File src = new File(main + "\\" + sub[z]);
            File[] images = src.listFiles((dir, name) -> {
                int lastDot = name.lastIndexOf(".");
                return ".jpg".equalsIgnoreCase(name.substring(lastDot));
            });
            t.tac("List jpgs in %s", src);

            printThem(images);
            final int wide = 640;
            final String dstFolder = main + wide + "\\" + sub[z];
            if (images != null) {
                t.tic();
                for (int i = 0; i < images.length; i++) {
                    File f = images[i];
                    File dst = new File(dstFolder, f.getName());
                    PngParam p = new PngParam(f);
                    t.tic();
                    PngResizer.from(p).longSide(wide).into(dst);
                    t.tac("Image OK -> %s", dst);
                }
                t.tac("All %s images OK in %s", images.length, sub[z]);
            }
        }
        t.tac("All images OK in %s", main);
    }

    private static void getStones() {
        // https://github.com/tinghan33704/tos_simulator/tree/master/img
        getRunestone30("stone", "stones/20190301_124230.jpg", "a_");
        getRunestone30("stone", "stones/20190301_124310.jpg", "b_");
        getRunestone30("stone", "stones/20190301_124422.jpg", "c_");
        getRunestone30("stone", "stones/20190301_124527.jpg", "d_");
        getRunestone30("stone", "stones/20190301_124642.jpg", "e_");
        getRunestone30("stone", "stones/20190301_124740.jpg", "f_");
        getRunestone30("stone", "stones/20190301_124806.jpg", "g_");
    }

    private static void runDiffFolder() {
        diff("Logos/Output/diff");
    }

    private static void getRunestone30(String out, String src, String prefix) {
        int x0 = 0; // left most
        int y0 = 870; // top most
        int w = 180; // width
        int h = 180; // height

        // Creating 30 rects
        Rect2[] rs = new Rect2[30];
        int r = 6; // rows
        int c = 5; // column
        for (int i = 0; i < c; i++) {
            for (int j = 0; j < r; j++) {
                int k = r * i + j;
                rs[k] = Rect2.atLTWH(x0 + j * w, y0 + i * h, w, h);
            }
        }

        // get rect images
        for (int i = 0; i < rs.length; i++) {
            getImage(out, src, rs[i], prefix + "" + i);
        }
    }


    private static void getMainIcons() {
        getIcon("shop", "Screenshot_20180819-174853.png", 69, 869, "a", 6);
        Rect2 r = Rect2.atLTWH(684, 190, 190, 100);
        getImage("shop", "Screenshot_20180819-174853.png", r, "0");
        getImageR("shop", "Screenshot_20180819-174853.png", r, "0r");
    }

    private static void getCrafts() {
        Rect2 r;
        // Normal crafts
        r = Rect2.atLTWH(70, 1970, 340, 340);
        getImageR("crafts", "Screenshot_20180727-140815.png", r, "1");
        // Armed crafts,
        r = Rect2.atLTWH(70, 2390, 340, 340);
        getImageR("crafts", "Screenshot_20180727-140815.png", r, "2");
    }

    private static class RunInvokeMethod implements Runnable {

        @Override
        public void run() {
            x();
        }

        private void x() {
            y(1f, this::a);
            y(2f, this::b);
            y(3f, this::c);
        }

        private void a(float a1) {
            L.log("a = %s", a1);
        }
        private void b(float b1) {
            L.log("b = %s", b1);
        }
        private void c(float c1) {
            L.log("c = %s", c1);
        }

        private <T> void y(T t, Consumer<T> a) {
            a.accept(t);
        }
    }

    private static void getActiveIcons() {
        Rect2 r;
        // ARNA
        r = Rect2.atLTWH(328, 1590, 185, 80);
        getImage("skills", "skills/Screenshot_20180705-224752.png", r, "1");
        // Active
        r = Rect2.atLTWH(132, 2285, 250, 80);
        getImage("skills", "skills/Screenshot_20180705-224752.png", r, "2");
        // Leader
        r = Rect2.atLTWH(132, 3011, 250, 80);
        getImage("skills", "skills/Screenshot_20180705-224752.png", r, "3");
        // TRAD
        r = Rect2.atLTWH(133, 1585, 185, 80);
        getImage("skills", "skills/Screenshot_20180705-224743.png", r, "4");
        // Active
        r = Rect2.atLTWH(132, 2285, 250, 80);
        getImage("skills", "skills/Screenshot_20180705-224743.png", r, "5");
        // Leader
        r = Rect2.atLTWH(132, 3011, 250, 80);
        getImage("skills", "skills/Screenshot_20180705-224743.png", r, "6");
    }

    private static void getCrop() {
        Rect2 r;
        r = Rect2.atLTWH(0, 2048, 2160, 1080);
        getImage("play", "play/Screenshot_20180512-230227.png", r, "1");
        r = Rect2.atLTWH(0, 432, 2160, 3121);
        getImage("play", "play/Screenshot_20180616-185554.png", r, "2");
        r = Rect2.atLTWH(0, 3, 1024, 500);
        getImage("play", "play/play.png", r, "play");
    }

    private static void getPrizeIcons() {
        getIcon("prize", "Screenshot_20180603-022221.png", 69, 1012, "a", 5);
        getIcon("prize", "Screenshot_20180603-022305.png", 69,  881, "b", 5);
        getIcon("prize", "Screenshot_20180603-022326.png", 69,  855, "c", 5);
        getIcon("prize", "Screenshot_20180603-022349.png", 69,  873, "d", 5);
        getIcon("prize", "Screenshot_20180603-022359.png", 69,  867, "e", 5);
        getIcon("prize", "Screenshot_20180603-022429.png", 69,  880, "f", 5);
    }

    private static void getPlusMinus() {
        String folder = "sign";
        String src = "Screenshot_20180602-131106.png";
        Rect2[] rects = new Rect2[2];
        String prefix = "";

        final int sx = 1393, sy = 1689;
        rects[0] = Rect2.atLTWH(sx, sy, 187, 187);
        rects[1] = Rect2.atLTWH(sx, sy + 400, 187, 187);

        for (int i = 0; i < 2; i++) {
            getImageR(folder, src, rects[i], prefix + "" + i + "");
        }
    }

    // Get images of Mission Club
    private static void getMissions() {
        getMission("mission", "Screenshot_20180602-012851.png", "a", 308);
        getMission("mission", "Screenshot_20180604-022215.png", "b", 291);
        getMission("mission", "Screenshot_20180604-022645.png", "c", 340);
        getMission("mission", "Screenshot_20180604-232548.png", "d", 340);
        getMission("mission", "Screenshot_20180604-232619.png", "e", 340);
    }

    private static void getMission(String folder, String src, String prefix, int w0) {
        final int w = 346;
        final int h = 346;
        Rect2 center = Rect2.atLTWH(913, 1807, w0, w0);
        Rect2 r0 = Rect2.atLTWH(131, 1041, w, h);
        List<Rect2> rects = createRects(5, r0, 388, 0);
        rects.add(0, center);
        for (int i = 0; i < rects.size(); i++) {
            getImageR(folder, src, rects.get(i), prefix + "" + i);
        }
    }

    // New version for number images
    private static void getNums() {
        getNumbers("nums", "nums/Screenshot_20180601-005206.png", 95, 853, "a", 6);
        getNumbers("nums", "nums/Screenshot_20180601-005257.png", 95, 869, "b", 6);
        getNumbers("nums", "nums/Screenshot_20180602-133446.png", 95, 867, "c", 6);
        getNumbers("nums", "nums/Screenshot_20180627-014602.png", 95, 863, "d", 6);
        getNumbers("nums", "nums/Screenshot_20180627-014613.png", 95, 867, "e", 6);
        getNumbers("nums", "nums/Screenshot_20180627-014738.png", 95, 869, "f", 6);
        getNumbers("nums", "nums/Screenshot_20180627-014749.png", 95, 877, "g", 6);
        getNumbers("nums", "nums/Screenshot_20180627-014800.png", 95, 879, "h", 6);
        getNumbers("nums", "nums/Screenshot_20180627-014811.png", 95, 863, "i", 6);
        getNumbers("nums", "nums/Screenshot_20180627-014820.png", 95, 873, "j", 6);
        getNumbers("nums", "nums/Screenshot_20180627-014832.png", 95, 873, "k", 6);
        getNumbers("nums", "nums/Screenshot_20180627-014952.png", 95, 883, "l", 6);
        getNumbers("nums", "nums/Screenshot_20180627-015003.png", 95, 887, "m", 6);
        getNumbers("nums", "nums/Screenshot_20180627-015013.png", 95, 867, "n", 6);
        getNumbers("nums", "nums/Screenshot_20180627-015022.png", 95, 867, "o", 6);
        getNumbers("nums", "nums/Screenshot_20180627-015037.png", 95, 863, "p", 6);
        getNumbers("nums", "nums/Screenshot_20180627-015051.png", 95, 869, "q", 6);
        getNumbers("nums", "nums/Screenshot_20180627-015445.png", 95, 879, "r", 6);
        getNumbers("nums", "nums/Screenshot_20180627-015455.png", 95, 879, "s", 6);
        getNumbers("nums", "nums/Screenshot_20180627-015509.png", 95, 863, "t", 6);
        getNumbers("nums", "nums/Screenshot_20180627-015518.png", 95, 863, "u", 6);
        getNumbers("nums", "nums/Screenshot_20180627-015527.png", 95, 867, "v", 6);
    }

    private static void getMonsterEat() {
        Rect2 r = Rect2.atXYWH(1240, 2700, 320, 320);
        getImage("eatMonster", "Screenshot_20180531-015212.png", r, "0");
    }

    // Get the event icons & number
    private static void getLogos() {
        getIcon("icons", "Screenshot_20180517-010049.png", 69, 894, "a", 6);
        getIcon("icons", "Screenshot_20180517-010106.png", 69, 874, "b", 6);
        getIcon("icons", "Screenshot_20180604-022155.png", 69, 857, "c", 6);
        getNumbers("num", "Screenshot_20180508-014834.png", 95, 1102, "a", 5);
        getNumbers("num", "Screenshot_20180508-014849.png", 95, 1052, "b", 5);
        getNumbers("num", "Screenshot_20180508-014901.png", 95, 1137, "c", 5);
        getNumbers("num", "Screenshot_20180508-014917.png", 95, 1109, "d", 5);
        getNumbers("num", "Screenshot_20180508-014933.png", 95, 879, "e", 6);
        getNumbers("num", "Screenshot_20180514-020822.png", 95, 869, "f", 6);
        getNumbers("num", "Screenshot_20180517-142710.png", 95, 879, "g", 6);
        getBig("Big", "Screenshot_20180521-095833.png", 12, 1337, 1);
    }

    private static void getIcon(String folder, String src, int sx, int sy, String prefix, int cardN) {
        final int w = 340;
        final int dy = 422;

        // Creating rects
        Rect2 r0 = Rect2.atLTWH(sx, sy, w, w);
        List<Rect2> rs = createRects(cardN, r0, 0, dy);
        // get Rounded image
        for (int i = 0; i < rs.size(); i++) {
            getImageR(folder, src, rs.get(i), prefix + "" + i);
        }
    }

    private static void getFriendCards(String folder, String src, int sx, int sy, String prefix, int cardN) {
        final int w = 310;
        final int dx = 365;

        // Creating rects
        Rect2 r0 = Rect2.atLTWH(sx, sy, w, w);
        List<Rect2> rs = createRects(cardN, r0, dx, 0);
        // get Rounded image
        for (int i = 0; i < rs.size(); i++) {
            getImageR(folder, src, rs.get(i), prefix + "" + i);
        }
    }

    private static void getBig(String folder, String src, int sx, int sy, int cardN) {
        final int w = 820;
        // Creating rects
        Rect2 r0 = Rect2.atLTWH(sx, sy, w, w);
        getImageR(folder, src, r0, "0");
    }

    private static void getNumbers(String folder, String src, int sx, int sy, String prefix, int cardN) {
        final int w = 325;
        final int dy = 422;

        // Creating rects
        Rect2 r0 = Rect2.atLTWH(sx, sy, w, w);
        List<Rect2> rs = createRects(cardN, r0, 0, dy);
        // get Rounded image
        for (int i = 0; i < rs.size(); i++) {
            getImageR(folder, src, rs.get(i), prefix + "" + i);
        }
    }

    /**
     * Returns the cropped image
     * @param folder saved file at "Logos/Output/" + folder
     * @param src image source from "Logos/Source/" + src
     * @param rect crop src image at specific range
     * @param dst file output as (dst + ".png") in folder
     */
    private static void getImage(String folder, String src, Rect2 rect, String dst) {
        int w = rect.width();
        int h = rect.height();
        final String base = "Logos/Output/" + folder;
        String name = base + "/" + dst + ".png";

        // Crop icon
        PngParam p = new PngParam("Logos/Source/" + src).size(w, h);
        PngCreator.from(p).copy(rect).into(name);
        L.log("created %s", name);
    }

    /**
     * Get the rounded cropped image
     * parameters are same as {@link #getImage(String, String, Rect2, String)}
     * @see #getImage(String, String, Rect2, String)
     */
    private static void getImageR(String folder, String src, Rect2 rect, String dst) {
        int w = rect.width();
        int h = rect.height();
        final String base = "Logos/Output/" + folder;
        String name = base + "/" + dst + ".png";

        // Crop icon
        PngParam p = new PngParam("Logos/Source/" + src).size(w, h);
        PngCreator.from(p).copy(rect).eraseCorners().into(name);
        L.log("created %s", name);
    }

    private static void diff(String folder) {
        // Diff folder
        PngParam dp = new PngParam(folder);
        PngDiffer.from(dp).diff();
    }

    private static List<Rect2> createRects(int N, Rect2 srcRect, int dx, int dy) {
        // Creating rects
        List<Rect2> list = new ArrayList<>();
        list.add(srcRect);
        for (int i = 1; i < N; i++) {
            Rect2 r = list.get(i - 1);
            list.add(Rect2.atLTWH(r.left + dx, r.top + dy, r.width(), r.height()));
        }
        return list;
    }

    private static void file() {
        String name = "C:/Users/Flyingkite/Desktop/sus";
        File folder = new File(name);
        File[] child = folder.listFiles();
        List<Set<String>> allSets = new ArrayList<>();
        if (child != null) {
            for (File f : child) {
                Scanner fin = null;
                try {
                    fin = new Scanner(f);

                    Set<String> tags = new HashSet<>();
                    String line;
                    String[] lines;
                    tt.tic();
                    while (fin.hasNextLine()) {
                        line = fin.nextLine();
                        lines = line.split(" ");
                        if (lines.length >= 3) {
                            String tag = lines[2];
                            if (!TextUtil.isEmpty(tag)) {
                                tags.add(tag);
                            }
                        }
                    }
                    tt.tac("Done : %s", f);

                    allSets.add(tags);

                    printThem(tags);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    IOUtil.closeIt(fin);
                }
            }
            Set<String> both = new HashSet<>(allSets.get(0));
            both.retainAll(allSets.get(1));
            L.log(" Ended -----");
            printThem(both);
        }
    }

    private static <T> void printThem(Collection<T> c) {
        L.log("Collection size = %s", c == null ? "N/A" : c.size());
        if (c != null) {
            for (T t : c) {
                L.log(" -> %s", t);
            }
        }
    }

    private static <T> void printThem(T[] a) {
        L.log("Array size = %s", a == null ? "N/A" : a.length);
        if (a != null) {
            for (T t : a) {
                L.log(" -> %s", t);
            }
        }
    }

    private static void google() {
        String link = "http://www.google.com";
        //link = "https://m.facebook.com/friends/center/requests/outgoing/#friends_center_main";
        // https://m.facebook.com/friends/center/requests/outgoing/

        try {
            tt.tic();
            Connection.Response r = Jsoup.connect(link).execute();
            tt.tac("google OK");
            System.out.println("r = " + r.body());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}