package main.images;

import util.MathUtil;
import util.files.FileUtil;
import util.logging.L;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class PngCreator {
    private PngCreator() { }

    private static final boolean DEBUG = false;

    public static void extract(String srcName, String dstName, int width, int height, int x, int y) {
        File fs = new File(srcName);
        File fd = new File(dstName);
        FileUtil.ensureDelete(fd);

        // Load image of PNG
        BufferedImage srcImg = null;
        BufferedImage dstImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        try {
            //noinspection ResultOfMethodCallIgnored
            fd.createNewFile();
            srcImg = ImageIO.read(fs);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (srcImg == null) {
            L.log("Fail to load image from %s", srcName);
            return;
        }

        int sw = srcImg.getWidth();
        int sh = srcImg.getHeight();

        if (!MathUtil.isInRange(x, 0, sw - width) || !MathUtil.isInRange(y, 0, sh - height)) {
            L.log("Invalid position (%s, %s)", x, y);
            return;
        }

        // Copy image
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int rgba = srcImg.getRGB(x + i, y + j);
                dstImg.setRGB(i, j, rgba);
            }
        }

        eraseCorners(dstImg, width, height, 0, 0, new int[]{9, 7, 5, 4, 3, 2, 2, 1, 1});

        // Write to output
        try {
            ImageIO.write(dstImg, "png", fd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Erase the image with erase from (x, y) + (w, h) with array erase
     */
    private static void eraseCorners(BufferedImage imgErase, int w, int h, int x, int y, int[] erase) {
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

        // Make 4 corners as transparent
        for (int i = 0; i < erase.length; i++) {
            for (int j = 0; j < erase[i]; j++) {
                int pi = x + i;
                int pj = y + j;
                int ri = x + w-i-1;
                int rj = y + h-j-1;
                if (DEBUG) {
                    //L.log("i = %s, j = %s, ri = %s, rj = %s", i, j, ri, rj);
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
    }

    private static int noColor(int index) {
        // R, G, B, Cyan
        final int[] clrDbg = {0xFFFF0000, 0xFF00FF00, 0xFF0000FF, 0xFF00FFFF};
        // All transparent
        final int[] colors = {0x00000000, 0x00000000, 0x00000000, 0x00000000};
        int[] color = DEBUG ? clrDbg : colors;
        return color[index];
    }
}
