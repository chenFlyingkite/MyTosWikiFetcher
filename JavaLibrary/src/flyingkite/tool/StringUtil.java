package flyingkite.tool;

import flyingkite.functional.MeetSS;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class StringUtil {

    public static int containsAt(String key, String[] data) {
        return containsAt(key, Arrays.asList(data));
    }

    public static int containsAt(String key, List<String> data) {
        for (int i = 0; i < data.size(); i++) {
            if (key.contains(data.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public static String contains(String key, Collection<String> data) {
        for (String s : data) {
            if (s.contains(key)) {//contains.meet(s, key)
                return s;
            }
        }
        return null;
    }

    private static final MeetSS<String, Boolean> contains = String::contains;
    private static final MeetSS<String, Boolean> equals = String::equals;

    /**
     * Convert milliseconds to mm:ss.SSS format
     * E.g. MMSSFFF(123456 ms)
     *    = MMSSFFF(123.456 sec)
     *    = MMSSFFF(2 min 3.5 sec)
     *    = "02:03.5"
     */
    public static String MMSSFFF(long ms) {
        if (ms < 0) return "-" + MMSSFFF(-ms);

        final long f = ms % 1000;
        final long s = ms / 1000;
        final long sec = s % 60;
        final long min = s / 60;
        return String.format(java.util.Locale.US, "%02d:%02d.%03d", min, sec, f);
    }

    public static int[] parse01(String s) {
        if (s == null) return null;

        char[] cs = s.toCharArray();
        int n = s.length();
        int[] ans = new int[n];
        for (int i = 0; i < n; i++) {
            ans[i] = cs[i] == '0' ? 0 : 1;
        }
        return ans;
    }

    public static int[][] parse01(String[] s) {
        if (s == null) return null;

        int n = s.length;
        int[][] ans = new int[n][];
        for (int i = 0; i < n; i++) {
            ans[i] = parse01(s[i]);
        }
        return ans;
    }

    /**
     * return <base> concatenate k times
     */
    public static String repeat(String base, int k) {
        StringBuilder ans = new StringBuilder();
        StringBuilder sb = new StringBuilder(base);
        int now = k;
        while (now > 0) {
            int r = now % 2;
            if (r == 1) {
                ans.append(sb);
            }
            now /= 2;
            sb.append(sb);
        }
        return ans.toString();
    }

    // merge the message block horizontally
    public static String[] concatenateAligned(String[][] a, String delim) {
        if (a == null) return null;

        int n = a.length;
        // width[i] = max width of a[0:n, i]
        int[] width = new int[n];
        // joined total row
        int row = 0;
        String[] space = new String[n];
        for (int i = 0; i < n; i++) {
            String[] ai = a[i];
            int m = ai.length;
            row = Math.max(row, m);
            for (int j = 0; j < m; j++) {
                width[i] = Math.max(width[i], ai[j].length());
            }
            space[i] = StringUtil.repeat(" ", width[i]);
        }
        // Appending string
        String[] ans = new String[row];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < row; i++) {
            // clear sb
            sb.delete(0, sb.length());
            for (int j = 0; j < n; j++) {
                String[] ai = a[j];
                int m = ai.length;
                if (j > 0) {
                    sb.append(delim);
                }
                if (i < m) {
                    // align left
                    sb.append(String.format("%-" + width[j] + "s", ai[i]));
                } else {
                    sb.append(space[j]);
                }
            }
            ans[i] = sb.toString();
        }
        return ans;
    }

    /**
     * To Arrays.toString() where each item is in "%{width}d"
     * toWidthString([0, 1, 3, 5], 2) = "[ 0,  1,  3,  5]"
     */
    public static String toWidthString(int[] a, int width) {
        if (a == null) return "null";

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < a.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(String.format("%"+width+"d", a[i]));
        }
        sb.append("]");
        return sb.toString();
    }
}
