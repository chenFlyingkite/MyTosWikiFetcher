package main;

import main.fetcher.*;
import main.images.PngCreator;
import util.logging.L;
import util.tool.StringUtil;
import util.tool.TicTac;

public class Main {
    public static void main(String[] args) {
        long tic = System.currentTimeMillis();
        TicTac.tic();
        PngCreator.extract(
                "D:\\GitHub\\Logos\\Source\\Screenshot_20180517-010049.png"
                //"D:\\GitHub\\Logos\\Output\\1.png"
                , "D:\\GitHub\\Logos\\Output\\1888n.png"
                , 340, 340 // w, h
                , 68, 1740 // x, y
        );
        //TosWikiIconFetcher.me.run();
        //TosWikiCardFetcher.me.run();
        //TosWikiFilePeeker.me.run();
        //TosWikiSummonerLevelFetcher.me.run();
        //TosWikiImageFileFetcher.me.run();
        //MobileComm.run();
        //ASD.run();
        //TosWikiStageFetcher.me.run();
        TicTac.tac("Main ended");
        long tac = System.currentTimeMillis();
        L.log("time = %s", StringUtil.MMSSFFF(tac - tic));
    }

}
