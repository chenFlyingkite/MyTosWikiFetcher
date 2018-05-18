package util.images;

public class RequestManager {
    @Deprecated
    public static PngRequest from(PngRequest.Param param) {
        return new PngRequest(param);
    }
}
