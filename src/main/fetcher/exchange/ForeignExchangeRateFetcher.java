package main.fetcher.exchange;

import flyingkite.files.FileUtil;
import flyingkite.log.Formattable;
import flyingkite.log.L;
import flyingkite.log.LF;
import flyingkite.math.statictics.Covariance;
import flyingkite.math.statictics.Stats;
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
import java.util.Arrays;
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
    private static final File taishinFolder = new File(rootData, "taishin");
    private static final Map<String, CURTable> loadedTable = new HashMap<>();
    // statistics for certain exchange list, maybe but, sell or cash
    // id -> list
    private static final Map<String, Stats<Double>> usedStats = new HashMap<>();
    private static final CurrencyBOT enr = CurrencyBOT.EUR;

    public static void main(String[] args) {
        boolean update = 0 > 0;
        if (update) {
            saveLatestCurrency();
            mergeAllCurrency();
        }
        // Choose bot or taishin data
        //loadAllCurrencyBOT();
        loadAllCurrencyTaiShin();

        eval();
    }

    private static void eval() {
        evalSellThreshold();
        evalCorrelationTable();
    }

    // mu =
    // ~=0.35 ,美金
    // ~= 1 ,澳幣,加幣,法郎,人民幣,歐元,英鎊,星幣
    // ~=1.3 ,港幣,日圓,紐西蘭幣
    // ~=2 ,瑞典幣
    // ~=4.2 ,南非幣
    // ~=4.5 ,泰銖
    private static void evalSellThreshold() {
        List<String> keys = new ArrayList<>(loadedTable.keySet());
        Collections.sort(keys);
        L.log("Sell Threshold");
        L.log("name,mu,std,q1,q3");
        for (int i = 0; i < keys.size(); i++) {
            String k = keys.get(i);
            CURTable table = loadedTable.get(k);
            List<Double> sellRates = new ArrayList<>();
            int n = table.buy.size();
            for (int j = 0; j < n; j++) {
                double buy = table.buy.get(j);
                double sell = table.sell.get(j);
                double val = 100 * (sell - buy) / buy;
                sellRates.add(val);
            }
            Stats<Double> stat = new Stats<>(sellRates);
            L.log("#%s : %s -> (in %%) ,%s,%s,%s,%s", i, k, stat.mean, stat.deviation, stat.quartile1, stat.quartile3);
        }
    }

    // Evaluate currency's correlation coefficient table
    private static void evalCorrelationTable() {
        List<String> si = new ArrayList<>(loadedTable.keySet());
        Collections.sort(si);
        List<Stats<Double>> stats = new ArrayList<>();
        List<List<Covariance<Double, Double>>> corr = new ArrayList<>();
        L.log("Correlation Table");
        int n = si.size();
        for (int i = 0; i < n; i++) {
            corr.add(new ArrayList<>());
            for (int j = i; j < n; j++) {
                String ki = si.get(i);
                String kj = si.get(j);
                // add for the first statistic data
                if (i == 0) {
                    Stats<Double> di = usedStats.get(kj);
                    stats.add(di);
                    //L.log("#%s / %s : mean, deviation = %s, %s", j, kj, di.mean, di.deviation);
                }
                if (i != j) {
                    Covariance<Double, Double> cov = new Covariance<>(stats.get(i), stats.get(j));
                    //L.log("cov(%s, %s) (%02d, %02d) = %s", ki, kj, i, j, cov.correlation);
                    corr.get(i).add(cov);
                }
            }
        }
        //-- Creating excel table
        // header
        String s = "";
        for (int i = 0; i < n; i++) {
            String ki = si.get(i);
            s += ki + ",";
        }
        L.log("%s", s);
        // excel for upper triangle covariance
        double[] sum = new double[n]; // covariance of currency to system
        for (int i = 0; i < n; i++) {
            s = "";
            for (int j = 0; j < n; j++) {
                double d = 0;
                if (j > i) {
                    Covariance<Double, Double> cij = corr.get(i).get(j-i-1);
                    s += cij.correlation + ",";
                    if (j != n-1) {
                        d = cij.correlation;
                    }
                } else {
                    s += ",";
                    // lower triangle
//                    if (j < i) {
//                        Covariance<Double, Double> cij = corr.get(j).get(i-j-1);
//                        s += cij.correlation + ",";
//                        if (i != n-1) {
//                            d = cij.correlation;
//                        }
//                    }
                }
                if (i != j) {
                    sum[i] += d;
                    // debug used
//                    if (i % 5 == 0) {
//                        L.log("sum[%s] += %s", i, d);
//                    }
                }
            }
            L.log("%s", s);
        }
        L.log("sum = %s", Arrays.toString(sum));
    }


    private static void loadAllCurrencyTaiShin() {
        File root = taishinFolder;
        File[] csvs = root.listFiles();
        L.log("%s items in folder %s", csvs.length, root);
        for (int i = 0; i < csvs.length; i++) {
            File it = csvs[i];
            List<String> srcAll = FileUtil.readAllLines(it);
            CURTable table = new CURTable();
            String key = "";
            for (int j = 2; j < srcAll.size(); j++) {
                String li = srcAll.get(j);
                String[] ss = li.split(",");

                key = CurrencyBOT.find(it.getName()).id;
                table.tag = key;
                table.date.add(ss[0]);
                table.buy.add(Double.parseDouble(ss[1]));
                table.sell.add(Double.parseDouble(ss[2]));
            }
            loadedTable.put(key, table);
            usedStats.put(key, new Stats<>(table.buy));
        }
        L.log("%s items in table", loadedTable.size());
        for (String k : loadedTable.keySet()) {
            L.log("%s -> %s", k, loadedTable.get(k));
        }
    }

    private static void loadAllCurrencyBOT() {
        File root = botFolder;
        File[] csvs = root.listFiles();
        L.log("%s items in folder %s", csvs.length, root);
        for (int i = 0; i < csvs.length; i++) {
            File it = csvs[i];
            List<String> srcAll = FileUtil.readAllLines(it);
            CURTable table = new CURTable();
            String key = "";
            for (int j = 1; j < srcAll.size(); j++) {
                String li = srcAll.get(j);
                String[] ss = li.split(",");

                key = ss[1];
                table.tag = key;
                table.date.add(ss[0]);
                // 2 = 本行買入, 12 = 本行賣出
                int b = 2;
                table.buy.add(Double.parseDouble(ss[b + 1]));
                table.cashBuy.add(Double.parseDouble(ss[b + 2]));
                b = 12;
                table.sell.add(Double.parseDouble(ss[b + 1]));
                table.cashSell.add(Double.parseDouble(ss[b + 2]));
            }
            loadedTable.put(key, table);
            usedStats.put(key, new Stats<>(table.buy));
        }
        L.log("%s items in table", loadedTable.size());
        for (String k : loadedTable.keySet()) {
            L.log("%s -> %s", k, loadedTable.get(k));
        }
    }

    private static class CURTable implements Formattable {
        public String tag;
        public List<String> date = new ArrayList<>();
        public List<Double> buy = new ArrayList<>();
        public List<Double> sell = new ArrayList<>();
        public List<Double> cashBuy = new ArrayList<>();
        public List<Double> cashSell = new ArrayList<>();

        @Override
        public String toString() {
            int n = date.size();
            String s = _fmt("%s ~ %s (%s items), buy = %s ~ %s, sell = %s ~ %s"
                    , date.get(0), date.get(n-1), n
                    , buy.get(0), buy.get(n-1), sell.get(0), sell.get(n-1)
            );
            return s;
        }
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
