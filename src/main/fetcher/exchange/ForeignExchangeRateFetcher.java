package main.fetcher.exchange;

import flyingkite.files.FileUtil;
import flyingkite.log.Formattable;
import flyingkite.log.L;
import flyingkite.log.LF;
import flyingkite.math.statictics.Covariance;
import flyingkite.math.statictics.Stats;
import flyingkite.tool.TextUtil;
import flyingkite.tool.TicTac2;
import main.fetcher.FetcherUtil;
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
    private static final File taishinFolder = new File(rootData, "taishin");
    private static final Map<String, CURTable> loadedTable = new HashMap<>();
    // statistics for certain exchange list, maybe but, sell or cash
    // id -> list
    private static final Map<String, Stats<Double>> usedStats = new HashMap<>();
    // AUD, CAD, CHF, CNY, EUR, GBP, HKD, JPY, NZD, SEK, SGD, THB, USD, ZAR...
    private static final List<String> sortedCurrencyKey = new ArrayList<>();
    private static final CurrencyBOT enr = CurrencyBOT.EUR;

    public static void main(String[] args) {
        boolean update = 0 > 0;
        if (update) {
            saveLatestCurrency();
            mergeAllCurrency();
        }
        //loadWeb();
        // Choose bot or taishin data
        //loadAllCurrencyBOT();
        loadAllCurrencyTaiShin();
        prepare();

        eval();
    }

    // take the price of stock
    private static void loadWeb() {
        String link, s;
        link = "https://www.twse.com.tw/exchangeReport/STOCK_DAY?response=csv&date=20100101&stockNo=1217";
        s = getApiBody(link, null);
        L.log("link = %s", link);
        L.log("%s", s);

        link = "https://www.twse.com.tw/exchangeReport/STOCK_DAY?response=csv&date=20200101&stockNo=1217";
        s = getApiBody(link, null);
        L.log("link = %s", link);
        L.log("%s", s);
    }

    private static void prepare() {
        List<String> keys = sortedCurrencyKey;
        keys.clear();
        keys.addAll(loadedTable.keySet());
        Collections.sort(keys);
    }

    private static void eval() {
        evalSellRatio();
//        evalCorrelationTable();
//        evalCorrelationStock();
    }

    // 0050 = https://www.yuantafunds.com/myfund/information/1066
    // 0056 = https://www.yuantafunds.com/myfund/information/1084
    // 0055 = https://www.yuantafunds.com/myfund/information/1083

    // cov 0(0050), 1(0056) = 0.8824874301719777
    // cov 0(0050), 2(0055) = 0.7337137251213941
    // cov 1(0056), 2(0055) = 0.6256854996659044
    private static void evalCorrelationStock() {
        File src = new File("foreignCurrency/stock/ETFPrices.csv");
        List<String> all = FileUtil.readAllLines(src);
        List<List<Double>> series = new ArrayList<>();
        List<Stats<Double>> stats = new ArrayList<>();
        String[] s = all.get(1).split(",");
        int row = s.length - 1;
        for (int i = 0; i < row; i++) {
            series.add(new ArrayList<>());
        }
        for (int i = 1; i < all.size(); i++) {
            s = all.get(i).split(",");
            for (int j = 0; j < row; j++) {
                double d = Double.parseDouble(s[1+j]);
                series.get(j).add(d);
            }
        }
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < row; j++) {
                if (i == 0) {
                    Stats<Double> dj = new Stats<>(series.get(j));
                    dj.name = all.get(0).split(",")[1+j];
                    stats.add(dj);
                    L.log("#%s = %s, mean = %s, std = %s", j, dj.name, dj.mean, dj.deviation);
                }
                if (j > i) {
                    Stats<Double> si = stats.get(i);
                    Stats<Double> sj = stats.get(j);
                    Covariance<Double, Double> cov = new Covariance<>(si, sj);
                    L.log("cov %s(%s), %s(%s) = %s", i, si.name, j, sj.name, cov.correlation);
                }
            }
        }
    }

    // mu =
    // ~=0.35 ,美金
    // ~= 1 ,澳幣,加幣,法郎,人民幣,歐元,英鎊,星幣
    // ~=1.3 ,港幣,日圓,紐西蘭幣
    // ~=2 ,瑞典幣
    // ~=4.2 ,南非幣
    // ~=4.5 ,泰銖
    private static void evalSellRatio() {
        List<String> keys = sortedCurrencyKey;
        List<Stats<Double>> rates = new ArrayList<>();
        LF log = new LF(rootData, "eval.csv");
        //log.getFile().open(false);
        log.setLogToL(true);
        log.setLogToFile(false);
        log.log("Sell Ratio");
        log.log("name,mu,std,q1,q3");
        for (int i = 0; i < keys.size(); i++) {
            String k = keys.get(i);
            CURTable table = loadedTable.get(k);
            List<Double> sellRates = new ArrayList<>();
            int n = table.buy.size();
            for (int j = 1; j < n; j++) {
                // b1 s1
                // b2 s2, => (b1-b2)/s2
                double b1 = table.buy.get(j-1);
                double b2 = table.buy.get(j);
                double s2 = table.sell.get(j);
                double val = 10000 * (b1 - b2) / s2;
                sellRates.add(val);
            }
            Stats<Double> stat = new Stats<>(sellRates);
            stat.name = k;
            log.log("%s,%.4f,%.4f,%.4f,%.4f", k, stat.mean, stat.deviation, stat.quartile1, stat.quartile3);
            rates.add(stat);
        }

        log.setLogToFile(true);
        // Combine the sellRates into vertical table
        StringBuilder sb = new StringBuilder();
        sb.append("date,");
        for (int i = 0; i < keys.size(); i++) {
            sb.append(keys.get(i)).append(",");
        }
        // date,k1,k2,...,kn
        log.log("%s", sb);
        int n = rates.get(0).source.size();
        for (int i = 0; i < n; i++) {
            sb = new StringBuilder();
            for (int j = 0; j < rates.size(); j++) {
                Stats<Double> rate = rates.get(j);
                if (j == 0) {
                    CURTable table = loadedTable.get(rate.name); // it is same from key
                    sb.append(table.date.get(i)).append(",");
                }
                sb.append(String.format("%.1f,", rate.source.get(i))); // each data field
            }
            // date_i,k1_v,k2_v,...kn_v
            log.log("%s", sb);
        }
        log.getFile().close();
    }

    // Evaluate currency's correlation coefficient table
    private static void evalCorrelationTable() {
        List<String> keys = sortedCurrencyKey;
        Map<String, Stats<Double>> stats = new HashMap<>();
        List<List<Covariance<Double, Double>>> corr = new ArrayList<>();
        L.log("Correlation Table");
        int n = keys.size();
        for (int i = 0; i < n; i++) {
            corr.add(new ArrayList<>());
            for (int j = i; j < n; j++) {
                String ki = keys.get(i);
                String kj = keys.get(j);
                // add for the first statistic data
                if (i == 0) {
                    Stats<Double> di = usedStats.get(kj);
                    stats.put(kj, di);
                    L.log("#%s / %s : mean, deviation = %s, %s", j, kj, di.mean, di.deviation);
                }
                if (j > i) {
                    Covariance<Double, Double> cov = new Covariance<>(stats.get(ki), stats.get(kj));
                    //L.log("cov(%s, %s) (%02d, %02d) = %s", ki, kj, i, j, cov.correlation);
                    corr.get(i).add(cov);
                }
            }
        }
        //-- Creating excel table
        // header
        StringBuilder s = new StringBuilder("");
        for (int i = 0; i < n; i++) {
            String ki = keys.get(i);
            s.append(ki).append(",");
        }
        L.log("%s", s);
        // excel for upper triangle covariance
        double[] sum = new double[n]; // covariance of currency to system
        for (int i = 0; i < n; i++) {
            s = new StringBuilder("");
            for (int j = 0; j < n; j++) {
                double d = 0;
                Covariance<Double, Double> cij;
                if (i != j) {
                    int x = Math.min(i, j);
                    int y = Math.max(i, j);
                    cij = corr.get(x).get(y-x-1);
                    if (!Double.isNaN(cij.correlation)) {
                        d = cij.correlation;
                        s.append(cij.correlation);
                    }
                }
                s.append(",");
                if (i != j) {
                    sum[i] += d;
                    // debug used
                    if (i % 4 == 0) {
                        //L.log("sum[%s] += %s", i, d);
                    }
                }
            }
            L.log("%s", s);
        }
        s.delete(0, s.length());
        s.append("sum = ,");
        for (int i = 0; i < sum.length; i++) {
            s.append(sum[i]).append(",");
        }
        L.log("%s", s);
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
            Stats<Double> stat = new Stats<>(table.buy);
            stat.name = table.tag;

            loadedTable.put(key, table);
            usedStats.put(key, stat);
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
            Stats<Double> stat = new Stats<>(table.buy);
            stat.name = table.tag;

            loadedTable.put(key, table);
            usedStats.put(key, stat);
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
        if (TextUtil.isEmpty(link)) return null;

        // Delete the log file
        //apiLf.getFile().delete().open();
        OkHttpClient.Builder b = new OkHttpClient().newBuilder()
                .readTimeout(20, TimeUnit.SECONDS)
                ;
        FetcherUtil.setupSSL(b);
        OkHttpClient client = b.build();
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

