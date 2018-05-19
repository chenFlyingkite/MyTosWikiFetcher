package util.images.create;

import util.images.base.PngParam;

public class PngCreator {
    private PngCreator() { }

    private static final boolean DEBUG = false;

    public static PngCreateRequest from(PngParam param) {
        return new PngCreateRequest(param);
    }
}
