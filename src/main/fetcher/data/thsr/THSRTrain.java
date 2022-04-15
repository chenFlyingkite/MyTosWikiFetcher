package main.fetcher.data.thsr;

// Each 車次
// 10:35 _____> 12:45, 0627, Car 10-12
//       02:10

import flyingkite.log.L;

import java.util.Arrays;

public class THSRTrain {
    // Basic information
    // Train No. %04d
    public String number;

    // stations.length = time.length
    public String[] stations;// no need
    public String[] time;

    public String operationDays;

    // additional flags for program
    // time[depart] ~ time[arrive], 0 <= depart < arrive < time.length
    public int depart;
    public int arrive;
    public boolean isNorthBound;

    public void parse(String s) {
        String[] ss = s.split("\t");
        L.log("source = %s", s);
        L.log("ss = %s", Arrays.toString(ss));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(number);

        if (stations != null) {
            sb.append(", [");
            final String del = ", ";
            for (int i = 0; i < stations.length; i++) {
                if (i > 0) {
                    sb.append(del);
                }
                sb.append(stations[i]).append("(").append(time[i]).append(")");
                //String it = (i == 0) ? ", " : del;
                //sb.append(String.format("%s%s(%s)", it, stations[i], time[i]));
            }
            sb.append("]");
        } else {
            sb.append(", ");
            sb.append(Arrays.toString(time));
        }

        if (operationDays != null) {
            sb.append(", ").append(operationDays);
        }
        return sb.toString();
    }
}
