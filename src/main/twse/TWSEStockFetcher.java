package main.twse;

import com.google.gson.Gson;
import flyingkite.log.L;
import flyingkite.log.LF;
import flyingkite.tool.GsonUtil;
import flyingkite.tool.TextUtil;
import flyingkite.tool.TicTac2;
import main.fetcher.FetcherUtil;
import main.fetcher.data.stock.YHDividend;
import main.fetcher.data.stock.YHStockPrice;
import main.fetcher.data.stock.YHYearDiv;
import main.fetcher.web.OnWebLfTT;
import main.fetcher.web.WebFetcher;
import main.kt.TWSEGet;
import main.kt.YahooGet;
import org.jsoup.nodes.Document;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 台灣銀行
// https://rate.bot.com.tw/twd?Lang=zh-TW
// 台新銀行
// https://www.taishinbank.com.tw/TSB/personal/deposit/lookup/NTD/
// 玉山銀行
// https://www.esunbank.com.tw/bank/personal/deposit/rate/twd/deposit-rate
// 中國信託
// https://www.ctbcbank.com/twrbc/twrbc-general/ot001
// 聯邦銀行
// https://mybank.ubot.com.tw/ubot/#/B0401001A?type=1
public class TWSEStockFetcher {
    public static final TWSEStockFetcher me = new TWSEStockFetcher();

    private static final String FOLDER = "twseStock";
    private LF mLf = new LF(FOLDER);
    private TicTac2 clock = new TicTac2();
    private WebFetcher fetcher = new WebFetcher();
    private OnWebLfTT onWeb = new OnWebLfTT(mLf, clock);

    private final Gson gson = new Gson();

    // en
    // https://www.twse.com.tw/en/page/products/stock-code2.html
    // https://www.twse.com.tw/zh/page/products/stock-code2.html
//    本國上櫃證券國際證券辨識號碼一覽表
//    https://isin.twse.com.tw/isin/C_public.jsp?strMode=4

    // https://pchome.megatime.com.tw/group/mkt0/cid05_2.html
    // 上櫃/興櫃公司專區 > 上櫃/興櫃公司資訊 > 上櫃公司資訊查詢
    // https://www.tpex.org.tw/web/regular_emerging/corporateInfo/regular/regular_stock.php?l=zh-tw

    // 本國未上市，未上櫃公開發行證券，國際證券辨識號碼一覽表
    // https://isin.twse.com.tw/isin/C_public.jsp?strMode=1
    private TWEquityList getTWSEUnlisted() {
        String link = "https://isin.twse.com.tw/isin/C_public.jsp?strMode=1";
        return parseEquityList(link);
    }

    // 本國上市證券國際證券辨識號碼一覽表
    // https://isin.twse.com.tw/isin/C_public.jsp?strMode=2
    private TWEquityList getTWSEListed() {
        String link = "https://isin.twse.com.tw/isin/C_public.jsp?strMode=2";
        return parseEquityList(link);
    }

    // 本國上市債券，上櫃債券，國際證券辨識號碼一覽表
    // https://isin.twse.com.tw/isin/C_public.jsp?strMode=3
    private TWEquityList getTWSEListedBond() {
        String link = "https://isin.twse.com.tw/isin/C_public.jsp?strMode=3";
        return parseEquityList(link);
    }

    // 本國上櫃證券國際證券辨識號碼一覽表
    // https://isin.twse.com.tw/isin/C_public.jsp?strMode=4
    private TWEquityList getTWSETPEx() {
        String link = "https://isin.twse.com.tw/isin/C_public.jsp?strMode=4";
        return parseEquityList(link);
    }

    // 本國興櫃證券國際證券辨識號碼一覽表
    // https://isin.twse.com.tw/isin/C_public.jsp?strMode=5
    private TWEquityList getEmerging() {
        String link = "https://isin.twse.com.tw/isin/C_public.jsp?strMode=5";
        return parseEquityList(link);
    }

