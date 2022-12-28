package flyingkite.algorithm.sequence;

import flyingkite.log.L;

import java.util.Arrays;
import java.util.Random;

public class Bowling {
    public static void main(String[] args) {
        // strike = (10, -1), spare = (7, 3), normal = (2, 3)
        int[][][] spec = {
                new int[][]{{10,-1},{10,-1},{10,-1},{10,-1},{10,-1},{10,-1},{10,-1},{10,-1},{10,-1},},
        };
        int[][] game;
        for (int i = 0; i < 5; i++) {
            game = random(9);
            int[] ans = scoreTable(game);
            L.log("game = %s", as(game));
            L.log("score = %s", Arrays.toString(ans));
        }
        for (int i = 0; i < spec.length; i++) {
            game = spec[i];
            int[] ans = scoreTable(game);
            L.log("game = %s", as(game));
            L.log("score = %s", Arrays.toString(ans));
        }
    }

    private static String as(int[][] a) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < a.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(Arrays.toString(a[i]));
        }
        sb.append("]");
        return sb.toString();
    }

    private static Random rnd = new Random();
    // generate random length of bowling case
    private static int[][] random(int k) {
        int[][] ans = new int[k][2];
        final int sum = 11;
        for (int i = 0; i < k; i++) {
            int x = rnd.nextInt(sum);
            ans[i][0] = x;
            if (x == 10) {
                ans[i][1] = -1;
            } else {
                ans[i][1] = rnd.nextInt(sum-x);
            }
        }
        return ans;
    }

    private static int[] scoreTable(int[][] a) {
        if (a == null) return null;
        int n = a.length;
        int[] score = new int[n];
        for (int i = n - 1; i >= 0; i--) {
            score[i] = get(a, i);
        }
        //L.log("score = %s", Arrays.toString(score));
        int ans = 0;
        for (int i = 0; i < score.length; i++) {
            ans += score[i];
        }
        L.log("ans = %s", ans);
        //return ans;
        return score;
    }

    private static int get(int[][] a, int k) {
        if (a == null) return 0;
        int n = a.length;
        if (k >= n) return 0;
        int x = a[k][0];
        int y = a[k][1];
        if (x == 10 && y == -1) {
            int sc = 0;
            int count = 2;
            while (count > 0 && k+1 < n) {
                sc += a[k+1][0];
                count--;
                if (count > 0 && a[k+1][1] != -1) {
                    sc += a[k+1][1];
                    count--;
                }
                k++;
            }
            return 10 + sc;
        } else if (x + y == 10) {
            int sc = 0;
            int count = 1;
            while (count > 0 && k+1 < n) {
                sc += a[k+1][0];
                count--;
                k++;
            }
            return 10 + sc;
        } else {
            return x + y;
        }
    }
}
/*
https://codecollab.io/@proj/TreeErrorPizza
// 簡化版保齡球計分，共 9 局
// 每一局可丟兩次球，共有 10 個球瓶，有 3 種可能：
// - 第一次丟球就全部擊倒：則第二次不計次(-1)並且分數為擊倒的瓶數（10) 加上接著後 2 次(不含不計次(-1))的瓶數總合
// - 第二次丟球才全部擊倒：分數為擊倒的瓶數（10) 加上接著 1 次(不含不計次(-1))的瓶數總合
// - 兩次丟球沒有全部擊倒：分數為本局得分為兩次擊倒的總瓶數，例如 3 + 5

// Swift
var hits: [[Int]] = [[0, 3], [10, -1], [10, -1], [3, 0], [4, 5], [9, 1], [6, 3], [10, -1], [10, -1]]
                  //   3        23       13        3       9        16      9        20        10

func calculateScore(hits: [[Int]]) -> Int {
    //           1  2  3  4  5  6  7  8  9
    var score = [0, 0, 0, 0, 0, 0, 0, 0, 0]
    // Evaluate each turn score
    let end = 8
    for j in 0...end {
        var i = end - j;
        var now = hits[i] // now = [x, y]
        var x = now[0]
        var y = now[1]
        if (x == 10 && y == -1) {
            // Good to 1st strike
            // take later two
            var sc = 10
            if (i+1 < end && hits[i+1][1] == -1) {
                sc += score[i+1]
            }
            if (i+2 < end && hits[i+1][1] == -1) {
                sc += score[i+2]
            }
            score[i] = sc;
        } else if (x + y == 10) {
            // Good to 2nd strike
            // take later one
            var sc = 10
            if (i+1 < end) {
                if (hits[i+1][1] == -1) { // strike
                    sc += score[i+1]
                } else if (hits[i+1][0] + hits[i+1][1] == 10) {
                    sc += score[i+1]
                } else {
                    sc += hits[i+1][0]
                }
            } else {

            }

        } else { // x + y < 10
            // remains bottle
            score[i] = x + y;
        }
    }
    //--
    // for i in 0...end {
    //     score[end-i] = cal(end - i)
    // }

    // Evaluate all the score
    var ans = 0
    for x in score {
        ans += x
    }
    return ans;
}

// eval a[k]'s score'
func cal(a:[[Int]], k:Int) {
    let n = a.count
    if (k >= n) return 0;

    var x = a[k][0]
    var y = a[k][1]
    if (x == 10 && y == -1) {
        // look next two
        if (k + 1 < n) {
            var s2 = 0
            // next one is strike

            if (a[k+1][1] == -1) {
                if (k+2 < n) {
                    s2 += a[k+2][0]
                }
            }
            s2 += a[k+1]
        }
        return 10 + s2
    } else if (x + y == 10) {
        // look next one
        if (k + 1 < n) {
            var s2 = 0
            // next one is strike
            if (a[k+1][1] == -1) {
                s2 += cal(a, k+1)
            } else {
                s2 += a[i+1][0]
            }
        }
        return 10 + s2
    } else {
        return x + y
    }
}
*/