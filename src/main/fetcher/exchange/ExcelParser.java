package main.fetcher.exchange;

import flyingkite.files.FileUtil;
import flyingkite.log.Loggable;
import flyingkite.tool.TicTac2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ExcelParser implements Loggable {
    private static final TicTac2 clock = new TicTac2();
    public static void main(String[] args) {
        ExcelParser p = new ExcelParser();
        String root = "D:\\Github\\MyTosWikiFetcher\\foreignCurrency";
        File f = new File(root + "\\data");
        File[] all = f.listFiles();

        clock.tic();
        for (int i = 0; i < all.length; i++) {
            //clock.tic();
            File src = all[i];
            File dst = new File(root + "/eval", src.getName());
            p.evalExpansion(src, dst);
            //clock.tac("#%s : OK %s", i, src);
        }
        clock.tac("all files done in %s", f);
    }

    // fill in the rate ratio of (sell - buy)/ buy
    public void evalExpansion(File src, File dst) {
        List<String> all = FileUtil.readAllLines(src);
        List<String> res = new ArrayList<>();

        double sumX = 0;
        double sumX2 = 0;
        int cnt = 0;
        FileUtil.createNewFile(dst);
        for (int i = 0; i < all.size(); i++) {
            String line = all.get(i);
            String out = line;
            if (i == 0) {
                out += ",";
            } else if (i == 1) {
                // Coefficient of thermal expansion
                out += ",Expand";
            } else {
                String[] si = line.split(",");
                double buy = Double.parseDouble(si[1]);
                double sell = Double.parseDouble(si[2]);
                double rate = 0;
                if (buy > 0) {
                    rate = (sell - buy) / buy;
                }
                out = line + _fmt(",%.4f", rate);
                // statistics
                sumX += rate;
                sumX2 += rate*rate;
                cnt++;
            }
            res.add(out);
        }
        double avg = sumX / cnt;
        double std = sumX2 - cnt * avg * avg;

        // print to file
        FileUtil.writeToFile(dst, res, false);
        log("avg = %s%%, std = %s, %s", avg * 100, std, src.getName());
    }

    /*
avg = 0.9293365919717694%, std = 2.7359860862330443E-5, 人民幣.csv
avg = 1.0387637951798148%, std = 6.511693111690003E-4, 加拿大幣.csv
avg = 4.274366390627419%, std = 0.016339539512712165, 南非幣.csv
avg = 0.9789740746041%, std = 0.0014496338330045044, 新加坡幣.csv
avg = 1.350135668777431%, std = 8.10402448055858E-4, 日幣.csv
avg = 1.0648746227053276%, std = 1.0810947549322325E-4, 歐元.csv
avg = 1.3512357894163873%, std = 1.863714707959485E-4, 港幣.csv
avg = 1.0692681272889182%, std = 1.2130009572280942E-4, 澳幣.csv
avg = 1.9067623501761177%, std = 5.377979525489351E-4, 瑞典幣.csv
avg = 1.033719962008265%, std = 0.001496044472963573, 瑞士法郎.csv
avg = 1.2442542128903704%, std = 1.2653966148035156E-4, 紐西蘭幣.csv
avg = 0.3453821061587271%, std = 1.3050910883395314E-5, 美金.csv
avg = 1.057841227323445%, std = 5.614163547404405E-5, 英鎊.csv
[157] : all files done in D:\Github\MyTosWikiFetcher\foreignCurrency\data
Done, Sat Aug 13 22:01:59 CST 2022

Process finished with exit code 0
    */

}
