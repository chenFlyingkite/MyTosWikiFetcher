package main;

import main.fetcher.TosWikiCardFetcher;
import main.fetcher.TosWikiIconFetcher;
import main.ptt.MobileComm;
import util.logging.L;
import util.tool.StringUtil;
import util.tool.TicTac;

public class Main {
    public static void main(String[] args) {
        long tic = System.currentTimeMillis();
        TicTac.tic();
        TosWikiIconFetcher.me.run();
        //TosWikiCardFetcher.me.run();
        //MobileComm.run();
        //ASD.run();
        TicTac.tac("Main ended");
        long tac = System.currentTimeMillis();
        L.log("time = %s", StringUtil.MMSSFFF(tac - tic));
    }
}
