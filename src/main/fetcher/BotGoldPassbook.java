package main.fetcher;

import flyingkite.log.LF;
import flyingkite.math.Statistics;
import flyingkite.tool.TicTac2;
import main.fetcher.web.OnWebLfTT;
import main.fetcher.web.WebFetcher;
import main.kt.BotGet;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BotGoldPassbook implements Runnable {
    private LF mLf = new LF("botGold");
    private TicTac2 clock = new TicTac2();
    private WebFetcher fetcher = new WebFetcher();

    public String mainLink() {
        return "https://rate.bot.com.tw/gold/chart/year/TWD";
    }

    public Document sendRequest() {
        return fetcher.sendAndParseDom(mainLink(), onWeb);
    }

    private OnWebLfTT onWeb = new OnWebLfTT(mLf, clock);

    @Override
    public void run() {
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
