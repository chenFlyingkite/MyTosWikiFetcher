package flyingkite.math;

public class MathUtil {
    /**
     * @return true if min &le; value &lt; max, false otherwise
     */
    public static boolean isInRange(long value, long min, long max) {
        return min <= value && value < max;
    }

    /**
     * @return true if min &le; value &lt; max, false otherwise
     */
    public static boolean isInRange(double value, double min, double max) {
        return min <= value && value < max;
    }

    /**
     * Returns the value clamped by [min, max]
     * @return value itself if min &le; value &lt; max.
     * Returns min if value &lt; min.
     * Returns max if value &ge; max.
     */
    public static long makeInRange(long value, long min, long max) {
        return Math.min(Math.max(min, value), max);
    }

    /**
     * Returns the value clamped by [min, max]
     * @return value itself if min &le; value &lt; max.
     * Returns min if value &lt; min.
     * Returns max if value &ge; max.
     */
    public static int makeInRange(int value, int min, int max) {
        return Math.min(Math.max(min, value), max);
    }

    /**
     * Returns the value clamped by [min, max]
     * @return value itself if min &le; value &lt; max.
     * Returns min if value &lt; min.
     * Returns max if value &ge; max.
     */
    public static double makeInRange(double value, double min, double max) {
        return Math.min(Math.max(min, value), max);
    }
    // For mins & maxs uses Collections.min() & Collections.max()

    public static long mins(long... values) {
        long min = values[0];
        for (int i = 1; i < values.length; i++) {
            min = Math.min(min, values[i]);
        }
        return min;
    }

    public static double mins(double... values) {
        double min = values[0];
        for (int i = 1; i < values.length; i++) {
            min = Math.min(min, values[i]);
        }
        return min;
    }

    public static int maxs(int... values) {
        int max = values[0];
        for (int i = 1; i < values.length; i++) {
            max = Math.max(max, values[i]);
        }
        return max;
    }

    public static double maxs(double... values) {
        double max = values[0];
        for (int i = 1; i < values.length; i++) {
            max = Math.max(max, values[i]);
        }
        return max;
    }

    /**
     * returns Greatest Common Diviser (GCD) of a & b
     * Let a = bq+r , gcd(a, b) = gcd(b, r)
     * Let m = max(a, b), Time complexity = O(log(m))
     */
    public static long gcd(long a, long b) {
        long x = a;
        long y = b;
        while (y > 0) {
            long r = x % y;
            x = y;
            y = r;
        }
        return x;
    }

    /**
     * returns Least Common Multiple of a & b
     * lcm(a, b) = a * b / gcd(b, r)
     * Let m = max(a, b), Time complexity = O(gcd()) = O(log(m))
     */
    public static long lcm(long a, long b) {
        long g = gcd(a, b);
        return a / g * b;
    }

    /**
     * returns whether x is prime
     * By division on possible odd numbers k, with k*k <= x
     * Time complexity = O(sqrt(x))
     */
    public static boolean isPrime(long x) {
        if (x < 10) {
            return x == 2 || x == 3 || x == 5 || x == 7;
        }

        if (x % 2 == 0) return false;

        for (int i = 3; i * i <= x; i += 2) {
            if (x % i == 0) {
                return false;
            }
        }
        return true;
    }

}
