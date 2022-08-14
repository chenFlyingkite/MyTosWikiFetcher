package main.fetcher.exchange;

// Listed from Bank of Taiwan
public enum CurrencyBOT {
    USD("美金", "USD"),
    HKD("港幣", "HKD"),
    GBP("英鎊", "GBP"),
    AUD("澳幣", "AUD"),
    CAD("加拿大幣", "CAD"),
    SGD("新加坡幣", "SGD"),
    CHF("瑞士法郎", "CHF"),
    JPY("日圓", "JPY"),
    ZAR("南非幣", "ZAR"),
    SEK("瑞典幣", "SEK"),
    NZD("紐西蘭幣", "NZD"),
    THB("泰幣", "THB"),
    PHP("菲國比索", "PHP"),
    IDR("印尼幣", "IDR"),
    EUR("歐元", "EUR"),
    KRW("韓元", "KRW"),
    VND("越南盾", "VND"),
    MYR("馬來幣", "MYR"),
    CNY("人民幣", "CNY"),
    ;

    public final String name;
    public final String id;
    CurrencyBOT(String _name, String _id) {
        name = _name;
        id = _id;
    }
}
