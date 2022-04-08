package main.fetcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import flyingkite.log.FileOutput;
import flyingkite.log.LF;
import flyingkite.tool.GsonUtil;

public class FetcherUtil {

    public static void overwriteFileJsonPrettyPrinting(Object obj, LF lf) {
        lf.getFile().open(false);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        GsonUtil.writeFile(lf.getFile().getFile(), gson.toJson(obj));
        lf.getFile().close();
    }

    public static void saveAsJson(Object data, String folder, String name) {
        LF lf = new LF(folder, name);
        overwriteFileJsonPrettyPrinting(data, lf);
    }

    public static void saveAsJson(Object data, String path) {
        LF lf = new LF(new FileOutput(path));
        overwriteFileJsonPrettyPrinting(data, lf);
    }
}
