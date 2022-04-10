package main.fetcher.data.stock;

import main.twse.TWEquity;

import java.util.ArrayList;
import java.util.List;

public class YHDividend {
    public TWEquity equity;
    public int recentYear;
    public double recentReturn; // in %
    public List<YHYearDiv> years = new ArrayList<>();
    public String note;

    @Override
    public String toString() {
        String s = String.format("%s : %s = %s%%, %s, %s", equity, recentYear, recentReturn, years, note);
        return s;
    }
}
