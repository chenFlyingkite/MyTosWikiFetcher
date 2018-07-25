package main.fetcher;

import main.card.ImageInfo2;
import main.card.TosGet;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import flyingkite.log.LF;

public class TosPageArchiveFetcher extends TosWikiBaseFetcher {
    private TosPageArchiveFetcher() {}
    public static final TosPageArchiveFetcher me = new TosPageArchiveFetcher();
    private static final String folder = "myPageTos";
    private static final String tosArchive = "http://www.towerofsaviors.com/zh/archives";
    private LF mLf = new LF(folder);
    private static final int archiveId = 22952;

    private String getPage() {
        // 【神魔之塔×獵人×冒險開始】慶祝活動詳情
        // http://www.towerofsaviors.com/zh/archives/22871
        return tosArchive + "/" + archiveId;
    }

    @Override
    public void run() {
        mLf.getFile().open(false);

        Document doc = getDocument(getPage());
        clock.tic();
        getImages(doc);
        clock.tac("Image OK");
        mLf.log(getEntryContent(doc));

        mLf.getFile().close();
    }

    private void getImages(Document doc) {
        Element post = doc.getElementById("post-image");
        //Element pMain = doc.getElementById("post-" + archiveId);
        if (post != null) {
            ImageInfo2 info = TosGet.me.getTosPageImageInfo(post);
            if (info == null || info.isEmpty()) return;

            String link = info.getLink();
            String ext = link.substring(link.lastIndexOf("."));
            String name = info.getAlt() + ext;
            downloadImage(link, folder, name);
        }
    }

    private String getEntryContent(Document doc) {
        return TosGet.me.getTosPageEntryContent(doc);
    }
}
