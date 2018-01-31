package main;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import util.tool.TicTac2;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
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
