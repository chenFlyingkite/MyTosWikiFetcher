package flyingkite.awt;

import flyingkite.log.L;
import flyingkite.log.LF;
import flyingkite.math.MathUtil;
import flyingkite.tool.ThreadUtil;

import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Robot2 extends Robot {

    public Robot2() throws AWTException {
        super();
        init();
    }

    public Robot2(GraphicsDevice screen) throws AWTException {
        super(screen);
        init();
    }

    private void init() {
        //setAutoDelay(10);
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
     * Clicks keycode by performing
     * {@link #keyPress(int)} and {@link #keyRelease(int)}
     */
    public synchronized void keyClick(char keycode) {
        List<Integer> ks = getKeyCodes(keycode);
        try {
            keyPressRelease(ks);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            L.log("keyClick failed for '%s'", keycode);
        }
    }

    public synchronized void keySend(int keycode) {
        try {
            keyPress(keycode);
            keyRelease(keycode);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private final String[] clickState = {"ok", "release", "press",};

    /**
     * Clicks a given key.
     * calls {@link #keyPress(int)} and {@link #keyRelease(int)}
     */
    private synchronized int keyClickOld(int keycode) {
        int step = 2;
        try {
            keyPress(keycode);
            step--;
            keyRelease(keycode);
            step--;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            L.log("keyClick failed at step[%s]: %s on code %s", step, clickState[step], keycode);
        }
        return step;
    }

    /**
     * @see #type(CharSequence)
     */
    private synchronized void typeOld(CharSequence cs) {
        if (cs == null) return;

        for (int i = 0; i < cs.length(); i++) {
            int c = cs.charAt(i);
            keyClickOld(c);
        }
    }

    /**
     * Types a string
     * by calls {@link #keyClickOld(int)} on each character, supports char = 32" " ~ 126"~"
     */
    public synchronized void type(CharSequence cs) {
        if (cs == null) return;

        for (int i = 0; i < cs.length(); i++) {
            char c = cs.charAt(i);
            int v = c + sh;
            boolean in = MathUtil.isInRange(v, 0, charKeys.length);
            if (in) {
                keyClick(c);
            } else {
                keyClickOld(c);
            }
        }
    }

    /**
     * @see #enter(CharSequence)
     */
    private synchronized void enterOld(CharSequence cs) {
        if (cs == null) return;

        typeOld(cs);
        keyClickOld(KeyEvent.VK_ENTER);
    }

    /**
     * Types a string, and click enter
     * by calls {@link #keyClickOld(int)} on each character and {@link java.awt.event.KeyEvent#VK_ENTER}
     */
    public synchronized void enter(CharSequence cs) {
        if (cs == null) return;

        type(cs);
        keyClickOld(KeyEvent.VK_ENTER);
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

    /**
     * Press all in array order, and release all keycodes in reverse order
     */
    public synchronized void keyPressRelease(List<Integer> a) {
        if (a == null) return;

        int n = a.size();
        for (int i = 0; i < n; i++) {
            keyPress(a.get(i));
        }
        for (int i = n - 1; i >= 0; i--) {
            keyRelease(a.get(i));
        }
    }

    // only public when we want to validate
    private void print() {
        ThreadUtil.sleep(2000*0);
        List<Character> chars = new ArrayList<>(charToVKCode.keySet());
        List<Character> pass = new ArrayList<>();
        List<Character> fail = new ArrayList<>();
        List<Character> empty = new ArrayList<>();
        List<Character> shift = new ArrayList<>();
        List<Character> noshift = new ArrayList<>();
        char[] chs = new char[chars.size()];
        for (int i = 0; i < chars.size(); i++) {
            char k = chars.get(i);
            chs[i] = k;
            int[] vs = charToVKCode.get(k);
            ThreadUtil.sleep(200);
            L.log("#%s : |%s| => %s", i, k, Arrays.toString(vs));
            if (vs.length == 0) {
                empty.add(k);
            } else {
                int v = vs[0];
                L.log("click %s", k);
                try {
                    keyClickOld(v);
                    pass.add(k);
                } catch (IllegalArgumentException iae) {
                    iae.printStackTrace();
                    fail.add(k);
                }
            }
        }
        keyClickOld(KeyEvent.VK_ENTER);
        for (int i = 0; i < chars.size(); i++) {
            char k = chars.get(i);
            ThreadUtil.sleep(200);
            int[] vs = charToVKCode.get(k);
            int[] vsshift = new int[vs.length + 1];
            vsshift[0] = KeyEvent.VK_SHIFT;
            for (int j = 0; j < vs.length; j++) {
                vsshift[j+1] = vs[j];
            }
            L.log("click shift + %s", k);
            try {
                keyPressRelease(vsshift);
                shift.add(k);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                noshift.add(k);
            }
        }
        String sc = new String(chs);
        L.log("chars = %s, /%s/", chs.length, sc);
        L.log("pass  = %s, /%s/", pass.size(), pass);
        L.log("fail  = %s, /%s/", fail.size(), fail);
        L.log("empty = %s, /%s/", empty.size(), empty);
        L.log("shift = %s, /%s/", shift.size(), shift);
        L.log("noshift = %s, /%s/", noshift.size(), noshift);
    }

    // print code for getKeyCodes()
    private static void printCode() {
        LF lf = new LF("robot");
        lf.getFile().open(false);
        lf.log("if (false) {");
        for (char c : charToVKCode.keySet()) {
            int v = c + sh;
            char shiftC = shiftCharSetItems2[v];
            boolean needShift = charSetItems2[v] == 0 && shiftC != 0;
            boolean isaz = MathUtil.isInRangeInclusive(c, 'a', 'z');
            boolean isAZ = MathUtil.isInRangeInclusive(c, 'A', 'Z');
            boolean isLetter = isaz || isAZ;
            if (needShift && isLetter && isLocked(KeyEvent.VK_CAPS_LOCK)) {
                needShift = false;
            }
            String s;
            // add escape char
            if (c == '\'' || c == '\\') {
                s = "\\" + c;
            } else {
                s = "" + c;
            }
            lf.log("} else if (c == '%s') {", s);
            if (isLetter) {
                if (isAZ) {
                    lf.log("    if (caps) {");
                    lf.log("    } else {");
                    lf.log("        ans.add(shift);");
                    lf.log("    }");
                } else {
                    lf.log("    if (caps) {");
                    lf.log("        ans.add(shift);");
                    lf.log("    } else {");
                    lf.log("    }");
                }
            } else {
                if (needShift) {
                    lf.log("    ans.add(shift);");
                }
            }
            String m = charToVKName.get(c);
            if (needShift) {
                m = charToVKName.get(shiftC);
            }
            if (m == null) {
                m = "";
            }
            lf.log("    ans.add(KeyEvent.VK_%s);", m);
            if (shiftC != 0) {
                lf.log("    // shift + %s", shiftC);
            }
        }
        lf.log("}");
        lf.getFile().close();
    }
//  ""<<-_.>/?0)1!2@3#4$5%6^7&8*9(::++AAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ[{\|]}~~
//    chars = 95, / !"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_`abcdefghijklmnopqrstuvwxyz{|}~/
//    pass  = 65, /[ , !, ", #, $, &, ', (, ), *, +, ,, -, ., /, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, :, ;, <, =, >, @, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z, [, \, ], ^, _, `, {, }]/
//    fail  = 0, /[]/
//    empty = 30, /[%, ?, a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z, |, ~]/
//    shift = 48, /[ , ', ,, -, ., /, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, ;, =, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z, [, \, ], `]/
//    noshift = 17, /[!, ", #, $, &, (, ), *, +, :, <, >, @, ^, _, {, }]/
//    Done, Tue May 03 18:44:42 CST 2022
/*
 ',-./0123456789;=abcdefghijklmnopqrstuvwxyz[\]`
 "<_>?)!@#$%^&*(:+ABCDEFGHIJKLMNOPQRSTUVWXYZ{|}~

 ',-./0123456789;=abcdefghijklmnopqrstuvwxyz[\]`
 "<_>?)!@#$%^&*(:+ABCDEFGHIJKLMNOPQRSTUVWXYZ{|}~
*/
//    charSetItems       = [ , ', ,, -, ., /, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, ;, =, a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z, [, \, ], `]
//    shiftCharSetItems  = [ , ", <, _, >, ?, ), !, @, #, $, %, ^, &, *, (, :, +, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z, {, |, }, ~]
//    charKeys           = [ , !, ", #, $, %, &, ', (, ), *, +, ,, -, ., /, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, :, ;, <, =, >, ?, @, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ]
//    charSetItems2      = [ ,  ,  ,  ,  ,  ,  , ",  ,  ,  ,  , <, _, >, ?, ), !, @, #, $, %, ^, &, *, (,  , :,  , +,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  , {, |, },  ,  , ~, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z,  ,  ,  ,  ,  ]
//    shiftCharSetItems2 = [ , 1, ', 3, 4, 5, 7,  , 9, 0, 8, =,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  , ;,  , ,,  , ., /, 2, a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z,  ,  ,  , 6, -,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  , [, \, ], `,  ]


    private static final Map<Character, int[]> charToVKCode = new TreeMap<>();
    private static final Map<Character, String> charToVKName = new HashMap<>();
    private static final Toolkit toolkit = Toolkit.getDefaultToolkit();

    public static final int LOCK_NUM    = KeyEvent.VK_NUM_LOCK;
    public static final int LOCK_CAPS   = KeyEvent.VK_CAPS_LOCK;
    public static final int LOCK_SCROLL = KeyEvent.VK_SCROLL_LOCK;

    public static boolean isLocked(int key) {
        return toolkit.getLockingKeyState(key);
    }

    public static void setLocked(int key, boolean on) {
        toolkit.setLockingKeyState(key, on);
    }

    public static final String charSet       = " ',-./0123456789;=abcdefghijklmnopqrstuvwxyz[\\]`";
    public static final String shiftCharSet = " \"<_>?)!@#$%^&*(:+ABCDEFGHIJKLMNOPQRSTUVWXYZ{|}~";

    // generated
    private static final char[] charSetItems;
    private static final char[] shiftCharSetItems;
    // charSet -> shiftCharSet
    private static final Map<Character, Character> addShift = new HashMap<>();
    // shiftCharSet -> charSet
    private static final Map<Character, Character> unShift = new HashMap<>();

    // charSetItems2[c] = char of shift+c or 0
    private static final int sh = -32;
    private static final char[] charSetItems2 = new char[128+sh];
    private static final char[] shiftCharSetItems2 = new char[128+sh];
    private static final char[] charKeys = new char[128+sh];

    // x java.awt.Robot.keyPress java.lang.IllegalArgumentException: Invalid key code
    static {
        charSetItems = charSet.toCharArray();
        shiftCharSetItems = shiftCharSet.toCharArray();
        makeVKCode();
        makeVKName();
        makeCharSet();
        makeCharSet2();
        L.log("charSetItems       = %s", Arrays.toString(charSetItems));
        L.log("shiftCharSetItems  = %s", Arrays.toString(shiftCharSetItems));
        //--
        L.log("charKeys           = %s", Arrays.toString(charKeys));
        L.log("charSetItems2      = %s", Arrays.toString(charSetItems2));
        L.log("shiftCharSetItems2 = %s", Arrays.toString(shiftCharSetItems2));
        L.log("addShift = %s", addShift);
        L.log("unShift = %s", unShift);
    }

    // Does not work...
    @Deprecated
    private static final Set<Integer> pressedKeys = new HashSet<>();
    @Deprecated
    private static void onKeyboardListener(boolean add) {
        L.log("onKeyboardListener(%s)", add);
        final KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        if (add) {
            kfm.addKeyEventDispatcher(listener);
        } else {
            kfm.removeKeyEventDispatcher(listener);
        }
    }

    private static final KeyEventDispatcher listener = (e) -> {
        int id = e.getID();
        int code = e.getKeyCode();
        L.log("KeyEventDispatcher e = %s", e);
        if (id == KeyEvent.KEY_PRESSED) {
            pressedKeys.add(code);
        } else if (id == KeyEvent.KEY_RELEASED) {
            pressedKeys.remove(code);
        }

        return false;
    };

    private static void makeCharSet2() {
        char[] src = charSetItems;
        char[] dst = shiftCharSetItems;

        if (src != null && dst != null && src.length == dst.length) {
            for (int i = 0; i < src.length; i++) {
                char x = src[i];
                char y = dst[i];
                charKeys[i] = (char) (i-sh);
                charSetItems2[x+sh] = y;
                shiftCharSetItems2[y+sh] = x;
            }
        }
    }

    // pass = no Error
    private static void validateCharSet() {
        char[] src = charSetItems;
        char[] dst = shiftCharSetItems;
        for (int i = 0; i < src.length; i++) {
            char x = src[i];
            char y = dst[i];
            int xy = charSetItems2[sh + x] + sh;
            int yx = shiftCharSetItems2[sh + y] + sh;
            if (xy >= 0) {
                char xyx = charSetItems2[xy];
                L.log("x, xyx = %s, %s", x, xyx);
                if (x != xyx && xyx != 0) {
                    L.log("Error x, xyx = %s, %s", x, xyx);
                }
            }
            if (yx >= 0) {
                char yxy = shiftCharSetItems2[yx];
                L.log("y, yxy = %s, %s", y, yxy);
                if (y != yxy && yxy != 0) {
                    L.log("Error y, yxy = %s, %s", y, yxy);
                }
            }
        }
    }

    private static void makeCharSet() {
        char[] src = charSetItems;
        char[] dst = shiftCharSetItems;

        if (src != null && dst != null && src.length == dst.length) {
            for (int i = 0; i < src.length; i++) {
                char x = src[i];
                char y = dst[i];
                addShift.put(x, y);
                unShift.put(y, x);
            }
        }
    }

    private static void makeVKCode() {
        charToVKCode.put(' ', new int[]{KeyEvent.VK_SPACE});
        charToVKCode.put('!', new int[]{KeyEvent.VK_EXCLAMATION_MARK}); // x
        charToVKCode.put('"', new int[]{KeyEvent.VK_QUOTEDBL}); // x
        charToVKCode.put('#', new int[]{KeyEvent.VK_NUMBER_SIGN}); // x
        charToVKCode.put('$', new int[]{KeyEvent.VK_DOLLAR}); // x
        charToVKCode.put('%', new int[]{});
        charToVKCode.put('&', new int[]{KeyEvent.VK_AMPERSAND}); // x
        charToVKCode.put('\'', new int[]{KeyEvent.VK_QUOTE}); // ' ok
        charToVKCode.put('(', new int[]{KeyEvent.VK_LEFT_PARENTHESIS}); // x
        charToVKCode.put(')', new int[]{KeyEvent.VK_RIGHT_PARENTHESIS}); // x
        charToVKCode.put('*', new int[]{KeyEvent.VK_ASTERISK}); // x
        charToVKCode.put('+', new int[]{KeyEvent.VK_PLUS}); // x
        charToVKCode.put(',', new int[]{KeyEvent.VK_COMMA}); // ok
        charToVKCode.put('-', new int[]{KeyEvent.VK_MINUS}); // ok
        charToVKCode.put('.', new int[]{KeyEvent.VK_PERIOD}); // ok
        charToVKCode.put('/', new int[]{KeyEvent.VK_SLASH}); // ok
        charToVKCode.put('0', new int[]{KeyEvent.VK_0}); // ok
        charToVKCode.put('1', new int[]{KeyEvent.VK_1}); // ok
        charToVKCode.put('2', new int[]{KeyEvent.VK_2}); // ok
        charToVKCode.put('3', new int[]{KeyEvent.VK_3}); // ok
        charToVKCode.put('4', new int[]{KeyEvent.VK_4}); // ok
        charToVKCode.put('5', new int[]{KeyEvent.VK_5}); // ok
        charToVKCode.put('6', new int[]{KeyEvent.VK_6}); // ok
        charToVKCode.put('7', new int[]{KeyEvent.VK_7}); // ok
        charToVKCode.put('8', new int[]{KeyEvent.VK_8}); // ok
        charToVKCode.put('9', new int[]{KeyEvent.VK_9}); // ok
        charToVKCode.put(':', new int[]{KeyEvent.VK_COLON}); // x
        charToVKCode.put(';', new int[]{KeyEvent.VK_SEMICOLON}); // ok
        charToVKCode.put('<', new int[]{KeyEvent.VK_LESS}); // x
        charToVKCode.put('=', new int[]{KeyEvent.VK_EQUALS}); // ok
        charToVKCode.put('>', new int[]{KeyEvent.VK_GREATER}); // x
        charToVKCode.put('?', new int[]{});
        charToVKCode.put('@', new int[]{KeyEvent.VK_AT}); // x
        charToVKCode.put('A', new int[]{KeyEvent.VK_A});
        charToVKCode.put('B', new int[]{KeyEvent.VK_B});
        charToVKCode.put('C', new int[]{KeyEvent.VK_C});
        charToVKCode.put('D', new int[]{KeyEvent.VK_D});
        charToVKCode.put('E', new int[]{KeyEvent.VK_E});
        charToVKCode.put('F', new int[]{KeyEvent.VK_F});
        charToVKCode.put('G', new int[]{KeyEvent.VK_G});
        charToVKCode.put('H', new int[]{KeyEvent.VK_H});
        charToVKCode.put('I', new int[]{KeyEvent.VK_I});
        charToVKCode.put('J', new int[]{KeyEvent.VK_J});
        charToVKCode.put('K', new int[]{KeyEvent.VK_K});
        charToVKCode.put('L', new int[]{KeyEvent.VK_L});
        charToVKCode.put('M', new int[]{KeyEvent.VK_M});
        charToVKCode.put('N', new int[]{KeyEvent.VK_N});
        charToVKCode.put('O', new int[]{KeyEvent.VK_O});
        charToVKCode.put('P', new int[]{KeyEvent.VK_P});
        charToVKCode.put('Q', new int[]{KeyEvent.VK_Q});
        charToVKCode.put('R', new int[]{KeyEvent.VK_R});
        charToVKCode.put('S', new int[]{KeyEvent.VK_S});
        charToVKCode.put('T', new int[]{KeyEvent.VK_T});
        charToVKCode.put('U', new int[]{KeyEvent.VK_U});
        charToVKCode.put('V', new int[]{KeyEvent.VK_V});
        charToVKCode.put('W', new int[]{KeyEvent.VK_W});
        charToVKCode.put('X', new int[]{KeyEvent.VK_X});
        charToVKCode.put('Y', new int[]{KeyEvent.VK_Y});
        charToVKCode.put('Z', new int[]{KeyEvent.VK_Z});
        charToVKCode.put('[', new int[]{KeyEvent.VK_OPEN_BRACKET}); // ok
        charToVKCode.put('\\', new int[]{KeyEvent.VK_BACK_SLASH}); // ok
        charToVKCode.put(']', new int[]{KeyEvent.VK_CLOSE_BRACKET}); // ok
        charToVKCode.put('^', new int[]{KeyEvent.VK_CIRCUMFLEX}); // x
        charToVKCode.put('_', new int[]{KeyEvent.VK_UNDERSCORE}); // x
        charToVKCode.put('`', new int[]{KeyEvent.VK_BACK_QUOTE}); // ` // ok
        charToVKCode.put('a', new int[]{KeyEvent.VK_A});
        charToVKCode.put('b', new int[]{KeyEvent.VK_B});
        charToVKCode.put('c', new int[]{KeyEvent.VK_C});
        charToVKCode.put('d', new int[]{KeyEvent.VK_D});
        charToVKCode.put('e', new int[]{KeyEvent.VK_E});
        charToVKCode.put('f', new int[]{KeyEvent.VK_F});
        charToVKCode.put('g', new int[]{KeyEvent.VK_G});
        charToVKCode.put('h', new int[]{KeyEvent.VK_H});
        charToVKCode.put('i', new int[]{KeyEvent.VK_I});
        charToVKCode.put('j', new int[]{KeyEvent.VK_J});
        charToVKCode.put('k', new int[]{KeyEvent.VK_K});
        charToVKCode.put('l', new int[]{KeyEvent.VK_L});
        charToVKCode.put('m', new int[]{KeyEvent.VK_M});
        charToVKCode.put('n', new int[]{KeyEvent.VK_N});
        charToVKCode.put('o', new int[]{KeyEvent.VK_O});
        charToVKCode.put('p', new int[]{KeyEvent.VK_P});
        charToVKCode.put('q', new int[]{KeyEvent.VK_Q});
        charToVKCode.put('r', new int[]{KeyEvent.VK_R});
        charToVKCode.put('s', new int[]{KeyEvent.VK_S});
        charToVKCode.put('t', new int[]{KeyEvent.VK_T});
        charToVKCode.put('u', new int[]{KeyEvent.VK_U});
        charToVKCode.put('v', new int[]{KeyEvent.VK_V});
        charToVKCode.put('w', new int[]{KeyEvent.VK_W});
        charToVKCode.put('x', new int[]{KeyEvent.VK_X});
        charToVKCode.put('y', new int[]{KeyEvent.VK_Y});
        charToVKCode.put('z', new int[]{KeyEvent.VK_Z});
        charToVKCode.put('{', new int[]{KeyEvent.VK_BRACELEFT}); // x
        charToVKCode.put('|', new int[]{});
        charToVKCode.put('}', new int[]{KeyEvent.VK_BRACERIGHT}); // x
        charToVKCode.put('~', new int[]{});
    }

    private static void makeVKName() {
        charToVKName.put(' ', "SPACE");
        charToVKName.put('\'', "QUOTE");
        charToVKName.put('=', "EQUALS");
        charToVKName.put(',', "COMMA");
        charToVKName.put('-', "MINUS");
        charToVKName.put('.', "PERIOD");
        charToVKName.put('/', "SLASH");
        charToVKName.put(';', "SEMICOLON");
        charToVKName.put('[', "OPEN_BRACKET");
        charToVKName.put('\\', "BACK_SLASH");
        charToVKName.put(']', "CLOSE_BRACKET");
        charToVKName.put('`', "BACK_QUOTE");
        for (int i = 0; i < 10; i++) {
            char k = (char) ('0' + i);
            String v = "" + k;
            charToVKName.put(k, v);
        }
        for (int i = 0; i < 26; i++) {
            char[] k = {'a', 'A'};
            String v;

            k[0] += i;
            k[1] += i;
            v = "" + k[1]; // upper case name
            charToVKName.put(k[0], v);
            charToVKName.put(k[1], v);
        }
    }

    // Directly return each character's keycodes, also consider CapsLock
    private static List<Integer> getKeyCodes(char c) {
        List<Integer> ans = new ArrayList<>();
        boolean num = isLocked(KeyEvent.VK_NUM_LOCK);
        boolean caps = isLocked(KeyEvent.VK_CAPS_LOCK);
        boolean scroll = isLocked(KeyEvent.VK_SCROLL_LOCK);
        //L.log("num, caps, scroll = %s, %s, %s", num, caps, scroll);
        final int shift = KeyEvent.VK_SHIFT;
        if (false) {
        } else if (c == ' ') {
            ans.add(KeyEvent.VK_SPACE);
            // shift +
        } else if (c == '!') {
            ans.add(shift);
            ans.add(KeyEvent.VK_1);
            // shift + 1
        } else if (c == '"') {
            ans.add(shift);
            ans.add(KeyEvent.VK_QUOTE);
            // shift + '
        } else if (c == '#') {
            ans.add(shift);
            ans.add(KeyEvent.VK_3);
            // shift + 3
        } else if (c == '$') {
            ans.add(shift);
            ans.add(KeyEvent.VK_4);
            // shift + 4
        } else if (c == '%') {
            ans.add(shift);
            ans.add(KeyEvent.VK_5);
            // shift + 5
        } else if (c == '&') {
            ans.add(shift);
            ans.add(KeyEvent.VK_7);
            // shift + 7
        } else if (c == '\'') {
            ans.add(KeyEvent.VK_QUOTE);
        } else if (c == '(') {
            ans.add(shift);
            ans.add(KeyEvent.VK_9);
            // shift + 9
        } else if (c == ')') {
            ans.add(shift);
            ans.add(KeyEvent.VK_0);
            // shift + 0
        } else if (c == '*') {
            ans.add(shift);
            ans.add(KeyEvent.VK_8);
            // shift + 8
        } else if (c == '+') {
            ans.add(shift);
            ans.add(KeyEvent.VK_EQUALS);
            // shift + =
        } else if (c == ',') {
            ans.add(KeyEvent.VK_COMMA);
        } else if (c == '-') {
            ans.add(KeyEvent.VK_MINUS);
        } else if (c == '.') {
            ans.add(KeyEvent.VK_PERIOD);
        } else if (c == '/') {
            ans.add(KeyEvent.VK_SLASH);
        } else if (c == '0') {
            ans.add(KeyEvent.VK_0);
        } else if (c == '1') {
            ans.add(KeyEvent.VK_1);
        } else if (c == '2') {
            ans.add(KeyEvent.VK_2);
        } else if (c == '3') {
            ans.add(KeyEvent.VK_3);
        } else if (c == '4') {
            ans.add(KeyEvent.VK_4);
        } else if (c == '5') {
            ans.add(KeyEvent.VK_5);
        } else if (c == '6') {
            ans.add(KeyEvent.VK_6);
        } else if (c == '7') {
            ans.add(KeyEvent.VK_7);
        } else if (c == '8') {
            ans.add(KeyEvent.VK_8);
        } else if (c == '9') {
            ans.add(KeyEvent.VK_9);
        } else if (c == ':') {
            ans.add(shift);
            ans.add(KeyEvent.VK_SEMICOLON);
            // shift + ;
        } else if (c == ';') {
            ans.add(KeyEvent.VK_SEMICOLON);
        } else if (c == '<') {
            ans.add(shift);
            ans.add(KeyEvent.VK_COMMA);
            // shift + ,
        } else if (c == '=') {
            ans.add(KeyEvent.VK_EQUALS);
        } else if (c == '>') {
            ans.add(shift);
            ans.add(KeyEvent.VK_PERIOD);
            // shift + .
        } else if (c == '?') {
            ans.add(shift);
            ans.add(KeyEvent.VK_SLASH);
            // shift + /
        } else if (c == '@') {
            ans.add(shift);
            ans.add(KeyEvent.VK_2);
            // shift + 2
        } else if (c == 'A') {
            if (caps) {
            } else {
                ans.add(shift);
            }
            ans.add(KeyEvent.VK_A);
            // shift + a
        } else if (c == 'B') {
            if (caps) {
            } else {
                ans.add(shift);
            }
            ans.add(KeyEvent.VK_B);
            // shift + b
        } else if (c == 'C') {
            if (caps) {
            } else {
                ans.add(shift);
            }
            ans.add(KeyEvent.VK_C);
            // shift + c
        } else if (c == 'D') {
            if (caps) {
            } else {
                ans.add(shift);
            }
            ans.add(KeyEvent.VK_D);
            // shift + d
        } else if (c == 'E') {
            if (caps) {
            } else {
                ans.add(shift);
            }
            ans.add(KeyEvent.VK_E);
            // shift + e
        } else if (c == 'F') {
            if (caps) {
            } else {
                ans.add(shift);
            }
            ans.add(KeyEvent.VK_F);
            // shift + f
        } else if (c == 'G') {
            if (caps) {
            } else {
                ans.add(shift);
            }
            ans.add(KeyEvent.VK_G);
            // shift + g
        } else if (c == 'H') {
            if (caps) {
            } else {
                ans.add(shift);
            }
            ans.add(KeyEvent.VK_H);
            // shift + h
        } else if (c == 'I') {
            if (caps) {
            } else {
                ans.add(shift);
            }
            ans.add(KeyEvent.VK_I);
            // shift + i
        } else if (c == 'J') {
            if (caps) {
            } else {
                ans.add(shift);
            }
            ans.add(KeyEvent.VK_J);
            // shift + j
        } else if (c == 'K') {
            if (caps) {
            } else {
                ans.add(shift);
            }
            ans.add(KeyEvent.VK_K);
            // shift + k
        } else if (c == 'L') {
            if (caps) {
            } else {
                ans.add(shift);
            }
            ans.add(KeyEvent.VK_L);
            // shift + l
        } else if (c == 'M') {
            if (caps) {
            } else {
                ans.add(shift);
            }
            ans.add(KeyEvent.VK_M);
            // shift + m
        } else if (c == 'N') {
            if (caps) {
            } else {
                ans.add(shift);
            }
            ans.add(KeyEvent.VK_N);
            // shift + n
        } else if (c == 'O') {
            if (caps) {
            } else {
                ans.add(shift);
            }
            ans.add(KeyEvent.VK_O);
            // shift + o
        } else if (c == 'P') {
            if (caps) {
            } else {
                ans.add(shift);
            }
            ans.add(KeyEvent.VK_P);
            // shift + p
        } else if (c == 'Q') {
            if (caps) {
            } else {
                ans.add(shift);
            }
            ans.add(KeyEvent.VK_Q);
            // shift + q
        } else if (c == 'R') {
            if (caps) {
            } else {
                ans.add(shift);
            }
            ans.add(KeyEvent.VK_R);
            // shift + r
        } else if (c == 'S') {
            if (caps) {
            } else {
                ans.add(shift);
            }
            ans.add(KeyEvent.VK_S);
            // shift + s
        } else if (c == 'T') {
            if (caps) {
            } else {
                ans.add(shift);
            }
            ans.add(KeyEvent.VK_T);
            // shift + t
        } else if (c == 'U') {
            if (caps) {
            } else {
                ans.add(shift);
            }
            ans.add(KeyEvent.VK_U);
            // shift + u
        } else if (c == 'V') {
            if (caps) {
            } else {
                ans.add(shift);
            }
            ans.add(KeyEvent.VK_V);
            // shift + v
        } else if (c == 'W') {
            if (caps) {
            } else {
                ans.add(shift);
            }
            ans.add(KeyEvent.VK_W);
            // shift + w
        } else if (c == 'X') {
            if (caps) {
            } else {
                ans.add(shift);
            }
            ans.add(KeyEvent.VK_X);
            // shift + x
        } else if (c == 'Y') {
            if (caps) {
            } else {
                ans.add(shift);
            }
            ans.add(KeyEvent.VK_Y);
            // shift + y
        } else if (c == 'Z') {
            if (caps) {
            } else {
                ans.add(shift);
            }
            ans.add(KeyEvent.VK_Z);
            // shift + z
        } else if (c == '[') {
            ans.add(KeyEvent.VK_OPEN_BRACKET);
        } else if (c == '\\') {
            ans.add(KeyEvent.VK_BACK_SLASH);
        } else if (c == ']') {
            ans.add(KeyEvent.VK_CLOSE_BRACKET);
        } else if (c == '^') {
            ans.add(shift);
            ans.add(KeyEvent.VK_6);
            // shift + 6
        } else if (c == '_') {
            ans.add(shift);
            ans.add(KeyEvent.VK_MINUS);
            // shift + -
        } else if (c == '`') {
            ans.add(KeyEvent.VK_BACK_QUOTE);
        } else if (c == 'a') {
            if (caps) {
                ans.add(shift);
            } else {
            }
            ans.add(KeyEvent.VK_A);
        } else if (c == 'b') {
            if (caps) {
                ans.add(shift);
            } else {
            }
            ans.add(KeyEvent.VK_B);
        } else if (c == 'c') {
            if (caps) {
                ans.add(shift);
            } else {
            }
            ans.add(KeyEvent.VK_C);
        } else if (c == 'd') {
            if (caps) {
                ans.add(shift);
            } else {
            }
            ans.add(KeyEvent.VK_D);
        } else if (c == 'e') {
            if (caps) {
                ans.add(shift);
            } else {
            }
            ans.add(KeyEvent.VK_E);
        } else if (c == 'f') {
            if (caps) {
                ans.add(shift);
            } else {
            }
            ans.add(KeyEvent.VK_F);
        } else if (c == 'g') {
            if (caps) {
                ans.add(shift);
            } else {
            }
            ans.add(KeyEvent.VK_G);
        } else if (c == 'h') {
            if (caps) {
                ans.add(shift);
            } else {
            }
            ans.add(KeyEvent.VK_H);
        } else if (c == 'i') {
            if (caps) {
                ans.add(shift);
            } else {
            }
            ans.add(KeyEvent.VK_I);
        } else if (c == 'j') {
            if (caps) {
                ans.add(shift);
            } else {
            }
            ans.add(KeyEvent.VK_J);
        } else if (c == 'k') {
            if (caps) {
                ans.add(shift);
            } else {
            }
            ans.add(KeyEvent.VK_K);
        } else if (c == 'l') {
            if (caps) {
                ans.add(shift);
            } else {
            }
            ans.add(KeyEvent.VK_L);
        } else if (c == 'm') {
            if (caps) {
                ans.add(shift);
            } else {
            }
            ans.add(KeyEvent.VK_M);
        } else if (c == 'n') {
            if (caps) {
                ans.add(shift);
            } else {
            }
            ans.add(KeyEvent.VK_N);
        } else if (c == 'o') {
            if (caps) {
                ans.add(shift);
            } else {
            }
            ans.add(KeyEvent.VK_O);
        } else if (c == 'p') {
            if (caps) {
                ans.add(shift);
            } else {
            }
            ans.add(KeyEvent.VK_P);
        } else if (c == 'q') {
            if (caps) {
                ans.add(shift);
            } else {
            }
            ans.add(KeyEvent.VK_Q);
        } else if (c == 'r') {
            if (caps) {
                ans.add(shift);
            } else {
            }
            ans.add(KeyEvent.VK_R);
        } else if (c == 's') {
            if (caps) {
                ans.add(shift);
            } else {
            }
            ans.add(KeyEvent.VK_S);
        } else if (c == 't') {
            if (caps) {
                ans.add(shift);
            } else {
            }
            ans.add(KeyEvent.VK_T);
        } else if (c == 'u') {
            if (caps) {
                ans.add(shift);
            } else {
            }
            ans.add(KeyEvent.VK_U);
        } else if (c == 'v') {
            if (caps) {
                ans.add(shift);
            } else {
            }
            ans.add(KeyEvent.VK_V);
        } else if (c == 'w') {
            if (caps) {
                ans.add(shift);
            } else {
            }
            ans.add(KeyEvent.VK_W);
        } else if (c == 'x') {
            if (caps) {
                ans.add(shift);
            } else {
            }
            ans.add(KeyEvent.VK_X);
        } else if (c == 'y') {
            if (caps) {
                ans.add(shift);
            } else {
            }
            ans.add(KeyEvent.VK_Y);
        } else if (c == 'z') {
            if (caps) {
                ans.add(shift);
            } else {
            }
            ans.add(KeyEvent.VK_Z);
        } else if (c == '{') {
            ans.add(shift);
            ans.add(KeyEvent.VK_OPEN_BRACKET);
            // shift + [
        } else if (c == '|') {
            ans.add(shift);
            ans.add(KeyEvent.VK_BACK_SLASH);
            // shift + \
        } else if (c == '}') {
            ans.add(shift);
            ans.add(KeyEvent.VK_CLOSE_BRACKET);
            // shift + ]
        } else if (c == '~') {
            ans.add(shift);
            ans.add(KeyEvent.VK_BACK_QUOTE);
            // shift + `
        }
        return ans;
    }
}
