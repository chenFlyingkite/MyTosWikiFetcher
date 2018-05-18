package util.images.create;

import util.data.Rect2;
import util.images.base.PngRequest;
import util.logging.L;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class PngCreateRequest extends PngRequest {
    private static final String TAG = "PngCreateRequest";

    // Main components
    private Param reqParam;
    private BufferedImage srcImg;
    private BufferedImage dstImg;
    private Rect2 allDstRect = new Rect2();

    public PngCreateRequest(Param param) {
        mClock.enable(mClockLog);
        reqParam = param;
        File f = param.file;

        mClock.tic();
        // Load image of PNG
        try {
            srcImg = ImageIO.read(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mClock.tac("ImageIO read << " + f.getAbsolutePath());

        if (srcImg == null) {
            L.log("Fail to load image from %s", f);
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

    @Override
    protected BufferedImage getResultImage() {
        return dstImg;
    }

    public PngCreateRequest copy() {
        return copy(new Rect2(0, 0, srcImg.getWidth(), srcImg.getHeight()));
    }

    public PngCreateRequest copy(Rect2 rect) {
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
        mClock.tic();
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int rgba = srcImg.getRGB(x + i, y + j);
                dstImg.setRGB(i, j, rgba);
            }
        }
        mClock.tac(TAG + " copy " + rect);
        return this;
    }

    public PngCreateRequest eraseCorners() {
        return eraseCorners(allDstRect, new int[]{9, 7, 5, 4, 3, 2, 2, 1, 1});
    }

    /**
     * Erase the image with transparent color,
     * Range: (x, y, x + w, y + h)
     * @param erase The erase array
     */
    public PngCreateRequest eraseCorners(Rect2 rect, int[] erase) {
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

        mClock.tic();
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
        mClock.tac(TAG + " erase corner = %s", Arrays.toString(erase));
        return this;
    }

}
