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

    // Returns the YHDiv contains year of "target"
    public YHYearDiv getYHDiv(String target) {
        for (int i = 0; i < years.size(); i++) {
            YHYearDiv yd = years.get(i);
            if (yd.year.contains(target)) {
                return yd;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        String s = String.format("%s : %s = %s%%, %s, %s", equity, recentYear, recentReturn, years, note);
        return s;
    }
}
