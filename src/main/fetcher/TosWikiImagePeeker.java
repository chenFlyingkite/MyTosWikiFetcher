package main.fetcher;

import main.card.TosGet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.data.Range;
import util.logging.L;
import util.logging.LF;
import util.tool.TicTac2;
import wikia.articles.UnexpandedArticle;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class TosWikiImagePeeker extends TosWikiBaseFetcher implements Runnable {
    private TosWikiImagePeeker() {}
    public static final TosWikiImagePeeker me = new TosWikiImagePeeker();
    private final boolean zh = false;
    private final String folder = zh ? "myImages" : "myImagesEng";
    private final LF Lf = new LF(folder);
    private final String tosApi =
            (zh ? "http://zh.tos.wikia.com/api/v1"
                : "http://towerofsaviors.wikia.com/api/v1")
            + "/Articles/List?namespaces=6&limit=2500000";
    //private final String tosBase = "http://zh.tos.wikia.com/wiki/Special:%E5%9B%BE%E7%89%87";

    @Override
    public String getAPILink() {
        return tosApi;
    }

    @Override
    public LF getHttpLF() {
        return Lf;
    }

    private int from = 0; // #131
    private int prefetch = 5;

    private List<Map<String, List<String>>> maps = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void run() {
        // Parameters setting
        mFetchAll = true;

        ResultSet set = getApiResults();
        if (!hasResult(set)) return;

        int size = set.getItems().length;

        Range rng = getRange(set, from, prefetch);

        TicTac2 tt = new TicTac2();

        // This takes about 1 min 46 s
        int q = 100; // Each file chunk size
        int n = (int) Math.ceil(1.0 / q * size);
        L.log("set = %s, n = %s", size, n);
        for (int i = 0; i < n; i++) {
            executors.submit(runGetLog(i, set, q * i, q * (i + 1)));
        }
        Lf.getFile().open();
        Lf.setLogToL(true);
        tt.tic();
        int z = 0;
        while (maps.size() < n) {
            try {
                Thread.sleep(2000);
                L.log("z %s", z);
                z++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        tt.tac("mapsizeOK");
        Lf.setLogToL(false);
        Map<String, List<String>> all = new HashMap<>();
        for (Map<String, List<String>> map : maps) {
            for (String s : map.keySet()) {
                List<String> userList = all.getOrDefault(s, new ArrayList<>());
                userList.addAll(map.get(s));
                all.put(s, userList);
            }
        }
        Lf.log("----- Users -----");
        List<String> allK = new ArrayList<>();

        Set<String> keys = all.keySet();
        for (String s : keys) {
            allK.add(s);
        }
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

        /*
        // Open logging files
        Lf.getFile().open();
        Lf.setLogToL(!mFetchAll);
        //Lfc.getFile().delete().open();
        //Lfc.setLogToL(false);
        Map<String, Integer> user = new HashMap<>();
        int ok = 0;
        for (int i = rng.min; i < rng.max; i++) {
            UnexpandedArticle a = set.getItems()[i];
            String link = set.getBasePath() + "" + a.getUrl();
            if (link.contains(".png") || link.contains(".jpg")) {
                ImageInfo img = getImageInfo(link);
                Lf.log("#%s = %s", i, img.html);
                Lf.log("u = %s, => %s", img.user, link);
                int cnt = user.getOrDefault(img.user, 0);
                cnt++;
                user.put(img.user, cnt);
                ok++;
            } else {
                Lf.log("#%s Omit => %s", i, link);
            }
        }

        Lf.setLogToL(true);

        Lf.log("---  OK = %s  ---", ok);
        Lf.log("----- Users -----");
        Set<String> keys = user.keySet();
        for (String s : keys) {
            Lf.log(" %s => %s");
        }
        Lf.log("----- Users =====");

        //Lf.log("sizes are %s", itemsN);
        //Lf.log("%s cards", cards.size());
        //Lf.log("%s cards not duplicate", cardsNoDup.size());
        Lf.getFile().close();
        */
    }

    ExecutorService executors = Executors.newCachedThreadPool();
            //new ThreadPoolExecutor(0, Integer.MAX_VALUE, 30L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());

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

    private Runnable runGetLog(int tid, ResultSet set, int from, int end) {
        return () -> {
            LF lf = new LF(folder, tid + ".txt");
            // Open logging files
            lf.getFile().open(false);
            lf.setLogToL(!mFetchAll);
            //Lfc.getFile().delete().open();
            //Lfc.setLogToL(false);
            Map<String, List<String>> user = new HashMap<>();
            int ok = 0;
            int endd = Math.min(end, set.getItems().length);
            for (int i = from; i < endd; i++) {
                UnexpandedArticle a = set.getItems()[i];
                String link = set.getBasePath() + "" + a.getUrl();
                if (link.contains(".png") || link.contains(".jpg")) {
                    ImageInfo img = getImageInfo(link, lf);
                    lf.log("#%s = %s", i, img.html);
                    lf.log("u = %s, => %s", img.user, link);
                    List<String> userLink = user.getOrDefault(img.user, new ArrayList<>());
                    userLink.add(link);
                    user.put(img.user, userLink);
                    ok++;
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
            maps.add(user);
        };
    }

    private ImageInfo getImageInfo(String link, LF logf) {
        final boolean logTime = false;
        ImageInfo info = new ImageInfo();

        // Step 1: Get the xml node from link by Jsoup
        Document doc = null;
        TicTac2 ts = new TicTac2();
        ts.setLog(logTime);
        ts.tic();
        try {
            doc = Jsoup.connect(link).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ts.tac("JSoup OK " + link);
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
