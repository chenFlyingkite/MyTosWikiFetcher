package util.math;

import util.datamining.clustering.util.IDPair;
import util.logging.L;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Statistics {
    public static <T extends Number> double mean(List<T> data) {
        double sum = 0;
        for (T d : data) {
            sum += d.doubleValue();
        }
        return sum / data.size();
    }

    public static <T extends Number> List<Integer> mode(List<T> data) {
        Map<T, IDPair> map = new HashMap<>();
        // Count the set as {value -> (index, count), ...}
        IDPair p;
        for (int i = 0; i < data.size(); i++) {
            T d = data.get(i);
            if (map.containsKey(d)) {
                p = map.get(d);
                p.setV(p.getV() + 1);
            } else {
                p = new IDPair(i, 1);
            }
            map.put(d, p);
        }
        // Sort (index, count) in map
        List<IDPair> counts = new ArrayList<>(map.values());
        Collections.sort(counts);
        // Take those count is max as indices
        int max = (int) counts.get(counts.size() - 1).getV();
        List<Integer> indices = new ArrayList<>();
        for (IDPair pair : counts) {
            if (max == (int)pair.getV()) {
                indices.add(pair.getK());
            }
        }
        return indices;
    }

    public static void run() {
        double x;
        List<Integer> y;
        x = Statistics.mean(Arrays.asList(1, 2, 3, 4, 5, 6));
        L.log("x = %s", x);
        x = Statistics.mean(Arrays.asList(1.5, 2.5, 3.5, 4.5, 5.5, 6.5));
        L.log("x = %s", x);
        y = Statistics.mode(Arrays.asList(1, 2, 3, 4, 5, 6, 6));
        L.log("y = %s", y);
        y = Statistics.mode(Arrays.asList(-1, -1, -1, -1, 0, 0, 0, 0, 1, 2, 3, 3, 3, 3, 4, 5, 6, 6));
        L.log("y = %s", y);
    }
}
