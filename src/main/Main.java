package main;

import main.fetcher.TosWikiFetcher;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import util.logging.L;
import util.tool.TicTac;
import util.tool.TicTac2;

import java.io.IOException;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        L.log("Now = %s", new Date());
        TicTac.tic();
        TosWikiFetcher.run();
        TicTac.tac("Main ended");
        L.log("Now = %s", new Date());
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
