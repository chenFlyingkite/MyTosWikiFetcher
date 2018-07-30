package flyingkite.images.resize;

import flyingkite.images.base.PngParam;

public class PngResizer {

    private static final boolean DEBUG = false;

    public static PngResizerRequest from(PngParam param) {
        return new PngResizerRequest(param);
    }
}
