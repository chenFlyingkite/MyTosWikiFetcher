package main.fetcher.exchange;

import flyingkite.log.Formattable;

import java.util.ArrayList;
import java.util.List;

public class FinTable implements Formattable {
    public String tag;
    public List<String> date = new ArrayList<>();
    public List<Double> buy = new ArrayList<>();
    public List<Double> sell = new ArrayList<>();
    public List<Double> cashBuy = new ArrayList<>();
    public List<Double> cashSell = new ArrayList<>();

    @Override
    public String toString() {
        int n = date.size();
        String s = _fmt("%s ~ %s (%s items), buy = %s ~ %s, sell = %s ~ %s"
                , date.get(0), date.get(n-1), n
                , buy.get(0), buy.get(n-1), sell.get(0), sell.get(n-1)
        );
        return s;
    }
}