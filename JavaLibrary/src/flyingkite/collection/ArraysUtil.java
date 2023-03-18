package flyingkite.collection;

import java.util.Arrays;

public class ArraysUtil {
    public static <T> boolean isEmpty(T[] a) {
        return a == null || a.length == 0;
    }

    public static <T> int length(T[] a) {
        return a == null ? 0 : a.length;
    }

    public static <T> T[] copy(T[] a) {
        if (a == null) {
            return null;
        } else {
            return Arrays.copyOf(a, length(a));
        }
    }
}
