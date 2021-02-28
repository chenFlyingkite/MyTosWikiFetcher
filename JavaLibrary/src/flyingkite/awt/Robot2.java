package flyingkite.awt;

import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.Robot;
import java.awt.event.KeyEvent;

public class Robot2 extends Robot {

    public Robot2() throws AWTException {
        super();
    }

    public Robot2(GraphicsDevice screen) throws AWTException {
        super(screen);
    }

    /**
     * Clicks one or more mouse buttons.
     * calls {@link #mousePress(int)} and {@link #mouseRelease(int)}
     */
    public synchronized void mouseClick(int buttons) {
        mousePress(buttons);
        mouseRelease(buttons);
    }

    /**
     * Clicks a given key.
     * calls {@link #keyPress(int)} and {@link #keyRelease(int)}
     */
    public synchronized void keyClick(int keycode) {
        keyPress(keycode);
        keyRelease(keycode);
    }

    /**
     * Types a string
     * by calls {@link #keyClick(int)} on each characters
     */
    public synchronized void type(CharSequence cs) {
        if (cs == null) return;

        for (int i = 0; i < cs.length(); i++) {
            int c = cs.charAt(i);
            keyClick(c);
        }
    }

    /**
     * Types a string, and click enter
     * by calls {@link #keyClick(int)} on each characters and {@link java.awt.event.KeyEvent#VK_ENTER}
     */
    public synchronized void enter(CharSequence cs) {
        if (cs == null) return;

        type(cs);
        keyClick(KeyEvent.VK_ENTER);
    }

}
