package flyingkite.javaxlibrary.images.base;

public interface ImageUtil {
    default boolean isImage(String file) {
        int lastDot = file.lastIndexOf(".");
        if (lastDot < 0) return false;

        String ext = file.substring(lastDot);

        String[] mime = {".jpg", ".bmp", ".png"};
        for (String s : mime) {
            if (s.equalsIgnoreCase(ext)) {
                return true;
            }
        }
        return false;
    }
}
