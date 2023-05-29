package main.fetcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import flyingkite.log.FileOutput;
import flyingkite.log.LF;
import flyingkite.tool.GsonUtil;
import flyingkite.tool.TicTac2;
import okhttp3.OkHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

public class FetcherUtil {
    public static boolean pretty = true;

    public static void overwriteFileJsonPrettyPrinting(Object obj, LF lf) {
        lf.getFile().open(false);
        GsonBuilder gb = new GsonBuilder();
        if (pretty) {
            gb.setPrettyPrinting();
        }
        Gson gson = gb.create();
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

    public static Document getDocJsoup(String link) {
        // Step 1: Get the xml node from link by Jsoup
        Document doc = null;
        TicTac2 ts = new TicTac2();
        ts.tic();
        try {
            doc = Jsoup.connect(link).timeout(60_000).maxBodySize(0).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ts.tac("JSoup OK " + link);
        return doc;
    }


    public static class MyX509TrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    public static class MyHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    }

    public static void setupSSL(OkHttpClient.Builder builder) {
        try {
            X509TrustManager[] tm = {new MyX509TrustManager()};
            HostnameVerifier hm = new MyHostnameVerifier();
            SSLContext context = SSLContext.getInstance("SSL");
            context.init(null, tm, new SecureRandom());

            builder.sslSocketFactory(context.getSocketFactory(), tm[0]);
            builder.hostnameVerifier(hm);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

}
