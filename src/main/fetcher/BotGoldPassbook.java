package main.fetcher;

import flyingkite.files.FileUtil;
import flyingkite.log.L;
import flyingkite.log.LF;
import flyingkite.math.Statistics;
import flyingkite.math.statictics.Stats;
import flyingkite.tool.TicTac2;
import main.fetcher.finance.currency.FinTable;
import main.fetcher.web.OnWebLfTT;
import main.fetcher.web.WebFetcher;
import main.kt.BotGet;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BotGoldPassbook implements Runnable {
    private LF mLf = new LF("botGold");
    private TicTac2 clock = new TicTac2();
    private WebFetcher fetcher = new WebFetcher();
    private static final File rootData = new File("botGold");
    private static final File taishinFolder = new File(rootData, "taishin");


    public String mainLink() {
        return "https://rate.bot.com.tw/gold/chart/year/TWD";
    }

    public Document sendRequest() {
        return fetcher.getDocument(mainLink());
    }

    private OnWebLfTT onWeb = new OnWebLfTT(mLf, clock);

    @Override
    public void run() {
        botGold();
        //taishinGold();
    }

    // taishin gold, (%%)
    // sell rate = 95.5, std = 12, 81 - 90 - 98 - 262
    // increase rate of daily =
    // volatility = 2.3, 101.7, -469.6, -46.7, 58.9, 498.5
    private void taishinGold() {
        File taishin = taishinFolder;
        boolean removeCommaSrc = false;
        File file = new File(taishin, "goldXAUTWD_src.txt");
        File file2 = new File(taishin, "goldXAUTWD.txt");
        List<String> data;
        if (removeCommaSrc) {
            data = FileUtil.readAllLines(file);
            // x = "2022/09/29	52,697.00	53,187.00"
            // o = "2022/09/29,52697.00,53187.00"
            for (int i = 1; i < data.size(); i++) {
                String[] ss = data.get(i).split(",");
                data.set(i, ss[0] + "," + ss[1] + ss[2] + "," + ss[3] + ss[4]);
            }
            FileUtil.writeToFile(file2, data, false);
        } else {
            data = FileUtil.readAllLines(file2);
        }

        FinTable table = new FinTable();
        table.tag = FileUtil.getNameBeforeExtension(file2);
        for (int i = 1; i < data.size(); i++) {
            String[] ss = data.get(i).split(",");
            table.date.add(ss[0]);
            double v1 = Double.parseDouble(ss[1]);
            double v2 = Double.parseDouble(ss[2]);
            table.buy.add(v1);
            table.sell.add(v2);
            table.cashBuy.add(10000 * (v2 - v1) / v1);
            if (i > 1) {
                double v0 = table.buy.get(i-2);
                double r = 10000 * (v0 - v1) / v2;
                table.cashSell.add(r);
            }
        }

        Stats<Double> stat = new Stats<>(table.cashBuy);
        stat.name = "d_Buy/Sell Rate";

        Stats<Double> st;
        st = stat;
        L.log("%s", st.name);
        L.log("name,mu,std,min,q1,q3,max");
        L.log("%s,%4f,%4f,%4f,%4f,%4f,%4f", "", st.mean, st.deviation, st.min, st.quartile1, st.quartile3, st.max);

        Stats<Double> stat2 = new Stats<>(table.cashSell);
        stat2.name = "Buy Increase Rate";
        st = stat2;
        L.log("%s", st.name);
        L.log("name,mu,std,min,q1,q3,max");
        L.log("%s,%4f,%4f,%4f,%4f,%4f,%4f", "", st.mean, st.deviation, st.min, st.quartile1, st.quartile3, st.max);
    }

    private void botGold() {
        Document doc = sendRequest();
        Elements es = doc.getElementsByTag("table");
        List<String> data = BotGet.me.goldTable(es);
        mLf.getFile().open();
        eval(data);
        mLf.getFile().close();
    }

    private void eval(List<String> data) {
        List<List<String>> table = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            table.add(new ArrayList<>());
        }

        // split data as day 0 ~ day 6
        for (int i = 0; i < data.size(); i++) {
            String s = data.get(i);
            String date = s.split(",")[0];
            Date d = new Date(date);
            table.get(d.getDay()).add(s);
        }

        evalStatistics(data);
        mLf.log("----------");
        for (int i = 2; i < 6; i++) { // sell at
            for (int j = 1; j < i; j++) { // buy at
                evalBuySell(j, i, table);
            }
        }
        mLf.log("----------");
        for (int i = 1; i < 6; i++) {
            evalBuySellWeek(i, table);
        }
        mLf.log("\n\n");

        for (List<String> list : table) {
            printAll(list);
        }
    }

    private void evalStatistics(List<String> data) {
        List<Integer> dx = new ArrayList<>();
        List<Integer> dy = new ArrayList<>();
        for (int i = 1; i < data.size(); i++) {
            String[] d = data.get(i-1).split(",");
            String[] c = data.get(i).split(",");
            int thiz = Integer.parseInt(d[3]);
            int prev = Integer.parseInt(c[3]);
            int dt = thiz - prev;
            dx.add(dt);
            dy.add(Math.abs(dt));
        }
        double mdx = Statistics.mean(dx);
        double mdy = Statistics.mean(dy);
        double ddx = Statistics.deviation(dx);
        double ddy = Statistics.deviation(dy);

        mLf.log("Daily volatility on %s data", data.size());
        mLf.log("      mean = %.2f,     std = %.2f", mdx, ddx);
        mLf.log("  abs.mean = %.2f, abs.std = %.2f", mdy, ddy);
    }

    private void evalBuySellWeek(int buyAt, List<List<String>> table) {
        List<String> buys = table.get(buyAt);
        int n = buys.size() / 2; // transaction times
        //n = 2;
        int endB = 0;
        int endS = 0;
        // perform buy, sell, buy, sell on weekly
        for (int i = 0; i < n; i++) {
            String[] thiz = buys.get(2*i).split(",");
            String[] prev = buys.get(2*i + 1).split(",");
            int b, s;
            // end with buy
            b = Integer.parseInt(thiz[4]); //
            s = Integer.parseInt(prev[3]); //
            endB += s - b;

            // end with sell
            b = Integer.parseInt(thiz[3]); //
            s = Integer.parseInt(prev[4]); //
            endS += s - b;
        }
        double avg = 0.5 * (endB + endS);
        mLf.log("buy & sell weekly at %s     =>  %s data, avg = %.1f, bsb = $%s, sbs = %s", buyAt, n, avg, endB, endS);
    }

    private void evalBuySell(int buyAt, int sellAt, List<List<String>> table) {
        List<String> buys = table.get(buyAt);
        List<String> sells = table.get(sellAt);
        int n = Math.min(buys.size(), sells.size());
        //n = 2; // eval n records(weeks)

        int sum = 0;
        // 日期 牌價幣別 商品重量 本行買入價格 本行賣出價格
        for (int i = 0; i < n; i++) {
            int buyP = Integer.parseInt(buys.get(i).split(",")[4]); //
            int sellP = Integer.parseInt(sells.get(i).split(",")[3]); //
            sum += sellP - buyP;
            //mLf.log("%s - %s", sellP, buyP);
        }
        mLf.log("buy %s sell %s     =>     %s data gets $%s", buyAt, sellAt, n, sum);
    }

    private void printAll(List<String> s) {
        mLf.log("%s items", s.size());
        for (int i = 0; i < s.size(); i++) {
            mLf.log("  [%2d] %s", i, s.get(i));
        }
    }


}
