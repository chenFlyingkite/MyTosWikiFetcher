package main.fetcher.hero;

import flyingkite.log.L;
import flyingkite.log.LF;
import flyingkite.tool.GsonUtil;
import flyingkite.tool.TextUtil;
import flyingkite.tool.TicTac2;
import flyingkite.tool.URLUtil;
import main.fetcher.hero.card.each.Heros;
import main.fetcher.hero.card.field.Hero;
import main.fetcher.hero.card.field.HeroSkill;
import main.fetcher.hero.card.field.HeroValue;
import main.fetcher.hero.card.field.LinkInfo;
import main.fetcher.hero.card.field.SideSkill;
import main.fetcher.hero.card.field.SideValue;
import main.fetcher.web.OnWebLfTT;
import main.fetcher.web.WebFetcher;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LiveAHeroMain {
    public static final Map<String, Hero> allHeros = new HashMap<>();
    private static final List<Hero> sortedHero = new ArrayList<>();
    public static void main(String[] args) {
        init();
        makeHero();
        //heroImages();
    }

    private static void makeHero() {
        heros();
        skills();
        seeHero();
        saveHero();
    }

    private static void init() {
        for (Heros h : Heros.values()) {
            allHeros.put(h.nameEn, new Hero());
        }
    }

    // Quartz Quests クオーツを探して (A級)
    // https://liveahero-wiki.github.io/events/2106shinrin/

    // https://zh.moegirl.org.cn/%E6%9C%A8%E4%BB%A3


    private static void seeHero() {
        List<String> keys = new ArrayList<>(allHeros.keySet());
        Collections.sort(keys, (k1, k2) -> {
            Hero h1 = allHeros.get(k1);
            Hero h2 = allHeros.get(k2);
            Heros hs1 = Heros.findJa(h1.nameJa);
            Heros hs2 = Heros.findJa(h2.nameJa);
            return Integer.compare(hs1.ordinal(), hs2.ordinal());
        });
        sortedHero.clear();
        for (int i = 0; i < keys.size(); i++) {
            String k = keys.get(i);
            Hero v = allHeros.get(k);
            sortedHero.add(v);
            L.log("%12s -> %s", k, v);
            String s = String.format("Hero/Side : %d skills, %d values / ", v.heroSkills.size(), v.heroValues.size());
            s += String.format("%d skills, %d values, %d equips", v.sideSkills.size(), v.sideValues.size(), v.sideEquips.size());
            L.log("  %s", s);
            //L.log("heroImage.put(\"%s\", R.drawable.icon_akashi_h01);", v.nameJa);
            //L.log("sideImage.put(\"%s\", R.drawable.icon_akashi_s01);", v.nameJa);
        }
    }

    // download hero images
    private static void heroImages() {
        // https://liveahero-wiki.github.io/charas/
        final String base = "https://liveahero-wiki.github.io";
        String link = base + "/charas/";
        Document doc = fetcher.sendAndParseDom(link, onWeb);
        Elements imgs = doc.getElementsByTag("img");
        String path = mImage.getFile().getFile().getAbsolutePath();
        for (int i = 0; i < imgs.size(); i++) {
            Element img = imgs.get(i);
            String imgLink = base + img.attr("src");
            L.log("img = %s", imgLink);
            String name = TextUtil.after(imgLink, "/", -1);
            URLUtil.downloadFile(imgLink, path, name);

            //https://liveahero-wiki.github.io/cdn/Sprite/ui_frame_h_base_shadow.png
        }
        // <img class="hero-chara-icon" src="/cdn/Sprite/icon_akashi_h01.png" loading="lazy">
    }

    private static void saveHero() {
        GsonUtil.writePrettyJson(mHeros.getFile().getFile(), sortedHero);
    }

    // create heros
    private static void heros() {

        String s = "";
        s = "Akashi\takashi_hero_3_ico.jpg\tアカシ\t☆☆☆\t火\t攻撃\t朱交赤成\t保坂俊行\tv1.0.0\n" +
            "Mokdai\tmokdai_hero_3_ico.jpg\tモクダイ\t☆☆☆\t木\tView獲得\t朱交赤成\t熊本健太\tv1.0.0\n" +
            "Sui\tsui_hero_3_ico.jpg\tスイ\t☆☆☆\t水\t弱体化\t朱交赤成\t伊瀬茉莉也\tv1.0.0\n" +
            "Ryekie\tryekie_hero_4_ico.jpg\tライキ\t☆☆☆☆\t光\tSpd操作\t朱交赤成\t中谷一博\tv1.0.0\n" +
            "Ryekie2\tryekie_nettou_hero_5_ico.jpg\t酔虎のライキ\t☆☆☆☆☆\t影\t補助\t朱交赤成\t中谷一博\tv1.0.11\n" +
            "Crowne\tcrowne_hero_4_ico.jpg\tクローネ\t☆☆☆☆\t影\t補助\t朱交赤成\t山口眞弓\tv1.0.0\n" +
            "Gammei\tgammei_hero_4_ico.jpg\tガンメイ\t☆☆☆☆\t木\tSpd操作\t朱交赤成\tてらそままさき\tv1.0.7\n" +
            "Barrel\tbarrel_hero_4_ico.jpg\tバレル\t☆☆☆☆\t光\t攻撃\t竹本嵐\t岩永悠平\tv1.0.4\n" +
            "Furlong\tfurlong_hero_4_ico.jpg\tハロン\t☆☆☆☆\t水\t防御\tきしぐま\tよねざわたかし\tv1.0.0\n" +
            "Victom\tvictom_hero_4_ico.jpg\tヴィクトム\t☆☆☆☆\t火\t攻撃\tダイエクスト\t尾形雅宏\tv1.0.0\n" +
            "Kyoichi\tkyoichi_hero_4_ico.jpg\tキョウイチ\t☆☆☆☆\t木\t攻撃\t一十\tよねざわたかし\tv1.0.0\n" +
            "Kyoichi2\tkyoichi_valentine2021_hero_5_ico.jpg\t潜行のキョウイチ\t☆☆☆☆☆\t火\t特殊\t一十\tよねざわたかし\tv2.0.2\n" +
            "Flamier\tflamier_hero_4_ico.jpg\tフラミー\t☆☆☆☆\t火\t弱体化\t黒ねずみいぬ\t戸板優衣\tv2.0.2\n" +
            "Shoen\tshoen_hero_5_ico.jpg\tショウエン\t☆☆☆☆☆\t水\t補助\t樹下次郎\t成田剣\tv1.0.0\n" +
            "Shoen2\tshoen_valentine2021_hero_4_ico.jpg\t隠密のショウエン\t☆☆☆☆\t木\t攻撃\t樹下次郎\t成田剣\tv2.0.2\n" +
            "Toshu\ttoshu_hero_4_ico.jpg\tトウシュウ\t☆☆☆☆\t影\t攻撃\tおーくす\t稲田徹\tv1.0.0\n" +
            "Marfik\tmarfic_hero_5_ico.jpg\tマルフィク\t☆☆☆☆☆\t影\t防御\tめんスケ\t大友龍三郎\tv1.0.0\n" +
            "Marfik2\tmarfik_ruins_hero_5_ico.jpg\t探険のマルフィク\t☆☆☆☆☆\t水\tView獲得\tめんスケ\t大友龍三郎\tv2.1.0\n" +
            "PolarisMask\tpolarismask_hero_5_ico.jpg\tポラリスマスク\t☆☆☆☆☆\t火\tView獲得\tGomTang\t三宅健太\tv1.0.0\n" +
            "Kuoki\tkouki_hero_5_ico.jpg\tコウキ\t☆☆☆☆☆\t光\tView獲得\tsteelwire/鉄線\t小林由美子\tv1.0.0\n" +
            "Hitomi\thitomi_hero_4_ico.jpg\tヒトミ\t☆☆☆☆\t光\t補助\tずじ\t夏怜\tv1.0.0\n" +
            "Rakkta\trakkta_hero_3_ico.jpg\tラクタ\t☆☆☆\t火\t特殊\t一十\t笠間淳\tv1.0.0\n" +
            "Loren\tloren_hero_3_ico.jpg\tロレン\t☆☆☆\t水\t回復\tぷらす野昆布\t大浪嘉仁\tv1.0.0\n" +
            "Isaribi\tisaribi_hero_3_ico.jpg\tイサリビ\t☆☆☆\t水\t攻撃\tきしぐま\t尾形雅宏\tv1.0.0\n" +
            "Goro\tgoro_hero_3_ico.jpg\tゴロウ\t☆☆☆\t木\t防御\tおーくす\t大浪嘉仁\tv1.0.0\n" +
            "Digram\tdigram_hero_3_ico.jpg\tディグラム\t☆☆☆\t影\t弱体化\tダイエクスト\t乃村健次\tv1.0.0\n" +
            "Andrew\tandrew_hero_5_ico.jpg\tアンドリュー\t☆☆☆☆☆\t光\t防御\tGomTang\t後藤ヒロキ\tv2.0.5\n" +
            "Alchiba\talchiba_hero_3_ico.jpg\tアルキバ\t☆☆☆\t影\t弱体化\t1boshi\t笠間淳\tv1.0.0\n" +
            "Alchiba2\talchiba_chasers2105_hero_5_ico.jpg\t追跡のアルキバ\t☆☆☆☆☆\t火\t攻撃\t1boshi\t笠間淳\tv2.1.3\n" +
            "Subaru\tsubaru_hero_4_ico.jpg\tスバル\t☆☆☆☆\t木\t弱体化\tうさ餅大福\t天野ユウ\tv1.0.11\n" +
            "Kirsch\tkirsch_hero_3_ico.jpg\tキルシュ\t☆☆☆\t火\t回復\tずじ\t樹元オリエ\tv1.0.0\n" +
            "Narihito\tnarihito_hero_3_ico.jpg\tナリヒト\t☆☆☆\t影\tView獲得\tめんスケ\t内匠靖明\tv1.0.0\n" +
            "Suhail\tsuhail_hero_5_ico.jpg\tスハイル\t☆☆☆☆☆\t火\t攻撃\tBomBom\tてらそままさき\tv1.0.7\n" +
            "Monomasa\tmonomasa_hero_5_ico.jpg\tモノマサ\t☆☆☆☆☆\t木\t攻撃\tおーくす\t野島健児\tv2.2.0\n" +
            "Procy\tprocy_hero_3_ico.jpg\tプロキー\t☆☆☆\t光\tView獲得\t樹下次郎\t山口勝平\tv1.0.0\n" +
            "Gomeisa\tgomeisa_hero_5_ico.jpg\tゴメイサ\t☆☆☆☆☆\t木\t回復\t黒ねずみいぬ\t内匠靖明\tv1.0.4\n" +
            "WolfmanWood\twolfman_hero_1_wood_ico.jpg\t木のウルフマン\t☆\t木\t攻撃\tきしぐま\t――\tv2.0.0\n" +
            "WolfmanDark\twolfman_hero_1_shadow_ico.jpg\t影のウルフマン\t☆\t影\t攻撃\tきしぐま\t――\tv2.0.5\n" +
            "Nessen\tnessen_hero_5_ico.jpg\tネッセン\t☆☆☆☆☆\t水\t攻撃\t一十\t置鮎龍太郎\tv1.0.11\n" +
            "Hisaki\thisaki_hero_3_ico.jpg\tヒサキ\t☆☆☆\t光\t補助\tぷらす野昆布\t山口勝平\tv2.0.2\n" +
            "Maculata\tmaculata_hero_4_ico.jpg\tマクラータ\t☆☆☆☆\t水\tSpd操作\tずじ\tならはしみき\tv2.0.5\n" +
            "Rutilix\trutilix_hero_5_ico.jpg\tルティリクス\t☆☆☆☆☆\t光\t攻撃\tきしぐま\t福山潤\tv2.1.0\n" +
            "Alphecca\talphecca_hero_4_ico.jpg\tアルフェッカ\t☆☆☆☆\t火\t防御\tRybiOK\t有元勇輝\tv2.1.0\n" +
            "Shaft\tshaft_hero_5_ico.jpg\tシャフト\t☆☆☆☆☆\t影\t弱体化\tやきそばおおもり\t増元拓也\tv2.1.3\n" +
            "Kalaski\tkalaski_hero_4_ico.jpg\tカラスキ\t☆☆☆☆\t火\tSpd操作\t藤三郎\t戸板優衣\tv2.1.3\n" +
            "Yoshiori\tyoshiori_hero_4_ico.jpg\tヨシオリ\t☆☆☆☆\t水\t弱体化\t英\t熊本健太\tv2.2.0\n" +
            "Huckle\tHuckle_side_4_ico.jpg\tハックル\t☆☆☆☆\t朱交赤成\t堀内賢雄\tv1.0.0\n" +
            "Melide\tmelide_side_4_ico.jpg\tメリデ\t☆☆☆☆\tふぐり\t夏怜\tv2.2.0\n" +
            "Player\tplayer_side_3_ico.gif\t主人公\t☆☆☆\t朱交赤成\t選択式\tv1.0.0"
        ;

        String[] each = s.split("\n");
        for (int i = 0; i < each.length; i++) {
            String x = each[i];
            String[] xx = x.split("\t");
            int xn = xx.length;
            //L.log("%s(\"%s\"),", xx[0], xx[2]); // creating enum of Heros
            Hero h = allHeros.get(xx[0]);
            if (h == null) {
                L.log("X_X fail %s", xx[0]);
            } else {
                h.idNorm = String.format("H%03d", i + 1);
                h.nameEn = xx[0];
                h.nameJa = xx[2];
                h.rankFirst = xx[3].length();
                if (xn == 9) { // hero
                    h.attribute = xx[4];
                    h.role = xx[5];
                    h.designer = xx[6];
                    h.characterVoice = xx[7];
                } else { // side
                    h.designer = xx[4];
                    h.characterVoice = xx[5];
                }
                L.log("Hero : %s", h);
            }
        }
    }

    // create heroSkills
    // https://github.com/liveahero-wiki/liveahero-wiki.github.io/blob/master/_data/SkillMaster.json
    private static void skills() {
        List<LinkInfo> li = skillLinks();
        String pre = "https://liveahero-wiki.github.io/charas/";
        for (LinkInfo each : li) {
            String link = pre + each.link;
            String key = each.key;
            Hero hero = allHeros.get(key);
            Hero hero2 = allHeros.get(key + "2");
            L.log("\nFor = %s, link = %s", hero.nameEn, link);
            L.log("hero2 = %s", hero2 == null ? "N/A" : hero2.nameEn);

            Document doc = fetcher.sendAndParseDom(link, onWeb);
            Elements ts = doc.getElementsByTag("table");
            L.log("%s tables", ts.size());
            for (Element ti : ts) {
                String txt = ti.text();
                if (txt.startsWith("Rarity")) { // hero
                    List<HeroValue> ci = readHeroValue(ti);
//                    L.log("%d basic hero", ci.size());
//                    for (int i = 0; i < ci.size(); i++) {
//                        L.log("%s", ci.get(i));
//                    }

                    if (hero.heroValues.isEmpty()) {
                        hero.heroValues.addAll(ci);
                    }

                    if (hero2 != null && hero2.heroValues.isEmpty()) {
                        hero2.heroValues.addAll(ci);
                    }
                } else if (txt.startsWith("Skill Name")) {
                    List<HeroSkill> si = readHeroSkill(ti);

                    int n = si.size();
                    boolean is2Plus = si.size() >= 3 && si.get(1).name.endsWith("+") && si.get(2).name.endsWith("++");
                    // WolfmanDark or WolfmanWood has 3 hero skill, no +
                    boolean isHeroSkill = n == 4 || (!is2Plus && n == 3);
                    boolean isEquip = n == 6;
                    if (is2Plus) { // WolfmanDark, WolfmanWood has no + skill, so n = 3
                        // side
                        hero.sideSkills.addAll(si);
                        if (hero2 != null) {
                            hero2.sideSkills.addAll(si);
                        }
                    } else if (isHeroSkill) { // hero
                        if (hero.heroSkills.isEmpty()) {
                            hero.heroSkills.addAll(si);
                        }

                        if (hero2 != null && hero2.heroSkills.isEmpty()) {
                            hero2.heroSkills.addAll(si);
                        }
                    } else if (isEquip) { // side equip skill
                        for (HeroSkill hs : si) {
                            SideSkill ss = new SideSkill();
                            ss.name = hs.name;
                            ss.content = hs.content;
                            hero.sideEquips.add(ss);
                            if (hero2 != null) {
                                hero2.sideEquips.add(ss);
                            }
                        }
                    }
                } else if (txt.startsWith("Level")) {
                    List<SideValue> ci = readSideValue(ti);

                    hero.sideValues.addAll(ci);
                    if (hero2 != null) {
                        hero2.sideValues.addAll(ci);
                    }
                }
                L.log("hero %s", hero);
            }

        }
    }

    private static List<HeroSkill> readHeroSkill(Element e) {
        List<HeroSkill> li = new ArrayList<>();
        if (e == null) return li;

        // 0 = <td title="1001101" class="translate" data-translate="燃ゆる白球">Burning Baseball</td>
        // 1 = <td title="敵単体に70%ダメージ。40%の確率で2ターンの間火傷を付与。"> [base skill] Deal 70% of damage to target enemy /100%<br> [base skill] Decrease HP by -10% to target enemy for 2 turn(s) /40%<br> </td>
        // 2 = <td>0</td>
        Elements tds = e.getElementsByTag("td");
        int n = tds.size();
        for (int i = 0; i < n / 3; i++) {
            int k = 3 * i;
            HeroSkill s = new HeroSkill();
            s.name    = tds.get(k + 0).attr("data-translate");
            s.content = tds.get(k + 1).attr("title");
            s.view    = parseInt(tds.get(k + 2).text());
            li.add(s);
        }
        return li;
    }

    private static List<HeroValue> readHeroValue(Element e) {
        List<HeroValue> li = new ArrayList<>();
        if (e == null) return li;

        Elements tds = e.getElementsByTag("td");
        int n = tds.size();
        int r = 6;
        for (int i = 0; i < n / r; i++) {
            int k = r * i;
            HeroValue c = new HeroValue();
            c.rarity = parseInt(tds.get(k + 0).text().replace("☆", "").trim());
            c.level  = parseInt(tds.get(k + 1).text());
            c.hp     = parseInt(tds.get(k + 2).text());
            c.attack = parseInt(tds.get(k + 3).text());
            c.speed  = parseInt(tds.get(k + 4).text());
            c.view   = parseInt(tds.get(k + 5).text());
            li.add(c);
        }
        return li;
    }

    private static List<SideValue> readSideValue(Element e) {
        List<SideValue> li = new ArrayList<>();
        if (e == null) return li;

        Elements tds = e.getElementsByTag("td");
        int n = tds.size();
        int r = 5;
        for (int i = 0; i < n / r; i++) {
            int k = r * i;
            SideValue c = new SideValue();
            c.level  = parseInt(tds.get(k + 0).text());
            c.hp     = parseInt(tds.get(k + 1).text());
            c.attack = parseInt(tds.get(k + 2).text());
            c.speed  = parseInt(tds.get(k + 3).text());
            c.view   = parseInt(tds.get(k + 4).text());
            li.add(c);
        }
        return li;
    }

    private static int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static WebFetcher fetcher = new WebFetcher();
    private static final String FOLDER = "liveAHero";
    private static LF mLf = new LF(FOLDER);
    private static LF mImage = new LF(FOLDER, "image");
    private static LF mHeros = new LF(FOLDER, "hero.json");
    private static TicTac2 clock = new TicTac2();
    private static OnWebLfTT onWeb = new OnWebLfTT(mLf, clock);
    private static List<LinkInfo> skillLinks() {
        List<LinkInfo> li = new ArrayList<>();
        li.add(new LinkInfo(Heros.Akashi, "akashi"));
        li.add(new LinkInfo(Heros.Mokdai, "mokdai"));
        li.add(new LinkInfo(Heros.Sui, "sui"));
        li.add(new LinkInfo(Heros.Ryekie, "ryekie"));
        li.add(new LinkInfo(Heros.Crowne, "crowne"));
        li.add(new LinkInfo(Heros.Gammei, "gammei"));
        li.add(new LinkInfo(Heros.Barrel, "barrel"));
        li.add(new LinkInfo(Heros.Furlong, "furlong"));
        li.add(new LinkInfo(Heros.Victom, "victom"));
        li.add(new LinkInfo(Heros.Kyoichi, "kyoichi"));
        li.add(new LinkInfo(Heros.Flamier, "flamier"));
        li.add(new LinkInfo(Heros.Shoen, "shoen"));
        li.add(new LinkInfo(Heros.Toshu, "toshu"));
        li.add(new LinkInfo(Heros.Marfik, "marfik"));
        li.add(new LinkInfo(Heros.PolarisMask, "polaris_mask"));
        li.add(new LinkInfo(Heros.Kuoki, "kouki_and_sirius"));
        li.add(new LinkInfo(Heros.Hitomi, "hitomi"));
        li.add(new LinkInfo(Heros.Rakkta, "rakta"));
        li.add(new LinkInfo(Heros.Loren, "loren"));
        li.add(new LinkInfo(Heros.Isaribi, "isaribi"));
        li.add(new LinkInfo(Heros.Goro, "goro"));
        li.add(new LinkInfo(Heros.Digram, "digram"));
        li.add(new LinkInfo(Heros.Andrew, "andrew"));
        li.add(new LinkInfo(Heros.Alchiba, "alchiba"));
        li.add(new LinkInfo(Heros.Subaru, "subaru"));
        li.add(new LinkInfo(Heros.Kirsch, "kirsch"));
        li.add(new LinkInfo(Heros.Narihito, "narihito"));
        li.add(new LinkInfo(Heros.Suhail, "suhail"));
        li.add(new LinkInfo(Heros.Monomasa, "monomasa"));
        li.add(new LinkInfo(Heros.Procy, "procy"));
        li.add(new LinkInfo(Heros.Gomeisa, "gomeisa"));
        li.add(new LinkInfo(Heros.Huckle, "huckle"));
        li.add(new LinkInfo(Heros.WolfmanWood, "wood_wolfman"));
        li.add(new LinkInfo(Heros.WolfmanDark, "shadow_wolfman"));
        li.add(new LinkInfo(Heros.Nessen, "nessen"));
        li.add(new LinkInfo(Heros.Hisaki, "hisaki"));
        li.add(new LinkInfo(Heros.Maculata, "maculata"));
        li.add(new LinkInfo(Heros.Rutilix, "rutilix"));
        li.add(new LinkInfo(Heros.Alphecca, "alphecca"));
        li.add(new LinkInfo(Heros.Shaft, "shaft"));
        li.add(new LinkInfo(Heros.Kalaski, "kalaski"));
        li.add(new LinkInfo(Heros.Melide, "melide"));
        li.add(new LinkInfo(Heros.Yoshiori, "yoshiori"));
        li.add(new LinkInfo(Heros.Player, "player"));
        return li;
    }
    // クエストバトル > 状態変化
    // https://live-a-hero.jp/help
    // Skill icons
}

