package main.misc;

import flyingkite.log.L;
import flyingkite.tool.IOUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Cmd {
    // https://stackabuse.com/executing-shell-commands-with-java/

    //        try {
////            Process process = Runtime.getRuntime().exec("ping www.stackabuse.com");
////            printResults(process);
//            String folder = "D:/FaceMeFintechSDK_Android/Fintech_Nile2/clrtc/build/outputs/aar";
//            String name = "clrtc-release.aar";
//            String path = folder + "/" + name;
//            String x = "x";
//            String out = folder + "/" + x;
//            Runtime r = Runtime.getRuntime();
//            L.log("path = %s", path);
//            L.log("out = %s", out);
//            // copy D:\FaceMeFintechSDK_Android\Fintech_Nile2\clrtc\build\outputs\aar\clrtc-release\jni
//            // into D:\FaceMeFintechSDK_Android\SA_02\fintechsdk\src\main\jniLibs
//            //printResults(r.exec("cmd /?"));
//            printResults(r.exec("cmd /c dir", null, new File(folder)));
//            if (new File(out).exists()) {
//                printResults(r.exec("cmd /c rm " + x, null, new File(folder)));
//            }
//            printResults(r.exec("cmd /c mkdir " + x, null, new File(folder)));
//            printResults(r.exec("cmd /c jar -xvf " + path + " " + out));
//            printResults(r.exec("cd " + out));
//            printResults(r.exec("dir"));
////            r.exec("jar -xvf " + path + " ");
//            //r.exec("jar -xvf ");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    private static void printResults(Process p) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = "";
        try {
            while ((line = reader.readLine()) != null) {
                L.log(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            IOUtil.closeIt(reader);
        }
    }
}
// https://www.itread01.com/p/1344318.html
/*
java的Runtime.getRuntime().exec(commandStr)可以呼叫執行cmd指令。

        cmd /c dir 是執行完dir命令後關閉命令視窗。

        cmd /k dir 是執行完dir命令後不關閉命令視窗。

        cmd /c start dir 會開啟一個新視窗後執行dir指令,原視窗會關閉。

        cmd /k start dir 會開啟一個新視窗後執行dir指令,原視窗不會關閉。

        可以用cmd /?檢視幫助資訊。

        ★CMD命令★
        1. gpedit.msc-----組策略
        2. sndrec32-------錄音機
        3. Nslookup-------IP地址偵測器
        4. explorer-------開啟資源管理器
        5. logoff---------登出命令
        6. tsshutdn-------60秒倒計時關機命令
        7. lusrmgr.msc----本機使用者和組
        8. services.msc---本地服務設定
        9. oobe/msoobe /a----檢查XP是否啟用
        10. notepad--------開啟記事本
        11. cleanmgr-------垃圾整理
        12. net start messenger----開始信使服務
        13. compmgmt.msc---計算機管理
        14. net stop messenger-----停止信使服務
        15. conf-----------啟動netmeeting
        16. dvdplay--------DVD播放器
        17. charmap--------啟動字元對映表
        18. diskmgmt.msc---磁碟管理實用程式
        19. calc-----------啟動計算器
        20. dfrg.msc-------磁碟碎片整理程式
        21. chkdsk.exe-----Chkdsk磁碟檢查
        22. devmgmt.msc--- 裝置管理器
        23. regsvr32 /u *.dll----停止dll檔案執行
        24. drwtsn32------ 系統醫生
        25. rononce -p ----15秒關機
        26. dxdiag---------檢查DirectX資訊
        27. regedt32-------登錄檔編輯器
        28. Msconfig.exe---系統配置實用程式
        29. rsop.msc-------組策略結果集
        30. mem.exe--------顯示記憶體使用情況
        31. regedit.exe----登錄檔
        32. winchat--------XP自帶區域網聊天
        33. progman--------程式管理器
        34. winmsd---------系統資訊
        35. perfmon.msc----計算機效能監測程式
        2. 36. winver---------檢查Windows版本
        37. sfc /scannow-----掃描錯誤並復原
        38. taskmgr-----工作管理員(2000/xp/2003
        39. winver---------檢查Windows版本
        40. wmimgmt.msc----開啟windows管理體系結構(WMI)
        41. wupdmgr--------windows更新程式
        42. wscript--------windows指令碼宿主設定
        43. write----------寫字板
        44. winmsd---------系統資訊
        45. wiaacmgr-------掃描器和照相機嚮導
        46. winchat--------XP自帶區域網聊天
        47. mem.exe--------顯示記憶體使用情況
        48. Msconfig.exe---系統配置實用程式
        49. mplayer2-------簡易widnows media player
        50. mspaint--------畫圖板
        51. mstsc----------遠端桌面連線
        52. mplayer2-------媒體播放機
        53. magnify--------放大鏡實用程式
        54. mmc------------開啟控制檯
        55. mobsync--------同步命令
        56. dxdiag---------檢查DirectX資訊
        57. drwtsn32------ 系統醫生
        58. devmgmt.msc--- 裝置管理器
        59. dfrg.msc-------磁碟碎片整理程式
        60. diskmgmt.msc---磁碟管理實用程式
        61. dcomcnfg-------開啟系統元件服務
        62. ddeshare-------開啟DDE共享設定
        63. dvdplay--------DVD播放器
        64. net stop messenger-----停止信使服務
        65. net start messenger----開始信使服務
        66. notepad--------開啟記事本
        67. nslookup-------網路管理的工具嚮導
        68. ntbackup-------系統備份和還原
        69. narrator-------螢幕“講述人”
        70. ntmsmgr.msc----移動儲存管理器
        71. ntmsoprq.msc---移動儲存管理員操作請求
        72. netstat -an----(TC)命令檢查介面
        73. syncapp--------建立一個公文包
        74. sysedit--------系統配置編輯器
        75. sigverif-------檔案簽名驗證程式
        76. sndrec32-------錄音機
        77. shrpubw--------建立共享資料夾
        78. secpol.msc-----本地安全策略
        79. syskey---------系統加密,一旦加密就不能解開,保護windows xp系統的雙重密碼
        80. services.msc---本地服務設定
        81. Sndvol32-------音量控制程式
        82. sfc.exe--------系統檔案檢查器
        83. sfc /scannow---windows檔案保護
        84. tsshutdn-------60秒倒計時關機命令
        3. 84. tsshutdn-------60秒倒計時關機命令
        85. tourstart------xp簡介(安裝完成後出現的漫遊xp程式)
        86. taskmgr--------工作管理員
        87. eventvwr-------事件檢視器
        88. eudcedit-------造字程式
        89. explorer-------開啟資源管理器
        90. packager-------物件包裝程式
        91. perfmon.msc----計算機效能監測程式
        92. progman--------程式管理器
        93. regedit.exe----登錄檔
        94. rsop.msc-------組策略結果集
        95. regedt32-------登錄檔編輯器
        96. rononce -p ----15秒關機
        97. regsvr32 /u *.dll----停止dll檔案執行
        98. regsvr32 /u zipfldr.dll------取消ZIP支援
        99. cmd.exe--------CMD命令提示符
        100. chkdsk.exe-----Chkdsk磁碟檢查
        101. certmgr.msc----證書管理實用程式
        102. calc-----------啟動計算器
        103. charmap--------啟動字元對映表
        104. cliconfg-------SQL SERVER 客戶端網路實用程式
        105. Clipbrd--------剪貼簿檢視器
        106. conf-----------啟動netmeeting
        107. compmgmt.msc---計算機管理
        108. cleanmgr-------垃圾整理
        109. ciadv.msc------索引服務程式
        110. osk------------開啟螢幕鍵盤
        111. odbcad32-------ODBC資料來源管理器
        112. oobe/msoobe /a----檢查XP是否啟用
        113. lusrmgr.msc----本機使用者和組
        114. logoff---------登出命令
        115. iexpress-------木馬捆綁工具,系統自帶
        116. Nslookup-------IP地址偵測器
        117. fsmgmt.msc-----共享資料夾管理器
        118. utilman--------輔助工具管理器
        119. gpedit.msc-----組策略
        120. explorer-------開啟資源管理器

        範例:磁碟雙擊打不開的

        處理方法:

        cmd /c reg delete "HKEY_CURRENT_USER\Software\Microsoft\Windows\CurrentVersion\Explorer\MountPoints2" /f
        cmd /k reg delete "HKEY_CURRENT_USER\Software\Microsoft\Windows\CurrentVersion\Explorer\MountPoints2" /f

        cmd k reg delete "HKEY_CLASSES_ROOT\Drive\shell\run" /f
        cmd /k reg delete "HKEY_CLASSES_ROOT\Drive\shell\run" /f
*/
