package main.fetcher.exchange;

import flyingkite.files.FileUtil;
import flyingkite.log.LF;
import flyingkite.tool.TextUtil;
import flyingkite.tool.TicTac2;
import main.fetcher.data.PPair;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ForeignExchangeRateFetcher {
    // Bank of Taiwan's exchange query page
    // https://rate.bot.com.tw/xrt?Lang=zh-TW
    // download csv
    // https://rate.bot.com.tw/xrt/flcsv/0/l6m/USD
    // query exchange
    // https://rate.bot.com.tw/xrt/quote/l6m/USD
    // https://rate.bot.com.tw/xrt/quote/l6m/HKD

    private static final LF mLF = new LF("foreignCurrency");
    private static final File rootData = new File("foreignCurrency");
    // new loaded files save here
    private static final File latestFolder = new File(rootData, "latest");
    // organized data
    private static final File botFolder = new File(rootData, "bot");

    public static void main(String[] args) {
        saveLatestCurrency();
        mergeAllCurrency();
    }

    private static void mergeAllCurrency() {
        File[] fl = latestFolder.listFiles();
        File[] bots = botFolder.listFiles();
        Map<String, PPair<File, File>> all = new HashMap<>();
        if (fl != null) {
            for (int i = 0; i < fl.length; i++) {
                File f = fl[i];
                PPair<File, File> p = new PPair<>(f, null);
                all.put(f.getName(), p);
            }
        }
        if (fl != null) {
            for (int i = 0; i < bots.length; i++) {
                File f = bots[i];
                PPair<File, File> p = all.get(f.getName());
                if (p == null) {
                    p = new PPair<>(null, f);
                } else {
                    p.second = f;
                }
                all.put(f.getName(), p);
            }
        }
        List<PPair<File, File>> li = new ArrayList<>(all.values());
        for (int i = 0; i < li.size(); i++) {
            PPair<File, File> p = li.get(i);
            mergeCurrency(p.first, p.second);
            //mergeCurrency(latestFolder, botFolder);
        }
    }

    //Merge currency history from two folders, both of them should be named as same file
    private static void mergeCurrency(File src, File dst) {
        List<String> srcAll = FileUtil.readAllLines(src);
        List<String> dstAll = FileUtil.readAllLines(dst);
        File target = dst;
        if (srcAll.size() < 2) {
            FileUtil.writeToFile(target, dstAll, false);
        } else if (dstAll.size() < 2) {
            FileUtil.writeToFile(target, srcAll, false);
        } else {
            // 0 = header
            // 1 = 1st line data
            List<String> merge = new ArrayList<>();
            merge.add(dstAll.get(0));
            int col = dstAll.get(0).split(",").length;

            Map<Integer, String> map = new HashMap<>();
            // add dst to map
            for (int i = 1; i < dstAll.size(); i++) {
                String line = dstAll.get(i);
                String[] parts = line.split(",");
                if (parts.length == col) {
                    int time = Integer.parseInt(parts[0]);
                    map.put(time, line);
                }
            }
            // add ony when not exists
            for (int i = 1; i < srcAll.size(); i++) {
                String line = srcAll.get(i);
                String[] parts = line.split(",");
                if (parts.length == col) {
                    int time = Integer.parseInt(parts[0]);
                    if (!map.containsKey(time)) {
                        map.put(time, line);
                    }
                }
            }
            List<Integer> keys = new ArrayList<>(map.keySet());
            Collections.sort(keys);
            // write from new to old
            for (int i = keys.size() - 1;  i >= 0; i--) {
                merge.add(map.get(keys.get(i)));
            }
            FileUtil.writeToFile(target, merge, false);
        }
    }

    //
    private static void saveLatestCurrency() {
        //String link = "https://rate.bot.com.tw/xrt/flcsv/0/l6m/USD";
        String format = "https://rate.bot.com.tw/xrt/flcsv/0/l6m/";
        CurrencyBOT[] all = CurrencyBOT.values();

        mLF.setLogToFile(false);
        //mLF.getFile().open(false);
        for (int i = 0; i < all.length; i++) {
            CurrencyBOT it = all[i];
            String link = format + it.id;
            String src = getApiBody(link, mLF);
            List<String> data = new ArrayList<>();
            data.add(src);
            File dst = new File(latestFolder, it.name + ".csv");
            FileUtil.writeToFile(dst, data, false);
        }
        //mLF.getFile().close();
    }

    public static String getApiBody(final String link, final LF apiLf) {
        if (apiLf == null || TextUtil.isEmpty(link)) return null;

        // Delete the log file
        //apiLf.getFile().delete().open();
        OkHttpClient client = new OkHttpClient().newBuilder()
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder().url(link).build();
        String answer = "";
        TicTac2 clock = new TicTac2();
        try {
            Response response;
            ResponseBody body;

            //apiLf.log("Linking %s", link);

            //onNewCallStart();
            clock.tic();
            response = client.newCall(request).execute();
            clock.tac("%s", response);
            //apiLf.log("response = %s", response);
            //onNewCallEnded();

            clock.tic();
            body = response.body();
            if (body != null) {
                answer = body.string();
            }
            clock.tac("body length = %s", answer.length());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //apiLf.getFile().close();
        }
        return answer;
    }

    // 歷史本行營業時間牌告匯率
    // USD, 6 month
    // https://rate.bot.com.tw/xrt/quote/l6m/USD

}
