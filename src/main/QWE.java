package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import flyingkite.tool.IOUtil;
import flyingkite.tool.TicTac2;

public class QWE {
    private static TicTac2 clk = new TicTac2();

    public static void run() {
        clk.tic();
        genTosEvent();
        genStageMemo();
        clk.tac("genTosEvent OK");
    }

    private static void genTosEvent() {
        genLines("D:\\GitHub\\MyTosWiki\\playstore\\tos", "tosEvent.txt");
    }

    private static void genStageMemo() {
        genLines("D:\\GitHub\\MyTosWiki\\playstore\\stage memo", "memo.txt");
    }

    private static void genLines(String folder, String name) {
        File src = new File(folder, name);
        File dst = new File(folder, "z" + name);
        printNewLines(src, dst);
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
