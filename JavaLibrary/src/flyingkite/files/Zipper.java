package flyingkite.files;

import flyingkite.log.L;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Easy class for unzipping *.zip file
 */
public final class Zipper {
    private static final String TAG = Zipper.class.getSimpleName();

    public interface ZipListener {
        //default void onException() {}
        default void onStart() {}
        default void onZipEntry(ZipEntry ze) {}
        default void onComplete() {}
    }
    public static ZipListener listener = null;

    /**
     * Open srcFile as {@link FileInputStream} and calls {@link #unzip(File, InputStream)}
     * @see #unzip(File, InputStream)
     */
    public static File unzip(File dstFolder, File srcFile) {
        try {
            return _unzip(dstFolder, new FileInputStream(srcFile));
        } catch (IOException e) { // or FileNotFoundException
            //Log.e(TAG, e.toString());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Unzip the srcPath into its local directory,
     * unzip("D:\A\b.rar") into "D:\A\b" folder
     */
    public static File unzip(String srcPath) {
        return unzip(new File(srcPath));
    }

    /**
     * Also see {@link #unzip(String)}
     */
    public static File unzip(File srcFile) {
        try {
            String name = srcFile.getName();
            name = name.substring(0, name.lastIndexOf('.'));
            File dstFolder = new File(srcFile.getParentFile(), name);
            return _unzip(dstFolder, new FileInputStream(srcFile));
        } catch (IOException e) { // or FileNotFoundException
            //Log.e(TAG, e.toString());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Unzip input stream into destination folder (dstFolder), and create folder if
     */
    public static File unzip(File dstFolder, final InputStream fis) {
        try {
            return _unzip(dstFolder, fis);
        } catch (IOException e) { // or FileNotFoundException
            //Log.e(TAG, e.toString());
            e.printStackTrace();
            return null;
        }
    }

    // NonNull dstFolder
    // NonNull fis
    private static File _unzip(File dstFolder, InputStream fis) throws IOException {
        dstFolder.mkdirs();
        // Create ZipInputStream from InputStream
        //ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis)); // or this?
        ZipInputStream zis = new ZipInputStream(fis);
        try {
            ZipEntry ze;
            if (listener != null) {
                listener.onStart();
            }
            while ((ze = zis.getNextEntry()) != null) {
                if (listener != null) {
                    listener.onZipEntry(ze);
                }
                String filename = ze.getName();

                File dstFile = new File(dstFolder, filename);
                //Log.i(TAG, "Extracting file: " + dstFile.getAbsolutePath());
                L.log("Extracting file: " + dstFile.getAbsolutePath());

                // Zip Path Traversal Vulnerability Checking (https://support.google.com/faqs/answer/9294009)
                String canonicalPath = dstFile.getCanonicalPath();
                if (!canonicalPath.startsWith(dstFolder.getCanonicalPath())) {
                    throw new SecurityException("Find a Zip path traversal vulnerability");
                }

                // Handle for cases that ZipInputStream's entry reaches file and then file's parent folder
                // Like "/a/b" and then next is "/a"
                dstFile.getParentFile().mkdirs();
                if (ze.isDirectory()) {
                    //continue;
                    dstFile.mkdirs();
                } else {
                    // normal file
                    // Read from zis and flush to dstFile
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dstFile));

                    byte[] buf = new byte[8192]; // 8192 = default buffer size of BufferedOutputStream & BufferedInputStream
                    int read;
                    while ((read = zis.read(buf)) != -1) {
                        bos.write(buf, 0, read);
                    }
                    bos.flush();
                    bos.close();
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
            return null;
        } finally {
            zis.closeEntry();
            if (listener != null) {
                listener.onComplete();
            }
        }

        return dstFolder;
    }
}
