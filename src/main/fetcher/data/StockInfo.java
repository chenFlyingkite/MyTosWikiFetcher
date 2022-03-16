package main.fetcher.data;

public class StockInfo {
    public String link = "";
    public String name = "";

    @Override
    public String toString() {
        return name + " -> " + link;
    }
}
