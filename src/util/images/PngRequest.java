package util.images;

import util.data.Rect2;
import util.files.FileUtil;
import util.logging.L;
import util.tool.TicTac2;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class PngRequest {
    private static final String TAG = "PngRequest";
    private static final boolean DEBUG = false;

    // Tracking performance
    private TicTac2 clock = new TicTac2();
    private boolean logTime = false;

    // Main components
    private File srcFile;
    private BufferedImage srcImg;
    private BufferedImage dstImg;
    private Rect2 allDstRect = new Rect2();

    public static class Param {
        private File file;
        private int w = -1; // -1 = Use parent's size
        private int h = -1;

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

    public PngRequest(Param param) {
        clock.enable(logTime);
        srcFile = param.file;

        clock.tic();
        // Load image of PNG
        try {
            srcImg = ImageIO.read(srcFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        clock.tac("ImageIO read << " + srcFile.getAbsolutePath());

        if (srcImg == null) {
            L.log("Fail to load image from %s", srcFile);
            return;
        }

        int w = param.w;
        if (w < 0) {
            w = srcImg.getWidth();
        }
        int h = param.h;
        if (h < 0) {
            h = srcImg.getHeight();
        }
        dstImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        allDstRect.set(0, 0, w, h);
    }

    public PngRequest copy() {
        return copy(new Rect2(0, 0, srcImg.getWidth(), srcImg.getHeight()));
    }

    public PngRequest copy(Rect2 rect) {
        // common used
        final int x = rect.left;
        final int y = rect.top;
        final int w = rect.width();
        final int h = rect.height();

        // Checking valid parameter
        Rect2 valid = new Rect2(0, 0, srcImg.getWidth() - w, srcImg.getHeight() - h);
        if (!rect.equals(allDstRect) && !valid.contains(x, y)) {
            L.log("Invalid position (%s, %s)", x, y);
            return this;
        }

        // Copy image
        clock.tic();
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int rgba = srcImg.getRGB(x + i, y + j);
                dstImg.setRGB(i, j, rgba);
            }
        }
        clock.tac(TAG + " copy " + rect);
        return this;
    }

    public PngRequest eraseCorners() {
        return eraseCorners(allDstRect, new int[]{9, 7, 5, 4, 3, 2, 2, 1, 1});
    }

    /**
     * Erase the image with transparent color,
     * Range: (x, y, x + w, y + h)
     * @param erase The erase array
     */
    public PngRequest eraseCorners(Rect2 rect, int[] erase) {
        // common used
        final int x = rect.left;
        final int y = rect.top;
        final int w = rect.width();
        final int h = rect.height();

        BufferedImage imgErase = dstImg;
        if (DEBUG) {
            L.log("eraseCorners (w, h, x, y) = (%s, %s, %s, %s)\nerase = %s", w, h, x, y, Arrays.toString(erase));
        }
        // Erase *, A = image, left/right top
        // ********AA*********
        // ******AAAAAA*******
        // *****AAAAAAAAA*****
        // ****AAAAAAAAAAA****
        // ***AAAAAAAAAAAAA***
        // **AAAAAAAAAAAAAAA**
        // **AAAAAAAAAAAAAAA**
        // *AAAAAAAAAAAAAAAAA*
        // *AAAAAAAAAAAAAAAAA*

        clock.tic();
        // Make 4 corners as transparent
        for (int i = 0; i < erase.length; i++) {
            for (int j = 0; j < erase[i]; j++) {
                int pi = x + i;
                int pj = y + j;
                int ri = x + w - i - 1;
                int rj = y + h - j - 1;
                if (DEBUG) {
                    L.log("pi = %s, pj = %s, ri = %s, rj = %s", pi, pj, ri, rj);
                }
                // Left Top color, 0xARGB
                imgErase.setRGB(pi, pj, noColor(0));
                // Left Bottom color
                imgErase.setRGB(pi, rj, noColor(1));
                // Right Top color
                imgErase.setRGB(ri, pj, noColor(2));
                // Right Bottom color
                imgErase.setRGB(ri, rj, noColor(3));
            }
        }
        clock.tac(TAG + " erase corner = %s", Arrays.toString(erase));
        return this;
    }

    public void into(String outputName) {
        into(new File(outputName));
    }

    public void into(File outFile) {
        FileUtil.ensureDelete(outFile);

        clock.tic();
        // Write to output
        try {
            ImageIO.write(dstImg, "png", outFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        clock.tac("ImageIO write >> " + outFile.getAbsolutePath());
    }

    private int noColor(int index) {
        // R, G, B, Cyan
        final int[] clrDbg = {0xFFFF0000, 0xFF00FF00, 0xFF0000FF, 0xFF00FFFF};
        // All transparent
        final int[] colors = {0x00000000, 0x00000000, 0x00000000, 0x00000000};
        int[] color = DEBUG ? clrDbg : colors;
        return color[index];
    }
}
