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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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

    // 本國指數國際證券辨識號碼一覽表
    // https://isin.twse.com.tw/isin/C_public.jsp?strMode=11
    private TWEquityList getSuspendListing() {
        String link = "https://www.twse.com.tw/company/suspendListingCsvAndHtml?type=html&selectYear=&lang=zh";
        Document doc = fetcher.getDocument(link);
        clock.tic();
        TWEquityList ans = new TWEquityList();
        ans.list = new ArrayList<>();
        TWEquityItem it = new TWEquityItem();
        ans.list.add(it);
        String now = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        ans.version = now;
        it.name = "終止上市公司";
        List<TWEquity> li = it.list;

        Element bd = doc.getElementsByTag("tbody").get(0);
        Elements tds = bd.getElementsByTag("td");
        Element e;
        for (int i = 0; i < tds.size(); i++) {
            TWEquity tw = new TWEquity();
            e = tds.get(i);
            tw.remarks = e.text();
            i++;
            if (i < tds.size()) {
                e = tds.get(i);
                tw.name = e.text();
            }
            i++;
            if (i < tds.size()) {
                e = tds.get(i);
                tw.code = e.text();
            }
            li.add(tw);
        }
        long ms = clock.tac("parseEquityList OK, %s", link);
        // print to log
        mLf.log("[%s] : parseEquityList OK, %s", ms, link);
        ans.print(mLf);
//        for (int i = 0; i < it.list.size(); i++) {
//            TWEquity t = it.list.get(i);
//            L.log("#%s : %s", i, t);
//        }
        return ans;
    }

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
        boolean web = 1 > 0; // false = load local file data
        if (1 > 0) {
            // database
            loadAllISINCode(web); // < 30 second in web, file = 300ms
            loadAllDividend(web); // ~70min in web, file = 20ms
        }
        loadPrices();
        mLf.getFile().close();
        //YahooGet.me.getPrices("1907.TW"); // 永豐餘 only
        //YahooGet.me.getPrices("2910.TW");
        clock.tac("TWSEStockFetcher parse OK");
    }

    private final Map<Integer, TWEquityList> allTWEquity = new HashMap<>();
    private final Map<Integer, List<YHDividend>> allDividend = new HashMap<>();

    // Load all ISIN code and put into allTWEquity
    private void loadAllISINCode(boolean web) {
        clock.tic();
        fetchWebSuspendListingCompany();
        for (int i = 1; i <= 11; i++) {
            TWEquityList li;
            boolean notFetched = !ISINFile(i).exists();
            if (web || notFetched) {
                li = fetchWebISINCode(i);
            } else {
                li = loadFileISINCode(i);
            }

            allTWEquity.put(i, li);
        }
        clock.tac("loadAllISINCode OK");
    }


    // get isin code from web and save as file
    private TWEquityList fetchWebSuspendListingCompany() {
        clock.tic();
        TWEquityList list = getSuspendListing();
        // save isin code as json
        FetcherUtil.saveAsJson(list, FOLDER, getISINFilename(-1));
        // print as simple csv, No need
        clock.tac("fetchWebSuspendListingCompany OK");
        return null;
    }

    private File ISINFile(int type) {
        return new File(FOLDER, getISINFilename(type));
    }

    // get isin code from web and save as file
    private TWEquityList fetchWebISINCode(int type) {
        clock.tic();
        TWEquityList list = getISINCodeList(type);
        // load isin code as json
        FetcherUtil.saveAsJson(list, FOLDER, getISINFilename(type));
        clock.tac("fetchWebISINCode OK, type = %s", type);
        // print as simple csv
        writeISINCSV(list, type); // fast to be < 30 ms
        return list;
    }

    // Load file
    private TWEquityList loadFileISINCode(int type) {
        clock.tic();
        File src = ISINFile(type);
        TWEquityList list = new TWEquityList();
        list = GsonUtil.loadFile(src, list.getClass());
        clock.tac("loadFileISINCode OK, type = %s", type);
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
    private void loadAllDividend(boolean web) {
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
        if (web) {
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
                    boolean ok = isFutureListedDate(ei);
                    if (!ok) {
                        L.log("future listed date %s", ei);
                        continue;
                    }

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
        } else {
            YHStockPrice[] pr = loadPriceFromFile();
            Collections.addAll(allPrices, pr);
            mLf.log("%d prices", allPrices.size() - 1);
        }
        clock.tac("loadAllDividend OK");
    }

    private boolean isFutureListedDate(TWEquity e) {
        Date listD = readyyyyMMdd(e.listedDate, "/");
        return listD.before(new Date());
    }

    private Date readyyyyMMdd(String str, String del) {
        String[] rs = str.split("" + del);
        Date ans = new Date();
        ans.setHours(0);
        ans.setMinutes(0);
        ans.setSeconds(0);
        int n = rs.length;
        if (n > 0) {
            int y = asD(rs[0]);
            ans.setYear(y - 1900);
        }
        if (n > 1) {
            int m = asD(rs[1]);
            ans.setMonth(m - 1);
        }
        if (n > 2) {
            int d = asD(rs[2]);
            ans.setDate(d);
        }
        return ans;
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
        List<YHDividend> sorted;
        sorted = sortYearDividend(year, dividends, priceMap);
        mLf.log("sortYearDividend %s, %s items", year, sorted.size());
        for (int i = 0; i < sorted.size(); i++) {
            YHDividend di = sorted.get(i);
            mLf.log("#%d : note = %s, (now = %s), %s", i, di.note, closePrice(di, priceMap), di);
        }
        //-- eval the ex-dividend date
        year = "2020";
        sorted = sortExDividendDate(year, dividends);
        mLf.log("sortExDividend %s, %s items", year, sorted.size());
        for (int i = 0; i < sorted.size(); i++) {
            YHDividend di = sorted.get(i);
            mLf.log("#%d : note = %s, %s", i, di.note, di);
        }
        //--
        List<YHStockPrice> prices2 = new ArrayList<>(priceMap.values());
        prices2 = sortPrice(prices2);
        mLf.log("YHStockPrice %s prices", prices2.size());
        for (int i = 0; i < prices2.size(); i++) {
            YHStockPrice p = prices2.get(i);
            mLf.log("#%d : %s", i, gson.toJson(p));
        }
    }

    // sort dividend date from now to unknown
    private List<YHDividend> sortExDividendDate(String year, List<YHDividend> dividends) {
        clock.tic();
        Collections.sort(dividends, new Comparator<>() {
            // "2020/01/01", 2022/12/21", null
            @Override
            public int compare(YHDividend x, YHDividend y) {
                YHYearDiv dx = getYHDiv(x, year);
                YHYearDiv dy = getYHDiv(y, year);
                Date x1 = exdate(dx);
                x.note = String.format("ExDate(%s) = %s", year, x1);
                Date y1 = exdate(dy);
                y.note = String.format("ExDate(%s) = %s", year, y1);
                return x1.compareTo(y1);
            }

            private Date exdate(YHYearDiv y) {
                Date d = new Date();
                d.setYear(2099-1900);
                d.setMonth(Calendar.DECEMBER);
                d.setDate(31);
                if (y != null) {
                    String[] ss = y.exDividendDate.split("/");
                    if (ss.length > 1) {
                        int y1 = (int) asL(ss[0]);
                        int y2 = (int) asL(ss[1]);
                        int y3 = (int) asL(ss[2]);
                        d.setYear(y1 - 1900);
                        d.setMonth(y2 - 1);
                        d.setDate(y3);
                    }
                }
                return d;
            }

            private YHYearDiv getYHDiv(YHDividend d, String target) {
                for (int i = 0; i < d.years.size(); i++) {
                    YHYearDiv yd = d.years.get(i);
                    if (yd.year.contains(target)) {
                        return yd;
                    }
                }
                return null;
            }
        });
        clock.tac("ExDividendDate %s year sorted OK", year);
        FetcherUtil.saveAsJson(dividends, FOLDER, "dividend/sorted_exDividend_" + year + ".json");
        List<String> simple = new ArrayList<>();
        for (int i = 0; i < dividends.size(); i++) {
            YHDividend yi = dividends.get(i);
            YHYearDiv yd = yi.getYHDiv(year);
            String date = yd == null ? "9999/12/31" : yd.exDividendDate;
            TWEquity eq = yi.equity;
            String s = String.format(Locale.US, "%s,%s,%s,%s", eq.code, eq.name, eq.market, date);
            simple.add(s);
        }
        FetcherUtil.saveAsJson(simple, FOLDER, "dividend/sorted_exDividend_" + year + "_simple.txt");
        return dividends;
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
        FetcherUtil.saveAsJson(dividends, FOLDER, "dividend/sorted_" + yearTarget + ".json");
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
            //L.log("no double for %s", s);
            return 0;
        }
    }


    private int asD(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            //L.log("no int for %s", s);
            return 0;
        }
    }

    private long asL(String s) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            //L.log("no long for %s", s);
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
