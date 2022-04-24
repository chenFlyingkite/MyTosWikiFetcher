package main.fetcher.data.thsr;

// Each 車次
// 10:35 _____> 12:45, 0627, Car 10-12
//       02:10

import flyingkite.log.L;
import flyingkite.tool.TextUtil;

import java.util.Arrays;

// Northbound or southbound is not included in this information,
// Since the stations and direction are from owner class
public class THSRTrain {
    // Basic information
    // Train No. %04d
    //@SerializedName("number")
    public String number;

    //@SerializedName("time")
    public String[] time;

    //@SerializedName("operationDays")
    public String operationDays;

    // additional flags for program
    // time[depart] ~ time[arrive], 0 <= depart < arrive < time.length
    // inferred fields
    public transient int depart = -1;
    public transient int arrive = -1;

    public void parse(String s) {
        String[] ss = s.split("\t");
        L.log("source = %s", s);
        L.log("ss = %s", Arrays.toString(ss));
        // 車 次	行駛日	南港	台北	板橋	桃園	新竹	苗栗	台中	彰化	雲林	嘉義	台南	左營
        // 583								06:25	06:37	06:47	06:59	07:17	07:30
        // ...
        // 1607	六	07:10	07:21	07:29	07:43	07:56	─	08:25	─	─	08:50	09:07	09:20
        int rowN = ss.length;
        number = get(ss, 0);
        operationDays = get(ss, 1);
        if (rowN > 2) {
            time = new String[rowN - 2];
            for (int i = 0; i < time.length; i++) {
                String si = get(ss, i+2);
                if (TextUtil.isEmpty(si)) {
                } else {
                    if (depart < 0) {
                        depart = i;
                    }
                    arrive = i;
                }
                time[i] = si;
            }
        }
    }

    private String get(String[] a, int at) {
        if (a != null) {
            if (0 <= at && at < a.length) {
                return a[at];
            }
        }
        return null;
    }

    public void fillFlags() {
        if (time != null) {
            for (int i = 0; i < time.length; i++) {
                String si = time[i];
                if (TextUtil.isEmpty(si)) {
                } else {
                    if (depart < 0) {
                        depart = i;
                    }
                    arrive = i;
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(number);

        if (operationDays != null) {
            sb.append(", ").append(operationDays);
        }
        sb.append(", d~a=").append(depart).append("~").append(arrive);
        sb.append(", ").append(Arrays.toString(time));

        return sb.toString();
    }
}
