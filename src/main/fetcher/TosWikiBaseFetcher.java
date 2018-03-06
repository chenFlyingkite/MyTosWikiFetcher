package main.fetcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import util.data.Range;
import util.logging.LF;
import util.tool.IOUtil;
import util.tool.TextUtil;
import util.tool.TicTac2;
import wikia.articles.UnexpandedListArticleResultSet;

import java.io.*;
import java.net.URL;

public abstract class TosWikiBaseFetcher {

    protected boolean mFetchAll = false;
    protected TicTac2 clock = new TicTac2();

    protected Gson mGson
            = new GsonBuilder().setPrettyPrinting().create();
    //= new Gson();

    public abstract String getAPILink();

    public abstract LF getHttpLF();

    public void onNewCallStart() {}
    public void onNewCallEnded() {}

    protected final boolean hasResult(ResultSet s) {
        return s != null && s.getItems() != null;
    }

    public ResultSet getApiResults() {
        return getApiResults(getAPILink(), getHttpLF());
    }

    public ResultSet getApiResults(final String apiLink, final LF apiLf) {
        // Delete the logging file
        apiLf.getFile().delete().open();
        apiLf.setLogToL(!mFetchAll);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(apiLink).build();
        ResultSet set = null;
        try {
            Response response;
            ResponseBody body;

            // Step 1: Fetch all links from Wikia API
            apiLf.log("Linking %s", apiLink);

            onNewCallStart();
            clock.tic();
            response = client.newCall(request).execute();
            clock.tac("response = %s", response);
            apiLf.log("response = %s", response);
            onNewCallEnded();

            clock.tic();
            body = response.body();
            clock.tac("body = %s", body);
            if (body == null) {
                return null;
            }

            String s = body.string();

            clock.tic();
            set = mGson.fromJson(s, ResultSet.class);
            clock.tac("from gson, %s", set);
            apiLf.log("from gson, %s", set);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            apiLf.getFile().close();
        }
        return set;
    }

    protected final Range getRange(ResultSet set, int from, int prefetch) {
        // Step 2: Determine the range of parsing
        int min = 0;
        int max = hasResult(set) ? set.getItems().length : 0;
        if (!mFetchAll) {
            min = Math.max(min, from);
            max = Math.min(max, from + prefetch);
        }

        return new Range(min, max);
    }

    protected void downloadImage(String link, String folder, String name) {
        InputStream fin = null;
        OutputStream fout = null;
        name = toValidIconName(name);
        try {
            URL url = new URL(link);
            File image = new File(folder, name);
            image.mkdirs();
            if (image.exists()) {
                image.delete();
            }
            image.createNewFile();
            fin = new BufferedInputStream(url.openStream());
            fout = new BufferedOutputStream(new FileOutputStream(image));
            byte[] buf = new byte[4096];

            int n;
            while ( (n = fin.read(buf)) >= 0) {
                fout.write(buf, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeIt(fin, fout);
        }
    }

    protected String toValidIconName(String oldName) {
        if (oldName == null) return null;

        char[] cs = oldName.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cs.length; i++) {
            char c = cs[i];
            boolean isUpper = Character.isUpperCase(c);
            boolean isDigit = Character.isDigit(c);
            char lc = Character.toLowerCase(c);
            char newC = c;
            if (i == 0) {
                if (isDigit) {
                    sb.append("icon_");
                } else if (isUpper) {
                    newC = lc;
                }
            } else {
                if (isUpper) {
                    sb.append("_");
                    newC = lc;
                }
            }
            sb.append(newC);
        }
        return sb.toString();
    }

    // class abbreviation
    protected final class ResultSet extends UnexpandedListArticleResultSet {
    }

// English Wiki
// http://towerofsaviors.wikia.com/wiki/Tower_of_Saviors_Wiki

// Chinese Wiki
// http://zh.tos.wikia.com/wiki/%E7%A5%9E%E9%AD%94%E4%B9%8B%E5%A1%94_%E7%B9%81%E4%B8%AD%E7%B6%AD%E5%9F%BA
}
