package main.fetcher.data;

public class StockInfo {
    public String link = "";
    public String clazz = "";

    @Override
    public String toString() {
        return clazz + " -> " + link;
    }
}
