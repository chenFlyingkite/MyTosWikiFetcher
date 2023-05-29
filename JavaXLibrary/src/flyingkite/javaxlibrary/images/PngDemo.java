package flyingkite.javaxlibrary.images;

import flyingkite.data.Rect2;
import flyingkite.javaxlibrary.images.base.PngParam;
import flyingkite.javaxlibrary.images.create.PngCreator;
import flyingkite.log.L;

import java.io.File;

public class PngDemo {
    private static void eraseRect() {
        String path;
        path = "D:\\新增資料夾 (2)\\新增資料夾";
        File f = new File(path);
        File[] fs = f.listFiles();
        Rect2 link = new Rect2(495, 55, 595, 75);
        Rect2 ic0 = new Rect2(1835, 45, 1875, 80);
        Rect2 ic1 = new Rect2(1605, 95, 1667, 156);
        final int W = 1920;
        final int H = 1030;
        for (int i = 0; i < fs.length; i++) {
            File g = fs[i];
            L.log("%s :> %s", i, g);
            if (g.isFile()) {
                File par = new File(f, "0");
                par.mkdirs();
                File dst = new File(par, i + ".png");
                L.log("#%s : %s", i, dst);
                PngCreator.from(new PngParam(g).size(W, H))
                        .copy(Rect2.atLTWH(0, 0, W, H))
                        .replace((x, y, w, h, c) -> {
                            if (link.contains(x, y)) {
                                return 0xFF202124;
                            } else if (ic0.contains(x, y)) {
                                return 0xFF35363A;
                            } else if (ic1.contains(x, y)) {
                                return 0xFFFFFFFF;
                            } else {
                                return c;
                            }
                        })
                        .into(dst);
            }
        }
    }
}