    // 本國期貨及選擇權國際證券辨識號碼一覽表
    // https://isin.twse.com.tw/isin/C_public.jsp?strMode=6
    private TWEquityList getFutureOption() {
        String link = "https://isin.twse.com.tw/isin/C_public.jsp?strMode=6";
        return parseEquityList(link);
    }

    // 本國開放式證券投資信託基金，國際證券辨識號碼一覽表
    // https://isin.twse.com.tw/isin/C_public.jsp?strMode=7
    private TWEquityList getTrustFund() {
        String link = "https://isin.twse.com.tw/isin/C_public.jsp?strMode=7";
        return parseEquityList(link);
    }

    // 本國未公開發行之創櫃板證券國際證券辨識號碼一覽表
    // https://isin.twse.com.tw/isin/C_public.jsp?strMode=8
    private TWEquityList getGISA() {
        String link = "https://isin.twse.com.tw/isin/C_public.jsp?strMode=8";
        return parseEquityList(link);
    }

    // 登錄買賣黃金現貨國際證券辨識號碼一覽表
    // https://isin.twse.com.tw/isin/C_public.jsp?strMode=9
    private TWEquityList getTPExGold() {
        String link = "https://isin.twse.com.tw/isin/C_public.jsp?strMode=9";
        return parseEquityList(link);
    }

    // 外幣計價可轉換定期存單，國際證券辨識號碼一覽表
    // https://isin.twse.com.tw/isin/C_public.jsp?strMode=10
    private TWEquityList getForeignCurrencyNCD() {
        String link = "https://isin.twse.com.tw/isin/C_public.jsp?strMode=10";
        return parseEquityList(link);
    }

    // 本國指數國際證券辨識號碼一覽表
    // https://isin.twse.com.tw/isin/C_public.jsp?strMode=11
    private TWEquityList getDomesticIndex() {
        String link = "https://isin.twse.com.tw/isin/C_public.jsp?strMode=11";
        return parseEquityList(link);
    }

    private TWEquityList parseEquityList(String link) {
        Document doc = fetcher.getDocument(link);
        int type = readType(link);
        //L.log("doc = %s", doc);
        clock.tic();
        TWEquityList ans = TWSEGet.me.parseEquityList(doc, type);
        long ms = clock.tac("parseEquityList OK, %s", link);
        // print to log
        mLf.log("[%s] : parseEquityList OK, %s", ms, link);
        ans.print(mLf);
        return ans;
    }

    public static void main(String[] args) {
        me.parse();
    }

    private void parse() {
        clock.tic();
        mLf.getFile().open(false);
        if (1 > 0) {
            // database
            loadAllISINCode(); // < 30 second
            loadAllDividend(); // ~70min
        }
        loadPrices();
        mLf.getFile().close();
        //YahooGet.me.getPrices("1907.TW"); // 永豐餘 only
        //YahooGet.me.getPrices("2910.TW");
        clock.tac("TWSEStockFetcher parse OK");
    }

    private Map<Integer, TWEquityList> allTWEquity = new HashMap<>();
    private Map<Integer, List<YHDividend>> allDividend = new HashMap<>();

    // Load all ISIN code and put into allTWEquity
    private void loadAllISINCode() {
        clock.tic();
        for (int i = 1; i <= 11; i++) {
            TWEquityList li = loadISINCode(i);
            allTWEquity.put(i, li);
        }
        clock.tac("loadAllISINCode OK");
    }

    private TWEquityList loadISINCode(int type) {
        clock.tic();
        TWEquityList list = getISINCodeList(type);
        // load isin code as json
        FetcherUtil.saveAsJson(list, FOLDER, getISINFilename(type));
        clock.tac("getISINCodeList OK, type = %s", type);
        // print as simple csv
        writeISINCSV(list, type); // fast to be < 30 ms
        return list;
    }

