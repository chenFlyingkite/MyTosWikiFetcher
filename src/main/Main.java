package main;

import util.data.Rect2;
import util.images.create.PngCreateRequest;
import util.images.create.PngCreator;
import util.images.diff.PngDiffer;
import util.logging.L;
import util.tool.StringUtil;
import util.tool.TicTac;

public class Main {
    public static void main(String[] args) {
        long tic = System.currentTimeMillis();
        TicTac.tic();

        PngCreateRequest.Param p = new PngCreateRequest
                .Param("Logos\\Source\\Screenshot_20180517-010049.png")
                .size(340, 340)
                ;
        Rect2 r = Rect2.ofSize(340, 340);
        //r.offsetTo(69, 895); //獎賞 , V
        r.offsetTo(69, 1317); //累積簽到 (68, 1316)
        //r.offsetTo(68, 1740); //我的禮包
        int y = 1740;
        for (int i = 68; i <= 68 && false; i++) {
            r.offsetTo(i, y);

            PngCreator.from(p).copy(r).eraseCorners()
                    .into("Logos\\Output\\a\\n.png");

            PngDiffer.diff("Logos\\Output\\a");

            try {
                L.log("sleep %s", i);
                Thread.sleep(5000);
                L.log("awake");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        PngCreator.from(p).copy(r).eraseCorners()
                .into("Logos\\Output\\a\\n.png");

        PngDiffer.diff("Logos\\Output\\a");
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
