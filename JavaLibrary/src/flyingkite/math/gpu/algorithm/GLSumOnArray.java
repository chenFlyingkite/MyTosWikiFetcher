package flyingkite.math.gpu.algorithm;

import flyingkite.log.L;
import flyingkite.math.gpu.Testable;

import java.util.Arrays;
import java.util.Random;

public class GLSumOnArray implements Testable {
    private static Random rand = new Random();

    public int[] array;

    @Override
    public void test() {
        for (int i = 0; i < 100; i++) {
            L.log("test #%d", i);
            check();
        }
    }

    public int sum() {
        if (array != null) {
            return sum(array);
        } else {
            return 0;
        }
    }

    // return m with 2^(m-1) <= x < 2^m
    private static int lg2(int x) {
        int ans = 0;
        int now = x;
        while (now > 0) {
            now >>= 1;
            ans++;
        }
        return ans;
    }

    private void check() {
        // let w = array length = [1:w], value range = [0:m)
        final int w = 1 + rand.nextInt(2000);
        final int m = 10;
        int ans = 0;
        int[] a = new int[w];
        for (int i = 0; i < a.length; i++) {
            a[i] = rand.nextInt(m);
            ans += a[i];
        }
        L.log("start on n = %d, ans = %d, a = %s", a.length, ans, Arrays.toString(a));
        int sum = sum(a);
        if (sum != ans) {
            L.log("X_X for sum = %d, ans = %d on %s", sum, ans, Arrays.toString(a));
        } else {
            L.log("^_^ OK for sum = %d on %s", sum, Arrays.toString(a));
        }
    }

    private int sum(int[] src) {
        int n = src.length;
        int[] a = src;
        int m = lg2(n);
        //L.log("m = %s for n = %s", m, n);
        // end = last element to add
        int end = n;
        for (int i = m - 1; i >= 0; i--) {
            // w = width to add
            int w = 1 << i;
            //L.log("w = %d, end = %d", w, end);
            for (int j = 0; j < end - w; j++) {
                //L.log(" a[%d] = %d + %d = a[%d]", j, a[j], a[j+w], j+w);
                a[j] += a[j + w];
            }
            end = w;
            //L.log("#%d : a = %s", i, Arrays.toString(a));
        }
        return a[0];
    }
}