    // print as simple csv
    private void writeISINCSV(TWEquityList list, int type) {
        LF lf = new LF(FOLDER, String.format("isinCode/isin_e_%02d.csv", type));
        lf.setLogToL(false);
        lf.getFile().open(false);
        List<TWEquityItem> li = list.list;
        int n = li.size();
        lf.log("part,%s", n);// format 1st line
        for (int i = 0; i < n; i++) {
            TWEquityItem it = li.get(i);
            lf.log("name,%s", it.name); // format 2nd line
            List<TWEquity> twe = it.list;
            for (int j = 0; j < twe.size(); j++) {
                TWEquity ej = twe.get(j);
                lf.log(",,%s,%s", ej.code, ej.name); // format of each row
            }
        }
        lf.getFile().close();
    }

    // Load all stock's dividend info, stock is defined block in stockTitle
    // Big-Oh of time
    // 40 tasks; dividend = 18845, avg = 471.125; prices = 35660, avg = 891.5
    // all ~= 70 min
    // 1757 tasks; dividend = 721198, avg = 410.4712578258395; prices = 3607870, avg = 2053.426294820717; [4329280] : loadAllDividend OK; [4334487] : TWSEStockFetcher parse OK
    private void loadAllDividend() {
        boolean log = 1 > 0;
        // preview snippet count, 0 = no preview, 5 = preview 5 item for each type
        int preview = 20 * 0;
        //--
        long msPrices = 0;
        long msDividend = 0;
        long msDt;

        clock.tic();
        allDividend.clear();
        allDividendValues.clear();
        allPrices.clear();
        makeStockTitle();
        YHStockPrice head = getRemarkPrice();
        allPrices.add(head);
        for (int i = 1; i <= 11; i++) {
            if (stockTitle.containsKey(i) == false) continue;

            TWEquityList list = allTWEquity.get(i);
            List<YHDividend> divs = new ArrayList<>();
            allDividend.put(i, divs);
            int k = stockTitle.get(i);
            List<TWEquity> eqs = list.list.get(k).list;
            int n = preview == 0 ? eqs.size() : preview;
            for (int j = 0; j < n; j++) {
                TWEquity ei = eqs.get(j);
                String symbol = ei.getSymbol();
                clock.tic();
                YHDividend div = YahooGet.me.getDividend(ei.code);
                msDt = clock.tac("getDividend %s", ei.code);
                msDividend += msDt;
                div.equity = ei;
                divs.add(div);
                allDividendValues.add(div);

                // -- get price values
                clock.tic();
                YHStockPrice price = YahooGet.me.getPrices(symbol);
                msDt = clock.tac("getPrices %s", symbol);
                msPrices += msDt;
                // fill in basic values
                price.code = ei.code;
                price.name = ei.name;
                price.trim();
                if (log) {
                    mLf.log("price #%s (%s) = %s", j, ei.code, gson.toJson(price));
                }
                allPrices.add(price);
            }
            FetcherUtil.saveAsJson(divs, dividendPath(i));
        }
        FetcherUtil.saveAsJson(allPrices, pricePath());
        int count = allPrices.size() - 1;
        mLf.log("%d tasks", count);
        mLf.log("time dividend = %s, avg = %s", msDividend, 1.0 * msDividend / count);
        mLf.log("time prices = %s, avg = %s", msPrices, 1.0 * msPrices / count);
        clock.tac("loadAllDividend OK");
    }

    private YHStockPrice[] loadPriceFromFile() {
        YHStockPrice[] prices = new YHStockPrice[1];
        File src = new File(pricePath());
        return GsonUtil.loadFile(src, prices.getClass());
    }

    private YHDividend[] loadDividendFromFile(int type) {
        YHDividend[] divid = new YHDividend[1];
        File src = new File(dividendPath(type));
        return GsonUtil.loadFile(src, divid.getClass());
    }

