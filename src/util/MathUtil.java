package util;

public final class MathUtil {
    private MathUtil() {}

    public static boolean isInRange(long value, long min, long max) {
        return min <= value && value < max && min <= max;
    }
}
