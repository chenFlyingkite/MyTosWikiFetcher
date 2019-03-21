package main.fetcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import flyingkite.log.LF;
import flyingkite.tool.GsonUtil;
import flyingkite.tool.TicTac2;
import main.fetcher.data.MoneyInfo;
import main.fetcher.data.StockInfo;
import main.fetcher.web.OnWebLfTT;
import main.fetcher.web.WebFetcher;
import main.kt.YahooGet;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class YahooStockFetcher implements Runnable {
    public static final YahooStockFetcher me = new YahooStockFetcher();

    private LF mLf = new LF("yahooStock");
    private LF clazzLf = new LF("yahooStock", "class.txt");
    private TicTac2 clock = new TicTac2();
    private WebFetcher fetcher = new WebFetcher();
    private OnWebLfTT onWeb = new OnWebLfTT(mLf, clock);

    private String classLink() {
        return "https://tw.stock.yahoo.com/h/getclass.php";
    }

    public List<StockInfo> links() {
        Document doc = fetcher.sendAndParseDom(classLink(), onWeb);

        Elements es = doc.getElementsByTag("table");
        List<StockInfo> si = YahooGet.me.classTable(es.get(5));
        clazzLf.getFile().open();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        GsonUtil.writeFile(clazzLf.getFile().getFile(), gson.toJson(si));
        clazzLf.getFile().close();
        //return si.subList(10, 11);
        return si;
        //水泥      食品         塑膠       紡織
        //電機      電器電纜     化學        生技醫療
        //玻璃      造紙         鋼鐵        橡膠
        //汽車      半導體       電腦週邊     光電
        //通信網路   電子零組件   電子通路     資訊服務
        //其它電子   營建        航運         觀光
        //金融      貿易百貨     油電燃氣      其他
    }

    public void parse() {
        List<StockInfo> all = links();
        List<MoneyInfo> money = new ArrayList<>();
        for (int i = 0; i < all.size(); i++) {
            StockInfo si = all.get(i);
            if (i > 0 && "其他".equals(all.get(i-1).clazz)) {
                break;
            }
            String s = si.link;

            onWeb.deleteAtPre(false);
            Document doc = fetcher.sendAndParseDom(s, onWeb);
            Elements es = doc.getElementsByTag("table");
            List<String> list = YahooGet.me.numberTable(es.get(4));
            List<Double> prices = YahooGet.me.dealTable(es.get(4));
            mLf.getFile().open();
            print(list);
            mLf.setLogToFile(false);
            for (int j = 0; j < list.size(); j++) {
                String item = list.get(j);
                String id = item.substring(0, 4);
                double div = YahooGet.me.dividend(id);
                double price = YahooGet.me.price(id);
                MoneyInfo m = new MoneyInfo();
                m.name = item;
                m.d1 = div;
                double p2 = prices.get(j);
                m.d2 = p2;
                m.link = YahooGet.me.companyLink(id);
                money.add(m);
                mLf.log("%s = %s元 / %s元", item, div, p2);
            }
            mLf.setLogToFile(true);
        }
        mLf.log("--------------");
        Collections.sort(money, new Comparator<MoneyInfo>() {
            @Override
            public int compare(MoneyInfo o1, MoneyInfo o2) {
                if (max(o1)) {
                    return 1;
                } else if (max(o2)) {
                    return -1;
                }

                double r1 = o1.d1 / o1.d2;
                double r2 = o2.d1 / o2.d2;

                return Double.compare(r2, r1);
            }

            private boolean max(MoneyInfo o) {
                if (o.name.equals("2025 千興")) {
                    return true;
                }
                return false;
            }
        });
        for (int i = 0; i < money.size(); i++) {
            MoneyInfo m = money.get(i);
            mLf.log("#%s = %s => %.3f%% , link = %s", i, m, 100 * m.d1 / m.d2, m.link);
        }
        mLf.log("--------------");
        mLf.getFile().close();
    }

    private void print(List<String> s) {
        mLf.log("%s items", s.size());
        for (int i = 0; i < s.size(); i++) {
            mLf.log("  [%2d] %s", i, s.get(i));
        }
    }

    @Override
    public void run() {

    }
}
