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
}
