package main;

import main.fetcher.TosActiveSkillFetcher;
import main.fetcher.TosAmeSkillFetcher;
import main.fetcher.TosWikiCardFetcher;
import main.fetcher.TosWikiHomeFetcher;
import util.logging.L;
import util.tool.StringUtil;
import util.tool.TicTac;

public class Main {
    public static void main(String[] args) {
        long tic = System.currentTimeMillis();
        TicTac.tic();
        //-- Regular
        TosWikiHomeFetcher.me.run();
        TosWikiCardFetcher.me.run();
        TosAmeSkillFetcher.me.run();
        TosActiveSkillFetcher.me.run();
        //-- Seldom
        //TosWikiPageFetcher.me.run();
        //TosPageArchiveFetcher.me.run();
        //TosWikiFilePeeker.me.run();
        //TosWikiIconFetcher.me.run();
        //TosWikiSummonerLevelFetcher.me.run();
        //TosWikiImageFileFetcher.me.run();
        //MobileComm.run();
        //ASD.run();
        //TosWikiStageFetcher.me.run();
        //ClusterMain.INSTANCE.main(args);
        TicTac.tac("Main ended");
        long tac = System.currentTimeMillis();
        L.log("time = %s", StringUtil.MMSSFFF(tac - tic));
    }

}
