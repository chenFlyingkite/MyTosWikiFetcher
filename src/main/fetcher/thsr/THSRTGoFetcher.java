package main.fetcher.thsr;

import flyingkite.log.L;
import flyingkite.log.LF;
import flyingkite.tool.TicTac2;
import main.fetcher.web.OnWebLfTT;
import main.fetcher.web.WebFetcher;
import org.jsoup.nodes.Document;

public class THSRTGoFetcher {
    // Basic pack
    public static final THSRTGoFetcher me = new THSRTGoFetcher();
    private static final String FOLDER = "thsr";
    private LF mLf = new LF(FOLDER);
    private TicTac2 clock = new TicTac2();
    private WebFetcher fetcher = new WebFetcher();
    private OnWebLfTT onWeb = new OnWebLfTT(mLf, clock);

    public static void main(String[] args) {
        me.parse();
    }

    // 星巴克 便利生活 美食餐廳 點心伴手禮 旅遊交通
    // 4 5 6 7 9
    // like
    // https://tgopoints.thsrc.com.tw/product?cat1=4

    private void parse() {
        // failed on its jsp dynamic server content...
        String base = "https://tgopoints.thsrc.com.tw/product?cat1=";

        for (int i = 1; i <= 11; i++) {
            String link = base + i;
            Document doc = fetcher.getDocument(link);
            L.log("#%s : %s", i, doc);
        }
    }

}
