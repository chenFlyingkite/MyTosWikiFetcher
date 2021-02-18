package flyingkite.javaxlibrary.images.create;

import flyingkite.data.Rect2;
import flyingkite.files.FileUtil;
import flyingkite.javaxlibrary.images.base.PngParam;
import flyingkite.log.L;
import flyingkite.tool.TicTac2;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class PngCreator {
    public static final PngCreator me = new PngCreator();

    public static PngCreateRequest from(PngParam param) {
        return new PngCreateRequest(param);
    }

    /**
     * Returns the cropped image
     * @param folder saved file at "Logos/Output/" + folder
     * @param src image source from "Logos/Source/" + src
     * @param rect crop src image at specific range
     * @param dst file output as (dst + ".png") in folder
     */
    public void getImage(String folder, String src, Rect2 rect, String dst) {
        int w = rect.width();
        int h = rect.height();
        final String base = "Logos/Output/" + folder;
        String name = base + "/" + dst + ".png";

        // Crop icon
        PngParam p = new PngParam("Logos/Source/" + src).size(w, h);
        PngCreator.from(p).copy(rect).into(name);
        L.log("created %s", name);
    }

    /**
     * Get the rounded cropped image
     * parameters are same as {@link #getImage(String, String, Rect2, String)}
     * @see #getImage(String, String, Rect2, String)
     */
    public void getImageR(String folder, String src, Rect2 rect, String dst) {
        int w = rect.width();
        int h = rect.height();
        final String base = "Logos/Output/" + folder;
        String name = base + "/" + dst + ".png";

        // Crop icon
        PngParam p = new PngParam("Logos/Source/" + src).size(w, h);
        PngCreator.from(p).copy(rect).eraseCorners().into(name);
        L.log("created %s", name);
    }

    public void moveImage() {
        // For left image, add right half blank
        // For right image, add left half blank
        String src = "D:\\aaa\\src\\";
        String dst = src + "n\\";
        final int w = 37;
        final int h = 104;
        Rect2 rL = Rect2.atLTWH(0, 0, w, h);
        Rect2 rR = Rect2.atLTWH(w, 0, w, h);
        String[] names = {
                "btn_trim_audio_left_n.png",
                "btn_trim_audio_right_n.png",
                "btn_trim_title_left_n.png",
                "btn_trim_title_right_n.png",
                "btn_trim_vp_left_n.png",
                "btn_trim_vp_right_n.png",
        };
        for (String s : names) {
            PngParam p = new PngParam(src + s).size(2*w, h);
            if (s.contains("left")) {
                PngCreator.from(p).copy(rL).into(dst + s);
            } else {
                PngCreator.from(p).copy(rL, rR).into(dst + s);
            }
        }
    }

    // 4096 x 4096 = 16*16 * (256x256)
    public void standard() {
        String dst = "D:\\images\\a.bmp";
        final int w = 256 * 16;
        final int h = w;

        File f = new File(dst);
        TicTac2 clk = new TicTac2();
        clk.tic();
        BufferedImage dstImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        // carpet = 16x16 tiles
        // r.00 ~ r.f0
        // r.f0 ~ r.ff
        // each tile = 256x256
        // 00 ~ 0f  -> b
        // f0 ~ ff
        // |
        // g
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int a = j / 256;
                int b = j % 256;
                int c = i / 256;
                int d = i % 256;
                // rgb each
                int cr = 16 * a + c;
                int cg = b;
                int cb = d;
                int rgb = rgb(cr, cg, cb);
                dstImg.setRGB(i, j, rgb);
            }
        }

        File file = f;
        FileUtil.ensureDelete(file);
        FileUtil.createFile(file);
        clk.tic();
        // Step : Write to output
        try {
            ImageIO.write(dstImg, "bmp", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        clk.tac("ImageIO write >> " + file.getAbsolutePath());
    }

    private static int rgb(int r, int g, int b) {
        return ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }
}
