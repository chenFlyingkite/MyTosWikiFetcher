package flyingkite.javaxlibrary.images.invert;

import java.io.File;

import flyingkite.javaxlibrary.images.base.ImageUtil;
import flyingkite.javaxlibrary.images.base.PngParam;
import flyingkite.tool.TicTac2;

public class PngInvert {
    public static PngInvertRequest from(PngParam param) {
        return new PngInvertRequest(param);
    }

    private static ImageUtil util = new ImageUtil() {};

    public static void invert(String path, String name) {
        TicTac2 t = new TicTac2();
        t.tic();
        PngParam p = new PngParam(path);
        PngInvert.from(p).into(name);
        t.tac("Created %s", name);
    }

    public static void invertImages(String src, String dst) {
        if (src == null || dst == null) return;

        File sf = new File(src);
        File[] sfs = sf.listFiles((file) -> {
            return util.isImage(file.getAbsolutePath());
        });

        if (sfs != null) {
            for (int i = 0; i < sfs.length; i++) {
                File f = sfs[i];
                File d = new File(dst, f.getName());
                invert(f.getAbsolutePath(), d.getAbsolutePath());
            }
        }
    }
}
