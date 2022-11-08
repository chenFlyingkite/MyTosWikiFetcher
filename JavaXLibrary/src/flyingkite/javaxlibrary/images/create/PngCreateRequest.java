package flyingkite.javaxlibrary.images.create;

import flyingkite.data.Rect2;
import flyingkite.javaxlibrary.images.base.PngParam;
import flyingkite.javaxlibrary.images.base.PngRequest;
import flyingkite.javaxlibrary.images.data.RGBInfo;
import flyingkite.log.L;
import flyingkite.math.MathUtil;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

public class PngCreateRequest extends PngRequest {
    private static final String TAG = "PngCreateRequest";

    public interface ColorSelector {
        int drawAt(int x, int y, int w, int h, int c);
    }

    // Main components
    private PngParam reqParam;
    private BufferedImage srcImg;
    private BufferedImage dstImg;
    private Rect2 canvasRect = new Rect2();
    private boolean saveHistogram;
    private BufferedImage histogram;
    private BufferedImage hsvHistogram;

    public PngCreateRequest(PngParam param) {
        File f = param.file;
        reqParam = param;

        mClock.enable(false); // print log or not
        mClock.tic();
        // Step : Load image of PNG
        srcImg = loadImage(f);
        mClock.tac("ImageIO read << " + f.getAbsolutePath());

        if (srcImg == null) {
            L.log("Fail to load image from %s", f);
            return;
        }

        int w = valueIfNegative(param.w, srcImg.getWidth());
        int h = valueIfNegative(param.h, srcImg.getHeight());

        dstImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        canvasRect.set(0, 0, w, h);
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

        // Step : Copy image
        mClock.tic();
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int sx = x + i;
                int sy = y + j;
                boolean valid = MathUtil.isInRange(sx, 0, srcImg.getWidth()) && MathUtil.isInRange(sy, 0, srcImg.getHeight());
                int rgba = 0; // Transparent
                if (valid) {
                    rgba = srcImg.getRGB(sx, sy);
                }
                dstImg.setRGB(i, j, rgba);
            }
        }
        mClock.tac(TAG + " copy " + rect);
        return this;
    }

    /**
     * Copy source image range of src into destination image's range of dst
     * treat this as translation
     * canvas size = (2w, h)
     * src = (0, 0, w, h), dst = (w, 0, w, h)
     * => Make image shift right w-pixels
     */
    public PngCreateRequest copy(Rect2 src, Rect2 dst) {
        final int imgW = canvasRect.width();
        final int imgH = canvasRect.height();
        // Step : Copy image
        mClock.tic();
        for (int i = 0; i < imgW; i++) {
            for (int j = 0; j < imgH; j++) {
                int sx = i - src.left - dst.left;
                int sy = j - src.top - dst.top;
                boolean valid = MathUtil.isInRange(sx, 0, srcImg.getWidth()) && MathUtil.isInRange(sy, 0, srcImg.getHeight());
                //L.log("(% 3d, % 3d) := (% 3d, % 3d)", i, j, sx, sy);
                int rgba = 0; // Transparent
                if (valid) {
                    rgba = srcImg.getRGB(sx, sy);
                }
                dstImg.setRGB(i, j, rgba);
            }
        }
        mClock.tac(TAG + " copy " + src + " -> dst = " + dst);
        return this;
    }

    public PngCreateRequest eraseCorners() {
        return eraseCorners(canvasRect, new int[]{9, 7, 5, 4, 3, 2, 2, 1, 1});
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
        // Step : Make 4 corners as transparent
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

    /**
     * Erase the image with transparent color,
     * Range: selector return color to be paint
     */
    public PngCreateRequest replace(ColorSelector selector) {
        // common used
        PngParam pp = reqParam;
        final int imgW = canvasRect.width();
        final int imgH = canvasRect.height();

        if (DEBUG) {
            L.log("replace pp = %s, rect = ", pp, canvasRect);
        }

        mClock.tic();
        for (int i = 0; i < imgW; i++) {
            for (int j = 0; j < imgH; j++) {
                int color = srcImg.getRGB(i, j);
                int replace = selector.drawAt(i, j, imgW, imgH, color);
                if (DEBUG) {
                    L.log("(%4d, %4d) = 0x%0x -> 0x%0x", i, j, color, replace);
                }
                dstImg.setRGB(i, j, replace);
            }
        }
        mClock.tac(TAG + " replace OK");
        return this;
    }

    @Override
    public void into(String name) {
        super.into(name);
        if (saveHistogram && histogram != null) {
            File dst = new File(name);
            into(histogram, new File(dst.getParentFile(), "his.png"));
            into(hsvHistogram, new File(dst.getParentFile(), "hsv.png"));
        }
    }

    public PngCreateRequest histogram(int height, int backgroundColor) {
        long[] hisR = new long[256];
        long[] hisG = new long[256];
        long[] hisB = new long[256];
        saveHistogram = true;
        histogram = new BufferedImage(256, 3 * height, BufferedImage.TYPE_INT_ARGB);

        // common used
        PngParam pp = reqParam;
        final int imgW = canvasRect.width();
        final int imgH = canvasRect.height();
        double size = imgW * imgH;

        if (DEBUG) {
            L.log("histogram pp = %s, rect = ", pp, canvasRect);
        }

        // [R, G, B] & [H, S, V]
        int[][] hisPixel = new int[3][256];
        int[][] hisHsv = new int[3][360];
        mClock.tic();
        for (int i = 0; i < imgW; i++) {
            for (int j = 0; j < imgH; j++) {
                RGBInfo it = new RGBInfo(srcImg, i, j);
                hisR[it.r]++;
                hisG[it.g]++;
                hisB[it.b]++;
                hisPixel[0][it.r] = (int) Math.round(1F * height * hisR[it.r] / size);
                hisPixel[1][it.g] = (int) Math.round(1F * height * hisG[it.g] / size);
                hisPixel[2][it.b] = (int) Math.round(1F * height * hisB[it.b] / size);
                for (int k = 0; k < 3; k++) {
                    int x = Math.min(359, Math.round(it.hsv[k] * 360));
                    hisHsv[k][x]++;
                }
                dstImg.setRGB(i, j, it.argb);
            }
        }
        mClock.tac(TAG + " histogram OK");
        BufferedImage img;
        img = histogram;
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                int chk = j / height;
                int[] ch = hisPixel[chk];
                int y = height - (j % height);
                int c = 0xff000000 | (i << (8*(2-chk))); // 2^8 = 256
                if (y <= ch[i]) {
                    // draw the bar
                    img.setRGB(i, j, c);
                } else {
                    // empty
                    img.setRGB(i,j, backgroundColor);
                }
            }
        }
        L.log("Histogram R = %s", Arrays.toString(hisR));
        L.log("Histogram G = %s", Arrays.toString(hisG));
        L.log("Histogram B = %s", Arrays.toString(hisB));
        for (int i = 0; i < 3; i++) {
            L.log("hisHsv[%s] = %s", i, Arrays.toString(hisHsv[i]));
        }

        hsvHistogram = new BufferedImage(360, 3 * height, BufferedImage.TYPE_INT_ARGB);
        img = hsvHistogram;
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                int chk = j / height;
                int[] ch = hisHsv[chk];
                int y = height - (j % height);
                int c = Color.HSBtoRGB(1F * i / 360, 1, 1);
                if (y <= ch[i]) {
                    // draw the bar
                    img.setRGB(i, j, c);
                } else {
                    // empty
                    img.setRGB(i, j, backgroundColor);
                }
            }
        }
        return this;
    }

    // It needs to check if bugs, its output looks strange
    // Try to make continuous color range into segmented color part
    @Deprecated
    public PngCreateRequest hsv(float hueUnit, float satUnit, float valUnit) {
        float[] hueSample = units(hueUnit, 0, 1);
        float[] satSample = units(satUnit, 0, 1);
        float[] valSample = units(valUnit, 0, 1);

        // common used
        PngParam pp = reqParam;
        final int imgW = canvasRect.width();
        final int imgH = canvasRect.height();

        if (DEBUG) {
            L.log("hsv pp = %s, rect = ", pp, canvasRect);
        }
        L.log("hue = %s", Arrays.toString(hueSample));
        L.log("sat = %s", Arrays.toString(satSample));
        L.log("val = %s", Arrays.toString(valSample));

        mClock.tic();
        for (int i = 0; i < imgW; i++) {
            for (int j = 0; j < imgH; j++) {
                RGBInfo it = new RGBInfo(srcImg, i, j);
                float[] srcHSV = it.hsv;
                int hx = (int) Math.floor(srcHSV[0] / hueUnit);
                int sx = (int) Math.floor(srcHSV[1] / satUnit);
                int vx = (int) Math.floor(srcHSV[2] / valUnit);
                hx = Math.min(hx, hueSample.length - 1);
                sx = Math.min(sx, satSample.length - 1);
                vx = Math.min(vx, valSample.length - 1);
                L.log("(%s, %s), hsv = %s, hueSample[%s] = %s", i, j, Arrays.toString(srcHSV), hx, hueSample[hx]);
                int color = Color.HSBtoRGB(hueSample[hx], satSample[sx], valSample[vx]);
                dstImg.setRGB(i, j, color);
            }
        }
        mClock.tac(TAG + " hsv OK");
        return this;
    }

    private float[] units(float unit, float min, float max) {
        int n = (int) Math.ceil((max - min) / unit);
        float[] ans = new float[n];
        for (int i = 0; i < n; i++) {
            ans[i] = min + unit * i;
        }
        return ans;
    }
}