// isaribi lv40 = hp=2489, atk=1100, spd=109, view=1448
// isaribi lv49 = hp=3485, atk=1539, spd=109, view=1561
// isaribi lv50 = hp=3596, atk=1588, spd=109, view=1574
// 50-49 = 111, 49, 0, 13
// 50-40 = 1107, 488, 0, 126
// https://wikiwiki.jp/live-a-hero/%E3%82%B5%E3%82%A4%E3%83%89%E3%82%AD%E3%83%83%E3%82%AF/ID%E5%88%A5
// https://wikiwiki.jp/live-a-hero/ID%E5%88%A5
/*
akashi_hero_3_ico.jpg	アカシ	☆☆☆	火	攻撃	朱交赤成	保坂俊行	v1.0.0
mokdai_hero_3_ico.jpg	モクダイ	☆☆☆	木	View獲得	朱交赤成	熊本健太	v1.0.0
sui_hero_3_ico.jpg	スイ	☆☆☆	水	弱体化	朱交赤成	伊瀬茉莉也	v1.0.0
ryekie_hero_4_ico.jpg	ライキ	☆☆☆☆	光	Spd操作	朱交赤成	中谷一博	v1.0.0
ryekie_nettou_hero_5_ico.jpg	酔虎のライキ	☆☆☆☆☆	影	補助	朱交赤成	中谷一博	v1.0.11
crowne_hero_4_ico.jpg	クローネ	☆☆☆☆	影	補助	朱交赤成	山口眞弓	v1.0.0
gammei_hero_4_ico.jpg	ガンメイ	☆☆☆☆	木	Spd操作	朱交赤成	てらそままさき	v1.0.7
barrel_hero_4_ico.jpg	バレル	☆☆☆☆	光	攻撃	竹本嵐	岩永悠平	v1.0.4
furlong_hero_4_ico.jpg	ハロン	☆☆☆☆	水	防御	きしぐま	よねざわたかし	v1.0.0
victom_hero_4_ico.jpg	ヴィクトム	☆☆☆☆	火	攻撃	ダイエクスト	尾形雅宏	v1.0.0
kyoichi_hero_4_ico.jpg	キョウイチ	☆☆☆☆	木	攻撃	一十	よねざわたかし	v1.0.0
kyoichi_valentine2021_hero_5_ico.jpg	潜行のキョウイチ	☆☆☆☆☆	火	特殊	一十	よねざわたかし	v2.0.2
flamier_hero_4_ico.jpg	フラミー	☆☆☆☆	火	弱体化	黒ねずみいぬ	戸板優衣	v2.0.2
shoen_hero_5_ico.jpg	ショウエン	☆☆☆☆☆	水	補助	樹下次郎	成田剣	v1.0.0
shoen_valentine2021_hero_4_ico.jpg	隠密のショウエン	☆☆☆☆	木	攻撃	樹下次郎	成田剣	v2.0.2
toshu_hero_4_ico.jpg	トウシュウ	☆☆☆☆	影	攻撃	おーくす	稲田徹	v1.0.0
marfic_hero_5_ico.jpg	マルフィク	☆☆☆☆☆	影	防御	めんスケ	大友龍三郎	v1.0.0
marfik_ruins_hero_5_ico.jpg	探険のマルフィク	☆☆☆☆☆	水	View獲得	めんスケ	大友龍三郎	v2.1.0
polarismask_hero_5_ico.jpg	ポラリスマスク	☆☆☆☆☆	火	View獲得	GomTang	三宅健太	v1.0.0
kouki_hero_5_ico.jpg	コウキ	☆☆☆☆☆	光	View獲得	steelwire/鉄線	小林由美子	v1.0.0
hitomi_hero_4_ico.jpg	ヒトミ	☆☆☆☆	光	補助	ずじ	夏怜	v1.0.0
rakkta_hero_3_ico.jpg	ラクタ	☆☆☆	火	特殊	一十	笠間淳	v1.0.0
loren_hero_3_ico.jpg	ロレン	☆☆☆	水	回復	ぷらす野昆布	大浪嘉仁	v1.0.0
isaribi_hero_3_ico.jpg	イサリビ	☆☆☆	水	攻撃	きしぐま	尾形雅宏	v1.0.0
goro_hero_3_ico.jpg	ゴロウ	☆☆☆	木	防御	おーくす	大浪嘉仁	v1.0.0
digram_hero_3_ico.jpg	ディグラム	☆☆☆	影	弱体化	ダイエクスト	乃村健次	v1.0.0
andrew_hero_5_ico.jpg	アンドリュー	☆☆☆☆☆	光	防御	GomTang	後藤ヒロキ	v2.0.5
alchiba_hero_3_ico.jpg	アルキバ	☆☆☆	影	弱体化	1boshi	笠間淳	v1.0.0
alchiba_chasers2105_hero_5_ico.jpg	追跡のアルキバ	☆☆☆☆☆	火	攻撃	1boshi	笠間淳	v2.1.3
subaru_hero_4_ico.jpg	スバル	☆☆☆☆	木	弱体化	うさ餅大福	天野ユウ	v1.0.11
kirsch_hero_3_ico.jpg	キルシュ	☆☆☆	火	回復	ずじ	樹元オリエ	v1.0.0
narihito_hero_3_ico.jpg	ナリヒト	☆☆☆	影	View獲得	めんスケ	内匠靖明	v1.0.0
suhail_hero_5_ico.jpg	スハイル	☆☆☆☆☆	火	攻撃	BomBom	てらそままさき	v1.0.7
monomasa_hero_5_ico.jpg	モノマサ	☆☆☆☆☆	木	攻撃	おーくす	野島健児	v2.2.0
procy_hero_3_ico.jpg	プロキー	☆☆☆	光	View獲得	樹下次郎	山口勝平	v1.0.0
gomeisa_hero_5_ico.jpg	ゴメイサ	☆☆☆☆☆	木	回復	黒ねずみいぬ	内匠靖明	v1.0.4
wolfman_hero_1_wood_ico.jpg	木のウルフマン	☆	木	攻撃	きしぐま	――	v2.0.0
wolfman_hero_1_shadow_ico.jpg	影のウルフマン	☆	影	攻撃	きしぐま	――	v2.0.5
nessen_hero_5_ico.jpg	ネッセン	☆☆☆☆☆	水	攻撃	一十	置鮎龍太郎	v1.0.11
hisaki_hero_3_ico.jpg	ヒサキ	☆☆☆	光	補助	ぷらす野昆布	山口勝平	v2.0.2
maculata_hero_4_ico.jpg	マクラータ	☆☆☆☆	水	Spd操作	ずじ	ならはしみき	v2.0.5
rutilix_hero_5_ico.jpg	ルティリクス	☆☆☆☆☆	光	攻撃	きしぐま	福山潤	v2.1.0
alphecca_hero_4_ico.jpg	アルフェッカ	☆☆☆☆	火	防御	RybiOK	有元勇輝	v2.1.0
shaft_hero_5_ico.jpg	シャフト	☆☆☆☆☆	影	弱体化	やきそばおおもり	増元拓也	v2.1.3
kalaski_hero_4_ico.jpg	カラスキ	☆☆☆☆	火	Spd操作	藤三郎	戸板優衣	v2.1.3
yoshiori_hero_4_ico.jpg	ヨシオリ	☆☆☆☆	水	弱体化	英	熊本健太	v2.2.0
*/

/*
【A級】クオーツを探して

*/