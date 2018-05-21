package main;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import util.data.Rect2;
import util.images.base.PngParam;
import util.images.create.PngCreator;
import util.images.diff.PngDiffer;
import util.logging.L;
import util.tool.IOUtil;
import util.tool.TextUtil;
import util.tool.TicTac2;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ASD {
    private static final TicTac2 tt = new TicTac2();

    public static void run() {
        tt.reset();
        tt.tic();
        //file();
        a();
        b();
        get_1_5();
        get_6_9();
        get_10_14();
        get_15_19();
        get_18_22();
        get_23_28();
        get_25_30();
        getBig("Big", "Screenshot_20180521-095833.png", 12, 1337, 1);
        tt.tac("Done");
    }

    private static void a() {
        getIcon("a", "Screenshot_20180517-010049.png", 69, 894);
    }

    private static void b() {
        getIcon("b", "Screenshot_20180517-010106.png", 69, 874);
    }

    private static void getIcon(String folder, String imageName, int sx, int sy) {
        final String base = "Logos\\Output\\" + folder;

        // Crop icon
        PngParam p = new PngParam("Logos\\Source\\" + imageName)
                .size(340, 340)
                ;
        Rect2 r = Rect2.ofSize(340, 340);

        final int x = sx, y = sy, dy = 422;
        //r.offsetTo(69, 874); //獎賞 , V
        r.offsetTo(x, y); //競技場 (68, 1297)
        //r.offsetTo(69, 1718); //我的禮包
        PngCreator.from(p).copy(r).eraseCorners()
                .into(base + "\\n.png");

        for (int i = 0; i < 6; i++) {
            String name = base + "\\New folder\\" + i + ".png";
            r.offsetTo(x, y + dy * i);
            PngCreator.from(p).copy(r).eraseCorners()
                    .into(name);
            L.log("created %s", name);
        }

        // Diff folder
        PngParam dp = new PngParam(base);
        PngDiffer.from(dp).diff();
    }

    private static void getBig(String folder, String imageName, int sx, int sy, int cardN) {
        final String base = "Logos\\Output\\" + folder;

        // Crop icon
        PngParam p = new PngParam("Logos\\Source\\" + imageName)
                .size(820, 820)
                ;
        Rect2 r = Rect2.ofSize(820, 820);
        final int x = sx, y = sy, dy = 422;
        r.offsetTo(x, y);
        PngCreator.from(p).copy(r).eraseCorners()
                .into(base + "\\n.png");

        for (int i = 0; i < cardN; i++) {
            String name = base + "\\New folder\\" + i + ".png";
            r.offsetTo(x, y + dy * i);
            PngCreator.from(p).copy(r).eraseCorners()
                    .into(name);
            L.log("created %s", name);
        }

        // Diff folder
        PngParam dp = new PngParam(base);
        PngDiffer.from(dp).diff();
    }

    private static void get_1_5() {
        get_x_y("num", "Screenshot_20180508-014834.png", 95, 1102, 5);
    }

    private static void get_6_9() {
        get_x_y("num2", "Screenshot_20180508-014849.png", 95, 1052, 5);
    }

    private static void get_10_14() {
        get_x_y("num3", "Screenshot_20180508-014901.png", 95, 1137, 5);
    }

    private static void get_15_19() {
        get_x_y("num4", "Screenshot_20180508-014917.png", 95, 1109, 5);
    }

    private static void get_18_22() {
        get_x_y("num5", "Screenshot_20180508-014933.png", 95, 879, 6);
    }

    private static void get_23_28() {
        get_x_y("num6", "Screenshot_20180514-020822.png", 95, 869, 6);
    }

    private static void get_25_30() {
        get_x_y("num7", "Screenshot_20180517-142710.png", 95, 879, 6);
    }

    private static void get_x_y(String folder, String imageName, int sx, int sy, int cardN) {
        final String base = "Logos\\Output\\" + folder;

        // Crop icon
        PngParam p = new PngParam("Logos\\Source\\" + imageName)
                .size(325, 325)
                ;
        Rect2 r = Rect2.ofSize(325, 325);
        final int x = sx, y = sy, dy = 422;
        r.offsetTo(x, y);
        PngCreator.from(p).copy(r).eraseCorners()
                .into(base + "\\n.png");

        for (int i = 0; i < cardN; i++) {
            String name = base + "\\New folder\\" + i + ".png";
            r.offsetTo(x, y + dy * i);
            PngCreator.from(p).copy(r).eraseCorners()
                    .into(name);
            L.log("created %s", name);
        }

        // Diff folder
        PngParam dp = new PngParam(base);
        PngDiffer.from(dp).diff();
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

                    printSet(tags);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    IOUtil.closeIt(fin);
                }
            }
            Set<String> both = new HashSet<>(allSets.get(0));
            both.retainAll(allSets.get(1));
            L.log(" Ended -----");
            printSet(both);
        }
    }

    private static <T> void printSet(Set<T> set) {
        L.log("size = %s", set.size());
        for (T s : set) {
            L.log(" -> %s", s);
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
