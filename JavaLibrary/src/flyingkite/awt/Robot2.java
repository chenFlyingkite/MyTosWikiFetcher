package flyingkite.awt;

import flyingkite.log.L;
import flyingkite.tool.ThreadUtil;

import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
     * by calls {@link #keyClick(int)} on each character
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
     * by calls {@link #keyClick(int)} on each character and {@link java.awt.event.KeyEvent#VK_ENTER}
     */
    public synchronized void enter(CharSequence cs) {
        if (cs == null) return;

        type(cs);
        keyClick(KeyEvent.VK_ENTER);
    }

    /**
     * Send the keycode of ctrl + v
     */
    public synchronized void paste() {
        int[] ks = {KeyEvent.VK_CONTROL, KeyEvent.VK_V};
        keyPressRelease(ks);
    }

    /**
     * Press all and release all the keycodes
     */
    public synchronized void keyPressRelease(int[] a) {
        if (a == null) return;

        int n = a.length;
        for (int i = 0; i < n; i++) {
            keyPress(a[i]);
        }
        for (int i = n - 1; i >= 0; i--) {
            keyRelease(a[i]);
        }
    }

    private static final Map<Character, int[]> keys = new TreeMap<>();

    public void print() {
        ThreadUtil.sleep(2000);
        List<Character> chars = new ArrayList<>(keys.keySet());
        for (int i = 0; i < chars.size(); i++) {
            char k = chars.get(i);
            int[] vs = keys.get(k);
            ThreadUtil.sleep(200);
            L.log("#%s : |%s| => %s", i, k, Arrays.toString(vs));
            if (vs.length > 0) {
                int v = vs[0];
                L.log("click %s", k);
                try {
                    keyClick(v);
                } catch (IllegalArgumentException iae) {
                    iae.printStackTrace();
                }
//                L.log("click shift + %s", k);
//                keyClick(KeyEvent.VK_SPACE);
//                keyPressRelease(new int[]{KeyEvent.VK_SHIFT, v});
//                keyClick(KeyEvent.VK_ENTER);
            }
        }
    }
    // x java.awt.Robot.keyPress java.lang.IllegalArgumentException: Invalid key code
    static {
        keys.put(' ', new int[]{KeyEvent.VK_SPACE});
        keys.put('!', new int[]{KeyEvent.VK_EXCLAMATION_MARK}); // x
        keys.put('"', new int[]{KeyEvent.VK_QUOTEDBL}); // x
        keys.put('#', new int[]{KeyEvent.VK_NUMBER_SIGN}); // x
        keys.put('$', new int[]{KeyEvent.VK_DOLLAR}); // x
        keys.put('%', new int[]{});
        keys.put('&', new int[]{KeyEvent.VK_AMPERSAND}); // x
        keys.put('\'', new int[]{KeyEvent.VK_QUOTE}); // ' ok
        keys.put('(', new int[]{KeyEvent.VK_LEFT_PARENTHESIS}); // x
        keys.put(')', new int[]{KeyEvent.VK_RIGHT_PARENTHESIS}); // x
        keys.put('*', new int[]{KeyEvent.VK_ASTERISK}); // x
        keys.put('+', new int[]{KeyEvent.VK_PLUS}); // x
        keys.put(',', new int[]{KeyEvent.VK_COMMA}); // ok
        keys.put('-', new int[]{KeyEvent.VK_MINUS}); // ok
        keys.put('.', new int[]{KeyEvent.VK_PERIOD}); // ok
        keys.put('/', new int[]{KeyEvent.VK_SLASH}); // ok
        keys.put('0', new int[]{KeyEvent.VK_0}); // ok
        keys.put('1', new int[]{KeyEvent.VK_1}); // ok
        keys.put('2', new int[]{KeyEvent.VK_2}); // ok
        keys.put('3', new int[]{KeyEvent.VK_3}); // ok
        keys.put('4', new int[]{KeyEvent.VK_4}); // ok
        keys.put('5', new int[]{KeyEvent.VK_5}); // ok
        keys.put('6', new int[]{KeyEvent.VK_6}); // ok
        keys.put('7', new int[]{KeyEvent.VK_7}); // ok
        keys.put('8', new int[]{KeyEvent.VK_8}); // ok
        keys.put('9', new int[]{KeyEvent.VK_9}); // ok
        keys.put(':', new int[]{KeyEvent.VK_COLON}); // x
        keys.put(';', new int[]{KeyEvent.VK_SEMICOLON}); // ok
        keys.put('<', new int[]{KeyEvent.VK_LESS}); // x
        keys.put('=', new int[]{KeyEvent.VK_EQUALS}); // ok
        keys.put('>', new int[]{KeyEvent.VK_GREATER}); // x
        keys.put('?', new int[]{});
        keys.put('@', new int[]{KeyEvent.VK_AT}); // x
        keys.put('A', new int[]{KeyEvent.VK_A}); // lower case
        keys.put('B', new int[]{KeyEvent.VK_B});
        keys.put('C', new int[]{KeyEvent.VK_C});
        keys.put('D', new int[]{KeyEvent.VK_D});
        keys.put('E', new int[]{KeyEvent.VK_E});
        keys.put('F', new int[]{KeyEvent.VK_F});
        keys.put('G', new int[]{KeyEvent.VK_G});
        keys.put('H', new int[]{KeyEvent.VK_H});
        keys.put('I', new int[]{KeyEvent.VK_I});
        keys.put('J', new int[]{KeyEvent.VK_J});
        keys.put('K', new int[]{KeyEvent.VK_K});
        keys.put('L', new int[]{KeyEvent.VK_L});
        keys.put('M', new int[]{KeyEvent.VK_M});
        keys.put('N', new int[]{KeyEvent.VK_N});
        keys.put('O', new int[]{KeyEvent.VK_O});
        keys.put('P', new int[]{KeyEvent.VK_P});
        keys.put('Q', new int[]{KeyEvent.VK_Q});
        keys.put('R', new int[]{KeyEvent.VK_R});
        keys.put('S', new int[]{KeyEvent.VK_S});
        keys.put('T', new int[]{KeyEvent.VK_T});
        keys.put('U', new int[]{KeyEvent.VK_U});
        keys.put('V', new int[]{KeyEvent.VK_V});
        keys.put('W', new int[]{KeyEvent.VK_W});
        keys.put('X', new int[]{KeyEvent.VK_X});
        keys.put('Y', new int[]{KeyEvent.VK_Y});
        keys.put('Z', new int[]{KeyEvent.VK_Z});
        keys.put('[', new int[]{KeyEvent.VK_OPEN_BRACKET}); // ok
        keys.put('\\', new int[]{KeyEvent.VK_BACK_SLASH}); // ok
        keys.put(']', new int[]{KeyEvent.VK_CLOSE_BRACKET}); // ok
        keys.put('^', new int[]{KeyEvent.VK_CIRCUMFLEX}); // x
        keys.put('_', new int[]{KeyEvent.VK_UNDERSCORE}); // x
        keys.put('`', new int[]{KeyEvent.VK_BACK_QUOTE}); // ` // ok
        keys.put('a', new int[]{});
        keys.put('b', new int[]{});
        keys.put('c', new int[]{});
        keys.put('d', new int[]{});
        keys.put('e', new int[]{});
        keys.put('f', new int[]{});
        keys.put('g', new int[]{});
        keys.put('h', new int[]{});
        keys.put('i', new int[]{});
        keys.put('j', new int[]{});
        keys.put('k', new int[]{});
        keys.put('l', new int[]{});
        keys.put('m', new int[]{});
        keys.put('n', new int[]{});
        keys.put('o', new int[]{});
        keys.put('p', new int[]{});
        keys.put('q', new int[]{});
        keys.put('r', new int[]{});
        keys.put('s', new int[]{});
        keys.put('t', new int[]{});
        keys.put('u', new int[]{});
        keys.put('v', new int[]{});
        keys.put('w', new int[]{});
        keys.put('x', new int[]{});
        keys.put('y', new int[]{});
        keys.put('z', new int[]{});
        keys.put('{', new int[]{KeyEvent.VK_BRACELEFT}); // x
        keys.put('|', new int[]{});
        keys.put('}', new int[]{KeyEvent.VK_BRACERIGHT}); // x
        keys.put('~', new int[]{});
    }

}
