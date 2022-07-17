package main;

import flyingkite.files.FileUtil;
import flyingkite.files.Zipper;
import flyingkite.log.L;
import flyingkite.tool.TicTac2;
import main.kt.CopyInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FaceMeAuto {
    private static final TicTac2 clk = new TicTac2();
    public static void replaceFintechNile() {
        //String nile_src = "D:/FaceMeFintechSDK_Android/Fintech_Nile2";
        String nile_src = "D:/FaceMeFintechSDK_Android/Nile_01";
        String fsdk_dst = "D:/FaceMeFintechSDK_Android/SA_02";
        clk.tic();
        unzipFaceMeNile(nile_src); // 84% time
        copyToFaceMeNile(nile_src, fsdk_dst); // 16% time
        clk.tac("replaceFintechNile OK");
    }

    private static void unzipFaceMeNile(String nile) {
        //String src = "D:/FaceMeFintechSDK_Android/Fintech_Nile2/clrtc/build/outputs/aar/clrtc-release.aar";
        String src = nile + "/clrtc/build/outputs/aar/clrtc-release.aar";
        //String p = "D:/FaceMeFintechSDK_Android/Fintech_Nile2/clrtc/build/outputs/aar/clrtc-release";
        String p = src.substring(0, src.lastIndexOf('.'));
        FileUtil.ensureDelete(new File(p)); // 14% time
        Zipper.unzip(src); // 86% time
    }

    // copy all contents from src into dst folder
    private static void copyToFaceMeNile(String nile, String fintech) {
        List<CopyInfo> paths = new ArrayList<>();
        // copy all contents from src into dst folder

        //String src = "D:/FaceMeFintechSDK_Android/Fintech_Nile2/clrtc/build/outputs/aar/clrtc-release/jni";
        //String dst = "D:/FaceMeFintechSDK_Android/SA_02/fintechsdk/src/main/jniLibs";
        String src = nile + "/clrtc/build/outputs/aar/clrtc-release/jni";
        String dst = fintech + "/fintechsdk/src/main/jniLibs";
        // List all files under srcF as fs
        File srcF = new File(src);
        File dstF = new File(dst);
        List<File> fs = FileUtil.listAllFiles(srcF);
        // Collects items to be copied in paths, 50% time
        for (int i = 0; i < fs.size(); i++) {
            File item = fs.get(i);
            if (item.isFile()) {
                String p = item.getAbsolutePath();
                String newP = p.replace(srcF.getAbsolutePath(), dstF.getAbsolutePath());
                paths.add(new CopyInfo(p, newP));
            }
        }
        // copy classs.jar to clrtc.jar
        File clazzJar = new File(nile + "/clrtc/build/outputs/aar/clrtc-release/classes.jar");
        File clrtcJar = new File(fintech + "/fintechsdk/libs/clrtc.jar");
        CopyInfo c = new CopyInfo(clazzJar, clrtcJar);
        paths.add(c);

        // perform copy, 50% time
        for (int i = 0; i < paths.size(); i++) {
            CopyInfo it = paths.get(i);
            String source = it.getSrcFile().getAbsolutePath();
            String target = it.getDstFile().getAbsolutePath();
            FileUtil.copy(source, target);
            L.log("#%2d copy: %s\n    to -> %s", i, source, target);
        }

    }

    public static void faceMeFintechBuild() {
        int appRevisionInSR = 63799;
        String appVersionName = "5.2.0";
        // from invitation link, permalink
        // Click FaceMe®Fintech - 5.0.0 > Invitation permalink
        String testFairyUrl = "https://my.testfairy.com/download/64R30D9G6WSKGB9M68S3JE9J6GR3CBB5DZJ6H9TPYYQ7BJSV3X5MG657M7Y8RWG/getapp";

        L.log("U-Message send following both in chats and board :\n");
        L.log("FaceMe Fintech SDK Android Demo %s_r%s", appVersionName, appRevisionInSR);
        L.log(testFairyUrl);
        L.log("----");
        L.log("SR template :\n");
        L.log("FaceMe Fintech SDK Android %s_r%s", appVersionName, appRevisionInSR);
        L.log("Cyberlink");
        L.log("----");
    }

    public static List<RevInfo> organizeCommitLog() {
        File src = new File("D:\\Github\\sample.txt");
        L.log("src %s %s", src.exists(), src);
        List<String> li = FileUtil.readAllLines(src);
        List<RevInfo> ans = new ArrayList<>();
        String[] msg = {"ready", "Revision: ", "Author: ", "Date: ", "Message:", "----"};
        int state = 0;
        RevInfo now = null;

        for (int i = 0; i < li.size(); i++) {
            String line = li.get(i);
            String focus;
            if (line.startsWith("Revision: ")) {
                if (state == 0) {
                    state = 1;
                    focus = line.replaceFirst("Revision: ", "");
                    int rev = 0;
                    try {
                        rev = Integer.parseInt(focus);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    now = new RevInfo();
                    now.revision = rev;
                }
            } else if (line.startsWith("Author: ")) {
                state = 2;
                focus = line.replaceFirst("Author: ", "");
                if (now != null) {
                    now.author = focus;
                }
            } else if (line.startsWith("Date: ")) {
                state = 3;
                focus = line.replaceFirst("Date: ", "");
                if (now != null) {
                    now.date = focus;
                }
            } else if (line.startsWith("Message:")) {
                state = 4;
                focus = line.replaceFirst("Message: ", "");
                // reading messages
                do {
                    i++;
                    line = li.get(i);
                    if (line.startsWith("----")) {
                        state = 5;
                    } else {
                        if (now != null) {
                            now.message.add(line);
                        }
                    }
                } while (state == 4);
            } else if (line.startsWith("Modified : ")) {
                String key = "Modified : ";
                focus = line.replaceFirst(key, "");
                if (state == 5) {
                    state = 6;
                }
                if (now != null) {
                    List<String> chg = now.changes.getOrDefault(key, new ArrayList<>());
                    chg.add(focus);
                    now.changes.put(key, chg);
                }
            } else if (line.startsWith("Added : ")) {
                String key = "Added : ";
                focus = line.replaceFirst(key, "");
                if (state == 5) {
                    state = 6;
                }
                if (now != null) {
                    List<String> chg = now.changes.getOrDefault(key, new ArrayList<>());
                    chg.add(focus);
                    now.changes.put(key, chg);
                }
            } else if (line.startsWith("Deleted : ")) {
                String key = "Deleted : ";
                focus = line.replaceFirst(key, "");
                if (state == 5) {
                    state = 6;
                }
                if (now != null) {
                    List<String> chg = now.changes.getOrDefault(key, new ArrayList<>());
                    chg.add(focus);
                    now.changes.put(key, chg);
                }
            } else if (line.startsWith("----")) {
                state = 5;
            } else if (line.isEmpty()) {
                state = 0;
                ans.add(now);
            }
        }
        for (int i = 0; i < ans.size(); i++) {
            L.log("#%s : %s", i, ans.get(i));
        }

        return ans;
    }

    public static class RevInfo {
        public int revision;
        public String author;
        public String date;
        public List<String> message = new ArrayList<>();
        public Map<String, List<String>> changes = new HashMap<>();

        @Override
        public String toString() {
            int n = 0;
            List<List<String>> li = new ArrayList<>(changes.values());
            for (int i = 0; i < li.size(); i++) {
                n += li.get(i).size();
            }
            String s = String.format("%s, %s, %s, %s msg, %s changes", revision, author, date, message.size(), n);
            return s;
        }
    }
}

/*
1. Check out new Fintech SDK project, and open its main script, build_sdk.py,
2. Change variable of FM_ANDROID_DEMO_DIR to your wanted path, like
FM_ANDROID_DEMO_DIR = "D:/FaceMeFintechDemo_Android/FA_01"
3. open shell to run "python build_sdk.py"
4. it creates aars of fintechSDK & facemeSDK & faceme_widget
5. commit its new AAR to svn and add the mapping.txt
6. build TR build as latest committed revision in SR,
https://ecl.cyberlink.com/PC/ShowSRF/ShowSRFResult.asp?SRF_no=FMI220104-02
7. find provided APK file in TR build path and upload it to Test fairy
https://3892356.testfairy.com/
stacy_chung@cyberlink.com
Cl23829868@
8. Create U-message chat in Group FaceMe[EDGE+CLOUD], board named "FaceMe Fintech Android SDK Demo"
format :
FaceMe Fintech Demo Android {version}_r{revision}
{testFairyUrl}

like
FaceMe Fintech Demo Android 3.0.0_r60408
https://my.testfairy.com/download/74V3JE1Q74SJTD1J68WKJCHM60V2TNDE9EQK3HHES75ECDPVBNWJMKQ80GHTE/getapp

Templates:
SR

FaceMe Fintech SDK Android 4.2.0_r61845
Cyberlink

FaceMe Fintech SDK Android Demo 4.2.0_r61845
https://my.testfairy.com/download/74VKJE1S6GSJTD1J68WKJCHM60V2TMJEY8A8QY98W72SP5KZTMA9HBTRZ1BC4/getapp


*/
