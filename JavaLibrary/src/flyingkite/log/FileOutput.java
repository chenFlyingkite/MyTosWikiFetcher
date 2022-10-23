package flyingkite.log;

import flyingkite.files.FileUtil;
import flyingkite.tool.IOUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class FileOutput implements Loggable {
    private PrintWriter pw;
    private File file;

    public FileOutput(String name) {
        this(new File(name));
    }

    public FileOutput(File f) {
        file = f;
        validate();
    }

    public File getFile() {
        return file;
    }

    private void validate() {
        FileUtil.deleteIfDirectory(file);
    }

    public FileOutput open() {
        return open(true);
    }

    public FileOutput open(boolean append) {
        try {
            FileUtil.createFile(file);
            close();
            pw = new PrintWriter(new FileOutputStream(file, append));
        } catch (IOException e) {
            e.printStackTrace();
            IOUtil.closeIt(pw);
        }
        return this;
    }

    @Override
    public void log(String msg) {
        writeln(msg);
    }

    public FileOutput writeln(String format, Object... param) {
        return writeln(_fmt(format, param));
    }

    public FileOutput writeln(String msg) {
        if (pw == null) {
            L.log("File not opened : %s", file);
        } else {
            pw.append(msg).append("\r\n");
        }
        return this;
    }

    public FileOutput close() {
        flush();
        IOUtil.closeIt(pw);
        pw = null;
        return this;
    }

    public FileOutput delete() {
        file.delete();
        return this;
    }

    public FileOutput flush() {
        IOUtil.flushIt(pw);
        return this;
    }

    @Override
    public String toString() {
        return file.toString();
    }
}
