package main;

import flyingkite.files.FileUtil;
import flyingkite.log.L;
import flyingkite.tool.IOUtil;
import flyingkite.tool.TicTac2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

public class MyTosWikiFirebase {
    private static final TicTac2 clk = new TicTac2();

    public static void run() {
        clk.tic();
        genFarmPool();
        genTosEvent();
        genStageMemo();
        clk.tac("genTosEvent OK");
    }

    private static void genFarmPool() {
        genLineAndPrint("..\\MyTosWiki\\playstore\\farm", "farm.txt");
    }

    private static void genTosEvent() {
        genLineAndPrint("..\\MyTosWiki\\playstore\\tos", "tosEvent.txt");
    }

    private static void genStageMemo() {
        genLineAndPrint("..\\MyTosWiki\\playstore\\stage memo", "memo.txt");
    }

    private static void genLineAndPrint(String folder, String name) {
        String gen = genLines(folder, name);
        printFile(gen);
        new File(gen).delete();
    }

    private static String genLines(String folder, String name) {
        File src = new File(folder, name);
        File dst = new File(folder, "z" + name);
        printNewLines(src, dst);
        return dst.getPath();
    }

    private static void printFile(String path) {
        List<String> data = FileUtil.readFromFile(path);
        if (data != null) {
            L.log("File : %s", path);
            for (String s : data) {
                L.log(s);
            }
            L.log("File Ended : %s", path);
        }
    }

    /** For the src file text,
     * Output the dst file as each line + "\n"
     */
    private static void printNewLines(File src, File dst) {
        Scanner fis = null;
        PrintWriter fos = null;
        try {
            fis = new Scanner(new FileInputStream(src));
            fos = new PrintWriter(new FileOutputStream(dst));

            String line;
            while (fis.hasNextLine()) {
                line = fis.nextLine();
                fos.print(line + "\\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeIt(fis, fos);
        }
    }
}
