package flyingkite.algorithm.sequence;

import flyingkite.log.L;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Permutation {

    // returns all permutations of 0, 1, ..., n-1
    public List<List<Integer>> get(int n) {
        List<List<Integer>> all = new LinkedList<>();

        if (n <= 0) {
            return all;
        }

        // for n = 1;
        List<Integer> a = new ArrayList<>();
        a.add(0);
        all.add(a);
        // For n = k+1, generate k items of ak from a(k-1) = aj
        for (int i = 1; i < n; i++) {
            int k = all.size();
            for (int j = 0; j < k; j++) {
                List<Integer> aj = all.remove(0);
                // make all order to be small to largest
                // {(0, 1) & (1, 0)} => {(0,1,2), (0,2,1), (1,0,2), (1,2,0), ...}
                for (int m = aj.size(); m >= 0; m--) {
                    List<Integer> ak = new ArrayList<>(aj);
                    ak.add(m, i);
                    all.add(ak);
                }
            }
        }
        return all;
    }

    public static void test() {
        int[] a = {-1,0,1,2,3,4,5,6,7,8,9};
        for (int x : a) {
            List<List<Integer>> z = new Permutation().get(x);
            L.log("all (%s) = %s", z.size(), z);
        }
    }
}
