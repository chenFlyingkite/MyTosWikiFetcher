package com.exam20200929;

import flyingkite.log.Formattable;

public interface MaximumPop extends Formattable {
    void push(int v);
    boolean canPop();
    int popLast();
    int popMaximum();
}
