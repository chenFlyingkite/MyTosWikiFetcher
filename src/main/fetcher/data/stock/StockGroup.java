package main.fetcher.data.stock;

public class StockGroup {
    public String link = "";
    public String name = "";

    @Override
    public String toString() {
        return name + " -> " + link;
    }
}
