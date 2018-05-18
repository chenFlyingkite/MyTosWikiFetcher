package util.images.diff;

import util.MathUtil;
import util.files.FileUtil;
import util.logging.L;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;

public class PngDiffer {
    private PngDiffer() {}
    private static final String diffName = "diff.png";

    public static void diff(String folder) {
        File parent = new File(folder);
        File[] child = parent.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && !pathname.getName().equals(diffName);
            }
        });
        if (child == null) {
            L.log("No file to diff in %s", folder);
            return;
        }
        if (child.length != 2) {
            L.log("Only can diff 2 files,\nFound = %s", Arrays.toString(child));
            return;
        }

        File f = new File("asd");
        L.log("f = %s", f.getAbsolutePath());

        // Load the pngs as Image
        BufferedImage imgA = loadImage(child[0]);
        BufferedImage imgB = loadImage(child[1]);

        if (imgA == null || imgB == null) {
            L.log("Fail to load image A = %s, B = %s", child[0], child[1]);
            return;
        }

        final int wa = imgA.getWidth();
        final int ha = imgA.getHeight();
        final int wb = imgB.getWidth();
        final int hb = imgB.getHeight();
        final int maxW = Math.max(wa, wb);
        final int maxH = Math.max(ha, hb);

        BufferedImage diff = new BufferedImage(maxW, maxH, BufferedImage.TYPE_INT_ARGB);

        // Perform diff
        RGBInfo infoA;
        RGBInfo infoB;
        for (int i = 0; i < maxW; i++) {
            for (int j = 0; j < maxH; j++) {
                infoA = new RGBInfo(imgA, i, j);
                infoB = new RGBInfo(imgB, i, j);
                int cid = (infoA.hasColor ? 2 : 0)
                        + (infoB.hasColor ? 1 : 0);
                switch (cid) {
                    case 0:
                        break;
                    case 1: // No A, Has B
                        diff.setRGB(i, j, infoB.argb);
                        break;
                    case 2: // Has A, No B
                        diff.setRGB(i, j, infoA.argb);
                        break;
                    case 3:
                        int col = 0xFFFF00FF;
                        if (infoA.argb == infoB.argb) {
                            col = infoA.argb;
                        }
                        diff.setRGB(i, j, col);
                        break;
                    default:
                        throw new NullPointerException();
                }
            }
        }

        // Write to file
        File dif = new File(folder, diffName);
        try {
            ImageIO.write(diff, "png", dif);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void into(String outputName) {
        into(new File(outputName));
    }

    private static void into(File outFile) {
        FileUtil.ensureDelete(outFile);

        //clock.tic();
        // Write to output
        try {
            ImageIO.write(null, "png", outFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //clock.tac("ImageIO write >> " + outFile.getAbsolutePath());
    }

    private static BufferedImage loadImage(String name) {
        return loadImage(new File(name));
    }

    private static BufferedImage loadImage(File f) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }

    private static class RGBInfo {
        private boolean hasColor;
        private int argb;

        private RGBInfo(BufferedImage image, int x, int y) {
            hasColor = MathUtil.isInRange(x, 0, image.getWidth()) &&
                    MathUtil.isInRange(y, 0, image.getHeight());

            if (hasColor) {
                argb = image.getRGB(x, y);
            }
        }
    }
}
