package flyingkite.files;

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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

public class FileUtil {

    public static boolean isGone(File f) {
        if (f == null || !f.exists()) {
            L.log("gone");
        }
        return f == null || !f.exists();
    }

    public static boolean isGone(String s) {
        return s == null || isGone(new File(s));
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
            if (inner != null && inner.length > 0) {
                for (File f : inner) {
                    r &= deleteAll(f);
                }
            }
        }
        r &= file.delete();
        return r;
    }

    public static void createNewFile(File f) {
        if (f == null) return;
        if (f.exists() && f.isDirectory()) {
            ensureDelete(f);
        }

        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

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

    public static void copy(String source, String target) {
        if (TextUtil.isEmpty(source) || TextUtil.isEmpty(target)) return;

        FileInputStream fin = null;
        FileOutputStream fout = null;
        try {
            File dst = new File(target);
            dst.getParentFile().mkdirs();
            fin = new FileInputStream(new File(source));
            fout = new FileOutputStream(new File(target));
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
            s = new Scanner(file);//, "UTF-8");
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
            s = new Scanner(file, "UTF-8");

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

    public static void writeToFile(File file, List<String> data, boolean append) {
        if (file == null){
            return;
        }

        FileOutputStream fos = null;
        PrintWriter pw = null;
        try {
            file.createNewFile();
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

    public static List<File> listAllFiles(File src) {
        return listAllFiles(src, null);
    }

    public static List<File> listAllFiles(File src, Projector<File, Boolean> filter) {
        File[] fs = src == null ? null : src.listFiles();
        List<File> pool = new ArrayList<>();
        List<File> scan = new ArrayList<>();
        if (fs != null) {
            Collections.addAll(pool, fs);
        }
        while (!pool.isEmpty()) {
            File g = pool.remove(0);
            File[] gs = g.listFiles();
            if (gs != null) {
                Collections.addAll(pool, gs);
            }
            addWhen(filter, scan, g);
        }
        return scan;
    }

    private static <T> void addWhen(Projector<T, Boolean> accept, List<T> from, T... to) {
        if (to == null) return;

        if (accept == null) {
            Collections.addAll(from, to);
        } else {
            for (T t : to) {
                boolean yes = accept.get(t);
                if (yes) {
                    from.add(t);
                }
            }
        }
    }

    public static boolean createFile(File f) {
        if (f == null) return false;

        f.getParentFile().mkdirs();
        boolean b = false;
        try {
            b = f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
    }

    public static void sortByPath(List<File> all) {
        //TicTac2 t = new TicTac2();
        //t.tic();
        Collections.sort(all, (f1, f2) -> {
            return f1.getAbsolutePath().compareTo(f2.getAbsolutePath());
        });
        //t.tac("sort %d items", all.size());
    }

    public static List<File> listImages(String src) {
        String[] imgs = {".png", ".jpg", ".gif", ".webp", ".webm"};
        List<File> all = listItems(src, imgs);
        int n = all.size();
        L.log("%s images", n);
        for (int i = 0; i < n; i++) {
            File fi = all.get(i);
            Point p = getSize(fi);
            L.log("#%4d = %dx%d", i, p.x, p.y);
            L.log("   %s", fi);
        }
        return all;
    }

    public static String getExtension(File f) {
        if (f == null) return "";
        String s = f.getName();
        int d = s.lastIndexOf('.');
        return s.substring(d + 1);
    }

    // Using ImageIO.read(file) isOK, but slower slightly
    public static Point getSize(File file) {
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

    public static List<File> listItems(String src, String[] exts) {
        TicTac2 t = new TicTac2();
        t.tic();
        List<File> all = FileUtil.listAllFiles(new File(src), (file) -> {
            String name = file.getName().toLowerCase();
            for (String x : exts) {
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
