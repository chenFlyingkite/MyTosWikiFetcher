package main.fetcher.data.stock;

public class YHYearDiv {
    public String year; // 2021, 2021Q4
    public String cash;
    public String stock;
    // 除息日
    public String exDividendDate = "";

    @Override
    public String toString() {
        String s = String.format("%s %s %s %s", year, cash, stock, exDividendDate);
        return s;
    }
}
