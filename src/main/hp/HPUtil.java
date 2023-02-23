package main.hp;

import flyingkite.awt.Robot2;
import flyingkite.log.L;
import flyingkite.tool.ThreadUtil;
import main.fetcher.web.WebFetcher;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Just class fast for development
 */
public class HPUtil {
    public static final String x64 = "x64-windows";
    public static final String x64static = "x64-windows-static-md";
    public static final String x86 = "x86-windows";
    public static final String x86static = "x86-windows-static-md";


    public static final Robot2 robot = Robot2.create();
    public static final WebFetcher webFetcher = new WebFetcher();

    public static void printNow() {
        L.log("Now = %s", yyyyMMdd().format(now()));
    }

    public static void sleep(long ms) {
        ThreadUtil.sleep(ms);
    }

    public static void ln(String f, Object... p) {
        L.log(f, p);
    }

    //--
    public static Date now() {
        return new Date();
    }

    public static SimpleDateFormat yyyyMMdd() {
        return new SimpleDateFormat("yyyyMMdd_hhmmss");
    }
}
