package flyingkite.gov.finance;

import flyingkite.log.Loggable;
import flyingkite.tool.TicTac2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TaxCalculator implements Loggable {
    public static final TaxCalculator me = new TaxCalculator();

    public void test() {
        testIncomeTaxBringUp();
    }


    public void testIncomeTaxBringUp() {
        // Ministry of Finance
        int[][] prices = {
                // 0, Mom, Dad, MomDad
                {    1,     2,     3,     4},
                {   80,    70,    60,    50},
                {  300,   200,   400,   700},
                { 1000,  2000,  3000,  4000},
                {80000, 30000, 40000, 70000},
//                {40643, 19523, 20963, 7435,},
//                {40643, 19523, 20963, 7435,},
        };
        int target = 3;

        Map<String, Integer> ans = null;
        ans = evaluateIncomeTaxBringUp(prices, target);
        List<String> keys = new ArrayList<>(ans.keySet());
        for (int i = 0; i < keys.size(); i++) {
            String k = keys.get(i);
            int val = ans.get(k);
            log("#%s : v = %s, k = %s", i, val, k);
        }
    }

    // for 5(m) people(A, B, C, D, E) to bring up 2(k) people(X, Y),
    // we have prices[5][2^2] (table[m][2^k]) for filling the computation
    // target = target bring up, as binary operator
    private Map<String, Integer> evaluateIncomeTaxBringUp(int[][] prices, int target) {
        Map<String, Integer> ans = new TreeMap<>();
        List<Integer> now = new ArrayList<>();
        log("Finding for all possible Income Tax with Bring up");
        TicTac2 clk = new TicTac2();
        clk.tic();
        findIncomeTaxBringUp(prices, now, 0, target, ans);
        clk.tac("found %s cases", ans.size());
        return ans;
    }

    // using binary operation to make remain who need to bring up,
    // prices = tax prices
    // now = Current decision of bring up, by tax list
    private void findIncomeTaxBringUp(int[][] prices, List<Integer> now, int row, int remain, Map<String, Integer> ans) {
        boolean log = false;
        int m = prices.length; // m peoples
        int n = prices[0].length; // n items
        if (log) {
            log("finding prices[%s], remain = %s, now = %s", row, remain, now);
            log("finding ans = %s", ans);
        }
        if (row >= m) {
            if (remain == 0) {
                // evaluation the income tax sum
                int sum = 0;
                for (int i = 0; i < now.size(); i++) {
                    sum += now.get(i);
                }
                String key = now.toString();
                if (log) {
                    log("Got case %s = %s", key, sum);
                }
                ans.put(key, sum);
            }
        } else {
            for (int i = 0; i < n; i++) {
                // find possible combination
                int c = i & remain;
                if (c == i) { // i & remain = i => remain takes the i's bring up
                    now.add(prices[row][c]);
                    int next = remain ^ c;
                    findIncomeTaxBringUp(prices, now, row + 1, next, ans);
                    now.remove(now.size() - 1);
                }
            }
        }
    }
}
