package com.exam0929;

import flyingkite.log.L;
import flyingkite.tool.TicTac2;

import java.util.Random;

public class Main0929 implements Runnable {
    @Override
    public void run() {
        // Test = +4, +3, pL = 3, +0, +3, pL = 3, pM = 4, +2, +1, +2, +0, +4, +3
        // =>
        /*
        ----  End  ----
        basic = list(9) = [0, 0, 3, 2, 1, 2, 0, 4, 3]
        mySol = list(10) = [4, 0, 0, 3, 2, 1, 2, 0, 4, 3]
        hole = 1, map = {0=[1, 2, 7], 1=[5], 2=[4, 6], 3=[3, 9], 4=[8]}
        real = 9
        mySol = list(10) = [4, 0, 0, 3, 2, 1, 2, 0, 4, 3]
        hole = 0, map = {0=[1, 2, 7], 1=[5], 2=[4, 6], 3=[3, 9], 4=[0, 8]}
        real = 10
        ---- ----
        */
        // command count
        int cmdCnt = 100_000;
        // input = [0, MAX)
        int MAX = 20;
        SolBase basic = new SolBase();
        MySol mySol = new MySol();
        TicTac2 c1 = new TicTac2();
        TicTac2 c2 = new TicTac2();
        for (int i = 0; i < cmdCnt; i++) {
            // rate of push & pop
            int m = 10;
            int c = cmd.nextInt(m);
            if (c <= m-3) {
                // push
                int v = val.nextInt(MAX);
                c1.tic();
                basic.push(v);
                c1.tac("basic.push(%s)", v);
                c2.tic();
                mySol.push(v);
                c2.tac("mySol.push(%s)", v);
            } else if (c == m-2) {
                // popLast
                c1.tic();
                int bp = basic.popLast();
                c1.tac("basic.popLast() = %s", bp);
                c2.tic();
                int bm = mySol.popLast();
                c2.tac("mySol.popLast() = %s", bm);
                if (bp != bm) {
                    L.log("----  Wrong : popLast ----");
                    L.log("basic = %s", basic);
                    L.log("mySol = %s", mySol);
                    L.log("---- ----\n\n");
                }
            } else if (c == m-1) {
                // popMax
                c1.tic();
                int bp = basic.popMaximum();
                c1.tac("basic.popMaximum() = %s", bp);
                c2.tic();
                int bm = mySol.popMaximum();
                c2.tac("mySol.popMaximum() = %s", bm);
                if (bp != bm) {
                    L.log("----  Wrong : popMaximum ----");
                    L.log("basic = %s", basic);
                    L.log("mySol = %s", mySol);
                    L.log("---- ----\n\n");
                }
            } else {
                L.log("Omit c = %s", c);
            }
        }
        L.log("----  End  ----");
        L.log("basic = %s", basic);
        L.log("mySol = %s", mySol);
        mySol.applyTrim();
        L.log("mySol = %s", mySol);
        L.log("---- ----");
    }

    Random val = new Random();
    Random cmd = new Random();
}
