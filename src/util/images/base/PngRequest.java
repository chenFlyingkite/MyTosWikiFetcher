package util.images.base;

import util.files.FileUtil;
import util.tool.TicTac2;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PngRequest {
    private static final String TAG = "PngRequest";
    protected final boolean DEBUG = false;

    // Tracking performance
    protected TicTac2 mClock = new TicTac2();
    protected boolean mClockLog = false;

    public static class Param {
        public File file;
        public int w = -1; // -1 = Use parent's size
        public int h = -1;

        public Param(String name) {
            this(new File(name));
        }

        public Param(File f) {
            file = f;
        }

        public Param size(int width, int height) {
            w = width;
            h = height;
            return this;
        }

        public Param copy() {
            Param p = new Param(file.getAbsolutePath());
            p.w = w;
            p.h = h;
            return p;
        }
    }

    /**
     * @return {@link BufferedImage} to output
     */
    protected BufferedImage getResultImage() {
        return null;
    }


    /**
     * {@link #into(File)}
     */
    public void into(String name) {
        into(new File(name));
    }

    /**
     * Save the {@link #getResultImage()} into file
     * @param file for output
     */
    public void into(File file) {
        FileUtil.ensureDelete(file);

        mClock.tic();
        // Write to output
        try {
            ImageIO.write(getResultImage(), "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mClock.tac("ImageIO write >> " + file.getAbsolutePath());
    }

    protected int noColor(int index) {
        // R, G, B, Cyan
        final int[] clrDbg = {0xFFFF0000, 0xFF00FF00, 0xFF0000FF, 0xFF00FFFF};
        // All transparent
        final int[] colors = {0x00000000, 0x00000000, 0x00000000, 0x00000000};
        int[] color = DEBUG ? clrDbg : colors;
        return color[index];
    }
}
