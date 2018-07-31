package flyingkite.javaxlibrary.images.resize;

import flyingkite.javaxlibrary.images.base.PngParam;

public class PngResizer {

    private static final boolean DEBUG = false;

    public static PngResizerRequest from(PngParam param) {
        return new PngResizerRequest(param);
    }
}
