package main.fetcher;

import flyingkite.log.L;
import flyingkite.log.LF;
import flyingkite.tool.TicTac2;
import main.kt.BotGet;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BotGoldPassbook implements Runnable {
    private LF mLf = new LF("botGold");
    private TicTac2 clock = new TicTac2();

    public String mainLink() {
        return "https://rate.bot.com.tw/gold/chart/year/TWD";
    }

    public String sendRequest() {
        String link = mainLink();
        OkHttpClient client = new OkHttpClient();
        Request req = new Request.Builder().url(link).build();
        String body = null;

        // Delete the log file
        mLf.getFile().delete().open();
        try {
            Response res;
            mLf.log("Linking %s", link);
            clock.tic();
            res = client.newCall(req).execute();
            clock.tac("request sent");
            mLf.log("request sent");
            ResponseBody rb = res.body();
            if (rb != null) {
                body = rb.string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mLf.getFile().close();
        }
        return body;
    }

    @Override
    public void run() {
        String body = sendRequest();
        Document doc = Jsoup.parse(body);
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

        for (int i = 2; i < 6; i++) { // sell at
            for (int j = 1; j < i; j++) { // buy at
                evalBuySell(j, i, table);
            }
        }
        mLf.log("\n\n");

        for (List<String> list : table) {
            printAll(list);
        }
    }

    private void evalBuySell(int buyAt, int sellAt, List<List<String>> table) {
        List<String> buys = table.get(buyAt);
        List<String> sells = table.get(sellAt);
        int n = Math.min(buys.size(), sells.size());
        //n = 4; // eval n records(weeks)

        int sum = 0;
        // 日期 牌價幣別 商品重量 本行買入價格 本行賣出價格
        for (int i = 0; i < n; i++) {
            int buyP = Integer.parseInt(buys.get(i).split(",")[4]); //
            int sellP = Integer.parseInt(sells.get(i).split(",")[3]); //
            sum += sellP - buyP;
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
