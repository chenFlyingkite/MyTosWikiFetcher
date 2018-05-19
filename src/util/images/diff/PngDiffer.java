package util.images.diff;

import util.images.base.PngParam;

public class PngDiffer {
    private PngDiffer() {}

    public static PngDiffRequest from(PngParam p) {
        return new PngDiffRequest(p);
    }
}
