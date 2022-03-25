package main.fetcher.data.stock;

import java.util.Locale;

public class Stock {
    //股票名稱/代號
    public String name;
    public String code;
    //股價
    public double price;
    //漲跌
    public double raise;
    //漲跌幅(%)
    public double raiseRate;
    //開盤
    public double start;
    //昨收
    public double last;
    //最高
    public double highest;
    //最低
    public double lowest;
    //成交量(張)
    public double quantity;
    //時間
    public String time;

    private String sign(double d) {
        if (d > 0) {
            return "+";
        } else if (d < 0) {
            return "-";
        } else {
            return " ";
        }
    }

    @Override
    public String toString() {
        String main = String.format(Locale.US, "%s(%s) %.2f %s%.2f %s%.2f%%",
                name, code, price, sign(raise), raise, sign(raiseRate), raiseRate);
        String more = String.format(Locale.US, "%.2f %.2f %.2f %.2f %s %s",
                start, last, highest, lowest, quantity, time);
        return String.format(Locale.US, "%s %s", main, more);
    }
}
