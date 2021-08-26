package main.fetcher.hero.card.each;

import java.util.HashMap;
import java.util.Map;

public enum Heros {
    Akashi("アカシ", "akashi", "赤司"),
    Akashi2("水弾のアカシ", "akashi", "赤司"),
    Mokdai("モクダイ", "mokdai", "木代"),
    Sui("スイ", "sui", "翠(LIVE_A_HERO)"),
    Ryekie("ライキ", "ryekie", "雷奇"),
    Ryekie2("酔虎のライキ", "ryekie", "雷奇"),
    Crowne("クローネ", "crowne", "克罗涅(LIVE_A_HERO)"),
    Gammei("ガンメイ", "gammei", "雁銘"),
    Barrel("バレル", "barrel", "巴雷尔(LIVE_A_HERO)"),
    Furlong("ハロン", "furlong", "弗隆"),
    Victom("ヴィクトム", "victom", "維克托姆"),
    Kyoichi("キョウイチ", "kyoichi", "恭一(LIVE_A_HERO)"),
    Kyoichi2("潜行のキョウイチ", "kyoichi", "恭一(LIVE_A_HERO)"),
    Flamier("フラミー", "flamier", "芙拉米"),
    Shoen("ショウエン", "shoen", "松煙"),
    Shoen2("隠密のショウエン", "shoen", "松煙"),
    Toshu("トウシュウ", "toshu", "東秀"),
    Marfik("マルフィク", "marfik", "馬爾菲克"),
    Marfik2("探険のマルフィク", "marfik", "馬爾菲克"),
    PolarisMask("ポラリスマスク", "polaris_mask", "北極星假面"),
    Hydoor("ハイドール", "hydoor", "海多爾"),
    Kuoki("コウキ", "kouki_and_sirius", "柯奇"),
    Hitomi("ヒトミ", "hitomi", "瞳(LIVE_A_HERO)"),
    Hitomi2("波音のヒトミ", "hitomi", "瞳(LIVE_A_HERO)"),
    Rakkta("ラクタ", "rakta", "拉克塔"),
    Loren("ロレン", "loren", "洛倫"),
    Isaribi("イサリビ", "isaribi", "渔火(LIVE_A_HERO)"),
    Goro("ゴロウ", "goro", "吾郎(LIVE_A_HERO)"),
    Digram("ディグラム", "digram", "迪克拉姆"),
    Andrew("アンドリュー", "andrew", "安德魯"),
    Alchiba("アルキバ", "alchiba", "亞爾基波"),
    Alchiba2("追跡のアルキバ", "alchiba", "亞爾基波"),
    Subaru("スバル", "subaru", "昴(LIVE_A_HERO)"),
    Kirsch("キルシュ", "kirsch", "琪爾修"),
    Narihito("ナリヒト", "narihito", "成仁"),
    Suhail("スハイル", "suhail", "斯海爾"),
    Monomasa("モノマサ", "monomasa", "物正"),
    Procy("プロキー", "procy", "普羅基"),
    Gomeisa("ゴメイサ", "gomeisa", "葛明薩"),
    Huckle("ハックル", "huckle", "哈克魯"), // sidekick only
    WolfmanWood("木のウルフマン", "wood_wolfman", ""),
    WolfmanDark("影のウルフマン", "shadow_wolfman", ""),
    Nessen("ネッセン", "nessen", "熱泉"),
    Hisaki("ヒサキ", "hisaki", "久希"),
    Maculata("マクラータ", "maculata", "瑪格菈塔"),
    Rutilix("ルティリクス", "rutilix", "路提利克斯"),
    Alphecca("アルフェッカ", "alphecca", "阿爾菲卡"),
    Shaft("シャフト", "shaft", "沙夫特"),
    Kalaski("カラスキ", "kalaski", "卡拉斯奇"),
    Melide("メリデ", "melide", "梅莉德"), // sidekick only
    Yoshiori("ヨシオリ", "yoshiori", "吉織"),
    Pubraseer("パブラシア", "pubraseer", "帕普拉西亞"),
    Player("主人公", "player", "主人公(LIVE_A_HERO)"), // sidekick only
    ;

    public String nameEn = "";
    public String nameJa = "";
    // format of
    // https://liveahero-wiki.github.io/charas/{$github}
    // For link for ryekie
    // https://liveahero-wiki.github.io/charas/ryekie/
    private String github = "";

    // format of
    // https://zh.moegirl.org.cn/{$moe}
    // For link for akashi
    // https://zh.moegirl.org.cn/%E8%B5%A4%E5%8F%B8
    private String moe = "";
    Heros(String ja, String inWiki, String inMoe) {
        nameEn = name();
        nameJa = ja;
        github = inWiki;
        moe = inMoe;
    }

    public String getWikiLink() {
        return "https://liveahero-wiki.github.io/charas/" + github;
    }

    public String getMoeLink() {
        return "https://zh.moegirl.org.cn/" + moe;
    }

    public Heros hero2() {
        return hero2Map.get(this);
    }

    public static Heros getByJA(String ja) {
        return mapJa.get(ja);
    }

    private static final Map<String, Heros> mapJa = new HashMap<>();
    private static final Map<Heros, Heros> hero2Map = new HashMap<>();

    static {
        Heros[] hs = Heros.values();
        for (Heros h : hs) {
            mapJa.put(h.nameJa, h);
        }
        // Hero 2
        hero2Map.put(Akashi, Akashi2);
        hero2Map.put(Ryekie, Ryekie2);
        hero2Map.put(Kyoichi, Kyoichi2);
        hero2Map.put(Shoen, Shoen2);
        hero2Map.put(Marfik, Marfik2);
        hero2Map.put(Hitomi, Hitomi2);
        hero2Map.put(Alchiba, Alchiba2);
    }

}
