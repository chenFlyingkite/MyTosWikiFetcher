package main.fetcher.data.stock;

import flyingkite.log.L;
import main.twse.TWEquity;

import java.util.Arrays;

// All field can get from https://finance.yahoo.com/quote/1907.TW
public class YHStockPrice {
    // 1907
    public String code;
    // 永豐餘
    public String name;
    // yyyyMMdd
    public String date;
    // 成交, closing price
    // double
    public String closingPrice;
    // 昨收,
    // double
    public String previousClose;
    // 開盤, open
    // double
    public String open;
    // FYC
    // double
    public String bid;
    // 均價,
    // double
    public String ask;
    // 最高,
    // double
    public String rangeHigh;
    // 最低,
    // double
    public String rangeLow;
    public String rangeHighW52;
    public String rangeLowW52;
    // 總量
    // long
    public String volume;
    // long
    public String volumeAvg;
    // 昨量,
    //public double previousVolume;

    // double or N/A
    // N/A : 1220 (台榮)
    public String beta;
    // double or N/A
    // N/A : 1213 (大飲)
    public String PERatio;
    // EPS (TTM)
    // double or N/A
    // N/A : 1752 (南光)
    public String EPS;

    // 振幅 = (high - low) / previousClose


    // 1907 = 永豐餘
    // 2002 = 中鋼
    // TSY
    // https://tw.stock.yahoo.com/quote/1907
    public String getTwStockYahooCom() {
        return "https://tw.stock.yahoo.com/quote/" + code;
    }

    /**
     * FYC
     * https://finance.yahoo.com/quote/1907.TW
     * @see TWEquity#getSymbol()
     * Deprecated
     */
    private String getFinanceYahooCom() {
        return "https://finance.yahoo.com/quote/" + code + ".TW";
        //return "https://finance.yahoo.com/quote/" + code + ".TWO";
    }

    public void trim() {
        ask = ask.trim();
        bid = bid.trim();
        EPS = EPS.trim();
        code = code.trim();
        name = name.trim();
        if (date != null) {
            date = date.trim();
        }
        open = open.trim();
        beta = beta.trim();
        volume = volume.trim();
        rangeLow = rangeLow.trim();
        rangeHigh = rangeHigh.trim();
        volumeAvg = volumeAvg.trim();
        rangeLowW52 = rangeLowW52.trim();
        closingPrice = closingPrice.trim();
        rangeHighW52 = rangeHighW52.trim();
        previousClose = previousClose.trim();
    }

    private void auto() {
        String[] ss = {"code", "name", "date", "open", "closingPrice"
                , "ask", "beta", "bid", "EPS", "previousClose", "rangeLow", "rangeHigh", "rangeLowW52", "rangeHighW52"
                , "volume", "volumeAvg"
        };
        Arrays.sort(ss, (x, y) -> {
            return x.length() - y.length();
        });
        for (String s : ss) {
            L.log("%s = %s.trim();", s, s);
        }
    }
}
/*
2022/04/06
from getTwStockYahooCom()
成交  33.65
開盤  34.25
最高  34.25
最低  33.60
均價  33.77
成交值(億)  0.388
昨收  34.00
漲跌幅 1.03%
漲跌  0.35
總量  1,148
昨量  1,174
振幅  1.91%

from getFinanceYahooCom()

Previous Close	34.00
Open	34.25
Bid	33.65 x 0
Ask	33.75 x 0
Day's Range	33.60 - 34.25
52 Week Range	28.80 - 51.80
Volume	1,145,694
Avg. Volume	1,904,905
Market Cap	55.871B
Beta (5Y Monthly)	0.80
PE Ratio (TTM)	10.75
EPS (TTM)	3.13
Earnings Date	May 12, 2022 - May 16, 2022
Forward Dividend & Yield	1.50 (4.41%)
Ex-Dividend Date	Jun 09, 2021
1y Target Est	N/A
*/