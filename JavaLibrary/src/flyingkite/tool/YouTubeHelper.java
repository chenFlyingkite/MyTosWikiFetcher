package flyingkite.tool;

import flyingkite.awt.Robot2;

import java.awt.event.KeyEvent;

public class YouTubeHelper {
    private static final Robot2 robot;

    public static final int Audience_private = 0;
    public static final int Audience_protected = 1;
    public static final int Audience_public = 2;
    public static int[] audienceValues() {
        return new int[]{Audience_private, Audience_protected, Audience_public};
    }

    public static void publicYTVideo(int mode) {
        boolean child = false;

        ThreadUtil.sleep(3000);
        robot.mouseClickLeft();
        // feedback, close,
        // detail, element, check items, permission
        // use old detail, title info, title
        // description info, description,
        // Thumb info, upload thumb, upload info, thumb1, thumb2, thumb3
        // playlist info, select playlist, child only info, child only detail, yes/no child,
        for (int i = 0; i < 22; i++) {
            robot.keyClick(KeyEvent.VK_TAB);
        }
        // yes/no child,
        if (child) {
            robot.keyClick(KeyEvent.VK_SPACE);
        } else {
            robot.keyClick(KeyEvent.VK_DOWN);
        }
        ThreadUtil.sleep(100);
        // year limit, show more,
        // play, mute, time, video setting, full screen, video link, copy link,
        for (int i = 0; i < 9; i++) {
            robot.keyClick(KeyEvent.VK_TAB);
        }
        // Next
        robot.keyClick(KeyEvent.VK_TAB);
        robot.keyClick(KeyEvent.VK_ENTER);
        //
        ThreadUtil.sleep(100);
        robot.keyClick(KeyEvent.VK_ENTER);
        ThreadUtil.sleep(100);
        robot.keyClick(KeyEvent.VK_ENTER);
        ThreadUtil.sleep(1_000);
        robot.mouseClickLeft();
        for (int i = 0; i < 8; i++) {
            robot.keyClick(KeyEvent.VK_TAB);
        }
        // select the public
        robot.keyClick(KeyEvent.VK_SPACE);
        for (int i = 0; i < mode; i++) {
            robot.keyClick(KeyEvent.VK_DOWN);
        }
        ThreadUtil.sleep(100);
        for (int i = 0; i < 14; i++) {
            robot.keyClick(KeyEvent.VK_TAB);
        }
        robot.keyClick(KeyEvent.VK_ENTER);
        ThreadUtil.sleep(3_000);
        robot.keyClick(KeyEvent.VK_ENTER);
        ThreadUtil.sleep(5_000);
        robot.keyClick(KeyEvent.VK_ENTER);
    }

    static {
        robot = Robot2.create();
    }
}