    private void loadPrices() {
        Map<String, YHStockPrice> priceMap = new HashMap<>();
        // load prices from src file
        int n;
        // load Yahoo stock prices
        YHStockPrice[] prices = loadPriceFromFile();
        n = prices.length;
        mLf.log("%s prices", n);
        for (int i = 0; i < n; i++) {
            YHStockPrice p = prices[i];
            if (TextUtil.isEmpty(p.code)) {
            } else {
                priceMap.put(p.code, p);
            }
            String s = gson.toJson(p);
            mLf.log("#%d = %s", i, s);
        }

        // load dividend
        List<YHDividend> dividends = new ArrayList<>();
        for (int i = 1; i <= 11; i++) {
            if (stockTitle.containsKey(i) == false) continue;
            YHDividend[] divid = loadDividendFromFile(i);
            for (int j = 0; j < divid.length; j++) {
                YHDividend dj = divid[j];
                dividends.add(dj);
            }
        }
        n = dividends.size();
        mLf.log("%s dividends", n);
        clock.tic();
        String year = "2020";
        List<YHDividend> sorted = sortYearDividend(year, dividends, priceMap);
        FetcherUtil.saveAsJson(sorted, FOLDER, "dividend/sorted_" + year + ".json");
        mLf.log("sortYearDividend %s, %s items", year, sorted.size());
        for (int i = 0; i < sorted.size(); i++) {
            YHDividend di = sorted.get(i);
            mLf.log("#%d : note = %s, (now = %s), %s", i, di.note, closePrice(di, priceMap), di);
        }
        //--
        List<YHStockPrice> prices2 = new ArrayList<>(priceMap.values());//loadPriceFromFile();
        prices2 = sortPrice(prices2);
        mLf.log("YHStockPrice %s prices", prices2.size());
        for (int i = 0; i < prices2.size(); i++) {
            YHStockPrice p = prices2.get(i);
            mLf.log("#%d : %s", i, gson.toJson(p));
        }
    }

    // sort prices from large to small
    private List<YHStockPrice> sortPrice(List<YHStockPrice> prices) {
        clock.tic();
        Collections.sort(prices, new Comparator<>() {
        //Arrays.sort(prices, new Comparator<YHStockPrice>() {
            @Override
            public int compare(YHStockPrice x, YHStockPrice y) {
                double px = asLD(x.closingPrice);
                double py = asLD(y.closingPrice);
                return Double.compare(py, px);
            }
        });
        clock.tac("prices sorted OK");
        FetcherUtil.saveAsJson(prices, FOLDER, "dividend/sorted_price.json");
        return prices;
    }

    // evalRateOfReturn
    private List<YHDividend> sortYearDividend(String yearTarget, List<YHDividend> dividends, Map<String, YHStockPrice> prices) {
        clock.tic();
        Collections.sort(dividends, new Comparator<>() {
            @Override
            public int compare(YHDividend x, YHDividend y) {
                double dx = div(x);
                x.note = dx + "";
                double dy = div(y);
                y.note = dy + "";
                return Double.compare(dy, dx); // Decreasing
            }

            private double div(YHDividend x) {
                List<YHYearDiv> years = x.years;
                for (int i = 0; i < years.size(); i++) {
                    YHYearDiv yd = years.get(i);
                    if (yearTarget.equals(yd.year)) {
                        double get = asLD(yd.cash) + asLD(yd.stock);
                        double price = closePrice(x, prices);
                        return get / price;
                    }
                }
                return 0;
            }

        });
        clock.tac("dividends sorted OK");
        return dividends;
    }

    private double closePrice(YHDividend x, Map<String, YHStockPrice> prices) {
        String now = prices.get(x.equity.code).closingPrice;
        double priceToday = asLD(now);
        return priceToday;
    }

