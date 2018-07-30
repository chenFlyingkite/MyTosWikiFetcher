package flyingkite.images.diff;

import flyingkite.images.base.PngParam;

import java.io.File;

public class PngDiffer {
    public static PngDiffRequest from(PngParam p) {
        return new PngDiffRequest(p);
    }

    public static void diff(File folder) {
        PngParam p = new PngParam(folder);
        new PngDiffRequest(p).diff();
    }
}
