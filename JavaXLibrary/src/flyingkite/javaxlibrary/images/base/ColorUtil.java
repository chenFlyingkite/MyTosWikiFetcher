package flyingkite.javaxlibrary.images.base;

public interface ColorUtil {

    default int getA(int c) {
        return (c >> 24) & 0xFF;
    }

    default int getR(int c) {
        return (c >> 16) & 0xFF;
    }

    default int getG(int c) {
        return (c >> 8) & 0xFF;
    }

    default int getB(int c) {
        return (c >> 0) & 0xFF;
    }

    default int toColor(int r, int g, int b) {
        return toColor(r, g, b, 255);
    }

    default int toColor(int r, int g, int b, int a) {
        return ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8)  |
                ((b & 0xFF) << 0);
    }
}
