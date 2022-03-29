package main.twse;

import java.util.Locale;

public class TWEquity {
    // 有價證券代號及名稱, Security Code & Security Name
    public String name = "";
    public String code = "";

    // 國際證券辨識號碼(ISIN Code), ISIN Code
    public String ISINCode = "";

    // 上市日, Date Listed
    public String listedDate = "";

    // 市場別, Market
    public String market = "";

    // 產業別, Industrial Group
    public String industry = "";

    // CFICode
    public String CFICode = "";

    // 備註, Remarks
    public String remarks = "";

    // for bonds, 債券
    // 到期日, maturity
    public String maturityDate = "";

    // 利率值, interest
    public String interest = "";

    @Override
    public String toString() {
        String s = String.format(Locale.US, "%s %s (%s)", code, name, industry);
        if (isBond()) {
            s += String.format(Locale.US, "%s ~ %s %s", listedDate, maturityDate, interest);
        }
        return s;
    }

    public boolean isBond() {
        return interest.isEmpty() == false && maturityDate.isEmpty() == false;
    }
}
