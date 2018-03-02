package main;

import main.fetcher.TosWikiCardFetcher;
import main.fetcher.TosWikiIconFetcher;
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
        TicTac.tac("Main ended");
        long tac = System.currentTimeMillis();
        L.log("time = %s", StringUtil.MMSSFFF(tac - tic));
    }

    private static void google() {
        TicTac2 t = new TicTac2();

        try {
            t.tic();
            Response r = Jsoup.connect("http://www.google.com").execute();
            t.tac("google OK");
            System.out.println("r = " + r.body());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
