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
        tt.tac("Done");
    }

    private static void a() {
        // Crop icon
        PngParam p = new PngParam("Logos\\Source\\Screenshot_20180517-010049.png")
                .size(340, 340)
                ;
        Rect2 r = Rect2.ofSize(340, 340);

        //r.offsetTo(69, 894); //獎賞 , V
        r.offsetTo(69, 1317); //累積簽到 (68, 1316)
        //r.offsetTo(69, 1740); //我的禮包
        PngCreator.from(p).copy(r).eraseCorners()
                .into("Logos\\Output\\a\\n.png");

        for (int i = 0; i < 6; i++) {
            String name = "Logos\\Output\\a\\New folder\\" + i + ".png";
            r.offsetTo(69, 894 + 422 * i);
            PngCreator.from(p).copy(r).eraseCorners()
                    .into(name);
            L.log("created %s", name);
        }

        // Diff folder
        PngParam dp = new PngParam("Logos\\Output\\a");
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
