package main.fetcher;

import flyingkite.files.FileUtil;
import flyingkite.log.L;
import flyingkite.log.LF;
import main.kt.SummonInfo;
import main.kt.TableInfo;

import java.util.Arrays;
import java.util.List;

public class TosWikiSummonerLevelFetcher extends TosWikiBaseFetcher {
    public static final TosWikiSummonerLevelFetcher me = new TosWikiSummonerLevelFetcher();

    // https://forum.gamer.com.tw/C.php?bsn=23805&snA=664033
    private static final String enPage = "http://towerofsaviors.wikia.com/wiki/Summoner_Levels";
    // 召喚師等級資訊列表
    private static final String zhPage = "http://zh.tos.wikia.com/wiki/%E5%8F%AC%E5%96%9A%E5%B8%AB%E7%AD%89%E7%B4%9A%E8%B3%87%E8%A8%8A%E5%88%97%E8%A1%A8";
    private static final LF mLf = new LF("mySummonerLevel");

    @Override
    public void run() {
        LF lf = mLf;

        lf.getFile().open(false);

        StringBuilder sb;
        SummonInfo table = getTableByFill();
        if (table != null) {
            // 0. Print Chinese Headers
            lf.log("public static final String[] headerZh = {\"等級\", \"升到下級\", \"累計經驗\", \"體力上限\", \"隊伍空間\", \"隊伍數\"};");

            // 1. Print items
            int[] align = {5, 12, 3, 3, 2};
            sb = new StringBuilder();
            sb.append("public static final long[][] table = {\n");
            List<List<String>> data = table.getCells();
            int n = data.size();
            for (int i = 0; i < n; i++) {
                List<String> row = data.get(i);
                int m = row.size();
                StringBuilder z = new StringBuilder();
                z.append("{");
                for (int j = 0; j < m; j++) {
                    long x = Long.parseLong(row.get(j));
                    String fmt = " %" + align[j] + "d";
                    if (j < m - 1) {
                        fmt += ",";
                    }
                    z.append(String.format(fmt, x));
                }
                z.append("}");
                if (i < n - 1) {
                    z.append(",\n");
                } else {
                    z.append("};");
                }
                sb.append(z);
            }
            lf.setLogToL(true);
            lf.log("%s", sb.toString());
            lf.setLogToL(true);
        }
        lf.getFile().close();
    }

    private TableInfo getTable() {
        return null;
//        Document doc = getDocument(enPage);
//        if (doc == null) return null;

//        Elements tables = doc.getElementsByClass("wikitable");
//
//        // Need fix on :After LV 300, team slot = 20 + (lv-300) / 20
//        return TosGet.me.getSummonerTable(0, tables);
    }

    // https://forum.gamer.com.tw/C.php?bsn=23805&snA=664033
    private SummonInfo getTableByFill() {
        String path = "mySummonerLevel/Level.csv";
        SummonInfo ans = new SummonInfo();
        List<String> all = FileUtil.readFromFile(path);
        if (all.isEmpty()) {
            L.log("Check UTF8 Load empty %s", path);
            return ans;
        }
        for (int i = 0; i < all.size(); i++) {
            String[] ss = all.get(i).split(",");
            List<String> row = Arrays.asList(ss);
            if (i == 0) {
                ans.getHeaders().addAll(row);
            } else {
                ans.getCells().add(row);
            }
        }

        return ans;
    }

// English Wiki:
// http://towerofsaviors.wikia.com/wiki/Summoner_Levels

// 召喚師等級資訊列表
// http://zh.tos.wikia.com/wiki/%E5%8F%AC%E5%96%9A%E5%B8%AB%E7%AD%89%E7%B4%9A%E8%B3%87%E8%A8%8A%E5%88%97%E8%A1%A8
}
