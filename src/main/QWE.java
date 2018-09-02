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
        clk.tac("genTosEvent OK");
    }

    private static void genTosEvent() {
        // For the src file text,
        // Output the dst file as each line + "\n"
        String folder = "D:\\GitHub\\MyTosWiki\\playstore\\tos";
        String name = "tosEvent.txt";
        File src = new File(folder, name);
        File dst = new File(folder, "z" + name);

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
