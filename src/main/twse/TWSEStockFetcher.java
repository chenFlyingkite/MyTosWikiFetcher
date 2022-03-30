package main.twse;

import flyingkite.log.L;
import flyingkite.log.LF;
import flyingkite.tool.TicTac2;
import main.fetcher.FetcherUtil;
import main.fetcher.web.OnWebLfTT;
import main.fetcher.web.WebFetcher;
import main.kt.TWSEGet;
import org.jsoup.nodes.Document;

public class TWSEStockFetcher {
    public static final TWSEStockFetcher me = new TWSEStockFetcher();

    private static final String FOLDER = "twseStock";
    private LF mLf = new LF(FOLDER);
    private TicTac2 clock = new TicTac2();
    private WebFetcher fetcher = new WebFetcher();
    private OnWebLfTT onWeb = new OnWebLfTT(mLf, clock);

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
        clock.tac("parseEquityList OK, %s", link);
        pln(ans);
        return ans;
    }

    public static void main(String[] args) {
        me.parse();
    }

    private void parse() {
        for (int i = 1; i <= 11; i++) {
            TWEquityList list = getISINCodeList(i);
            String name = getISINFilename(i);
            LF lf = new LF(FOLDER, name);
            FetcherUtil.writeFileJsonPrettyPrinting(lf, list);
        }
    }

    //
    // y:資料時間：2022/03/29 14:30
    // https://tw.stock.yahoo.com/quote/1101
    // https://pchome.megatime.com.tw/stock/sto0/sid1101.html
    // 股利狀況 股利政策
    // Yahoo Stock
    // https://tw.stock.yahoo.com/quote/1101/dividend
    // PCHome
    // https://pchome.megatime.com.tw/stock/sto3/ock1/sid1101.html

    private String getISINFilename(int type) {
        return String.format("isinCode/isin_%02d.txt", type);
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

    private void pln(TWEquityList it) {
        L.log("ans = %s, %s in list", it.version, it.list.size());
        for (int i = 0; i < it.list.size(); i++) {
            L.log("#%s : %s", i, it.list.get(i));
        }
    }
}
