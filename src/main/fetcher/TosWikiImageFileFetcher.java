package main.fetcher;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import flyingkite.files.FileUtil;
import flyingkite.log.L;
import flyingkite.log.LF;
import flyingkite.tool.TextUtil;
import main.kt.IconInfo;
import main.kt.ImageFileInfo;
import main.kt.TosGet;
import org.jsoup.nodes.Document;

public class TosWikiImageFileFetcher extends TosWikiBaseFetcher {
    private TosWikiImageFileFetcher() { }
    public static final TosWikiImageFileFetcher me = new TosWikiImageFileFetcher();

    private static final boolean zh = false;

    private static final String wikiPageBase = zh ? wikiBaseZh : wikiBaseEn;
    private static final String tosWiki = wikiPageBase +
        (zh ? "/wiki/Special:%E5%9B%BE%E7%89%87"
            : "/wiki/Special:Images");
    private static final int PAGES = zh ? 309 : 155;
    private static final String folder = zh ? "myImagesFile" : "myImagesFileEng";
    // http://zh.tos.wikia.com/wiki/Special:圖片
    // https://towerofsaviors.wikia.com/wiki/Special:Images

    private final LF mLf = new LF(folder);
    private final LF mLfImage = new LF(folder, "imageInfo.txt");
    private final File dlFolder = new File(folder + "/temp/");

    @Override
    public void run() {
        FileUtil.ensureDelete(dlFolder);
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
                        executors.submit(runDownloadImage(dlFolder.toString(), info));
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
        List<String> listZh = Arrays.asList("Btoky" , "Hugochau", "Wingwing007", "Towerofsaviors", "Ahhei0403", "Imnoob92"
                , "568736", "Wallance1992", "Eeepc900", "Blueeighthnote", "BrockF5");
        List<String> listEn = Arrays.asList("JoetjeF", "Lycentia", "RaccoonKun", "Wingwing007", "Towerofsaviors"
                , "Btoky", "Aekun");
        List<String> list = zh ? listZh : listEn;
        //return false;
        return list.contains(info.getUploader());
        //return true;
    }

    private Runnable runDownloadImage(String folder, ImageFileInfo info) {
        return new Runnable() {
            @Override
            public void run() {
                String link = info.getWikiPage();
                if ("http://zh.tos.wikia.com/wiki/File:SI058.png".equals(link)) {
                    L.log("HiError s = %s", link);
                }

                IconInfo icf = getIconInfo(link);
                icf = retry(10, icf, link);

                String s = downloadImage(icf.getLink(), folder, icf.getName());
                s = retryDL(10, s, icf, link);
                //L.log("OK s = %s", s);
                if (s == null) {
                    L.log("X_X fail: link = %s, %s", link, s);
                }
            }

            private IconInfo retry(int times, IconInfo icf, String link) {
                int n = 0;
                while (icf.isEmpty() && n < times) {
                    try {
                        Thread.sleep(10_000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    L.log("Try #%s = %s", n, link);
                    icf = getIconInfo(link);
                    n++;
                }
                return icf;
            }

            private String retryDL(int times, String fos, IconInfo icf, String link) {
                int n = 0;
                while (TextUtil.isEmpty(fos) && n < times) {
                    try {
                        Thread.sleep(10_000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    L.log("Try DL #%s = %s", n, link);
                    fos = downloadImage(icf.getLink(), folder, icf.getName());
                    n++;
                }
                return fos;
            }
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
