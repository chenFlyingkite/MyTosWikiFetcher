package main.fetcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import flyingkite.data.Range;
import flyingkite.log.L;
import flyingkite.log.LF;
import flyingkite.tool.IOUtil;
import flyingkite.tool.TextUtil;
import flyingkite.tool.TicTac2;
import main.kt.NameLink;
import main.kt.TosGet;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import wikia.articles.UnexpandedArticle;
import wikia.articles.result.UnexpandedListArticleResultSet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TosWikiBaseFetcher implements Runnable {
    // basic links
    public static final String wikiBaseZh = "https://tos.fandom.com";
    public static final String wikiBaseEn = "http://towerofsaviors.fandom.com";
    public static final String wikiFileZh = "http://zh.tos.wikia.com/wiki/File:";

    public static final String zhApi1 = wikiBaseZh + "/api/v1";
    public static final String enApi1 = wikiBaseEn + "/api/v1";
    public static final String zhWiki = wikiBaseZh + "/zh/wiki/"; // https://tos.fandom.com/zh/wiki


    protected final TicTac2 clock = new TicTac2();

    protected boolean mFetchAll = false;

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


    public ResultSet getApiResultsOffset() {
        return getApiResultsOffset(getAPILink(), getHttpLF());
    }

    public ResultSet getApiResultsOffset(final String apiLink, final LF apiLf) {
        if (apiLf == null || TextUtil.isEmpty(apiLink)) return null;

        // Delete the log file
        apiLf.getFile().delete().open();
        apiLf.setLogToL(!mFetchAll);
        apiLf.setLogToL(true);

        OkHttpClient client = new OkHttpClient();
        Request request = null;
        ResultSet set = null;
        ResultSet merged = new ResultSet();
        try {
            Response response;
            ResponseBody body;
            String apiNextLink = apiLink;

            do {
                // Step 1: Fetch all links from Wikia API
                request = new Request.Builder().url(apiNextLink).build();
                apiLf.log("Linking %s", apiNextLink);

                onNewCallStart();
                clock.tic();
                response = client.newCall(request).execute();
                clock.tac("response = %s", response);
                apiLf.log("response = %s", response);
                onNewCallEnded();

                body = response.body();
                String s = "";
                if (body != null) {
                    s = body.string();
                }

                clock.tic();
                set = mGson.fromJson(s, ResultSet.class);
                clock.tac("from gson, %s", set);
                apiLf.log("from gson, %s", set);
                String offset = null;
                if (set != null) {
                    offset = set.getOffset();
                    merged.getItems().addAll(set.getItems());
                    if (TextUtil.isEmpty(merged.getBasePath())) {
                        merged.setBasePath(set.getBasePath());
                    }
                }
                if (offset != null) {
                    apiNextLink = apiLink + "&offset=" + offset;
                } else {
                    apiNextLink = null;
                }
            } while (!TextUtil.isEmpty(apiNextLink));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            apiLf.getFile().close();
        }
        apiLf.log("merged = %s, %s", merged.getItems().size(), merged);
        return merged;
    }

    public ResultSet getApiResults() {
        return getApiResults(getAPILink(), getHttpLF());
    }

    public ResultSet getApiResults(final String apiLink, final LF apiLf) {
        if (apiLf == null || TextUtil.isEmpty(apiLink)) return null;

        // Delete the log file
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

    public String getApiBody(final String apiLink, final LF apiLf) {
        if (apiLf == null || TextUtil.isEmpty(apiLink)) return null;

        // Delete the log file
        apiLf.getFile().delete().open();
        apiLf.setLogToL(!mFetchAll);

        OkHttpClient client = new OkHttpClient().newBuilder()
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder().url(apiLink).build();
        String answer = "";
        try {
            Response response;
            ResponseBody body;

            // Step 1: Fetch all links from Wikia API
            apiLf.log("Linking %s", apiLink);

            onNewCallStart();
            clock.tic();
            response = client.newCall(request).execute();
            clock.tac("%s", response);
            apiLf.log("response = %s", response);
            onNewCallEnded();

            clock.tic();
            body = response.body();
            if (body != null) {
                answer = body.string();
            }
            clock.tac("body length = %s", answer.length());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            apiLf.getFile().close();
        }
        return answer;
    }

    protected final Range getRange(ResultSet set, int from, int prefetch) {
        // Step 2: Determine the range of parsing
        int min = 0;
        int max = hasResult(set) ? set.getItems().size() : 0;
        if (!mFetchAll) {
            min = Math.max(min, from);
            max = Math.min(max, from + prefetch);
        }

        return new Range(min, max);
    }

    protected NameLink getIconInfo(String link) {
        Document doc = getDocument(link);
        if (doc == null) {
            return new NameLink();
        } else {
            return TosGet.me.getIcon(doc);
        }
    }

    protected String getPoem(Document doc) {
        Elements poem = doc.getElementsByClass("poem");
        int n = poem == null ? 0 : poem.size();
        if (n > 0) {
            return poem.get(0).html();
        }
        return "";
    }

    protected String downloadImage(String link, String folder, String name) {
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
            L.log("X_X Failed = %s", link);
            return null;
        } finally {
            IOUtil.closeIt(fin, fout);
        }

        return name;
    }

    protected String toValidIconName(String oldName) {
        if (oldName == null) return null;

        char[] cs = oldName.toCharArray();
        // Fix .EXT to .ext
        int dot = oldName.lastIndexOf('.');
        for (int i = dot + 1; i < cs.length; i++) {
            cs[i] = Character.toLowerCase(cs[i]);
        }

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

    public String wikiLink(String src) {
        return wikiLink(src, wikiBaseZh);
    }

    public String wikiLink(String src, String baseWiki){
        // Handle for the link with baseWiki prefix
        if (src.isEmpty()) {
            return src;
        } else if (src.startsWith(baseWiki)) {
            return src;
        } else {
            return baseWiki + src;
        }
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
            if (true) {
                doc = Jsoup.connect(link).timeout(60_000).maxBodySize(0).get();
            } else {
                doc = getByOkHttp(link);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        ts.tac("JSoup OK " + link);
        return doc;
    }

    public Document getByOkHttp(String link, Map<String, String> map) {
        OkHttpClient c = new OkHttpClient();
        HttpUrl.Builder ub = HttpUrl.parse(link).newBuilder();
        if (map != null) {
            for (String key : map.keySet()) {
                String value = map.get(key);
                ub.addQueryParameter(key, value);
            }
        }

        Request r = new Request.Builder().url(ub.build()).build();
        ResponseBody rb = null;
        try {
            rb = c.newCall(r).execute().body();
            if (rb != null) {
                return Jsoup.parse(rb.string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Document getByOkHttp(String link) {
        OkHttpClient c = new OkHttpClient();
        HttpUrl u = HttpUrl.parse(link);

        Request r = new Request.Builder().url(link).build();
        ResponseBody rb = null;
        try {
            rb = c.newCall(r).execute().body();
            if (rb != null) {
                return Jsoup.parse(rb.string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected Runnable runLogToFile(LF lfFile, List<String> list) {
        return new Runnable() {
            @Override
            public void run() {
                LF lf = lfFile;
                // Open log files
                lf.getFile().open(false);
                for (String s : list) {
                    lf.log(s);
                }
                lf.getFile().close();
            }
        };
    }

    protected <T> String writeAsGson(T array, LF lg) {
        // Convert to String
        String msg = mGson.toJson(array, array.getClass());
        // Write to file
        lg.setLogToL(false);
        lg.getFile().open(false);
        lg.log(msg);
        lg.getFile().flush();
        lg.getFile().close();
        return msg;
    }

    // Get the links from node of <li><a><href>
    // Usually for get inner tabs
    protected final List<String> getLiAHref(String page) {
        Document doc = getDocument(page);

        Element main = doc.getElementById("mw-content-text");
        return TosGet.me.getLiAHref(main, wikiBaseZh);
    }

    protected final String tag() {
        return getClass().getSimpleName();
    }

    public <T> void printList(List<T> list, LF lg, String name) {
        lg.log("%s %s", list.size(), name);
        for (int i = 0; i < list.size(); i++) {
            T a = list.get(i);
            lg.log("%s", a);
        }
    }

    protected <T> int[] findAnchors(List<T> list, T[] anchor) {
        int n = list.size();
        int an = anchor.length;
        int[] anchors = new int[an];
        // init as -1
        Arrays.fill(anchors, -1);

        for (int i = 0; i < n; i++) {
            T si = list.get(i);
            for (int j = 0; j < an; j++) {
                if (anchors[j] < 0 && anchor[j].equals(si)) {
                    anchors[j] = i;
                }
            }
        }
        return anchors;
    }

    protected List<String> getTestLinks() {
        return new ArrayList<>();
    }

    protected boolean useTest() {
        return getTestLinks().size() > 0;
    }

    protected Source getSource(int from, int prefetch) {
        // Get the range sets
        List<String> tests = getTestLinks();
        boolean useTest = useTest();
        Source src = new Source();
        if (useTest) {
            src.results = new ResultSet();
            src.range = new Range(0, tests.size());
            src.links = tests;
        } else {
            src.results = getApiResults();
            if (!hasResult(src.results)) return null;

            src.range = getRange(src.results, from, prefetch);
        }
        return src;
    }

    protected String getSourceLinkAt(Source s, int i) {
        ResultSet set = s.results;
        String link;
        if (useTest()) {
            link = s.links.get(i);
        } else {
            UnexpandedArticle a = set.getItems().get(i);
            link = set.getBasePath() + "" + a.getUrl();
        }
        return link;
    }

    public String decodeUrl(String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return s;
    }

    @Override
    public void run() {

    }

    protected class Source {
        public ResultSet results;
        public Range range;
        public List<String> links = new ArrayList<>();
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
