package flyingkite.javaxlibrary.images.resize;

import flyingkite.javaxlibrary.images.base.PngParam;

/**
 * Resize image into wanted width/height
 * Usage : resize into 60x50 image
 * <code>
 * <p>File src3x = new File("/Users/ericchen/Desktop/image@3x.png");
 * <p>File dst1x = new File("/Users/ericchen/Desktop/image@1x.png");
 * <p>PngResizer.from(new PngParam(src3x).size(60, 50)).into(dst1x);
 * </code>
 */
public class PngResizer {
    public static final PngResizer me = new PngResizer();

    public static PngResizerRequest from(PngParam param) {
        return new PngResizerRequest(param);
    }
}
