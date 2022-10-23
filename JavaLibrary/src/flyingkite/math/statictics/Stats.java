package flyingkite.math.statictics;

import flyingkite.log.L;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class Stats<T extends Number> {
    // the name of this Stats, user provided
    public String name;
    // original source of data set
    public List<T> source;
    // sorted dataset of $source
    private List<Double> sorted = new ArrayList<>();

    // -- Statistics values

    public double min;
    public double max;
    // https://en.wikipedia.org/wiki/Mean
    public double mean;
    // https://en.wikipedia.org/wiki/Variance#Sample_variance
    public double variance;
    public double deviation; // = sqrt(variance)

    // Skewness of sample
    // https://en.wikipedia.org/wiki/Skewness
    //           m_3     1 / n * Sum_i=1:n {(x_i - μ)^3}
    // gamma1 = ----- = ------------------------------------------------------
    //           s^3     sqrt(1 / (n - 1) * ( Sum_i=1:n {(x_i - μ)^2} ) ) ^ 3
    public double skewness;
    public double kurtosis;
    // https://en.wikipedia.org/wiki/Median
    public double median;
    // PR25
    public double quartile1;
    // PR75
    public double quartile3;
    public List<Double> mode = new ArrayList<>();

    // series trend for the source,
    public Map<String, Integer> seriesTrend = new TreeMap<>();

    public Stats(List<T> data) {
        source = data;
        init();
    }

    // K kind values in data, data.size = N, K <= N
    // O(N*logN) = O(N) + O(N*logN) + O(N) + O(K)
    private void init() {
        int n = source.size();
        double sum = 0;
        double min_ = 0;
        double max_ = 0;
        for (int i = 0; i < n; i++) {
            T t = source.get(i);
            double x = t.doubleValue();
            sum += x;
            if (i == 0) {
                min_ = x;
                max_ = x;
            } else {
                min_ = Math.min(min_, x);
                max_ = Math.max(max_, x);
            }
            sorted.add(x);
        }
        Collections.sort(sorted);
        min = min_;
        max = max_;
        mean = sum / n;

        double sum2 = 0; // = (x_i - mu)^2
        double sum3 = 0; // = (x_i - mu)^3
        double sum4 = 0; // = (x_i - mu)^4
        Map<Double, Integer> count = new HashMap<>();
        int maxCnt = 0;
        for (int i = 0; i < n; i++) {
            T t = source.get(i);
            double x = t.doubleValue();
            double dx = x - mean;
            double dx2 = dx * dx;
            sum2 += dx2;
            sum3 += dx2*dx;
            sum4 += dx2*dx2;
            //
            int cnt = 0;
            if (count.containsKey(x)){
                cnt = count.get(x);
            }
            cnt++;
            maxCnt = Math.max(maxCnt, cnt);
            count.put(x, cnt);
        }
        // evaluation central momentum
        variance = sum2 / (n-1);
        deviation = Math.sqrt(variance);
        double ex3 = sum3 / n;
        double v2 = sum2 / n;
        skewness = ex3 / Math.pow(v2, 1.5);
        double ex4 = sum4 / n;
        kurtosis = -3 + ex4 / v2 / v2;

        // evaluation quartile
        median = mid(sorted, 0, n-1);
        // 11 items, 6 items
        // 0,1,2,3,4,5,6,7,8,9,10   // 0,1,2,3,4,5
        //           ^                      ^
        int ml = n/2 - 1;// 11 = n/2-1, 6 = n/2 - 1
        int mr = n%2 == 0 ? (n/2) : (n/2 + 1);// 11 = n/2+1, 6 = n/2

        quartile1 = mid(sorted, 0, ml);
        quartile3 = mid(sorted, mr, n-1);

        // evaluation mode
        for (double k : count.keySet()) {
            if (count.get(k) == maxCnt) {
                mode.add(k);
            }
        }
        Collections.sort(mode);

        // evaluation on +++++ cases
        final int window = 5; // sliding window size
        if (n >= window) {
            // delta = s[v] - s[v-1]
            Deque<Double> delta = new ArrayDeque<>();
            // t0, t1, t2, t3
            final int dt = -1; // diff day = one
            for (int i = 1; i < window; i++) {
                double d = diff(i, dt);
                delta.addLast(d);
            }
            for (int i = window; i < n; i++) {
                double next = diff(i, dt);
                delta.addLast(next);
                String trend = trend(delta);
                int v = 0;
                if (seriesTrend.containsKey(trend)) {
                    v = seriesTrend.get(trend);
                }
                seriesTrend.put(trend, v + 1);
                delta.removeFirst();
            }
        }
    }

    // Represent deque value changes into +|-
    private String trend(Deque<Double> a) {
        int n = a.size();
        char[] cs = new char[n];
        for (int i = 0; i < n; i++) {
            double x = a.removeFirst();
            if (x >= 0) {
                cs[i] = '+';
            } else {
                cs[i] = '-';
            }
            a.addLast(x); // add back
        }
        String key = String.valueOf(cs);
        return key;
    }

    // trend of a = c1...cn, ci = '+'|'-' depends on a[i]
    private String trend(double[] a) {
        int n = a.length;
        char[] cs = new char[n];
        for (int i = 0; i < n; i++) {
            if (a[i] >= 0) {
                cs[i] = '+';
            } else {
                cs[i] = '-';
            }
        }
        String key = String.valueOf(cs);
        return key;
    }

    // s[t] - s[t+dt]
    private double diff(int i, int dt) {
        T t0 = source.get(i);
        double x0 = t0.doubleValue();

        T tk = source.get(i + dt);
        double xk = tk.doubleValue();
        return x0 - xk;
    }

    // inclusive head and tail
    private double mid(List<Double> sorted, int head, int tail) {
        int size = tail - head + 1;
        // size = 2*q + r
        int q = head + size / 2;
        int r = size % 2;
        if (r == 1) {
            return sorted.get(q);
        } else {
            return (sorted.get(q - 1) + sorted.get(q)) / 2;
        }
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%s items in %s, range = %s ~ %s, mean = %s, std = %s", source.size(), name, min, max, mean, deviation);
    }

    public static void test() {
        List<Double> y;
        Stats<Integer> ss;
        Stats<Double> sd;
        ss = new Stats<>(Arrays.asList(1, 2, 3, 4, 5, 6));
        L.log("mu = %s", ss.mean);
        ss = new Stats<>(Arrays.asList(1, 2, 3, 4, 5, 6));
        L.log("x = %s", ss.variance);
        ss = new Stats<>(Arrays.asList(1, 2, 3, 4, 5, 6));
        L.log("x = %s", ss.deviation);
        sd = new Stats<>(Arrays.asList(1.5, 2.5, 3.5, 4.5, 5.5, 6.5));
        L.log("x = %s", sd.mean);
        ss = new Stats<>(Arrays.asList(1, 2, 3, 4, 5, 6, 6));
        L.log("m = %s", ss.mode);
        ss = new Stats<>(Arrays.asList(-1, -1, -1, -1, 0, 0, 0, 0, 1, 2, 3, 3, 3, 3, 4, 5, 6, 6));
        L.log("m = %s", ss.mode);
        // https://zh.wikipedia.org/wiki/%E5%9B%9B%E5%88%86%E4%BD%8D%E6%95%B0
        ss = new Stats<>(Arrays.asList(6, 47, 49, 15, 42, 41, 7, 39, 43, 40, 36));
        // q = 15, 40, 43 for 11 items
        L.log("q1 = %s, mu = %s, q3 = %s", ss.quartile1, ss.median, ss.quartile3);
        ss = new Stats<>(Arrays.asList(7, 15, 36, 39, 40, 41));
        // q = 15, 37.5, 40 for 6 items
        L.log("q1 = %s, mu = %s, q3 = %s", ss.quartile1, ss.median, ss.quartile3);
        for (int i = 0; i < 32; i++) {
            // generate the sample source
            List<Integer> diff = toBinary(i, 5);
            List<Integer> src = new ArrayList<>();
            int now = 5; // starting series of src[0]
            src.add(now);
            for (int j = 0; j < diff.size(); j++) {
                if (diff.get(j) == 1) {
                    now++;
                } else {
                    now--;
                }
                src.add(now);
            }
            L.log("#%d : diff = %s, src = %s", i, diff, src);
            ss = new Stats<>(src);
        }
    }

    // convert v into binary length of n, v = 0x"a[0]a[1]..a[n-1]"
    private static List<Integer> toBinary(int v, int n) {
        List<Integer> ans = new ArrayList<>();
        int now = v;
        for (int i = n-1; i >= 0; i--) {
            ans.add(0, now % 2);
            now /= 2;
        }
        return ans;
    }
}
