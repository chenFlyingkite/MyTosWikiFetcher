package main.hp;

import flyingkite.awt.Robot2;
import flyingkite.log.L;

import java.awt.AWTException;

public class HPMain extends HPUtil {
    public static final HPMain me = new HPMain();
    public static void main(String[] args) {
        me.makePages();
    }

    private void makePages() {
        // fillCrossDepartmentReview
        // To click on everyone of no, and click the 1st one on leftmost Yes
        //robot.delay(5_000);
        String[] ss = {
                "SIO2145947",
                "SIO2173112",
                "SIO2139445",
        };
        for (int i = 0; i < ss.length; i++) {
            L.log("%s", toSIOPage(ss[i]));
        }
        String link = "https://pulsarweb.twn.hp.com/pulsar2/ProductPage?productId=3152";
        L.log("%s\n%s", link, webFetcher.getDocument(link));
    }

    private String toSIOPage(String id) {
        if (!id.startsWith("SIO")) {
            id = "SIO" + id;
        }
        return String.format("https://si.austin.hp.com/si/?ObjectType=6&Object=%s", id);
    }

    private static Robot2 robot;
    static {
        try {
            robot = new Robot2();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

}
