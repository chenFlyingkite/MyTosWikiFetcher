package main;

import main.fetcher.TosWikiFilePeeker;
import util.logging.L;
import util.tool.StringUtil;
import util.tool.TicTac;

public class Main {
    public static void main(String[] args) {
        long tic = System.currentTimeMillis();
        TicTac.tic();
        //TosWikiIconFetcher.me.run();
        //TosWikiHomeFetcher.me.run();
        //TosWikiCardFetcher.me.run();
        //TosAmeSkillFetcher.me.run();
        //TosActiveSkillFetcher.me.run();
        //TosWikiPageFetcher.me.run();
        //TosPageArchiveFetcher.me.run();
        TosWikiFilePeeker.me.run();
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
