package flyingkite.files;

import flyingkite.collection.ArraysUtil;
import flyingkite.functional.Projector;
import flyingkite.log.L;
import flyingkite.tool.IOUtil;
import flyingkite.tool.TextUtil;
import flyingkite.tool.TicTac2;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

public class FileUtil {

    /**
     * Return true if file exists, false otherwise
     * @see #isGone(String)
     */
    public static boolean isGone(File f) {
        return f == null || !f.exists();
    }

    /**
     * @see #isGone(File)
     */
    public static boolean isGone(String s) {
        return TextUtil.isEmpty(s) || isGone(new File(s));
    }

    /**
     * Delete the file by renaming to another one and call delete.
     * <p>
     * When file is created and deleted and then again created, it will throw
     * FileNotFoundException : open failed: EBUSY (Device or resource busy)
     * So we rename it to safe delete the file.
     * </p><p>
     * Also see
     * <a href="
     * https://stackoverflow.com/questions/11539657/open-failed-ebusy-device-or-resource-busy
     * ">open-failed-ebusy-device-or-resource-busy</a>
     * </p>
     *
     * @param from The file or directory to be safely deleted (so it can be created again in code)
     * @return {@link File#delete()}
     * @see File#delete()
     * */
    public static boolean ensureDelete(File from) {
        if (isGone(from)) return true;

        // When file is created and deleted and then again created, it will throw
        // FileNotFoundException : open failed: EBUSY (Device or resource busy)
        // So we rename it to safe delete the file
        // See https://stackoverflow.com/questions/11539657/open-failed-ebusy-device-or-resource-busy
        final File to = new File(from.getAbsolutePath() + System.currentTimeMillis());
        from.renameTo(to);
        boolean r = deleteAll(to);
        return r;
    }


    /**
     * Delete file, or folder with all files within it.
     *
     * @param file The file or folder that caller want to delete.
     * @return <code>true</code> all files were deleted successfully.
     *         Otherwise, <code>false</code> some files cannot be deleted.
     */
    private static boolean deleteAll(File file) {
        if (isGone(file)) return true;

        boolean r = true;
        if (file.isDirectory()) {
            File[] inner = file.listFiles();
            if (inner != null) {
                for (File f : inner) {
                    r &= deleteAll(f);
                }
            }
        }
        r &= file.delete();
        return r;
    }

    /**
     * {@link #createNewFile(File, boolean)} with (_, true)
     * @see #createNewFile(File, boolean)
     */
    public static void createNewFile(File f) {
        createNewFile(f, true);
    }

    /**
     * Create file named f, delete if it already exists
     */
    public static void createNewFile(File f, boolean deleteIfFolder) {
        if (f == null) return;
        if (deleteIfFolder) {
            deleteIfDirectory(f);
        }

        if (!f.exists()) {
            createFile(f);
        }
    }

    /**
     * Delete file if it is a directory
     */
    public static void deleteIfDirectory(File f) {
        if (f == null) return;

        if (f.exists() && f.isDirectory()) {
            ensureDelete(f);
        }
    }

    /**
     * @see File#mkdirs()
     */
    public static boolean mkdirs(File f) {
        if (f == null) return false;

        if (f.exists() && f.isFile()) {
            ensureDelete(f);
        }
        return f.mkdirs();
    }

    /**
     * Return true if file exists as directory, not file
     */
    public static boolean isExistDir(File f) {
        return !isGone(f) && f.isDirectory();
    }

    /**
     * Return true if file exists as file, not directory
     */
    public static boolean isExistFile(File f) {
        return !isGone(f) && f.isFile();
    }

