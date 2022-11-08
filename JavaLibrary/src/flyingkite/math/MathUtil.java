package flyingkite.math;

public class MathUtil {
    /**
     * @return true if min &le; value &lt; max, false otherwise
     */
    public static boolean isInRange(long value, long min, long max) {
        return min <= value && value < max;
    }

    public static boolean isInRangeInclusive(char value, char min, char max) {
        return min <= value && value <= max;
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

    public static int mins(int... values) {
        int min = values[0];
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
     * returns the Greatest Common Divisor (GCD) of a & b
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
        if (x <= 1) return false;

        final int[] p = {2, 3, 5, 7};
        for (int z : p) {
            if (x == z) {
                return true;
            } else if (x % z == 0) {
                return false;
            }
        }

        // Only check 6n-1 & 6n+1
        long z = 0;
        for (long i = 1; z * z <= x; i++) {
            z = 6 * i - 1;
            if (x % z == 0) {
                return false;
            }
            z = 6 * i + 1;
            if (x % z == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * returns the smallest factor, k, such that k*m = x, m >= k >= 1
     * Return x itself if x is prime number, return x if x <= 1
     * Time complexity = O(sqrt(x))
     */
    public static long getSmallestFactor(long x) {
        if (x <= 1) return x; // false

        final int[] p = {2, 3, 5, 7};
        for (int z : p) {
            if (x == z) {
                return x; // true, prime
            } else if (x % z == 0) {
                return z; // false, factor
            }
        }

        // Only check 6n-1 & 6n+1, starts with n = 1 (5 & 7)
        long z = 0;
        for (long i = 1; z * z <= x; i++) {
            z = 6 * i - 1;
            if (x % z == 0) {
                return z; // factor
            }
            z = 6 * i + 1;
            if (x % z == 0) {
                return z; // factor
            }
        }
        return x;
    }

    //long[] a =  {10, 100, 1_000, 10_000, 100_000, 1_000_000, 10_000_000, 100_000_000, 1_000_000_000};
    //nextPrime = {11, 101, 1_009, 10_007, 100_003, 1_000_003, 10_000_019, 100_000_007, 1_000_000_007};

    public static long nextPrime(long x) {
        return nextPrime(x, Long.MAX_VALUE);
    }

    // Returns the smallest prime in (x, max), for values = [x+1, x+2, ..., max-1]
    // returns max if all values are not prime
    public static long nextPrime(long x, long max) {
        long now = x+1;
        while (now < max) {
            if (isPrime(now)) {
                return now;
            }
            now++;
        }
        return max;
    }

}
