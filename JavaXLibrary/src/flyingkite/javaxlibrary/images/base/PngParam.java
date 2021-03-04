package flyingkite.javaxlibrary.images.base;

import java.io.File;

public class PngParam {
    public static final int WRAP_CONTENT = -1;
    public File file;
    public int w = WRAP_CONTENT; // -1 = Use parent's size
    public int h = WRAP_CONTENT;

    public PngParam(String name) {
        this(new File(name));
    }

    public PngParam(File f) {
        file = f;
    }

    public PngParam size(int width, int height) {
        w = width;
        h = height;
        return this;
    }

    public PngParam copy() {
        PngParam p = new PngParam(file.getAbsolutePath());
        p.w = w;
        p.h = h;
        return p;
    }

    @Override
    public String toString() {
        return w + "x" + h + ", on " + file;
    }
}
