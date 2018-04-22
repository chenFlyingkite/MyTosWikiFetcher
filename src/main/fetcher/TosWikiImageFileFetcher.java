package main.fetcher;

import main.card.IconInfo;
import main.card.ImageFileInfo;
import main.card.TosGet;
import org.jsoup.nodes.Document;
import util.logging.L;
import util.logging.LF;

import java.util.Arrays;
import java.util.List;

public class TosWikiImageFileFetcher extends TosWikiBaseFetcher implements Runnable {
    private TosWikiImageFileFetcher() { }
    public static final TosWikiImageFileFetcher me = new TosWikiImageFileFetcher();

    private static final boolean zh = false;

    private static final String wikiPageBase = zh ? wikiBaseZh : wikiBaseEn;
    private static final String tosWiki = wikiPageBase +
        (zh ? "/wiki/Special:圖片"
            : "/wiki/Special:Images");
    private static final int PAGES = zh ? 295 : 141;
    private static final String folder = zh ? "myImagesFile" : "myImagesFileEng";

    private final LF mLf = new LF(folder);
    private final LF mLfImage = new LF(folder, "imageInfo.txt");

    @Override
    public void run() {
        // English takes 1:54 (no download)
        // ZH takes 8:27 (no download)

        LF lf = mLf;
        LF lfi = mLfImage;
        lfi.getFile().open(false);
        lf.getFile().open(false);
        lf.setLogToL(false);

        long tic, tac;
        Document doc;
        int n = Math.min(PAGES, PAGES);
        for (int i = 1; i <= n; i++) {
            tic = System.currentTimeMillis();
            doc = getDocument(tosWiki + "?page=" + i);
            tac = System.currentTimeMillis();
            if (doc != null) {
                //lf.log("[%s]: #%s -> %s", tac - tic, i, doc.baseUri());
                List<ImageFileInfo> infoList = TosGet.me.getImageFileInfo(doc, wikiPageBase);
                for (ImageFileInfo info : infoList) {
                    lf.log(info.getWikiPage());
                    lfi.log(info.toString());
                    if (canDownload(info)) {
                        executors.submit(runDownloadImage(folder + "/" + i + "/", info));
                    }
                }
                //executors.submit(runDownloadImage(folder + "/" + i + "/", inf));
            }
        }
        lfi.getFile().close();
        lf.getFile().close();
    }

    // TODO :Download the wanted file here
    private boolean canDownload(ImageFileInfo info) {
        List<String> listZh = Arrays.asList("Wingwing007", "Hugochau", "Btoky", "Towerofsaviors");
        List<String> listEn = Arrays.asList("JoetjeF", "Lycentia", "RaccoonKun", "Wingwing007", "Towerofsaviors");
        List<String> list = zh ? listZh : listEn;
        return false;
        //return list.contains(info.getUploader());
        //return true;
    }

    private Runnable runDownloadImage(String folder, ImageFileInfo info) {
        return () -> {
            String link = info.getWikiPage();
            IconInfo icf = getIconInfo(link);
            String s = downloadImage(icf.getLink(), folder, icf.getName());
            //L.log("OK s = %s", s);
        };
    }

    private Runnable runDownloadImageFolder(String folder, List<ImageFileInfo> fileInfos) {
        return () -> {
            for (ImageFileInfo info : fileInfos) {
                runDownloadImage(folder, info).run();
            }
        };
    }
}
