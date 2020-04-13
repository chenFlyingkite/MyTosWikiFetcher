package flyingkite.algorithm.sequence;

/**
 * Given two strings S and T, each of which represents a non-negative rational number, return True if and only if they represent the same number. The strings may use parentheses to denote the repeating part of the rational number.
 *
 * In general a rational number can be represented using up to three parts: an integer part, a non-repeating part, and a repeating part. The number will be represented in one of the following three ways:
 *
 * <IntegerPart> (e.g. 0, 12, 123)
 * <IntegerPart><.><NonRepeatingPart>  (e.g. 0.5, 1., 2.12, 2.0001)
 * <IntegerPart><.><NonRepeatingPart><(><RepeatingPart><)> (e.g. 0.1(6), 0.9(9), 0.00(1212))
 * The repeating portion of a decimal expansion is conventionally denoted within a pair of round brackets.  For example:
 *
 * 1 / 6 = 0.16666666... = 0.1(6) = 0.1666(6) = 0.166(66)
 *
 * Both 0.1(6) or 0.1666(6) or 0.166(66) are correct representations of 1 / 6.
 *
 * See https://leetcode.com/problems/equal-rational-numbers/
 */
public class EqualRationalNum {

    public boolean isRationalEqual(String S, String T) {
        return sol1(S, T);
    }

    private boolean sol1(String s, String t) {
        Frac sf = parse(s);
        Frac tf = parse(t);
        return sf.equals(tf);
    }

    // Make String into a + b / c, c > b > 0,
    private Frac parse(String s) {
        String[] ss = s.split("[.()]");
        Frac f = new Frac(0, 0, 1); // = 0 + 0/1

        for (int i = 0; i < ss.length; i++) {
            String si = ss[i];
            long x = 0;
            if (si.isEmpty()) {
                continue;
            }
            x = Long.parseLong(si);
            //ln("parse %s as %s", si, x);
            if (i == 0) { // integer
                f.a += x;
            } else if (i == 1) { // decimal
                long z = get10(ss[1].length());
                f.add(new Frac(0, x, z));
            } else if (i == 2) { // repeating
                long zx = get10(ss[2].length());
                long zn = get10(ss[1].length());
                // make as (9, 990, 999, ...) * 10^k
                // z = (10^s2 - 1) * 10^s1
                long z = (zx-1)*zn;
                //ln("z = %s, zn = %s, zx = %s", z, zn, zx);
                f.add(new Frac(0, x, z));

            }
            //ln("#%d, f = %s", i, f);
        }
        //ln("parse %s = %s", s, f);
        return f;
    }

    // return 10^n
    private long get10(int n) {
        int[] ten = {1, 10, 100, 1_000, 10_000, 100_000};
        return ten[n];
    }

    private long get10_(int n) {
        int s = 1;
        for (int i = 0; i < n; i++) {
            s *= 10;
        }
        return s;
    }

    private class Frac {
        // f = a + b / c
        long a;
        long b;
        long c = 1;

        Frac(long _a, long _b, long _c) {
            a = _a;
            b = _b;
            c = _c;
            normalize();
        }

        void add(Frac f) {
            //ln("Add    %s + %s", this, f);
            // perform b/c + f.b/f.c = bb / cc
            a = a + f.a;
            b = b * f.c + f.b * c;
            c = f.c * c;
            normalize();
            //ln("Add += %s", this);
        }

        // make c > b > 0, gcd(b, c) = 1
        public void normalize() {
            long q = b / c;
            long r = b % c;
            long g = gcd(r, c);
            a += q;
            b = r / g;
            c = c / g;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Frac) {
                Frac f = (Frac) o;
                return a == f.a && b == f.b && c == f.c;
            } else {
                return false;
            }
        }

        public String toString() {
            return a + " + " + b + " / " + c;
        }
    }

    private long lcm(long a, long b) {
        return a / gcd(a, b) * b;
    }

    private long gcd(long a, long b) {
        long x = a;
        long y = b;
        while (y > 0) {
            long r = x % y;
            x = y;
            y = r;
        }
        return x;
    }

    private static void ln(String fmt, Object... p) {
        System.out.println((p == null) ? fmt : String.format(fmt, p));
    }
}
/*
"0.(52)"
"0.5(25)"
"0.1666(6)"
"0.166(66)"
"0.9(9)"
"1."
"7575.7(57)"
"7575.7575(7575)"
"0.08(9)"
"0.09"
"12.375(75)"
"12.37575(75)"
"12.375(00)"
"12.375(00)"
"1.001(01)"
"1.00(10)"
*/
