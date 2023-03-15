package main.hp;

import flyingkite.awt.Robot2;
import flyingkite.files.FileUtil;
import flyingkite.log.L;

import java.awt.AWTException;
import java.io.File;

public class HPMain extends HPUtil {
    public static final HPMain me = new HPMain();


    public static void main(String[] args) {
        //me.makePages();
        //HPTask.autoHPDigitalAuth();
        //HPTask.main(null);
        //me.clickPasteEnter();
        //cloneGit("C:\\Users\\chener\\Documents\\HP\\GitHub\\CMITSW\\WinPVT03", HPUtil.repo_WinPVT);
        cloneGit("C:\\Users\\chener\\Documents\\HP\\GitHub\\CMITSW\\AuditTool03", HPUtil.repo_AuditTool);
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

    /**
     * Print out the command line commands for check out specified git into local folder
     * For example : Checkout WinPVT into C:\Users\chener\Documents\HP\GitHub\CMITSW\WinPVT03c
     * mkdir C:\Users\chener\Documents\HP\GitHub\CMITSW
     * cd C:\Users\chener\Documents\HP\GitHub\CMITSW
     * git clone https://github.azc.ext.hp.com/CMITSW/WinPVT WinPVT03c
     */
    private static void cloneGit(String folder, String name, String repository) {
        File f = new File(folder);
        if (!FileUtil.isExistDir(f)) {
            L.log("mkdir %s", f);
        }
        L.log("cd %s", folder);
        L.log("git clone %s %s", repository, name);
    }

    /**
     * Print out the command line commands for check out specified git into specified folder directly
     * {@link #cloneGit(String, String)}
     */
    private static void cloneGit(String path, String repository) {
        File dst = new File(path);
        File p = dst.getParentFile();
        cloneGit(p.getAbsolutePath(), dst.getName(), repository);
    }

    // "New item" in the WinPVT board by using paste board
    private void clickPasteEnter() {
        robot.mouseClickLeft().defer(1000).paste().defer(1000).enter();
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
