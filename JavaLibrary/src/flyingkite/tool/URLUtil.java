package flyingkite.tool;

import flyingkite.log.L;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class URLUtil {
    // Download {link} to in {folder/name}
    // E.g. link = "https://liveahero-wiki.github.io/cdn/Sprite/ui_frame_h_base_shadow.png"
    // file = "D:\\LiveAHero\\image"
    // name = "ui_frame_h_base_shadow.png"
    // you will see file appears in {folder/name}, i.e. "D:\\LiveAHero\\image\\ui_frame_h_base_shadow.png"
    public static String downloadFile(String link, String folder) {
        String name = TextUtil.after(link, "/", -1);
        return downloadFile(link, folder, name);
    }

    public static String downloadFile(String link, String folder, String name) {
        InputStream fin = null;
        OutputStream fout = null;

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
}
