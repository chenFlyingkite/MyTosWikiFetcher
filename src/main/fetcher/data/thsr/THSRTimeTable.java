package main.fetcher.data.thsr;

import flyingkite.files.FileUtil;
import flyingkite.log.L;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class THSRTimeTable {
    public String name;
    public String startDate;
    public String endDate;

    public THSRTrain[] northbound;
    public THSRTrain[] southbound;

    public void parse(File file) {
        L.log("file = %s, %s", file.exists(), file);
        List<String> lines;
        lines = FileUtil.readAllLines(file);
        L.log("%s lines", lines.size());
        List<THSRTrain> bound = new ArrayList<>();
        THSRTrain tr;
        int direct = 0;
        for (int i = 0; i < lines.size(); i++) {
            String si = lines.get(i);
            L.log("#%3d : %s", i, si);
            if (si.contains("Southbound")) {
                String[] sj = lines.get(i+1).split("\t");
                String[] sk = lines.get(i+2).split("\t");
                fillInStations(true, sj);
                fillInStations(false, sk);
                i += 2;
                fillBound(direct, bound);
                bound.clear();
                direct = -1;
            } else if (si.contains("Northbound")) {
                String[] sj = lines.get(i+1).split("\t");
                String[] sk = lines.get(i+2).split("\t");
                fillInStations(true, sj);
                fillInStations(false, sk);
                i += 2;
                fillBound(direct, bound);
                bound.clear();
                direct = 1;
            } else {
                tr = new THSRTrain();
                tr.parse(si);
                L.log("#%s : %s, %s, %s", i, dirStr(direct), bound(direct, tr), tr);
                bound.add(tr);
            }
        }
        fillBound(direct, bound);
    }

    private String dirStr(int d) {
        if (d > 0) {
            return "北上";
        } else if (d < 0) {
            return "南下";
        }
        return "";
    }

    private void fillInStations(boolean isZh, String[] row) {
        List<String> list = isZh ? THSRStation.allSouthboundStationsZh : THSRStation.allSouthboundStationsEn;
        if (list.isEmpty()) {
            for (int i = 2; i < row.length; i++) {
                list.add(row[i]);
            }
            L.log("zh= %s, list = %s", isZh, list);
        }
    }

    private String bound(int dir, THSRTrain tr) {
        String src = "";
        String dst = "";
        List<String> list = THSRStation.allSouthboundStationsZh;
        final int n = list.size() - 1;
        if (dir < 0) {
            src = list.get(tr.depart);//southBoundStations[tr.depart];
            dst = list.get(tr.arrive);//southBoundStations[tr.arrive];
        } else if (dir > 0) {
            src = list.get(n - tr.depart);//northBoundStations[tr.depart];
            dst = list.get(n - tr.arrive);//northBoundStations[tr.arrive];
        }
        return String.format("%s ~ %s", src, dst);
    }

    private void fillBound(int dir, List<THSRTrain> bound) {
        if (dir < 0) {
            southbound = bound.toArray(new THSRTrain[1]);
        } else if (dir > 0) {
            northbound = bound.toArray(new THSRTrain[1]);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(name);
        sb.append(", ").append(startDate).append(" ~ ").append(endDate);
        if (northbound != null) {
            sb.append(", ").append(northbound.length).append(" north");
        }
        if (southbound != null) {
            sb.append(", ").append(southbound.length).append(" south");
        }
        return sb.toString();
    }
}
