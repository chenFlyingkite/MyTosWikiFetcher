package main.fetcher.hero.card.each;

public enum Heros {
    Akashi("アカシ"),
    Mokdai("モクダイ"),
    Sui("スイ"),
    Ryekie("ライキ"),
    Ryekie2("酔虎のライキ"),
    Crowne("クローネ"),
    Gammei("ガンメイ"),
    Barrel("バレル"),
    Furlong("ハロン"),
    Victom("ヴィクトム"),
    Kyoichi("キョウイチ"),
    Kyoichi2("潜行のキョウイチ"),
    Flamier("フラミー"),
    Shoen("ショウエン"),
    Shoen2("隠密のショウエン"),
    Toshu("トウシュウ"),
    Marfik("マルフィク"),
    Marfik2("探険のマルフィク"),
    PolarisMask("ポラリスマスク"),
    Kuoki("コウキ"),
    Hitomi("ヒトミ"),
    Rakkta("ラクタ"),
    Loren("ロレン"),
    Isaribi("イサリビ"),
    Goro("ゴロウ"),
    Digram("ディグラム"),
    Andrew("アンドリュー"),
    Alchiba("アルキバ"),
    Alchiba2("追跡のアルキバ"),
    Subaru("スバル"),
    Kirsch("キルシュ"),
    Narihito("ナリヒト"),
    Suhail("スハイル"),
    Monomasa("モノマサ"),
    Procy("プロキー"),
    Gomeisa("ゴメイサ"),
    WolfmanWood("木のウルフマン"),
    WolfmanDark("影のウルフマン"),
    Nessen("ネッセン"),
    Hisaki("ヒサキ"),
    Maculata("マクラータ"),
    Rutilix("ルティリクス"),
    Alphecca("アルフェッカ"),
    Shaft("シャフト"),
    Kalaski("カラスキ"),
    Yoshiori("ヨシオリ"),
    Huckle("ハックル"), // sidekick only
    Melide("メリデ"), // sidekick only
    Player("主人公"), // sidekick only
    ;

//                "akashi", "mokdai", "sui", "ryekie",
//                        "crowne", "gammei", "barrel", "furlong", "victom",
//                        "kyoichi", "flamier", "shoen",
//                        "toshu", "marfik", "polaris_mask", "kouki_and_sirius",
//                        "hitomi", "rakta", "loren", "isaribi", "goro",
//                        "digram", "andrew", "alchiba", "subaru",
//                        "kirsch", "narihito", "suhail", "monomasa", "procy",
//                        //--
//                        "gomeisa",
//                        "huckle", // sidekick only
//                        "wood_wolfman", "shadow_wolfman", "nessen", "hisaki",
//                        //--
//                        "makulata", "rutilix", "alphecca", "shaft", "kalaski",
//
//                        "melide", // sidekick only
//                        "yoshiori",
//                        "player", // sidekick only
    public String nameEn = "";
    public String nameJa = "";
    Heros(String ja) {
        nameEn = name();
        nameJa = ja;
    }

}
