package flyingkite.data;

public class PointN {
    public double[] v = new double[2];

    public PointN() {}

    public PointN(double[] values) {
        set(values);
    }

    public PointN(PointN p) {
        this(p.v);
    }

    public final void set(double[] values) {
        int n = values.length;
        v = new double[n];
        for (int i = 0; i < n; i++) {
            v[i] = values[i];
        }
    }

    public final void set(PointN p) {
        set(p.v);
    }

    public final void negate() {
        int n = v.length;
        for (int i = 0; i < n; i++) {
            v[i] = -v[i];
        }
    }

    public final void offset(double[] dv) {
        int n = Math.min(v.length, dv.length);
        for (int i = 0; i < n; i++) {
            v[i] += dv[i];
        }
    }

    public final void offset(PointN p) {
        offset(p.v);
    }

    /**
     * Returns true if the point's coordinates equal (x,y)
     */
    public final boolean equals(double[] u) {
        int n = Math.min(v.length, u.length);
        if (n == 0) {
            return v.length == 0;
        }

        for (int i = 0; i < n; i++) {
            if (v[i] != u[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PointN p = (PointN) o;
        return equals(p.v);
    }

    @Override
    public String toString() {
        int n = v.length;
        StringBuilder s = new StringBuilder("P[").append(n).append("] = (");

        for (int i = 0; i < n; i++) {
            if (i > 0) {
                s.append(", ");
            }
            s.append(v[i]);
        }
        s.append(")");
        return s.toString();
    }

    public final double length() {
        double s = 0;
        int n = v.length;
        for (int i = 0; i < n; i++) {
            s += v[i] * v[i];
        }
        return Math.sqrt(s);
    }

    public static double length(double[] p) {
        PointN z = new PointN(p);
        return z.length();
    }

    /**
     * Return the euclidian distance from (0,0) to the point
     */
    public final double lengthHypot() {
        double z = 0;
        int n = v.length;

        for (int i = 0; i < n; i++) {
            z = Math.hypot(z, v[i]);
        }
        return z;
    }

    /**
     * Returns the euclidian distance from (0,0) to (x,y)
     */
    public static double lengthHypot(double[] p) {
        double z = 0;
        int pn = p.length;

        for (int i = 0; i < pn; i++) {
            z = Math.hypot(z, p[i]);
        }
        return z;
    }

}
