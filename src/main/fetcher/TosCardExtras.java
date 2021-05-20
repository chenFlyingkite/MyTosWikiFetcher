package main.fetcher;

import com.google.gson.Gson;
import flyingkite.log.L;
import flyingkite.log.LF;
import flyingkite.tool.GsonUtil;
import flyingkite.tool.ThreadUtil;
import main.kt.CardItem;
import main.kt.FullStatsMax;
import main.kt.TosGet;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;

public class TosCardExtras extends TosWikiBaseFetcher {
    public static final TosCardExtras me = new TosCardExtras();
    private static final String folder = "myExtra";
    private LF mLf = new LF(folder);
    private final File amJson = new File(folder, "amBonus.json");
    private String source = TosWikiCardsLister.me.VALAID_LINKS;

    // Raise up if fixing card content
    private final boolean fixing = 0 > 0;

    private Map<String, String> allMaxMap = Collections.synchronizedMap(new TreeMap<>());
    private final Object lock = new Object();
    private CardItem[] allItems;

    @Override
    public void run() {
        loadCardItems();

        fetchAllMaxBonus();
        //fetchAllMaxBonusTryMultithread();
        // in Mac mini, it can only have 2048 thread, so will fails on multithread
        // [36.320s][warning][os,thread] Failed to start thread - pthread_create failed (EAGAIN) for attributes: stacksize: 1024k, guardsize: 4k, detached.

        //loadAllMaxBonus();
    }

