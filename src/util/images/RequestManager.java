package util.images;

import util.images.create.PngCreateRequest;

public class RequestManager {
    @Deprecated
    public static PngCreateRequest from(PngCreateRequest.Param param) {
        return new PngCreateRequest(param);
    }
}
