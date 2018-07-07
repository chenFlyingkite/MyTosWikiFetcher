package main.fetcher;

import main.card.TableInfo;
import main.card.TosGet;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import util.logging.LF;

import java.util.List;

public class TosWikiSummonerLevelFetcher extends TosWikiBaseFetcher {
    private TosWikiSummonerLevelFetcher() {}
    public static final TosWikiSummonerLevelFetcher me = new TosWikiSummonerLevelFetcher();

    private static final String enTosPage = "http://towerofsaviors.wikia.com/wiki/Summoner_Levels";
    private static final LF mLf = new LF("mySummonerLevel");

    @Override
    public void run() {
        LF lf = mLf;

        lf.getFile().open(false);

        int n, m;
        StringBuilder sb;
        List<String> items;
        TableInfo table = getTable();
        if (table != null) {
            // 0. Print Chinese Headers
            lf.log("private static final String[] headerZh = {\"等級\", \"升到下級\", \"累計經驗\", \"體力上限\", \"隊伍空間\", \"隊伍數\"};");

            // 1. Print Headers
            sb = new StringBuilder();
            sb.append("private static final String[] header = {");
            items = table.getHeaders();
            n = items.size();
            for (int i = 0; i < n; i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append("\"").append(items.get(i)).append("\"");
            }
            sb.append("};");

            lf.log("%s", sb.toString());

            // 2. Print items
            sb = new StringBuilder();
            sb.append("private static final long[][] table = {\n");
            items = table.getCells();
            m = items.size();
            int[] align = {4, 10, 12, 3, 3, 2};
            boolean useAlign = align.length == n;
            for (int i = 0; i < m; i++) {
                int mod = i % n;
                if (mod == 0) { // Row starts& 1st element
                    sb.append("    {");
                }
                if (mod != 0) { // 2nd ~ last elements
                    sb.append(", ");
                }
                int val = Integer.parseInt(items.get(i).replace(",",""));
                if (useAlign) {
                    sb.append(String.format("%" + align[mod] + "d", val));
                } else {
                    sb.append(val);
                }
                if (mod == n-1) {
                    sb.append("}");
                    if (i != m-1) {
                        sb.append(",\n");
                    }
                }
            }
            sb.append("};");
            lf.setLogToL(false);
            lf.log("%s", sb.toString());
            lf.setLogToL(true);
        }
        lf.getFile().close();
    }

    private TableInfo getTable() {
        Document doc = getDocument(enTosPage);
        if (doc == null) return null;

        Elements tables = doc.getElementsByClass("wikitable");
        return TosGet.me.getSummonerTable(0, tables);
    }

// English Wiki:
// http://towerofsaviors.wikia.com/wiki/Summoner_Levels

// 召喚師等級資訊列表
// http://zh.tos.wikia.com/wiki/%E5%8F%AC%E5%96%9A%E5%B8%AB%E7%AD%89%E7%B4%9A%E8%B3%87%E8%A8%8A%E5%88%97%E8%A1%A8
}