    // https://tos.fandom.com/zh/api.php
    // https://tos.fandom.com/zh/api.php?format=json&action=expandtemplates&text=%7B%7B1234%7CfullstatsMax}}
    private void fetchAllMaxBonus() {
        L.log("Start fetchAllMaxBonus at %s", new Date());
        clock.tic();
        //mLf.getFile().open();
        List<String> pages = getLinks();
        int k = pages.size();
        int m = 0;
        TosGet tosget = new TosGet();
        for (int i = 0; i < k; i++) {
            String p = pages.get(i);
            int n = 0;
            try {
                int f = p.lastIndexOf("/");
                n = Integer.parseInt(p.substring(f + 1));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            if (n > 0) {
                String s = tosget.getAllMaxBonusSrc(n);

                allMaxMap.put(allItems[i].getId(), s);
                FullStatsMax f = new FullStatsMax().parse(s);
                if (f.exists()) {
                    m++;
                }
            }
        }

        amJson.getParentFile().mkdirs();
        GsonUtil.writeFile(amJson, mGson.toJson(allMaxMap, Map.class));
        L.log("In %s cards, %s cards has AMBonus", allMaxMap.size(), m); // m = 171/2425
        //mLf.getFile().close();
        clock.tac("End at %s", new Date());
    }

    public Map<String, String> loadAllMaxBonus() {
        Map<String, String> m = new TreeMap<>();
        return GsonUtil.loadFile(amJson, m.getClass());
    }

    private void saveFile(List<String> data) {
        amJson.getParentFile().mkdirs();
        GsonUtil.writeFile(amJson, new Gson().toJson(data.toArray(), String[].class));

    }

    private void loadCardItems() {
        clock.tic();
        allItems = GsonUtil.loadFile(new File(source), CardItem[].class);
        clock.tac("%s cards in %s", allItems.length, source);
    }

    private List<String> getLinks() {
        // Start here
        List<String> pages = getTests();
        if (pages.isEmpty()) {
            for (CardItem c : allItems) {
                pages.add(c.getLinkId());
            }
        }
        return pages;
    }

    private List<String> getTests() {
        List<String> link = new ArrayList<>();
        Collections.addAll(link
                // 昇華
                , "https://tos.fandom.com/zh/wiki/004" // 水元素賢者莫莉
                // 合體
                , "https://tos.fandom.com/zh/wiki/724" // 鬼面魔刃 ‧ 源義經
                , "https://tos.fandom.com/zh/wiki/597" // 連肢機偶 · 格蕾琴與海森堡
                // 突破
                , "https://tos.fandom.com/zh/wiki/818" // 憶念雙子 ‧ 加斯陀與波魯克斯
                // 雙技能
                , "https://tos.fandom.com/zh/wiki/1166" // 冰花
                , "https://tos.fandom.com/zh/wiki/1063" // 鳴動威嚴 ‧ 摩迪與曼尼
                // 潛能解放
                , "https://tos.fandom.com/zh/wiki/230" // 白臉金毛 ‧ 妲己
                // 異空轉生
                , "https://tos.fandom.com/zh/wiki/595" // 傾世媚狐 ‧ 蘇妲己
                , "https://tos.fandom.com/zh/wiki/1082" // 孤高龍王 ‧ 敖廣
                , "https://tos.fandom.com/zh/wiki/1777" // 斯芬克斯
        );
        //---
        Collections.addAll(link
                , "https://tos.fandom.com/zh/wiki/001" // TosCardCreator = 18
                , "https://tos.fandom.com/zh/wiki/024" // TosCardCreator = 28
                , "https://tos.fandom.com/zh/wiki/1001" // TosCardCreator = 16
                , "https://tos.fandom.com/zh/wiki/1017" // TosCardCreator = 22
                , "https://tos.fandom.com/zh/wiki/1063" // TosCardCreator = 32
                , "https://tos.fandom.com/zh/wiki/651" // TosCardCreator = 24
                , "https://tos.fandom.com/zh/wiki/656" // TosCardCreator = 31
                , "https://tos.fandom.com/zh/wiki/2001" // TosCardCreator = 33
        );

        link.clear(); // uncomment this if use test links
        link.add("https://tos.fandom.com/zh/wiki/1234");
        //link.add("https://tos.fandom.com/zh/wiki/1327"); // 伊莉莎白
        //link.add("https://tos.fandom.com/zh/wiki/6200");

        //link.add("https://tos.fandom.com/zh/wiki/1436");
        //link.add("https://tos.fandom.com/zh/wiki/6070"); // 妲己
        //link.add("https://tos.fandom.com/zh/wiki/6174");
        //link.add("https://tos.fandom.com/zh/wiki/595");
        //link.add("https://tos.fandom.com/zh/wiki/230");
        if (!fixing) {
            link.clear();
        }
        return link;
    }

    private final int[] info = {0, 0};

    // https://tos.fandom.com/zh/api.php
    // https://tos.fandom.com/zh/api.php?format=json&action=expandtemplates&text=%7B%7B1234%7CfullstatsMax}}
    private void fetchAllMaxBonusTryMultithread() {
        L.log("Start fetchAllMaxBonus at %s", new Date());
        clock.tic();
        List<String> pages = getLinks();
        int k = pages.size();

        // pool
        final ExecutorService exes = ThreadUtil.cachedThreadPool;//.newFlexThreadPool(k, 30);
        for (int i = 0; i < k; i++) {
            String p = pages.get(i);
            int n = 0;
            try {
                int f = p.lastIndexOf("/");
                n = Integer.parseInt(p.substring(f + 1));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            if (n > 0) {
                final int id = n;
                final int at = i;
                L.log("#%d send (%d, %d)", i, at, id);
                exes.submit(() -> {
                    long tid = Thread.currentThread().getId();
                    //L.log("goid = %s", tid);
                    L.log("Fetch #%s = No.%s, %s", at, id, new Date());
                    String s = new TosGet().getAllMaxBonusSrc(id);
                    L.log("Returns #%s = No.%s, %s", at, id, new Date());
                    allMaxMap.put(allItems[at].getId(), s);
                    FullStatsMax f = new FullStatsMax().parse(s);
                    if (f.exists()) {
                        info[0]++;
                    }
                    synchronized (info) {
                        info[1]++;
                        L.log("info = %s/%s %s", info[1], pages.size(), new Date());
                        //L.log("endid = %s", tid);
                        if (info[1] == pages.size()) {
                            amJson.getParentFile().mkdirs();
                            GsonUtil.writeFile(amJson, mGson.toJson(allMaxMap, Map.class));
                            //In 3171 cards, 221 cards has AMBonus
                            L.log("In %s cards, %s cards has AMBonus", allMaxMap.size(), info[0]); // m = 171/2425
                            //mLf.getFile().close();
                            clock.tac("End at %s", new Date());
                            synchronized (lock) {
                                L.log("Notify");
                                lock.notifyAll();
                                L.log("All");
                            }
                        }
                    }
                });
            }
        }
        synchronized (lock) {
            try {
                L.log("waiting %s", new Date());
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        L.log("waiting Done %s", new Date());
//        while (info[1] != pages.size()) {
//            //ThreadUtil.sleep(500);
//            ThreadUtil.sleep(500);
////            try {
////                Thread.sleep(500);
////            } catch (InterruptedException e) {
////                e.printStackTrace();
////            }
//        }
//
//        amJson.getParentFile().mkdirs();
//        GsonUtil.writeFile(amJson, mGson.toJson(allMaxMap, Map.class));
//        L.log("In %s cards, %s cards has AMBonus", allMaxMap.size(), info[0]); // m = 171/2425
//        //mLf.getFile().close();
//        clock.tac("End at %s", new Date());
    }
}
