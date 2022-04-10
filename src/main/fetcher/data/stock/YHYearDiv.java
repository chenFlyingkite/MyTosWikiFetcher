package main.fetcher.data.stock;

public class YHYearDiv {
    // 股利所屬期間
    public String year; // 2021, 2021Q4
    // 現金股利
    public String cash;
    // 股票股利
    public String stock;
    // 除息日
    public String exDividendDate;
    // 除權日
    public String exRightsDate;
    //現金股利發放日 Cash dividend payment date?
    public String cashDate;
    //股票股利發放日
    public String stockDate;
    //填息天數
    public String fillInInterval;

    public void trim() {
        cash = cash.trim();
        year = year.trim();
        stock = stock.trim();
        cashDate = cashDate.trim();
        stockDate = stockDate.trim();
        exRightsDate = exRightsDate.trim();
        exDividendDate = exDividendDate.trim();
        fillInInterval = fillInInterval.trim();
    }

    @Override
    public String toString() {
        String s = String.format("%s %s %s %s", year, cash, stock, exDividendDate);
        return s;
    }
}
