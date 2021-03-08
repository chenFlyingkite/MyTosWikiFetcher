package flyingkite.math;

import flyingkite.log.L;

import java.util.Arrays;

public class Matrix {
    public double[][] m;

    public Matrix(double[][] values) {
        m = values;
    }

    public static Matrix multiply(Matrix ma, Matrix mb) {
        double[][] a = ma.m;
        double[][] b = mb.m;
        int am = a.length;
        int an = a[0].length;
        int bm = b.length;
        int bn = b[0].length;
        if (an != bm) {
            L.log("Wrong dim");
        }
        double[][] c = new double[am][bn];
        for (int i = 0; i < am; i++) {
            for (int j = 0; j < bn; j++) {
                double s = 0;
                for (int k = 0; k < an; k++) {
                    s += a[i][k] * b[k][j];
                }
                c[i][j] = s;
            }
        }
        return new Matrix(c);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        String ln = String.format("%dx%d {\n", m.length, m[0].length);
        b.append(ln);
        for (int i = 0; i < m.length; i++) {
            ln = String.format("%d : %s\n", i, Arrays.toString(m[i]));
            b.append(ln);
        }
        return b.toString();
    }
}
