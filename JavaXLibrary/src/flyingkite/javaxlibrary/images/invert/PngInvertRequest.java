package flyingkite.javaxlibrary.images.invert;

import java.awt.image.BufferedImage;
import java.io.File;

import flyingkite.javaxlibrary.images.base.PngParam;
import flyingkite.javaxlibrary.images.base.PngRequest;
import flyingkite.log.L;
import flyingkite.math.MathUtil;

public class PngInvertRequest extends PngRequest {

    private static final String TAG = "PngInvertRequest";

    // Main components
    private PngParam reqParam;
    private BufferedImage srcImg;
    private int sumColor = 0xFFFFFFFF; // Color.WHITE

    public PngInvertRequest(PngParam param) {
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
    }

    public PngInvertRequest invert(int rgbSum) {
        sumColor = rgbSum;
        return this;
    }

    @Override
    protected BufferedImage getResultImage() {
        int w = valueIfNegative(reqParam.w, srcImg.getWidth());
        int h = valueIfNegative(reqParam.h, srcImg.getHeight());
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int c = srcImg.getRGB(i, j);
                int a = getA(c);
                int r = MathUtil.makeInRange(getR(sumColor) - getR(c), 0, 255);
                int g = MathUtil.makeInRange(getG(sumColor) - getG(c), 0, 255);
                int b = MathUtil.makeInRange(getB(sumColor) - getB(c), 0, 255);
                out.setRGB(i, j, toColor(r, g, b, a));
            }
        }

        return out;
    }
}
