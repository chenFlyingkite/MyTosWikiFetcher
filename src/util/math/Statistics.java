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
        double mean = 0;
        int n = data.size();
        for (T d : data) {
            mean += d.doubleValue() / n;
        }
        return mean;
    }

    public static <T extends Number> double variance(List<T> data) {
        final double mean = mean(data);
        double var = 0, dx;
        int n = data.size();
        for (T d : data) {
            dx = d.doubleValue() - mean;
            var += dx * dx / n;
        }
        return var;
    }

    public static <T extends Number> double deviation(List<T> data) {
        return Math.sqrt(variance(data));
    }

    public static <T extends Number> double median(List<T> data) {
        return quartile(2, data);
    }

    private static <T extends Number> double quartile(int qi, List<T> data) {
        // Add data and sort them
        List<Double> sorted = new ArrayList<>();
        for (T t : data) {
            sorted.add(t.doubleValue());
        }
        Collections.sort(sorted);

        // Take the quartile
        int size = sorted.size();
        if (qi == 2) {
            int q = size / 2;
            int r = size % 2;
            if (r == 1) {
                return sorted.get(q);
            } else {
                return 0.5 * sorted.get(q - 1) + 0.5 * sorted.get(q);
            }
        }
        return Math.sqrt(variance(data));
    }

    /**
     * Returns the indices of mode in data, (we returns the index of item)
     * Use data[result[0]] to take the 1st mode number
     */
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
        x = Statistics.variance(Arrays.asList(1, 2, 3, 4, 5, 6));
        L.log("x = %s", x);
        x = Statistics.deviation(Arrays.asList(1, 2, 3, 4, 5, 6));
        L.log("x = %s", x);
        x = Statistics.mean(Arrays.asList(1.5, 2.5, 3.5, 4.5, 5.5, 6.5));
        L.log("x = %s", x);
        y = Statistics.mode(Arrays.asList(1, 2, 3, 4, 5, 6, 6));
        L.log("y = %s", y);
        y = Statistics.mode(Arrays.asList(-1, -1, -1, -1, 0, 0, 0, 0, 1, 2, 3, 3, 3, 3, 4, 5, 6, 6));
        L.log("y = %s", y);
        x = quartile(2, Arrays.asList(6, 47, 49, 15, 42, 41, 7, 39, 43, 40, 36));
        L.log("x = %s", x);
        x = quartile(2, Arrays.asList(7, 15, 36, 39, 40, 41));
        L.log("x = %s", x);
    }
}
