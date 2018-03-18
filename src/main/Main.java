package main;

import main.fetcher.TosWikiCardFetcher;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import util.logging.L;
import util.tool.StringUtil;
import util.tool.TicTac;
import util.tool.TicTac2;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        long tic = System.currentTimeMillis();
        TicTac.tic();
        //TosWikiIconFetcher.me.run();
        TosWikiCardFetcher.me.run();
        //google();
        //print();
        //evalExpCurve();
        TicTac.tac("Main ended");
        long tac = System.currentTimeMillis();
        L.log("time = %s", StringUtil.MMSSFFF(tac - tic));
    }

    private static void google() {
        TicTac2 t = new TicTac2();
        String link = "http://www.google.com";
        //link = "https://m.facebook.com/friends/center/requests/outgoing/#friends_center_main";
        // https://m.facebook.com/friends/center/requests/outgoing/

        try {
            t.tic();
            Response r = Jsoup.connect(link).execute();
            t.tac("google OK");
            System.out.println("r = " + r.body());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void print() {

    }

    private static void evalExpCurve() {
        int expCurve = 150_0000;
        int minScExp = 600;
        int dExp = 1800;

        int max = 100;
        int[] accuExp = new int[max];
        int[] scfyExp = new int[max];

        int last = -1, tall = -1;
        for (int i = 1; i < max; i++) {
            double dx = 1.0F * (i - 1) / 98;
            accuExp[i] = (int) Math.ceil(expCurve * dx * dx);
            scfyExp[i] = minScExp + dExp * (i - 1);

            if (accuExp[i] <= scfyExp[i]) {
                last = i;
            }

            if (accuExp[i] - accuExp[i - 1] <= dExp) {
                tall = i;
            }
        }

        System.out.println("tall = " + tall);
        System.out.println("last = " + last);

        boolean logTable = false;
        for (int i = 1; i < max && logTable; i++) {
            String s = String.format("%2s : %7s, %7s", i, accuExp[i], scfyExp[i]);
            System.out.println(s);
        }
    }
}
