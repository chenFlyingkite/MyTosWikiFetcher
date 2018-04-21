package main.fetcher;

import main.card.TosGet;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import util.logging.L;
import util.logging.LF;
import util.tool.TicTac2;
import wikia.articles.UnexpandedArticle;

import java.util.*;

public class TosWikiImagePeeker extends TosWikiBaseFetcher implements Runnable {
    private TosWikiImagePeeker() {}
    public static final TosWikiImagePeeker me = new TosWikiImagePeeker();

    private final boolean zh = true;
    private final String folder = zh ? "myImages" : "myImagesEng";
    private final LF Lf = new LF(folder);
    private final String tosApi = (zh ? zhApi1 : enApi1)
            + "/Articles/List?namespaces=6&limit=2500000";
    //private final String tosBase = "http://zh.tos.wikia.com/wiki/Special:%E5%9B%BE%E7%89%87";

    private List<Map<String, List<String>>> imageMaps = Collections.synchronizedList(new ArrayList<>());
    private List<String> musicLinks = Collections.synchronizedList(new ArrayList<>());

    @Override
    public String getAPILink() {
        return tosApi;
    }

    @Override
    public LF getHttpLF() {
        return Lf;
    }

    @Override
    public void run() {
        // Parameters setting
        mFetchAll = true;

        ResultSet set = getApiResults();
        if (!hasResult(set)) return;

        int size = set.getItems().length;

        TicTac2 tt = clock;

        // This takes about 1 min 46 s
        int q = 100; // Each file chunk size
        int n = (int) Math.ceil(1.0 / q * size);
        L.log("set = %s, n = %s", size, n);
        for (int i = 0; i < n; i++) {
            executors.submit(runGetImageInfo(i, set, q * i, q * (i + 1)));
        }
        Lf.getFile().open();
        Lf.setLogToL(true);
        tt.tic();
        int z = 1;
        final String[] pgs = {".", ".", ".", ".", ".", "-" ,".", ".", ".", ".", "="};
        while (imageMaps.size() < n) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Print progress
            String s;
            if (z % 10 == 0) {
                s = "" + z / 10 % 10;
            } else {
                s = pgs[z % 10];
            }
            L.getImpl().print(s);
            z++;
        }
        L.log("");
        tt.tac("imageMaps OK");
        Lf.setLogToL(false);
        Map<String, List<String>> all = new HashMap<>();
        for (Map<String, List<String>> map : imageMaps) {
            for (String s : map.keySet()) {
                List<String> userList = all.getOrDefault(s, new ArrayList<>());
                userList.addAll(map.get(s));
                all.put(s, userList);
            }
        }
        Lf.log("----- Users -----");

        List<String> allK = new ArrayList<>(all.keySet());
        Collections.sort(allK, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int n1 = all.get(o1).size();
                int n2 = all.get(o2).size();
                if (n1 == n2) {
                    return o1.compareTo(o2);
                } else {
                    return Integer.compare(n2, n1);
                }
            }
        });
        for (String s : allK) {
            Lf.log("%s => %s", s, all.get(s).size());
        }
        Lf.log("----- Users =====");
        Lf.getFile().close();

        //Select the top 4 users that their information is most useful
        // But image link duplicates?
        for (int i = 0; i < 4; i++) {
            String k = allK.get(i);
            List<String> allImages = all.get(k);
            Collections.sort(allImages);
            executors.submit(runToFile(allK.get(i), allImages));
        }
    }

    private Runnable runToFile(String name, List<String> list) {
        return () -> {
            LF lf = new LF(folder, name + ".txt");
            // Open logging files
            lf.getFile().open(false);
            lf.setLogToL(!mFetchAll);
            for (String s : list) {
                lf.log(s);
            }
            lf.getFile().close();
        };
    }

    private Runnable runGetImageInfo(int tid, ResultSet set, int from, int end) {
        return () -> {
            LF lf = new LF(folder, tid + ".txt");
            // Open logging files
            lf.getFile().open(false);
            lf.setLogToL(!mFetchAll);

            Map<String, List<String>> user = new HashMap<>();
            int ok = 0;
            int endd = Math.min(end, set.getItems().length);
            for (int i = from; i < endd; i++) {
                UnexpandedArticle a = set.getItems()[i];
                String link = set.getBasePath() + "" + a.getUrl();
                String lnk = link;//.toLowerCase();
                boolean isImage = lnk.contains(".png") || lnk.contains(".jpg");
                boolean isMusic = lnk.contains(".ogg");
                if (isImage) {
                    ImageInfo img = getImageInfo(link, lf);
                    lf.log("#%s = %s", i, img.html);
                    lf.log("u = %s, => %s", img.user, link);
                    List<String> userLink = user.getOrDefault(img.user, new ArrayList<>());
                    userLink.add(link);
                    user.put(img.user, userLink);
                    ok++;
                } else if (isMusic) {
                    musicLinks.add(link);
                    lf.log("#%s Omit Music => %s", i, link);
                    //L.log("#%s Omit Music => %s", i, link);
                } else {
                    lf.log("#%s Omit => %s", i, link);
                }
            }

            lf.log("---  OK = %s  ---", ok);
            lf.log("----- Users -----");
            Set<String> keys = user.keySet();
            for (String s : keys) {
                lf.log("%s => %s", s, user.get(s));
            }
            lf.log("----- Users =====");
            lf.getFile().close();
            imageMaps.add(user);
        };
    }

    private ImageInfo getImageInfo(String link, LF logf) {
        ImageInfo info = new ImageInfo();

        // Step 1: Get the xml node from link by Jsoup
        Document doc = getDocument(link);
        if (doc == null) return info;

        // Step 2: Find the <center> nodes
        logf.log("Title = %s, Children = %s", doc.title(), doc.getAllElements().size());
        Elements fileInfo = doc.getElementsByClass("fileInfo");
        info.html = TosGet.me.getHtmlAt(0, fileInfo);

        Elements es = doc.getElementsByClass("mw-userlink");
        info.user = TosGet.me.getHtmlAt(0, es);

        return info;
    }

    private class ImageInfo {
        private String html = "";
        private String user = "";
    }


// http://zh.tos.wikia.com/wiki/Special:%E5%9B%BE%E7%89%87
}