    private double asLD(String s) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            L.log("no double for %s", s);
            return 0;
        }
    }

    private void evalBeta(List<YHStockPrice> prices) {
        int n = prices.size();
        double beta = 0;
        int kb = 0;
        for (int i = 0; i < n; i++) {
            YHStockPrice p = prices.get(i);
            String s = gson.toJson(p);
            double be;
            String it = p.beta;
            if (it != null) {
                try {
                    be = Double.parseDouble(it);
                    kb++;
                    beta += be;
                } catch (NumberFormatException e) {
                    L.log("no beta #%s, %s", i, it);
                }
            }
            L.log("#%d = %s", i, s);
        }
        L.log("Summed %s beta values, with sum = %s, and betaAvg = %s", kb, beta, beta / kb);
    }

    // type = 1 ~ 11
    private String dividendPath(int type) {
        return pathOf(getDividendFilename(type));
    }

    private String pricePath() {
        return pathOf("dividend/price.json");
    }

    private String pathOf(String s) {
        File f = new File(FOLDER, s);
        return f.getAbsolutePath();
    }

    // Remark price is the head column info for this sheet
    private YHStockPrice getRemarkPrice() {
        String now = nowDate();
        YHStockPrice p = new YHStockPrice();
        p.date = now;
        return p;
    }

    private final SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
    private String nowDate() {
        return yyyyMMdd.format(new Date());
    }

    private List<YHDividend> allDividendValues = new ArrayList<>();
    private List<YHStockPrice> allPrices = new ArrayList<>();

    // name = "股票"
    // (k, v) = isin_{k}.txt 's {v}'s parts has stock information
    private Map<Integer, Integer> stockTitle = new HashMap<>();

    private void makeStockTitle() {
        boolean update = false;
        if (update) {
            Map<Integer, TWEquityList> allList = new HashMap<>();
            for (int i = 1; i <= 11; i++) {
                TWEquityList list = allTWEquity.get(i);
                addStockTitle(i, list); // finding "股票"
                allList.put(i, list);
            }
        } else {
            stockTitle.put(2, 0); // 上市
            stockTitle.put(4, 2); // 上櫃
            //stockTitle.put(5, 1); // 興櫃 // no dividend
        }
    }

    private void addStockTitle(int key, TWEquityList list) {
        List<Integer> li = list.findName("股票");
        if (li.size() > 0) {
            stockTitle.put(key, li.get(0));
        }
    }

    //
    // y:資料時間：2022/03/29 14:30
    // https://tw.stock.yahoo.com/quote/1101
    // https://finance.yahoo.com/quote/1101.TW?p=1101.TW&.tsrc=fin-srch
    // https://pchome.megatime.com.tw/stock/sto0/sid1101.html
    // 股利狀況 股利政策
    // Yahoo Stock
    // https://tw.stock.yahoo.com/quote/1101/dividend
    // PCHome
    // https://pchome.megatime.com.tw/stock/sto3/ock1/sid1101.html

    private String getISINFilename(int type) {
        return String.format("isinCode/isin_%02d.txt", type);
    }

    private String getDividendFilename(int type) {
        return String.format("dividend/isin_%02d.txt", type);
    }

    // https://www.twse.com.tw/en/page/products/stock-code2.html
    private TWEquityList getISINCodeList(int type) {
        TWEquityList ans = null;
        if (type == 1) {
            ans = getTWSEUnlisted();
        } else if (type == 2) {
            ans = getTWSEListed();
        } else if (type == 3) {
            ans = getTWSEListedBond();
        } else if (type == 4) {
            ans = getTWSETPEx();
        } else if (type == 5) {
            ans = getEmerging();
        } else if (type == 6) {
            ans = getFutureOption();
        } else if (type == 7) {
            ans = getTrustFund();
        } else if (type == 8) {
            ans = getGISA();
        } else if (type == 9) {
            ans = getTPExGold();
        } else if (type == 10) {
            ans = getForeignCurrencyNCD();
        } else if (type == 11) {
            ans = getDomesticIndex();
        }
        return ans;
    }

    // "~~~=type"
    private int readType(String s) {
        try {
            return Integer.parseInt(s.substring(s.lastIndexOf("=") + 1));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
