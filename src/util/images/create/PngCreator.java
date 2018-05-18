package util.images.create;

public class PngCreator {
    private PngCreator() { }

    private static final boolean DEBUG = false;

    public static PngCreateRequest from(PngCreateRequest.Param param) {
        return new PngCreateRequest(param);
    }
}
