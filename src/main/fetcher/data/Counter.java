package main.fetcher.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Counter<T> extends DataList<T> {

    default String key(T t) {
        return "";
    }

    default Map<String, Integer> count() {
        List<T> data = getData();
        Map<String, Integer> ans = new HashMap<>();

        if (data != null) {
            for (T t : data) {
                String k = key(t);
                ans.merge(k, 1, (a, b) -> {
                    return a + b;
                });
            }
        }
        return ans;
    }
}
