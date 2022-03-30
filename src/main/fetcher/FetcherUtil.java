package main.fetcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import flyingkite.log.LF;
import flyingkite.tool.GsonUtil;

public class FetcherUtil {

    public static void writeFileJsonPrettyPrinting(LF lf, Object obj) {
        lf.getFile().open();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        GsonUtil.writeFile(lf.getFile().getFile(), gson.toJson(obj));
        lf.getFile().close();
    }
}
