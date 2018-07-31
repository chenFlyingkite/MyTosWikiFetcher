package flyingkite.javaxlibrary.images.resize;

import flyingkite.javaxlibrary.images.base.PngParam;
import flyingkite.javaxlibrary.images.base.PngRequest;
import flyingkite.log.L;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class PngResizerRequest extends PngRequest {

    private static final String TAG = "PngResizerRequest";

    // Main components
    private PngParam reqParam;
    private BufferedImage srcImg;

    public PngResizerRequest(PngParam param) {
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

    public PngResizerRequest longSide(int wide) {
        int w = srcImg.getWidth();
        int h = srcImg.getHeight();
        boolean horiz = w >= h;
        int newW;
        int newH;
        if (horiz) {
            newW = wide;
            newH = Math.round(1F * h * wide / w);
        } else {
            newW = Math.round(1F * w * wide / h);
            newH = wide;
        }
        reqParam.w = newW;
        reqParam.h = newH;

        return this;
    }

    @Override
    protected BufferedImage getResultImage() {
        int w = valueIfNegative(reqParam.w, srcImg.getWidth());
        int h = valueIfNegative(reqParam.h, srcImg.getHeight());
        BufferedImage out = new BufferedImage(w, h, srcImg.getType());

        Graphics2D g2d = out.createGraphics();
        //L.log("src = %s\n%s x %s -> %s x %s", reqParam.file, srcImg.getWidth(), srcImg.getHeight(), w, h);
        //--
        Image img = srcImg.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        g2d.drawImage(img, 0, 0, w, h, null);
        //--
        // Normal one is not good looking
        //g2d.drawImage(srcImg, 0, 0, w, h, null);
        //--
        g2d.dispose();

        return out;
    }
}
