package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import flyingkite.data.Rect2;
import flyingkite.javaxlibrary.images.base.PngParam;
import flyingkite.javaxlibrary.images.create.PngCreator;
import flyingkite.javaxlibrary.images.diff.PngDiffer;
import flyingkite.javaxlibrary.images.resize.PngResizer;
import flyingkite.log.L;
import flyingkite.log.LF;
import flyingkite.math.ChiSquarePearson;
import flyingkite.math.ChiSquareTable;
import flyingkite.math.DiscreteSample;
import flyingkite.math.Statistics;
import flyingkite.tool.GsonUtil;
import flyingkite.tool.IOUtil;
import flyingkite.tool.TextUtil;
import flyingkite.tool.TicTac2;
import main.card.TosCard;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

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
        //loadCardsCSV();
        getEnochianTower();
        //scaleImage("D:\\GitHub\\MyTosWikiFetcher\\Logos\\Output\\enochian", 400);
        //scaleAllImage("D:\\GitHub\\MyTosWiki\\app\\src\\main\\res\\drawable-xxxhdpi", new String[]{"x"}, 100);
        tt.tac("Done");
    }

    private static void loadCardsCSV() {
        TicTac2 clk = new TicTac2();
        Gson mGson = new Gson();

        File fc = new File("myCard", "cardList.json");
        // Try to parsing back to know its performance
        clk.tic();
        TosCard[] allCards = GsonUtil.loadFile(fc, TosCard[].class);
        clk.tac("%s cards loaded", allCards.length);

        // Put cards as map
        Map<String, TosCard> map = new HashMap<>();
        for (TosCard c : allCards) {
            if (map.containsKey(c.idNorm)) {
                L.log("X_X Duplicate on %s", c.idNorm);
            }
            map.put(c.idNorm, c);
        }

        /*
        for (TosCard c : allCards) {
            TosCard d = map.get(c.idNorm);
            if (d == null) {
                L.log("Missing card %s", c.idNorm);
            }
            if (d != null) {
                boolean has0257 = false;
                for (Evolve ev : d.evolveInfo) {
                    for (String s : ev.evolveNeed) {
                        if ("0257".equals(s)) {
                            has0257 = true;
                            L.log("Here found ev = %s", ev);
                        }
                    }
                }
                if (has0257) {
                    L.log("Card id = %s", c.idNorm);
                }
            }
        }
        */

        // Find cards
        findCards("將場上的符石變回原始模樣", allCards);
        //findCards("傷害減少", allCards);
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

    private static void findCards(String key, TosCard[] allCards) {
        int sn = 0, tn = 0, un = 0;

        for (TosCard c : allCards) {
            String s = c.skillDesc1 + " & " + c.skillDesc2;
            String t = c.cardDetails;
            String u = c.skillLeaderDesc;
            if (s.contains(key)) {
                L.log(sc(c));
                sn++;
            }
            if (t.contains(key)) {
                L.log("detail\n" + sc(c));
                tn++;
            }
            if (u.contains(key)) {
                L.log("Leader\n" + sc(c));
                un++;
            }
            //L.log(sc(c));
        }

        L.log("%s in skill, %s in detail, %s in leader, key = %s", sn, tn, un, key);
    }

    private static String sc(TosCard c) {
        return "#" + c.idNorm + "," + c.name
            + "\n      " + c.skillDesc1 + "," + c.skillDesc2
            + "\n      " + c.skillLeaderDesc
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

    private static void getEnochianTower() {
        Rect2 r;
        r = Rect2.atLTWH(680, 1480, 800, 800);
        getImageR("enochian", "Screenshot_20180915-225351.png", r, "0");
        r = Rect2.atLTWH(0, 700, 600, 600);
        getImageR("enochian", "Screenshot_20180915-225351.png", r, "1");

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

class Jsoner {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static void json() {
        toFile("bw06.json", getBW6());
        toFile("hdr_dreamy.json", getHdrDreamy());
        toFile("hdr_highlight.json", getHdrHightlight());
        toFile("hdr_realistic.json", getHdrRealistic());
    }

    public static void toFile(String filename, Object src) {
        LF mLf = new LF("myJson", filename);
        mLf.getFile().open(false);
        mLf.log(gson.toJson(src));
        mLf.getFile().close();
    }

    static class BW6 {
        String guid;
        Map<String, String> name = new TreeMap<>();
        String version;
        List<Extras> extras = new ArrayList<>();
        List<Map<String, String>> body = new ArrayList<>();
    }
    static class Extras {
        String os;
        String minAppVersion;
    }
    static class HdrDreamy extends BW6 {
    }
    static class HdrHightlight extends BW6 {
    }
    static class HdrRealistic extends BW6 {
    }

    private static HdrRealistic getHdrRealistic() {
        HdrRealistic b = new HdrRealistic();
        b.guid = "fa625752-7816-4075-add3-fbddcd00493b";
        b.version = "6.0";

        b.name.put("def", "Realistic");
        b.name.put("chs", "现实");
        b.name.put("cht", "擬真");
        b.name.put("deu", "Realistisch");
        b.name.put("enu", "Realistic");
        b.name.put("esp", "Realista");
        b.name.put("fra", "Réaliste");
        b.name.put("ita", "Realistico");
        b.name.put("jpn", "リアリスティック");
        b.name.put("kor", "사실적");
        b.name.put("plk", "Realistyczny");
        b.name.put("ptb", "Realista");
        b.name.put("ptg", "Realístico");
        b.name.put("rus", "Реалистичный");

        Extras e;
        e = new Extras();
        e.os = "Android";
        e.minAppVersion = "1.2.0";
        b.extras.add(e);
        e = new Extras();
        e.os = "iOS";
        e.minAppVersion = "1.2.0";
        b.extras.add(e);

        Map<String, String> m;
        m = new TreeMap<>();
        m.put("name", "GPUImageHighlightShadowFilter");
        m.put("highlight", "0.9178");
        m.put("shadow", "0.2857");
        b.body.add(m);

        m = new TreeMap<>();
        m.put("name", "GPUImageContrastFilter");
        m.put("intensity", "1.1214");
        b.body.add(m);

        m = new TreeMap<>();
        m.put("name", "CLHdrGlowFilter");
        m.put("glow", "0.5392");
        m.put("radius", "0.4678");
        m.put("balance", "0.5");
        b.body.add(m);

        m = new TreeMap<>();
        m.put("name", "CLHdrEdgeFilter");
        m.put("edge", "0.55");
        m.put("radius", "0.5392");
        m.put("balance", "0.5");
        b.body.add(m);

        m = new TreeMap<>();
        m.put("name", "GPUImageClarityFilter");
        m.put("intensity", "-0.10720003");
        m.put("radius", "144.789");
        b.body.add(m);
        return b;
    }

    private static HdrHightlight getHdrHightlight() {
        HdrHightlight b = new HdrHightlight();
        b.guid = "dcbfd8db-3319-495c-bbdc-573180cf8b12";
        b.version = "6.0";

        b.name.put("def", "Highlight");
        b.name.put("chs", "高光");
        b.name.put("cht", "加亮");
        b.name.put("deu", "Glanzlicht");
        b.name.put("enu", "Highlight");
        b.name.put("esp", "Reflejo");
        b.name.put("fra", "Surbrillance");
        b.name.put("ita", "Luci");
        b.name.put("jpn", "ハイライト");
        b.name.put("kor", "강조");
        b.name.put("plk", "Podświetlony");
        b.name.put("ptb", "Realçar");
        b.name.put("ptg", "Destaque");
        b.name.put("rus", "Выделение");

        Extras e;
        e = new Extras();
        e.os = "Android";
        e.minAppVersion = "1.2.0";
        b.extras.add(e);
        e = new Extras();
        e.os = "iOS";
        e.minAppVersion = "1.2.0";
        b.extras.add(e);

        Map<String, String> m;
        m = new TreeMap<>();
        m.put("name", "GPUImageWhiteBalanceFilter");
        m.put("temperature", "8000.0");
        m.put("tint", "0.0");
        b.body.add(m);

        m = new TreeMap<>();
        m.put("name", "GPUImageHighlightShadowFilter");
        m.put("highlight", "0.9821");
        m.put("shadow", "0.6785");
        b.body.add(m);

        m = new TreeMap<>();
        m.put("name", "GPUImageContrastFilter");
        m.put("intensity", "1.4214");
        b.body.add(m);

        m = new TreeMap<>();
        m.put("name", "CLHdrGlowFilter");
        m.put("glow", "0.6214");
        m.put("radius", "0.3607");
        m.put("balance", "0.5");
        b.body.add(m);

        m = new TreeMap<>();
        m.put("name", "CLHdrEdgeFilter");
        m.put("edge", "0.275");
        m.put("radius", "0.4607");
        m.put("balance", "0.5");
        b.body.add(m);

        m = new TreeMap<>();
        m.put("name", "GPUImageHSVExFilter");
        m.put("hue_red", "0.0");
        m.put("hue_orange", "0.0");
        m.put("hue_yellow", "0.0");
        m.put("hue_green", "0.0");
        m.put("hue_aqua", "0.0");
        m.put("hue_blue", "0.0");
        m.put("hue_purple", "0.0");
        m.put("hue_magenta", "0.0");
        m.put("saturation_red", "0.0");
        m.put("saturation_orange", "0.0");
        m.put("saturation_yellow", "0.0");
        m.put("saturation_green", "0.0");
        m.put("saturation_aqua", "0.0");
        m.put("saturation_blue", "0.5142");
        m.put("saturation_purple", "0.0");
        m.put("saturation_magenta", "0.0");
        m.put("lightness_red", "0.0");
        m.put("lightness_orange", "0.0");
        m.put("lightness_yellow", "0.0");
        m.put("lightness_green", "0.0");
        m.put("lightness_aqua", "0.0");
        m.put("lightness_blue", "0.0");
        m.put("lightness_purple", "0.0");
        m.put("lightness_magenta", "0.0");
        b.body.add(m);
        return b;
    }

    private static HdrDreamy getHdrDreamy() {
        HdrDreamy b = new HdrDreamy();
        b.guid = "b55f0be6-2dfd-4047-8b9e-6c549d1205bc";
        b.version = "6.0";

        b.name.put("def", "Dreamy");
        b.name.put("chs", "梦幻");
        b.name.put("cht", "夢幻");
        b.name.put("deu", "Verträumt");
        b.name.put("enu", "Dreamy");
        b.name.put("esp", "Soñador");
        b.name.put("fra", "Onirique");
        b.name.put("ita", "Irreale");
        b.name.put("jpn", "ドリーミー");
        b.name.put("kor", "몽환적");
        b.name.put("plk", "Senny");
        b.name.put("ptb", "Sonhador");
        b.name.put("ptg", "Sonho");
        b.name.put("rus", "Мечтательный");

        Extras e;
        e = new Extras();
        e.os = "Android";
        e.minAppVersion = "1.2.0";
        b.extras.add(e);
        e = new Extras();
        e.os = "iOS";
        e.minAppVersion = "1.2.0";
        b.extras.add(e);

        Map<String, String> m;
        m = new TreeMap<>();
        m.put("name", "GPUImageWhiteBalanceFilter");
        m.put("temperature", "6071.0");
        m.put("tint", "32.14");
        b.body.add(m);

        m = new TreeMap<>();
        m.put("name", "GPUImageContrastFilter");
        m.put("intensity", "0.757");
        b.body.add(m);

        m = new TreeMap<>();
        m.put("name", "CLHdrGlowFilter");
        m.put("glow", "1.0");
        m.put("radius", "0.2928");
        m.put("balance", "1.0");
        b.body.add(m);

        m = new TreeMap<>();
        m.put("name", "CLHdrEdgeFilter");
        m.put("edge", "0.4928");
        m.put("radius", "0.4857");
        m.put("balance", "0.5");
        b.body.add(m);

        m = new TreeMap<>();
        m.put("name", "GPUImageClarityFilter");
        m.put("intensity", "-0.10720003");
        m.put("radius", "144.789");
        b.body.add(m);
        return b;
    }

    private static BW6 getBW6() {
        BW6 b = new BW6();
        b.guid = "3ee6c191-47cc-4ea4-b229-8e4037462a7e";
        b.version = "6.0";

        b.name.put("def", "B&amp;06");
        b.name.put("chs", "黑白-06");
        b.name.put("cht", "黑白-06");
        b.name.put("deu", "SW-06");
        b.name.put("enu", "B&amp;W-06");
        b.name.put("esp", "B y N-06");
        b.name.put("fra", "NB-06");
        b.name.put("ita", "B/N-06");
        b.name.put("jpn", "白黒-06");
        b.name.put("kor", "흑백-06");
        b.name.put("plk", "Czarno-białe-06");
        b.name.put("ptb", "P&amp;B-06");
        b.name.put("ptg", "P/B-06");
        b.name.put("rus", "Ч&amp;Б-06");

        Extras e;
        e = new Extras();
        e.os = "Android";
        e.minAppVersion = "1.2.0";
        b.extras.add(e);
        e = new Extras();
        e.os = "iOS";
        e.minAppVersion = "1.2.0";
        b.extras.add(e);

        Map<String, String> m;
        m = new TreeMap<>();
        m.put("name", "GPUImageHighlightShadowFilter");
        m.put("highlight", "0.0");
        m.put("shadow", "0.0");
        b.body.add(m);

        m = new TreeMap<>();
        m.put("name", "GPUImageBrightnessFilter");
        m.put("intensity", "0.004600048");
        b.body.add(m);

        m = new TreeMap<>();
        m.put("name", "GPUImageContrastFilter");
        m.put("intensity", "1.7046");
        b.body.add(m);

        m = new TreeMap<>();
        m.put("name", "GPUImageClarityFilter");
        m.put("intensity", "-0.028599977");
        m.put("radius", "55.233");
        b.body.add(m);

        m = new TreeMap<>();
        m.put("name", "GPUImageSaturationFilter");
        m.put("intensity", "0.0");
        b.body.add(m);

        m = new TreeMap<>();
        m.put("name", "GPUImageToneCurveRGBFilter");
        m.put("curve", "[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 4.987512E-4, 0.0012073386, 0.0019524393, 0.0027346828, 0.0035546985, 0.0044131163, 0.0053105652, 0.0062476755, 0.0072250757, 0.008243397, 0.009303266, 0.010405316, 0.011550174, 0.01273847, 0.013970833, 0.015247894, 0.016570283, 0.017938627, 0.019353557, 0.020815702, 0.022325693, 0.023884157, 0.025491726, 0.02714903, 0.028856695, 0.030615354, 0.032425635, 0.034288164, 0.036203578, 0.038172502, 0.04019557, 0.042273402, 0.044406638, 0.0465959, 0.04884182, 0.051145032, 0.05350616, 0.05592583, 0.058404684, 0.06094334, 0.06354243, 0.06620256, 0.0689238, 0.07170578, 0.07454812, 0.07745042, 0.0804123, 0.083433375, 0.08651326, 0.089651555, 0.09284788, 0.096101865, 0.0994131, 0.10278121, 0.1062058, 0.10968649, 0.11322288, 0.116814606, 0.12046127, 0.12416248, 0.12791786, 0.13172701, 0.13558955, 0.13950509, 0.14347325, 0.14749365, 0.15156588, 0.15568955, 0.1598643, 0.16408975, 0.16836548, 0.17269112, 0.17706628, 0.18149057, 0.1859636, 0.190485, 0.19505438, 0.19967134, 0.2043355, 0.20904647, 0.21380387, 0.2186073, 0.2234564, 0.22835074, 0.23328997, 0.2382737, 0.24330153, 0.24837308, 0.25348794, 0.25864577, 0.26384616, 0.2690887, 0.27437302, 0.27969876, 0.28506547, 0.29047284, 0.29592043, 0.30140787, 0.30693477, 0.31250077, 0.31810543, 0.3237484, 0.32942927, 0.33514768, 0.34090325, 0.34669554, 0.35252422, 0.35838887, 0.3642891, 0.37022457, 0.37619483, 0.38219956, 0.38823828, 0.3943107, 0.40041637, 0.40655494, 0.412726, 0.4189292, 0.42516407, 0.4314303, 0.43772748, 0.44405523, 0.45041317, 0.45680088, 0.463218, 0.46966413, 0.4761389, 0.4826419, 0.48917276, 0.4957311, 0.50231653, 0.5089286, 0.515567, 0.52223134, 0.52892125, 0.53563625, 0.54237604, 0.54914016, 0.5559283, 0.56274, 0.569575, 0.57643276, 0.583313, 0.59021527, 0.5971392, 0.60408443, 0.6110505, 0.6180371, 0.62504387, 0.6320703, 0.6391161, 0.64618087, 0.65326416, 0.6603657, 0.667485, 0.6746217, 0.68177545, 0.68894583, 0.6961325, 0.7033349, 0.71055293, 0.71778595, 0.7250337, 0.7322958, 0.73957175, 0.74686134, 0.754164, 0.76147944, 0.7688073, 0.7761471, 0.7834986, 0.79086125, 0.7982347, 0.80561864, 0.81301266, 0.82041633, 0.82782924, 0.8352511, 0.84268147, 0.85011995, 0.8575662, 0.86501974, 0.8724803, 0.8799474, 0.8874207, 0.8948998, 0.90238434, 0.9098739, 0.91736805, 0.92486656, 0.9323689, 0.93987465, 0.9473836, 0.95489514, 0.9624091, 0.9699249, 0.9774424, 0.9849609, 0.9924803]");
        b.body.add(m);

        return b;
    }
}