package flyingkite.tool;

import java.util.ArrayList;
import java.util.List;

public class TextUtil {
    public static boolean isEmpty(CharSequence c) {
        return c == null || c.length() == 0;
    }

    // Return substring of src, where key is at-th position
    // for src = "1/2/3/4/5", key = "/", at = 2
    // => returns     ^^^^^ = "3/4/5"
    // at is support for negative number, count from tail
    public static String after(String src, String key, int at) {
        //String[] spl = src.split(key);
        // find each key as pos
        List<Integer> pos = new ArrayList<>();
        int now = 0;
        int end = src.length();
        while (now < end) {
            int loc = src.indexOf(key, now);
            if (loc < 0) {
                break;
            }
            pos.add(loc);
            now = loc + key.length();
        }
        int use = at;
        if (use < 0) {
            use += pos.size();
        }
        //L.log("key = %s, src = %s", key, src);
        //L.log("use = %d, pos = %s", use, pos);
        // return the substring
        if (pos.isEmpty()) {
            return src;
        }
        return src.substring(pos.get(use) + key.length());
    }

}
