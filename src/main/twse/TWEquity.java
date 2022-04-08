package main.twse;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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

    private static final Map<String, String> symbol = new HashMap<>();

    static {
        symbol.put("上市", ".TW");
        symbol.put("上櫃", ".TWO");
        symbol.put("興櫃戰略新板", ".TWO");
        symbol.put("興櫃一般板", ".TWO");
    }

    public String getSymbol() {
        String val = "";
        if (symbol.containsKey(market)) {
            val = symbol.get(market);
        }
        return code + val;
    }

    public void trim() {
        name = trim(name);
        code = trim(code);
        market = trim(market);
        CFICode = trim(CFICode);
        remarks = trim(remarks);
        interest = trim(interest);
        industry = trim(industry);
        ISINCode = trim(ISINCode);
        listedDate = trim(listedDate);
        maturityDate = trim(maturityDate);
    }

    private String trim(String s) {
        if (s != null) {
            return s.trim();
        } else {
            return s;
        }
    }

    public boolean isBond() {
        return interest.isEmpty() == false && maturityDate.isEmpty() == false;
    }
}
