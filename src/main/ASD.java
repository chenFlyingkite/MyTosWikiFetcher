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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class ASD {
    private static final TicTac2 tt = new TicTac2();

    public static void run() {
        tt.reset();
        tt.tic();
        //getLogos();
        //getPlusMinus();
        //getMissions();
        //getNums();
        //getMonsterEat();
        //getPrizeIcons();
        getCrop();
        tt.tac("Done");
    }

    private static void getCrop() {
        Rect2 r;
        r = Rect2.atLTWH(0, 2048, 2160, 1080);
        getImage("play", "play//Screenshot_20180512-230227.png", r, "1");
        r = Rect2.atLTWH(0, 432, 2160, 3121);
        getImage("play", "play//Screenshot_20180616-185554.png", r, "2");
        r = Rect2.atLTWH(0, 3, 1024, 500);
        getImage("play", "play//play.png", r, "play");
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
        getNumbers("nums", "nums\\Screenshot_20180601-005206.png", 95, 853, "a", 6);
        getNumbers("nums", "nums\\Screenshot_20180601-005257.png", 95, 869, "b", 6);
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
     * @param folder saved file at "Logos\\Output\\" + folder
     * @param src image source from "Logos\\Source\\" + src
     * @param rect crop src image at specific range
     * @param dst file output as (dst + ".png") in folder
     */
    private static void getImage(String folder, String src, Rect2 rect, String dst) {
        int w = rect.width();
        int h = rect.height();
        final String base = "Logos\\Output\\" + folder;
        String name = base + "\\" + dst + ".png";

        // Crop icon
        PngParam p = new PngParam("Logos\\Source\\" + src).size(w, h);
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
        final String base = "Logos\\Output\\" + folder;
        String name = base + "\\" + dst + ".png";

        // Crop icon
        PngParam p = new PngParam("Logos\\Source\\" + src).size(w, h);
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
