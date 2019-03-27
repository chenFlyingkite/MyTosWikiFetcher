package main.fetcher.data;

public class MoneyInfo {
    public String name;
    public String link;
    public double d1;
    public double d2;

    @Override
    public String toString() {
        return name + " : " + d1 + ", " + d2;
    }

    public MoneyInfo copy() {
        MoneyInfo m = new MoneyInfo();
        m.name = name;
        m.link = link;
        m.d1 = d1;
        m.d2 = d2;
        return m;
    }
}
