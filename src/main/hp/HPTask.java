package main.hp;

import java.awt.event.KeyEvent;

public class HPTask extends HPUtil {
    public static void main(String[] args) {
        printNow();
        sleep(3_000);
        //cmdInstallWinPVT_vcpkg("C:\\MicrosoftGithub\\vcpkg", x64static);
        //autoHPDigitalAuth();
        //enterEidEmail();
        autoOOO();
        printNow();
    }

    private static void autoOOO() {
        sleep(2_000);
        robot.keyPressRelease(new int[]{KeyEvent.VK_ALT, KeyEvent.VK_S})
                .keyPressRelease(new int[]{KeyEvent.VK_ALT, KeyEvent.VK_E})
                .keyPressRelease(new int[]{KeyEvent.VK_ALT, KeyEvent.VK_M})
                ;
    }

    private static void enterEidEmail() {
        robot.type("24208582")
                .keySend(KeyEvent.VK_TAB)
                .type("eric.chen@hp.com")
                .keySend(KeyEvent.VK_TAB)
                .keySend(KeyEvent.VK_TAB)
                .enter();
    }

    private static void autoHPDigitalAuth() {
        // failed...
        sleep(5_000);
        robot.keySend(KeyEvent.VK_SPACE); // accept
        robot.keySend(KeyEvent.VK_TAB);
        robot.keySend(KeyEvent.VK_TAB);
        robot.keySend(KeyEvent.VK_SPACE); // next
        sleep(5_000);
        robot.keySend(KeyEvent.VK_TAB); // renew my VDB
        robot.keySend(KeyEvent.VK_TAB); // enroll new VDB
        robot.keySend(KeyEvent.VK_SPACE); //
        sleep(2_000);
        robot.type("eric.chen@hp.com");
        robot.keySend(KeyEvent.VK_TAB);
        robot.type("24208582");
        robot.keySend(KeyEvent.VK_TAB);
        robot.type("ezr.fit-32");
        robot.keySend(KeyEvent.VK_TAB);
        robot.type("M");
        robot.keySend(KeyEvent.VK_TAB);
        robot.keySend(KeyEvent.VK_TAB);
        robot.type("chener");
        robot.keySend(KeyEvent.VK_TAB);
        robot.keySend(KeyEvent.VK_SPACE); // next
    }

    private static void cmdInstallWinPVT_vcpkg(String dir, String system) {
        String[] depend = {
            "cpprestsdk",
            "jsoncpp",
            "curl[ssh]",
        };
        ln("");
        ln("");
        ln("");
        ln("date /t && time /t");
        ln("mkdir %s", dir);
        ln("cd %s", dir);
        ln("git clone https://github.com/microsoft/vcpkg %s", dir);
        ln(".\\vcpkg\\bootstrap-vcpkg.bat");
        for (int i = 0; i < depend.length; i++) {
            // exec each cmd
            ln("date /t && time /t");
            ln(".\\vcpkg\\vcpkg install %s:%s", depend[i], system);
            ln("date /t && time /t");
        }
        ln("");
        ln("");
        ln("");
    }
}