    /**
     * Create file named f, and will not delete if f exists as a folder
     * returns {@link File#createNewFile()}
     */
    public static boolean createFile(File f) {
        if (f == null) return false;

        File g = f.getParentFile();
        if (g != null) {
            g.mkdirs();
        }
        boolean b = false;
        try {
            b = f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
    }

    /**
     * Copy input stream data into output stream
     * @param is Input stream
     * @param fos Output stream
     */
    public static void copy(InputStream is, OutputStream fos) {
        if (is == null || fos == null) return;

        try {
            // Read stream and write to file
            int read;
            byte[] buffer = new byte[65536];
            while ((read = is.read(buffer)) != -1) {
                fos.write(buffer, 0, read);
            }
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeIt(is, fos);
        }
    }

    /**
     * Copy source into target, where source and target are file absolute paths
     * @param source Absolute path file for source
     * @param target Absolute path file for target
     */
    public static void copy(String source, String target) {
        if (TextUtil.isEmpty(source) || TextUtil.isEmpty(target)) return;

        FileInputStream fin = null;
        FileOutputStream fout = null;
        try {
            File dst = new File(target);
            dst.getParentFile().mkdirs();
            fin = new FileInputStream(source);
            fout = new FileOutputStream(target);
            copy(fin, fout);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeIt(fin, fout);
        }
    }

    public static List<String> readFromFile(String name) {
        return readFromFile(new File(name));
    }

    // suggest to use this method
    public static List<String> readAllLines(String path) {
        return readAllLines(new File(path));
    }

    // suggest to use this method
    public static List<String> readAllLines(File file) {
        List<String> ans = new ArrayList<>();
        try {
            ans = Files.readAllLines(Path.of(file.getAbsolutePath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ans;
    }

    public static List<String> readFromFile(File file) {
        if (isGone(file)){
            return Collections.emptyList();
        }

        List<String> all = new ArrayList<>();
        Scanner s = null;
        try {
            s = new Scanner(file);
            while (s.hasNextLine()) {
                all.add(s.nextLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeIt(s);
        }
        return all;
    }

    // https://stackoverflow.com/questions/4716503/reading-a-plain-text-file-in-java
    // failed to read text
    public static String readFileAsString(File file) {
        if (isGone(file)){
            return "";
        }

        StringBuilder sb = new StringBuilder();
        Scanner s = null;
        try {
            s = new Scanner(file);

            while (s.hasNextLine()) {
                sb.append(s.nextLine()).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeIt(s);
        }
        return sb.toString();

    }

    /**
     * Write the data into file, data strings are treated as ordered each input line
     * @param file Destination file
     * @param data Data for written into file, ordered by each line
     * @param append append to file or overwrite content
     */
    public static void writeToFile(File file, List<String> data, boolean append) {
        if (file == null) return;

        FileOutputStream fos = null;
        PrintWriter pw = null;
        try {
            createFile(file);
            fos = new FileOutputStream(file, append);
            pw = new PrintWriter(fos);
            for (String s : data) {
                pw.println(s);
            }
            pw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeIt(fos, pw);
        }
    }

    /**
     * Return files where the provided file list items matches condition
     * @param list Source file list
     * @param proj Selection condition
     */
    public static List<File> findFile(List<File> list, Projector<File, Boolean> proj) {
        List<File> ans = new ArrayList<>();
        if (list != null) {
            for (File f : list) {
                boolean a = proj.get(f);
                if (a) {
                    ans.add(f);
                }
            }
        }
        return ans;
    }

    /**
     * @see #listAllFiles(File)
     */
    public static List<File> listAllFiles(String src) {
        return listAllFiles(new File(src));
    }

    /**
     * @see #listAllFiles(File, Projector)
     */
    public static List<File> listAllFiles(File src) {
        return listAllFiles(src, null);
    }

    /**
     * @see #listAllFiles(File, Projector)
     */
    public static List<File> listAllFiles(String path, Projector<File, Boolean> filter) {
        return listAllFiles(new File(path), filter);
    }

    /**
     * Lists all internal files (including files inside every depth directories) and return by its level order results with each level are sorted
     * @param src Root folder
     * @param filter Selection condition
     */
    public static List<File> listAllFiles(File src, Projector<File, Boolean> filter) {
        if (src == null) return null;

        File[] fs = src.listFiles();
        if (fs == null) return null;

        List<File> pool = new ArrayList<>();
        List<File> scan = new ArrayList<>();
        // sort by name
        Arrays.sort(fs);
        // start BFS
        addWhen(filter, pool, fs);
        while (!pool.isEmpty()) {
            File g = pool.remove(0);
            File[] gs = g.listFiles();
            if (gs != null) {
                Arrays.sort(gs);
            }
            addWhen(filter, pool, gs);
            addWhen(filter, scan, g);
        }
        return scan;
    }

    /**
     * Add sources into destination when item matches accept condition
     * @param <T> List type
     * @param accept Accept condition to add item
     * @param src Source list
     * @param dst Target list
     */
    @SafeVarargs
    private static <T> void addWhen(Projector<T, Boolean> accept, List<T> dst, T... src) {
        if (src == null) return;

        if (accept == null) {
            Collections.addAll(dst, src);
        } else {
            for (T t : src) {
                boolean yes = accept.get(t);
                if (yes) {
                    dst.add(t);
                }
            }
        }
    }

    /**
     * Sort list by its absolute path
     */
    public static void sortByPath(List<File> all) {
        //TicTac2 t = new TicTac2();
        //t.tic();
        Collections.sort(all, (f1, f2) -> {
            return f1.getAbsolutePath().compareTo(f2.getAbsolutePath());
        });
        //t.tac("sort %d items", all.size());
    }

    /**
     * Return image file list where src folder has files matches extension of
     * ".png", ".jpg", ".gif", ".webp", ".webm"
     * @param src Source folder path to list
     */
    public static List<File> listImages(String src) {
        String[] imgs = {".png", ".jpg", ".gif", ".webp", ".webm"};
        List<File> all = listItems(src, imgs);
        int n = all.size();
        L.log("%s images", n);
        for (int i = 0; i < n; i++) {
            File fi = all.get(i);
            Point p = getImageSize(fi);
            L.log("#%4d = %dx%d", i, p.x, p.y);
            L.log("   %s", fi);
        }
        return all;
    }

    /**
     * Return file's name after last dot (.)
     */
    public static String getExtension(File f) {
        if (f == null) return "";
        String s = f.getName();
        int d = s.lastIndexOf('.');
        return s.substring(d + 1);
    }

    /**
     * Return file's name before last dot (.)
     */
    public static String getNameBeforeExtension(File f) {
        if (f == null) return "";
        String s = f.getName();
        int d = s.lastIndexOf('.');
        return s.substring(0, d);
    }

    // Using ImageIO.read(file) isOK, but slower slightly
    public static Point getImageSize(File file) {
        String ext = getExtension(file);
        Iterator<ImageReader> readers = ImageIO.getImageReadersBySuffix(ext);

        Point p = new Point();

        while (readers.hasNext()) {
            ImageReader r = readers.next();
            try {
                ImageInputStream iis = new FileImageInputStream(file);
                r.setInput(iis);
                int at = r.getMinIndex();
                p.x = r.getWidth(at);
                p.y = r.getHeight(at);
                return p;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                r.dispose();
            }
        }
        return p;
    }

    /**
     * Return file lists where source's internal files match extension is one of provided
     * @param src Source folder to find wanted extension
     * @param ext Provided extension
     */
    public static List<File> listFilesExt(File src, String[] ext) {
        TicTac2 t = new TicTac2();
        t.tic();
        List<File> all = new ArrayList<>();
        File[] found = src.listFiles((file) -> {
            if (ArraysUtil.isEmpty(ext)) return true;

            String path = file.getAbsolutePath();
            for (int i = 0; i < ext.length; i++) {
                if (path.endsWith(ext[i])) {
                    return true;
                }
            }
            return false;
        });
        int size = -1;
        if (found != null) {
            size = found.length;
            for (int i = 0; i < found.length; i++) {
                L.log("#%s : %s", i, found[i]);
                all.add(found[i]);
            }
        }
        t.tac("found %d items in %s", size, src);

        //sortByPath(all);
        return all;
    }

    /**
     * @see #listFilesExt(File, String[])
     */
    public static List<File> listItems(String src, String[] ext) {
        TicTac2 t = new TicTac2();
        t.tic();
        List<File> all = FileUtil.listAllFiles(new File(src), (file) -> {
            if (ArraysUtil.isEmpty(ext)) return true;

            String name = file.getName().toLowerCase();
            for (String x : ext) {
                if (name.endsWith(x)) {
                    return true;
                }
            }
            return false;
        });
        int size = all.size();
        t.tac("found %d items in %s", size, src);

        sortByPath(all);
        return all;
    }
}
