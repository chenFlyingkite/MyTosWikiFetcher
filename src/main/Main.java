package main;

import util.images.PngCreator;
import util.images.PngRequest;
import util.data.Rect2;
import util.logging.L;
import util.tool.StringUtil;
import util.tool.TicTac;

public class Main {
    public static void main(String[] args) {
        long tic = System.currentTimeMillis();
        TicTac.tic();

        PngRequest.Param p = new PngRequest
                .Param("D:\\GitHub\\Logos\\Source\\Screenshot_20180517-010049.png")
                .size(340, 340)
                ;
        Rect2 r = Rect2.ofSize(340, 340);
        r.offset(69, 1740);
        PngCreator.from(p).copy(r).eraseCorners()
                .into("D:\\GitHub\\Logos\\Output\\1888n.png");
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
