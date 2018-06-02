package main.fetcher;

import main.card.IconInfo;
import main.card.ImageInfo2;
import main.card.TosGet;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.logging.LF;

public class TosWikiPageFetcher extends TosWikiBaseFetcher {
    private TosWikiPageFetcher() {}
    public static final TosWikiPageFetcher me = new TosWikiPageFetcher();
    private static final String folder = "myPage";
    private LF mLf = new LF(folder);

    private String getPage() {
        // http://zh.tos.wikia.com/wiki/神魔之塔×獵人×冒險開始
        //return "http://zh.tos.wikia.com/wiki/%E7%A5%9E%E9%AD%94%E4%B9%8B%E5%A1%94%C3%97%E7%8D%B5%E4%BA%BA%C3%97%E5%86%92%E9%9A%AA%E9%96%8B%E5%A7%8B";
        // 護衛×千鈞一髮×直擊
        return "http://zh.tos.wikia.com/wiki/%E8%AD%B7%E8%A1%9B%C3%97%E5%8D%83%E9%88%9E%E4%B8%80%E9%AB%AE%C3%97%E7%9B%B4%E6%93%8A";
    }

    public void run() {
        mLf.getFile().open(false);

        Document doc = getDocument(getPage());
        clock.tic();
        getImages(doc);
        clock.tac("Image OK");
        mLf.log(getPoem(doc));

        mLf.getFile().close();
    }

    private void getImages(Document doc) {
        Elements imgs = doc.getElementsByClass("image image-thumbnail");
        if (imgs != null) {
            Element e1 = imgs.get(0);
            ImageInfo2 info = TosGet.me.getImgInfo2(e1);

            String link = wikiFileZh + "" + info.getImageName();
            IconInfo icf = getIconInfo(link);
            downloadImage(icf.getLink(), folder, icf.getName());
        }
    }
}
