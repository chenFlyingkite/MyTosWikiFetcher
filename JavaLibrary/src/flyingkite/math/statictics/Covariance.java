package flyingkite.math.statictics;

import flyingkite.log.L;

import java.util.Arrays;

// https://en.wikipedia.org/wiki/Pearson_correlation_coefficient
public class Covariance<X extends Number, Y extends Number> {
    public Stats<X> seriesX;
    public Stats<Y> seriesY;
    public double correlation = -2;
    public double sumXY; // For undefined

    public Covariance(Stats<X> x, Stats<Y> y) {
        seriesX = x;
        seriesY = y;
        init();
    }

    private void init() {
        int n = seriesX.source.size();
        int m = seriesY.source.size();
        if (n == m) {
            sumXY = 0;
            for (int i = 0; i < n; i++) {
                double xi = seriesX.source.get(i).doubleValue();
                double yi = seriesY.source.get(i).doubleValue();
                sumXY += xi*yi;
            }
            double up = sumXY - n * seriesX.mean * seriesY.mean;
            double dm = (n-1) * seriesX.deviation * seriesY.deviation;
            correlation = up / dm;
        }
    }

    @Override
    public String toString() {
        return "Cor = " + correlation;
    }

    public static void test() {
        Stats<Integer> x;
        Stats<Integer> y;
        Covariance<Integer, Integer> cov;
        x = new Stats<>(Arrays.asList(1, 2, 3, 4, 5, 6));
        y = new Stats<>(Arrays.asList(1, 2, 3, 4, 5, 6));
        cov = new Covariance<>(x, y);
        L.log("cor = %s, \nx = %s,\ny = %s", cov.correlation, x, y);
    }
}
