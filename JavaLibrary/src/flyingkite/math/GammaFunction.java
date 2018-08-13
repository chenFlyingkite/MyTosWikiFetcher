package flyingkite.math;

/**
 * Γ function
 * Γ γ gamma
 * https://en.wikipedia.org/wiki/Gamma_function
 *
 * The gamma function along part of the real axis
 * In mathematics, the gamma function (represented by Γ, the capital Greek alphabet letter gamma) is an extension of the factorial function, with its argument shifted down by 1, to real and complex numbers. If n is a positive integer,
 * {\displaystyle \Gamma (n)=(n-1)!} \Gamma (n)=(n-1)!
 */
public class GammaFunction {


    /**
     * Returns Γ(n), for integer n
     * G(21) will overflows
     * G(-3) = 0
     * G(-2) = 0
     * G(-1) = 0
     * G(0) = 1
     * G(1) = 1
     * G(2) = 2
     * G(3) = 6
     * G(4) = 24
     * G(5) = 120
     * G(6) = 720
     * G(7) = 5040
     * G(8) = 40320
     * G(9) = 362880
     * G(10) = 3628800
     * G(11) = 39916800
     * G(12) = 479001600
     * G(13) = 6227020800
     * G(14) = 87178291200
     * G(15) = 1307674368000
     * G(16) = 20922789888000
     * G(17) = 355687428096000
     * G(18) = 6402373705728000
     * G(19) = 121645100408832000
     * G(20) = 2432902008176640000
     * G(21) = -4249290049419214848
     */
    public static long gammaN(int n) {
        if (n < 0) return 0;

        long g1 = 1;
        long gn = g1;

        for (int i = 2; i <= n; i++) {
            gn = gn * i;
        }
        return gn;
    }

    /**
     * Double version for {@link #gammaN(int)}
     */
    public static double gammaNDouble(int n) {
        if (n < 0) return 0;

        double g1 = 1;
        double gn = g1;

        for (int i = 2; i <= n; i++) {
            gn = gn * i;
        }
        return gn;
    }

    /**
     * Return Γ(n/2), n is integer
     * Γ(1/2) = sqrt(π)
     * Γ( 1/2 + k ) = kΓ(1/2 + k-1)
     * return Γ(n) {@link #gammaN(int)} if n even
     * G(340 / 2) = 7.257415615307994E306
     * G(341 / 2) = 1.2863434254974502E307
     * G(342 / 2) = Infinity
     */
    public static double gammaN2(int n) {
        if (n < 0) return 0;
        if (n % 2 == 0) {
            return gammaNDouble(n / 2);
        }

        double g1 = Math.sqrt(Math.PI);
        double gn = g1;

        for (int i = 3; i <= n; i += 2) {
            gn = gn * (i - 1) / 2;
        }
        return gn;

    }
}
