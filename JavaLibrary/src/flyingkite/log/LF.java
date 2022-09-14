package flyingkite.log;

import java.io.File;

/**
 * The class for logging message to console log and file
 * L = console log of {@link L}
 * F = File output of {@link FileOutput}
 *
 * @see System#out
 * @see FileOutput
 */
public class LF implements Loggable {
    private final FileOutput file;

    private boolean logToFile = true;

    private boolean logToL = true;

    public LF(String it) {
        this(new File(it));
    }

    public LF(String folder, String name) {
        this(new File(folder, name));
    }

    public LF(File folder, String name) {
        this(new File(folder, name));
    }

    public LF(File f) {
        if (f.isDirectory()) {
            f = new File(f, "log.txt");
        }
        file = new FileOutput(f);
    }

    public LF(FileOutput fo) {
        file = fo;
    }

    public FileOutput getFile() {
        return file;
    }

    /**
     * True if following callings of {@link #log(String)}, {@link #log(String, Object...)}
     * will also output to file<br/>
     * False if no need to output to file
     */
    public void setLogToFile(boolean toFile) {
        logToFile = toFile;
    }

    public void setLogToL(boolean toL) {
        logToL = toL;
    }

    private static final L.Impl impl = L.getImpl();

    @Override
    public void log(String msg) {
        if (logToL) {
            impl.log(msg);
        }
        if (logToFile) {
            file.writeln(msg);
        }
    }

    @Override
    public void log(String msg, Object... param) {
        if (logToL) {
            impl.log(msg, param);
        }
        if (logToFile) {
            file.writeln(msg, param);
        }
    }
}
