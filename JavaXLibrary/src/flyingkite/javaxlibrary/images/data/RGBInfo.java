package flyingkite.javaxlibrary.images.data;

import java.awt.image.BufferedImage;

import flyingkite.math.MathUtil;

public class RGBInfo {
    public final boolean hasColor;
    public final int argb;

    public RGBInfo(BufferedImage image, int x, int y) {
        hasColor = MathUtil.isInRange(x, 0, image.getWidth()) &&
                MathUtil.isInRange(y, 0, image.getHeight());

        argb = hasColor ? image.getRGB(x, y) : 0;
    }
}
