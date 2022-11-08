package com.exam20220928;

import java.util.PriorityQueue;

public class IntervalOperation {

    // Return the interval length of union of all intervals, That is the length of U
    // For example, times = [ [5, 25], [20, 30], [35, 55]] = |[5, 30]| + |[35, 55]| = 30-5 + 55-35 = 45
    //     0....10....15....20....25....30....35....40....45....50....55....60
    // A = ...*********************..........................................
    // B = .................*************....................................
    // C = ...................................**************************.....
    // Let U = Union(A, B, C)
    // U = ...***************************.....**************************.....
    public int unionLength(int[][] times) {
        if (times == null) return -1; // invalid data
        int ans = 0;
        int n = times.length;
        if (times[0].length < 2) return -1; // invalid data

        // Sort the intervals to be from left to right
        PriorityQueue<int[]> queue = new PriorityQueue<>(n, (x, y) -> {
            if (x[0] != y[0]) {
                return x[0] - y[0];
            } else {
                return x[1] - y[1];
            }
        });
        for (int[] x : times) {
            queue.add(x);
        }

        int[] x = queue.remove();
        // // [15, 35], [20, 45], [60, 80]
        // calculating intervals
        int start = x[0]; // x[0]
        int end = x[1];   // x[1]
        // [15, 35], [20, 45], [60, 80]
        while (queue.size() > 0) {
            x = queue.remove(); // 20~45, 60~80
            if (x[0] <= end) { // 25 <= 35, 60 > 45
                // start/end =    ^-------^
                // x         =          -----    start < x[0] < end < x[1]
                // x         =    -------------  start = x[0] < end < x[1]
                // x         =       ----        start < x[0] < x[1] < end    => [1, 100], [2, 99], [3, 97]
                // x         =    ------         start = x[0] < x[1] < end    => [1, 100], [1, 99], [3, 97]
                // x         =    ---------      start = x[0] < x[1] = end    => [1, 100], [1, 99], [3, 97]
                // x         =                 ------ not
                end = Math.max(end, x[1]); // end = 35 => 45
            } else {
                // x[0] > end
                // start/end =               ^-------^
                // x         =                ---------
                // now       =   -----------
                ans += end - start;   //  ans = 0 + 45 - 15 = 30
                start = x[0];         // start = 60
                end = x[1];           // end = 80
            }
        }
        ans += end - start; // take care the last interval!
        return ans;
    }
}
