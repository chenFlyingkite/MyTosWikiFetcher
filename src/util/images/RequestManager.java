package util.images;

import util.images.base.PngParam;
import util.images.create.PngCreateRequest;

public class RequestManager {
    @Deprecated
    public static PngCreateRequest from(PngParam param) {
        return new PngCreateRequest(param);
    }
}
