package main.twse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TWEquityItem {
    public String name = "";
    public List<TWEquity> list = new ArrayList<>();

    @Override
    public String toString() {
        String s = String.format(Locale.US, "%s : %s in list, %s", name, list.size(), list);
        return s;
    }
}
