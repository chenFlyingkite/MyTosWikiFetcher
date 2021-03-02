package flyingkite.javaxlibrary.images.util;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImagesUtil {
    public static Point getSize(File file) {
        Point p = new Point();
        try {
            BufferedImage m = ImageIO.read(file);
            p.x = m.getWidth();
            p.y = m.getHeight();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return p;
    }
}
