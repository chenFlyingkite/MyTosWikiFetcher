package com.exam20211208;

import java.util.ArrayList;
import java.util.List;

public class Main1208 implements Runnable {
    @Override
    public void run() {
        LuckyMatcher lm = new LuckyMatcher();
        //lm.luckyMatcher(Arrays.asList("orange"), Arrays.asList("mango"));
        //lm.luckyMatcher(Arrays.asList("orange"), Arrays.asList("orange"));
        lm.testCase();
    }

    // create string product on {ab} as C with all {a in A}, {b in B}, and C has size of |A|*|B|
    private List<String> cross(List<String> head, List<String> tail) {
        List<String> now = new ArrayList<>();
        for (int i = 0; i < head.size(); i++) {
            String x = head.get(i);
            for (int j = 0; j < tail.size(); j++) {
                String y = tail.get(j);
                // z = x + y
                StringBuilder z = new StringBuilder(x).append(y);
                now.add(z.toString());
            }
        }
        return now;
    }
}
