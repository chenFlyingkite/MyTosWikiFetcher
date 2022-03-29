package main.twse;

import java.util.ArrayList;
import java.util.List;

public class TWEquityList {
    public String version = "";
    public List<TWEquityItem> list = new ArrayList<>();

    @Override
    public String toString() {
        return version + ", " + list;
    }
}
