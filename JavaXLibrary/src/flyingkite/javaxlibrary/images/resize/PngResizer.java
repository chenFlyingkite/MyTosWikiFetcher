package flyingkite.javaxlibrary.images.resize;

import flyingkite.javaxlibrary.images.base.PngParam;

public class PngResizer {
    public static final PngResizer me = new PngResizer();

    public static PngResizerRequest from(PngParam param) {
        return new PngResizerRequest(param);
    }
}
