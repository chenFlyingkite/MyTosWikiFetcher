package flyingkite.geometry;

import flyingkite.data.PointN;
import flyingkite.log.L;

import java.util.Arrays;

public class Triangle {
    public final double[] x = new double[3];
    public final double[] y = new double[3];

    public Triangle() {}

    public Triangle(Triangle t) {
        for (int i = 0; i < 3; i++) {
            x[i] = t.x[i];
            y[i] = t.y[i];
        }
    }

    public void setPoint(int i, double xi, double yi) {
        x[i] = xi;
        y[i] = yi;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("Triangle = ");
        for (int i = 0; i < 3; i++) {
            if (i > 0) {
                s.append(", ");
            }
            s.append("(").append(x[i]).append(", ").append(y[i]).append(")");
        }
        return s.toString();
    }

    /** return determinent
     * | x0  y0  1 |
     * | x1  y1  1 |
     * | x2  y2  1 |
     */
    public double det() {
        return x[0]*(y[1]-y[2]) + x[1]*(y[2]-y[0]) + x[2]*(y[0]-y[1]);
    }

    public void changeClock() {
        double x1 = x[1];
        double y1 = y[1];
        setPoint(1, x[2], y[2]);
        setPoint(2, x1, y1);
    }

    public boolean isClockwise() {
        return det() < 0; // for y is up, euclidean
    }

    /** return determinent
     * | x1  y1  1 |
     * | x2  y2  1 |
     * | x3  y3  1 |
     */
    public double area() {
        return Math.abs(det() / 2.0);
    }

    public boolean isInTriangle(double px, double py) {
        Triangle abc = this;
        Triangle zbc = new Triangle(abc);
        zbc.setPoint(0, px, py);
        Triangle azc = new Triangle(abc);
        azc.setPoint(1, px, py);
        Triangle abz = new Triangle(abc);
        abz.setPoint(2, px, py);

        double A = abc.area();
        double B = zbc.area() + azc.area() + abz.area();
        return A == B;
    }

    public static void test() {
        PointN p = new PointN();
        for (int i = 1; i < 11; i++) {
            p.v = new double[i];
            Arrays.fill(p.v, 1);
            L.log("#%s : p.l = %s, p = %s", i, p.length(), p);
            //L.log("sqrt(%s)  = %s", i, Math.sqrt(i));
        }
        Triangle t = new Triangle();
        t.x[2] = 3;
        t.y[1] = 4;
        L.log("det(t) = %s, t = %s", t.det(), t);

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                boolean b = t.isInTriangle(i, j);
                L.log("(%s, %s) is%s in %s", i, j, b ? "" : " Not", t);
            }
        }
        t.setPoint(0, 5, 1);
        t.setPoint(1, 1, 4);
        t.setPoint(2, 4, 8);
        L.log("det(t) = %s, t = %s", t.det(), t);
        Triangle z = new Triangle(t);
        L.log("clock = %s, %s", z.isClockwise(), z);
        z.changeClock();
        L.log("clock = %s, %s", z.isClockwise(), z);
    }

}

