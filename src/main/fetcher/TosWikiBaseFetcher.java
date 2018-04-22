package main.fetcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.card.IconInfo;
import main.card.TosGet;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import util.data.Range;
import util.logging.L;
import util.logging.LF;
import util.tool.IOUtil;
import util.tool.TextUtil;
import util.tool.TicTac2;
import wikia.articles.UnexpandedListArticleResultSet;

import java.io.*;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TosWikiBaseFetcher {
    public static final String wikiBaseZh = "http://zh.tos.wikia.com";
    public static final String wikiBaseEn = "http://towerofsaviors.wikia.com";

    public static final String zhApi1 = wikiBaseZh + "/api/v1";
    public static final String enApi1 = wikiBaseEn + "/api/v1";

    protected boolean mFetchAll = false;
    protected TicTac2 clock = new TicTac2();

    protected Gson mGson = new GsonBuilder().setPrettyPrinting().create();
    //= new Gson();

    protected ExecutorService executors = Executors.newCachedThreadPool();
    //= new ThreadPoolExecutor(0, Integer.MAX_VALUE, 30L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());

    public String getAPILink() {
        return "";
    }

    public LF getHttpLF() {
        return null;
    }

    public void onNewCallStart() {}
    public void onNewCallEnded() {}

    protected final boolean hasResult(ResultSet s) {
        return s != null && s.getItems() != null;
    }

    public ResultSet getApiResults() {
        return getApiResults(getAPILink(), getHttpLF());
    }

    public ResultSet getApiResults(final String apiLink, final LF apiLf) {
        if (apiLf == null || TextUtil.isEmpty(apiLink)) return null;

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

    protected IconInfo getIconInfo(String link) {
        Document doc = getDocument(link);
        if (doc == null) {
            return new IconInfo();
        } else {
            return TosGet.me.getIcon(doc);
        }
    }

    protected String downloadImage(String link, String folder, String name) {
        InputStream fin = null;
        OutputStream fout = null;
        name = toValidIconName(name);
        String ok = name;
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
            ok = "X_X Failed";
            L.log("%s = %s", ok, link);
        } finally {
            IOUtil.closeIt(fin, fout);
        }

        return ok;
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

    public Document getDocument(String link) {
        return getDocument(link, false);
    }

    public Document getDocument(String link, boolean logTime) {
        // Step 1: Get the xml node from link by Jsoup
        Document doc = null;
        TicTac2 ts = new TicTac2();
        ts.setLog(logTime);
        ts.tic();
        try {
            doc = Jsoup.connect(link).timeout(40_000).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ts.tac("JSoup OK " + link);
        return doc;
    }

    // class abbreviation
    protected final class ResultSet extends UnexpandedListArticleResultSet {
    }

// Reference
// http://towerofsaviors.wikia.com/wiki/User:JoetjeF
// http://towerofsaviors.wikia.com/wiki/User:JoetjeF/Game_Data_Extraction

// English Wiki
// http://towerofsaviors.wikia.com/wiki/Tower_of_Saviors_Wiki

// Chinese Wiki
// http://zh.tos.wikia.com/wiki/%E7%A5%9E%E9%AD%94%E4%B9%8B%E5%A1%94_%E7%B9%81%E4%B8%AD%E7%B6%AD%E5%9F%BA
}
