package com.exam20211208;

public class Main1208 implements Runnable {
    @Override
    public void run() {
        LuckyMatcher lm = new LuckyMatcher();
        //lm.luckyMatcher(Arrays.asList("orange"), Arrays.asList("mango"));
        //lm.luckyMatcher(Arrays.asList("orange"), Arrays.asList("orange"));
        lm.testCase();
    }
}
