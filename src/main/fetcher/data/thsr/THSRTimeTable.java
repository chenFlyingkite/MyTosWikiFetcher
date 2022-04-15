package main.fetcher.data.thsr;

import flyingkite.files.FileUtil;
import flyingkite.log.L;

import java.io.File;
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
        THSRTrain tr = new THSRTrain();
        for (int i = 0; i < lines.size(); i++) {
            String si = lines.get(i);
            L.log("#%3d : %s", i, si);
            tr.parse(si);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(name);
        sb.append(", ").append(startDate).append(" ~ ").append(endDate);
        if (northbound != null) {
            sb.append(",").append(northbound.length).append(" north");
        }
        if (southbound != null) {
            sb.append(",").append(southbound.length).append(" south");
        }
        return sb.toString();
    }
}
