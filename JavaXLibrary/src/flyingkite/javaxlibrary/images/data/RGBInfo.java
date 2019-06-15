package flyingkite.javaxlibrary.images.data;

import flyingkite.math.MathUtil;

import java.awt.image.BufferedImage;

public class RGBInfo {
    public final boolean hasColor;
    public final int argb;

    // Separated field of argb
    public final int a;
    public final int r;
    public final int g;
    public final int b;

    public RGBInfo(BufferedImage image, int x, int y) {
        hasColor = MathUtil.isInRange(x, 0, image.getWidth()) &&
                MathUtil.isInRange(y, 0, image.getHeight());

        argb = hasColor ? image.getRGB(x, y) : 0;

        // Convert to argb
        int c = argb;
        a = (c & 0xFF000000) >>> 24;
        r = (c & 0x00FF0000) >>> 16;
        g = (c & 0x0000FF00) >>> 8;
        b = (c & 0x000000FF) >>> 0;
        // HSV <-> RGB = java.awt.Color.HSBtoRGB()
    }

    @Override
    public String toString() {
        return String.format("%s 0x%x = 0x %x %x %x %x", hasColor ? "o" : "x", argb, a, r, g, b);
    }
}
