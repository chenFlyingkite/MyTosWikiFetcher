package main.ptt;

import org.jsoup.select.Elements;
import util.logging.L;
import util.logging.LF;

import java.util.List;

public class MobileComm extends BasePPTFetcher {
    private static final String pptMobileComm = pptBBS + "/MobileComm/";
    //private static final LF Lf = new LF("myppt");

    @Override
    public String getPPTLink() {
        return pptMobileComm;
    }

    public static void run() {
        int startPage = 6040;
        int endPage = 6050;
        ttClient.setLog(false);

        //Lf.getFile().delete().open();
        int n = 0;
        for (int i = endPage; i >= startPage; i--) {
            String link = pptMobileComm + "index" + i + ".html";
            Elements es = getHttp(link, "r-ent");
            List<MobileCommArticle> mca = MobileCommGet.me.get(es);
            for (MobileCommArticle a : mca) {
                L.log("-> %s, %s", a.getTitle(), pptCC + a.getLink());
            }
            n += mca.size();
        }
        L.log(" %s page", n);

        //Lf.getFile().close();
    }
}
