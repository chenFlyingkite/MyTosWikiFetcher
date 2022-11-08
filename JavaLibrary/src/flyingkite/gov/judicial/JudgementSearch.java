package flyingkite.gov.judicial;

import flyingkite.files.FileUtil;
import flyingkite.log.L;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

// Names is in /gov/法庭及訴訟程序常用法律詞彙-英語版.pdf
public class JudgementSearch {

    public static void run() {
        JudgementSearch me = new JudgementSearch();

        File root = new File("thsrRes", "/陳秋白/");
        List<File> all = listDom(root);
        for (int i = 0; i < all.size(); i++) {
            File fi = all.get(i);
            me.read(fi);
        }
    }

    private static List<File> listDom(File root) {
        List<File> htmls;
        //htmls = FileUtil.listFilesExt(root, new String[]{".html"});
        htmls = FileUtil.listItems(root.getAbsolutePath(), new String[]{"qryresultlst.html"});
        for (int i = 0; i < htmls.size(); i++) {
            L.log("%s : %s", i, htmls.get(i));
        }
        return htmls;
    }

    public void read(File xml) {
        String all = FileUtil.readFileAsString(xml);
        Document doc = Jsoup.parse(all);
        Element jud = doc.getElementById("jud");
        Elements trs = jud.getElementsByTag("tr");
        int n = (trs.size() - 1) / 2;
        L.log("%s judges in %s", n, xml);
        List<Judgement> juds = new ArrayList<>();
        for (int i = 1; i < trs.size(); i++) {
            Element tri = trs.get(i);
            Elements tds = tri.getElementsByTag("td");
            Judgement jj = new Judgement();

            jj.title = tds.get(1).text();
            jj.contextLink = tds.get(1).child(0).attr("href");
            jj.date = tds.get(2).text();
            jj.reason = tds.get(3).text();
            juds.add(jj);
            i++;
            Element di = trs.get(i);
            jj.desc = di.text();
            L.log("#%2d : %s", i, jj);
        }
    }
}
