package flyingkite.javaxlibrary.images.invert;

import java.io.File;

import flyingkite.javaxlibrary.images.base.ImageUtil;
import flyingkite.javaxlibrary.images.base.PngParam;
import flyingkite.tool.TicTac2;

public class PngInvert implements ImageUtil {
    public static PngInvertRequest from(PngParam param) {
        return new PngInvertRequest(param);
    }
    public static final PngInvert me = new PngInvert();

    public void invert(String srcPath, String dstPath) {
        TicTac2 t = new TicTac2();
        t.tic();
        PngParam p = new PngParam(srcPath);
        PngInvert.from(p).into(dstPath);
        t.tac("Created %s", dstPath);
    }

    public void invertImages(String srcFolder, String dstFolder) {
        if (srcFolder == null || dstFolder == null) return;

        File sf = new File(srcFolder);
        File[] sfs = sf.listFiles((file) -> {
            return isImage(file.getAbsolutePath());
        });

        if (sfs != null) {
            for (int i = 0; i < sfs.length; i++) {
                File f = sfs[i];
                File d = new File(dstFolder, f.getName());
                invert(f.getAbsolutePath(), d.getAbsolutePath());
            }
        }
    }
}
