package main.fetcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import flyingkite.log.L;
import flyingkite.log.LF;
import flyingkite.tool.GsonUtil;
import flyingkite.tool.TicTac2;
import main.fetcher.data.MoneyInfo;
import main.fetcher.data.StockInfo;
import main.fetcher.web.OnWebLfTT;
import main.fetcher.web.WebFetcher;
import main.kt.YahooGet;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class YahooStockFetcher implements Runnable {
    public static final YahooStockFetcher me = new YahooStockFetcher();

    private static final String FOLDER = "yahooStock";
    private LF mLf = new LF(FOLDER);
    private TicTac2 clock = new TicTac2();
    private WebFetcher fetcher = new WebFetcher();
    private OnWebLfTT onWeb = new OnWebLfTT(mLf, clock);

    // 上櫃/興櫃公司專區 > 上櫃/興櫃公司資訊 > 上櫃公司資訊查詢
    // https://www.tpex.org.tw/web/regular_emerging/corporateInfo/regular/regular_stock.php?l=zh-tw
    private String classLink() {
        // https://tw.stock.yahoo.com/class/
        return "https://tw.stock.yahoo.com/h/getclass.php";
    }
    // 上市類股 : id = LISTED_STOCK
    // Listed Company
    // 上櫃類股 : id = OVER_THE_COUNTER_STOCK
    // TPEx Listed Company
    // 電子產業 : id = ELECTRONICS_INDUSTRY
    // 概念股 : id = CONCEPT_STOCK
    // 集團股 : id = CONSORTIUM_STOCK

    private void listedStock() {
        Document doc = fetcher.getDocument(classLink());
        Element main = doc.getElementById("LISTED_STOCK");
        Element want = main.getElementsByTag("ul").get(0);
        List<StockInfo> si = YahooGet.me.fetchStockInfo(want);
        LF industry = new LF(FOLDER, "m_industry.txt");
        prettyWriteJsonFile(industry, si);

        if (1 > 0) {
            for (int i = 0; i < si.size(); i++) {
                L.log("上市類股 #%d: %s", i + 1, si.get(i));
            }
        }

//        上市類股                   大盤指數走勢   類股指數走勢
//        水泥      食品      塑膠        紡織      電機
//        電器電纜  化學      生技        玻璃      造紙
//        鋼鐵      橡膠      汽車        半導體    電腦週邊
//        光電      通訊網路  電子零組件  電子通路  資訊服務
//        其他電子  營建      航運        觀光      金融業
//        貿易百貨  油電燃氣  存託憑證    ETF       受益證券
//        ETN       其他      市認購      市認售    指數類
//        市牛證    市熊證
    }

    private void counterStock() {
        Document doc = fetcher.getDocument(classLink());
        Element main = doc.getElementById("OVER_THE_COUNTER_STOCK");
        Element want = main.getElementsByTag("ul").get(0);
        List<StockInfo> si = YahooGet.me.fetchStockInfo(want);
        LF industry = new LF(FOLDER, "t_industry.txt");
        prettyWriteJsonFile(industry, si);

        if (1 > 0) {
            for (int i = 0; i < si.size(); i++) {
                L.log("上櫃類股 #%d: %s", i + 1, si.get(i));
            }
        }
//        上櫃類股     櫃檯指數走勢
//        櫃食品      櫃塑膠      櫃紡織      櫃電機        櫃電器電纜
//        櫃化學      櫃生技      櫃鋼鐵      櫃橡膠        櫃半導體
//        櫃電腦週邊  櫃光電      櫃通訊網路  櫃電子零組件  櫃電子通路
//        櫃資訊服務  櫃其他電子  櫃營建      櫃航運        櫃觀光
//        櫃金融      櫃貿易百貨  櫃油電燃氣  櫃文化創意    櫃農業科技業
//        櫃電子商務  櫃其他      櫃公司債    櫃ETF         櫃ETN
//        櫃認購      櫃認售      櫃指數類
    }

    private void prettyWriteJsonFile(LF lf, Object obj) {
        lf.getFile().open();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        GsonUtil.writeFile(lf.getFile().getFile(), gson.toJson(obj));
        lf.getFile().close();
    }

    @Deprecated
    private List<StockInfo> marketLinks() { // 集中市場當日行情表
        Document doc = fetcher.getDocument(classLink());
        LF clazzLf = new LF(FOLDER, "m_class.txt");
        Elements es = doc.getElementsByTag("ul");

        List<StockInfo> si = YahooGet.me.fetchStockInfo(es.get(5));
        L.log("si = %s", si);
        clazzLf.getFile().open();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        gson = new Gson();
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

    @Deprecated
    private List<StockInfo> tableLinks() { // 櫃檯買賣市場行情
        Document doc = fetcher.getDocument(classLink());
        LF clazzLf = new LF(FOLDER, "t_class.txt");

        Elements es = doc.getElementsByTag("table");
        List<StockInfo> si = YahooGet.me.fetchStockInfo(es.get(8));
        clazzLf.getFile().open();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        GsonUtil.writeFile(clazzLf.getFile().getFile(), gson.toJson(si));
        clazzLf.getFile().close();
        //return si.subList(10, 11);
        return si;
        // 食品         塑膠       紡織       電機
        // 電器     化工        生技  油電
        // 鋼鐵   橡膠   半導  電腦
        // 光電  通信   電零 通路
        // 資服  他電 營建  航運
        // 觀光  金融  貿易 指數類
        // 其他
    }

    public void parse() {
        listedStock();
        counterStock();
    }

    @Deprecated
    public void parse2() {
        List<MoneyInfo> market = findMarket(marketLinks(), "m");
        List<MoneyInfo> table = findMarket(tableLinks(), "t");

        market.sort((o1, o2) -> Double.compare(o1.d2, o2.d2));
        printToFile(market, new LF(FOLDER, "m_price.txt"));

        table.sort((o1, o2) -> Double.compare(o1.d2, o2.d2));
        printToFile(table, new LF(FOLDER, "t_price.txt"));
    }

    @Deprecated
    private List<MoneyInfo> findMarket(List<StockInfo> all, String prefix) { // 集中市場
        //List<StockInfo> all = links();
        List<MoneyInfo> cash1 = new ArrayList<>();
        List<MoneyInfo> cash2 = new ArrayList<>();
        for (int i = 0; i < all.size(); i++) {
            StockInfo si = all.get(i);
            if ("指數類".equals(si.name)) {
                continue;
            }
            if (i > 0 && "其他".equals(all.get(i-1).name)) {
                break;
            }
            String s = si.link;

            onWeb.deleteAtPre(false);
            Document doc = fetcher.getDocument(s);
            Elements es = doc.getElementsByTag("table");
            List<String> list = YahooGet.me.numberTable(es.get(4));
            List<Double> prices = YahooGet.me.dealTable(es.get(4));
            mLf.getFile().open();
            print(list);
            mLf.setLogToFile(false);
            for (int j = 0; j < list.size(); j++) {
                String item = list.get(j);
                String id = item.substring(0, 4);
                double[] divs = YahooGet.me.dividend(id);
                //double price = YahooGet.me.price(id);
                double p2 = prices.get(j);
                // Create for cash
                MoneyInfo m = new MoneyInfo();
                m.name = item;
                m.d1 = divs[0];
                m.d2 = p2;
                m.link = YahooGet.me.companyLink(id);
                cash1.add(m);
                // Create for cash + stock
                MoneyInfo m2 = m.copy();
                m2.d1 = divs[0] + divs[1];
                cash2.add(m2);
                mLf.log("%s = %s元 / %s元", item, m2.d1, p2);
            }
            mLf.setLogToFile(true);
        }
        mLf.getFile().open();
        mLf.log("--------------");
        sortByRateReturn(cash1);
        sortByRateReturn(cash2);
        LF lf1 = new LF(FOLDER, prefix + "_cash.txt");
        LF lf2 = new LF(FOLDER, prefix + "_cash+stock.txt");
        printToFile(cash1, lf1);
        printToFile(cash2, lf2);
        mLf.getFile().close();
        return new ArrayList<>(cash1);
    }

    @Deprecated
    private void printToFile(List<MoneyInfo> list, LF lf) {
        lf.getFile().delete().open();
        for (int i = 0; i < list.size(); i++) {
            MoneyInfo m = list.get(i);
            lf.log("#%03d = %s => %.3f%% , link = \n  %s", i, m, 100 * m.d1 / m.d2, m.link);
        }
        lf.getFile().close();
    }

    @Deprecated
    private void sortByRateReturn(List<MoneyInfo> list) {
        list.sort(new Comparator<>() {
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
