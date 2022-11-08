package main;

import flyingkite.files.FileUtil;
import flyingkite.files.Zipper;
import flyingkite.log.L;
import flyingkite.tool.TicTac2;
import main.kt.CopyInfo;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
        int appRevisionInSR = 64645;
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


    public static void organizeCommitLog() {
        File root = new File("D:\\Github\\svnSource");
        File[] fs = root.listFiles();
        Map<File, List<RevInfo>> map = new HashMap<>();
        if (fs != null) {
            for (int i = 0; i < fs.length; i++) {
                List<RevInfo> li = organizeCommitLog(fs[i]);
                map.put(fs[i], li);
            }
        }
    }

    public static List<RevInfo> organizeCommitLog(File src) {
        L.log("src %s %s", src.exists(), src);
        List<String> li = FileUtil.readAllLines(src);
        List<RevInfo> ans = new ArrayList<>();
        String[] anchor = {"ready", "Revision: ", "Author: ", "Date: ", "Message:", "----"};
        int state = 0;
        RevInfo now = new RevInfo();

        for (int i = 0; i < li.size(); i++) {
            String line = li.get(i);
            String focus;
            //L.log("line #%d = %s", i, line);
            if (line.startsWith("Revision: ")) {
                if (state == 0) {
                    state = 1;
                    focus = line.replaceFirst("Revision: ", "");
                    // core
                    int rev = 0;
                    try {
                        rev = Integer.parseInt(focus);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    now.revision = rev;
                }
            } else if (line.startsWith("Author: ")) {
                state = 2;
                focus = line.replaceFirst("Author: ", "");
                // core
                now.author = focus;
            } else if (line.startsWith("Date: ")) {
                state = 3;
                focus = line.replaceFirst("Date: ", "");
                // core
                now.date = focus;
            } else if (line.startsWith("Message:")) {
                state = 4;
                focus = line.replaceFirst("Message: ", "");
                // core reading messages
                do {
                    i++;
                    line = li.get(i);
                    if (line.startsWith("----")) {
                        state = 5;
                    } else {
                        now.message.add(line);
                        //L.log("now = %s, st = %s, %s", now, state, line);
                    }
                } while (state == 4);
            } else if (line.startsWith("Modified : ")) {
                String key = "Modified : ";
                focus = line.replaceFirst(key, "");
                if (state == 5) {
                    state = 6;
                }
                // core
                List<String> chg = now.changes.getOrDefault(key, new ArrayList<>());
                chg.add(focus);
                now.changes.put(key, chg);
            } else if (line.startsWith("Added : ")) {
                String key = "Added : ";
                focus = line.replaceFirst(key, "");
                if (state == 5) {
                    state = 6;
                }
                // core
                List<String> chg = now.changes.getOrDefault(key, new ArrayList<>());
                chg.add(focus);
                now.changes.put(key, chg);
            } else if (line.startsWith("Deleted : ")) {
                String key = "Deleted : ";
                focus = line.replaceFirst(key, "");
                if (state == 5) {
                    state = 6;
                }
                List<String> chg = now.changes.getOrDefault(key, new ArrayList<>());
                chg.add(focus);
                now.changes.put(key, chg);
            } else if (line.startsWith("----")) {
                state = 5;
            } else if (line.isEmpty()) {
                state = 0;
                now.fillInFields();
                ans.add(now);
                now = new RevInfo(); // build new one for it
            }
        }
        // used for commit SDK logs
        boolean printSDKLogs = false;
        if (printSDKLogs) {
            for (int i = 0; i < ans.size(); i++) {
                RevInfo it = ans.get(i);
                L.log("  - [%s] : %s", it.revision, it.message.get(0));
            }
        }
        // printing the MBO report
        L.log("%s:", FileUtil.getNameBeforeExtension(src));
        L.log("");
        L.log("Total committed %s revisions", ans.size());
        L.log("Number, Revision, Date, Changes, Message");
        for (int i = 0; i < ans.size(); i++) {
            RevInfo it = ans.get(i);
            String msg = it.message.get(0);
            L.log("#%2d: %s, %s, %2d, %s", i+1, it.revision, it.dateShort, it.changes.size(), msg);
        }
        L.log("");
        L.log("");
        L.log("");

        return ans;
    }

    public static class RevInfo {
        public int revision;
        public String author;
        public String date;
        public String dateShort;
        public List<String> message = new ArrayList<>();
        public Map<String, List<String>> changes = new HashMap<>();
        public int totalChanges;
        public static final SimpleDateFormat svnDate = new SimpleDateFormat("yyyy年M月d日 a hh:mm:ss", Locale.TAIWAN);
        public static final SimpleDateFormat shortDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.TAIWAN);

        public void fillInFields() {
            parseTWDate();
            countTotalChanges();
        }

        private void parseTWDate() {
            if (date == null) return;

            try {
                Date d = svnDate.parse(date);
                if (d == null) return;
                dateShort = shortDate.format(d);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        private void countTotalChanges() {
            int n = 0;
            List<List<String>> li = new ArrayList<>(changes.values());
            for (int i = 0; i < li.size(); i++) {
                n += li.get(i).size();
            }
            totalChanges = n;
        }

        @Override
        public String toString() {
            String s = String.format("%s, %s, %s, %s msg, %s changes", revision, author, date, message.size(), totalChanges);
            return s;
        }
    }

    // 2022/08/16 = 202 groups
    //[[0], [1, 126, 151, 244], [2], [3, 99, 131], [4], [5, 271], [6], [7, 27, 50, 88], [8, 77]
    // , [9, 32, 36, 104, 240]
    //                    ^^^
    // , [10, 270], [11], [12, 220, 286], [13], [14], [15], [16, 134], [17], [18], [19], [20], [21, 75, 125], [22, 42], [23], [24, 60, 115, 145], [25], [26], [28], [29, 55, 216], [30], [31]
    // , [33], [34, 147, 221, 256], [35], [37, 217], [38], [39], [40], [41], [43], [44, 105], [45], [46, 69, 144, 158, 225, 235, 260, 273, 280, 282, 299], [47], [48], [49, 284], [51], [52, 153]
    // , [53, 59, 65, 93, 196, 212, 219, 228, 233, 283, 287]
    //                         ^^^^
    // , [54], [56], [57, 239], [58], [61, 128], [62, 94, 292], [63, 66, 132, 195], [64], [67], [68], [70], [71, 117], [72], [73], [74], [76], [78, 82, 116, 175, 267, 285], [79], [80], [81], [83], [84], [85], [86, 203], [87, 127], [89], [90], [91], [92]
    // , [95], [96], [97], [98, 204], [100], [101], [102, 168], [103], [106, 143, 150, 200], [107], [108], [109, 180, 215, 245], [110, 133, 137, 159, 188, 263], [111], [112], [113], [114, 251], [118], [119], [120, 163], [121], [122], [123], [124], [129], [130, 183], [135], [136], [138], [139, 279], [140], [141], [142], [146, 171], [148, 165, 206, 242], [149], [152], [154], [155], [156, 223], [157], [160], [161], [162], [164], [166, 275, 276], [167], [169], [170], [172, 248, 277], [173], [174], [176], [177], [178, 209], [179], [181, 231], [182], [184], [185], [186], [187], [189], [190], [191], [192], [193], [194], [197], [198], [199], [201], [202], [205], [207], [208], [210], [211], [213], [214], [218], [222], [224], [226], [227], [229], [230], [232], [234], [236], [237], [238], [241, 247], [243], [246], [249], [250], [252], [253], [254, 295], [255], [257], [258], [259], [261], [262], [264], [265], [266], [268], [269], [272], [274], [278], [281], [288], [289], [290], [291], [293], [294], [296], [297], [298]]
    // 2022/08/04 = 202 groups
    //[[0], [1, 126, 151, 244], [2], [3, 99, 131], [4], [5, 271], [6], [7, 27, 50, 88], [8, 77]
    // , [9, 32, 36, 104]
    // , [10, 270], [11], [12, 220, 286], [13], [14], [15], [16, 134], [17], [18], [19], [20], [21, 75, 125], [22, 42], [23], [24, 60, 115, 145], [25], [26], [], [28], [29, 55, 216], [30], [31]
    // , [240]
    // , [33], [34, 147, 221, 256], [35], [], [37, 217], [38], [39], [40], [41], [], [43], [44, 105], [45], [46, 69, 144, 158, 225, 235, 260, 273, 280, 282, 299], [47], [48], [49, 284], [], [51], [52, 153]
    // , [53, 59, 65, 93, 196, 219, 228, 233, 283, 287]
    // , [54], [], [56], [57, 239], [58], [], [], [61, 128], [62, 94, 292], [63, 66, 132, 195], [64], [], [], [67], [68], [], [70], [71, 117], [72], [73], [74], [], [76], [], [78, 82, 116, 175, 267, 285], [79], [80], [81], [], [83], [84], [85], [86, 203], [87, 127], [], [89], [90], [91], [92]
    // , [212], []
    // , [95], [96], [97], [98, 204], [], [100], [101], [102, 168], [103], [], [], [106, 143, 150, 200], [107], [108], [109, 180, 215, 245], [110, 133, 137, 159, 188, 263], [111], [112], [113], [114, 251], [], [], [], [118], [119], [120, 163], [121], [122], [123], [124], [], [], [], [], [129], [130, 183], [], [], [], [], [135], [136], [], [138], [139, 279], [140], [141], [142], [], [], [], [146, 171], [], [148, 165, 206, 242], [149], [], [], [152], [], [154], [155], [156, 223], [157], [], [], [160], [161], [162], [], [164], [], [166, 275, 276], [167], [], [169], [170], [], [172, 248, 277], [173], [174], [], [176], [177], [178, 209], [179], [], [181, 231], [182], [], [184], [185], [186], [187], [], [189], [190], [191], [192], [193], [194], [], [], [197], [198], [199], [], [201], [202], [], [], [205], [], [207], [208], [], [210], [211], [], [213], [214], [], [], [], [218], [], [], [], [222], [], [224], [], [226], [227], [], [229], [230], [], [232], [], [234], [], [236], [237], [238], [], [], [241, 247], [], [243], [], [], [246], [], [], [249], [250], [], [252], [253], [254, 295], [255], [], [257], [258], [259], [], [261], [262], [], [264], [265], [266], [], [268], [269], [], [], [272], [], [274], [], [], [], [278], [], [], [281], [], [], [], [], [], [], [288], [289], [290], [291], [], [293], [294], [], [296], [297], [298], []]
    // = 240 ,
}

/*
How to use the source project of framework in FaceMeFintech :
1. FaceMe FintechDemoApp uses FaceMe FintechSDK framework
2. FaceMe FintechSDK source project build a framework to Fintech Demo to use
DemoApp <- FintechSDK.framework <- FintechSDK

steps:
1. Ensure xcode only open DemoApp project, Fintech SDK did not open xcode
2. Use finder to drag the Fintech SDK *.xcodeproject into Project list's item
  (Check you can see its *.xcodeproj's items inside)
3. FintechDemoApp.xcodeproj -> targets/DemoApp -> General / Frameworks, Library, and embedded Content
  -> Remove FintechSDK, and add workspace/FintechSDK/FintechSDK.framework
4. FintechDemoApp.xcodeproj -> targets/BroadcastExtension -> General / Frameworks and Library
  -> Add the FintechExtensionSDK.framework
5. Build and run to check the added log is success!

------

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
