package main.twse;

import flyingkite.log.LF;

import java.util.ArrayList;
import java.util.List;

public class TWEquityList {
    public String version = "";
    public List<TWEquityItem> list = new ArrayList<>();

    public void print(LF lf) {
        int n = list.size();
        lf.log("TWEquityList = %s, %s items", version, n);
        for (int i = 0; i < n; i++) {
            lf.log("#%s : %s", i, list.get(i));
        }
    }

    public List<Integer> findName(String target) {
        List<Integer> ans = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            TWEquityItem q = list.get(i);
            if (target.equals(q.name)) {
                ans.add(i);
            }
        }
        return ans;
    }

    @Override
    public String toString() {
        return version + ", " + list;
    }
}
