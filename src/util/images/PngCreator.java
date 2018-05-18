package util.images;

public class PngCreator {
    private PngCreator() { }

    private static final boolean DEBUG = false;

    public static PngRequest from(PngRequest.Param param) {
        return new PngRequest(param);
    }
}
