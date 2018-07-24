package flyingkite.tool;

import flyingkite.logging.L;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;

public class TicTac2 {
    private final Stack<Long> tictac = new Stack<>();

    protected boolean log = true;
    protected boolean enable = true;

    public void tic() {
        if (!enable) return;
        tictac.push(System.currentTimeMillis());
    }

    public void tac(String format, Object... params) {
        tac(String.format(format, params));
    }

    public void tac(String msg) {
        if (!enable) return;

        long tac = System.currentTimeMillis();
        if (tictac.empty()) {
            logError(tac, msg);
            return;
        }
        long tic = tictac.pop();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tictac.size(); i++) {
            sb.append(" ");
        }
        sb.append("[").append(tac - tic).append("] : ").append(msg);
        logTac(sb.toString());
    }

    public void enable(boolean enabled) {
        enable = enabled;
    }

    public void reset() {
        tictac.clear();
    }

    protected void logError(long tac, String msg) {
        L.log("X_X Omitted. tic = N/A, tac = %s : %s", getTime(tac), msg);
    }

    protected void logTac(String msg) {
        if (log) {
            L.log(msg);
        }
    }

    protected String getTime(long time) {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date(time));
    }

    public void setLog(boolean writeLog) {
        log = writeLog;
    }
}
