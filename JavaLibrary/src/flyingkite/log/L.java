package flyingkite.log;

/**
 * The class for System.out.println()
 */
public class L {
    private static final Impl impl = new Impl();

    // only print message
    public static void print(String msg) {
        impl.print(msg);
    }

    // print message"\n"
    public static void log(String msg) {
        impl.log(msg);
    }

    public static void log(String msg, Object... param) {
        impl.log(msg, param);
    }

    public static Impl getImpl() {
        return impl;
    }

    public static class Impl implements Loggable {

        public void print(String msg) {
            System.out.print(msg);
        }

        @Override
        public void log(String msg) {
            System.out.println(msg);
        }
    }
}
