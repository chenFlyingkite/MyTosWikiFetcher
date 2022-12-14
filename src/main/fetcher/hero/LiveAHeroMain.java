package main.fetcher.hero;

import flyingkite.files.FileUtil;
import flyingkite.log.L;
import flyingkite.log.LF;
import flyingkite.tool.GsonUtil;
import flyingkite.tool.TextUtil;
import flyingkite.tool.URLUtil;
import main.fetcher.hero.card.each.Heros;
import main.fetcher.hero.card.field.Hero;
import main.fetcher.hero.card.field.HeroSkill;
import main.fetcher.hero.card.field.HeroValue;
import main.fetcher.hero.card.field.SideSkill;
import main.fetcher.hero.card.field.SideValue;
import main.fetcher.web.WebFetcher;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class LiveAHeroMain {
    public static final Map<String, Hero> allHeros = new HashMap<>();
    private static final List<Hero> sortedHero = new ArrayList<>();
    private static final List<Hero> loadedHero = new ArrayList<>();
    public static void main(String[] args) {
        //peekGotcha();
        // 0 = load hero
        // 1 = parse hero
        init();

        // web = true if it has new hero to reload
        boolean web = 0 > 0;
        if (web) {
            fetchHero();
        } else {
            loadHero();
        }

        ////imageMoe(); // dead

        List<Hero> hs;
        if (sortedHero.size() > 0) {
            hs = sortedHero;
        } else {
            hs = loadedHero;
        }
        L.log("%s Hero : ", hs.size());
        for (int i = 0; i < hs.size(); i++) {
            Hero h = hs.get(i);
            String s = h.toString();
            //s = h.title() + ", " + h.sidekickRelated();
            L.log("#%2d: %s", i, s);
        }

        peekHeroHP(hs);
        peekHeroView(hs);
        peekHeroAttack(hs);
        //peekHeroHpAttack(hs);
    }

    private static void fetchHero() {
        heroBasic();
        skills();
        makeSortedHero();
        saveHero();
    }

    private static void init() {
        // prepare all hero to be filled attributes
        for (Heros h : Heros.values()) {
            Hero hs = new Hero();
            hs.nameEn = h.name();
            allHeros.put(h.nameJa, hs);
        }
    }

    // Hero ID
    // https://wikiwiki.jp/live-a-hero/ID%E5%88%A5
    // Side kick, ID
    // https://wikiwiki.jp/live-a-hero/%E3%82%B5%E3%82%A4%E3%83%89%E3%82%AD%E3%83%83%E3%82%AF/ID%E5%88%A5
    private static void peekGotcha() {
        File root = new File("liveAHero", "gotcha/20221010");
        List<File> fs = FileUtil.listAllFiles(root);
        // pools has cards
        // Filename : {cardID -> probability tail}
        Map<String, Map<String, String>> fileToCard = new TreeMap<>();
        // card can be drawn in specific pools
        // cardID : [File]
        Map<String, List<String>> cardInDraw = new TreeMap<>();
        for (int i = 0; i < fs.size(); i++) {
            File it = fs.get(i);
            List<String> all = FileUtil.readAllLines(it);
            int start = 0;
            while (start < all.size() && !all.get(start).startsWith("小数点以下")) {
                start++;
            }
            start += 2;
            Map<String, String> card = new HashMap<>();
            fileToCard.put(it.getName(), card);
            for (int j = start; j < all.size(); j++) {
                String li = all.get(j);

                // find last two space
                int want = li.contains("ヒーロー") ? 2 : 3;
                int at = li.length() - 1;
                for (int k = li.length() - 1; k >= 0 && want > 0; k--) {
                    if (li.charAt(k) == ' ') {
                        want--;
                        at = k;
                    }
                }
                String id = li.substring(0, at);
                String prob = li.substring(at + 1);
                card.put(id, prob);

                List<String> files = cardInDraw.get(id);
                if (files == null) {
                    files = new ArrayList<>();
                    cardInDraw.put(id, files);
                }
                files.add(it.getName());
            }
        }
        int i;
        L.log("fileToCard = %s", fileToCard.size());
        i = 0;
        for (String k : fileToCard.keySet()) {
            Map<String, String> v = fileToCard.get(k);
            L.log("#%d,%s,%s,%s", i, k, v.size(), v);
            i++;
        }
        L.log("cardInDraw = %s", cardInDraw.size());
        i = 0;
        for (String k : cardInDraw.keySet()) {
            List<String> v = cardInDraw.get(k);
            L.log("#%d,%s,%s,%s", i, k, v.size(), v);
            i++;
        }
    }

    // Quartz Quests クオーツを探して (A級)
    // https://liveahero-wiki.github.io/events/2106shinrin/

    // https://zh.moegirl.org.cn/%E6%9C%A8%E4%BB%A3


    private static void makeSortedHero() {
        sortedHero.clear();
        List<Hero> hs = new ArrayList<>();
        for (Heros h : Heros.values()) {
            String k = h.nameJa;
            Hero v = allHeros.get(k);
            sortedHero.add(v);
            hs.add(v);
            L.log("%12s -> %s", k, v);
            String s = String.format("Hero/Side : %d skills, %d values / ", v.heroSkills.size(), v.heroValues.size());
            s += String.format("%d skills, %d values, %d equips, %d passive", v.sideSkills.size(), v.sideValues.size(), v.sideEquips.size(), v.passive.size());
            L.log("  %s", s);
            //L.log("heroImage.put(\"%s\", R.drawable.icon_akashi_h01);", v.nameJa);
            //L.log("sideImage.put(\"%s\", R.drawable.icon_akashi_s01);", v.nameJa);
        }

    }

    private static void peekHeroAttack(List<Hero> hs) {
        Collections.sort(hs, (x, y) -> {
            int hx = x.heroValues.size();
            int hy = y.heroValues.size();
            // no last -> as smallest
            if (hx == 0 || hy == 0) {
                return hx - hy;
            }
            HeroValue vx = x.heroValues.get(x.heroValues.size() - 1);
            HeroValue vy = y.heroValues.get(y.heroValues.size() - 1);
            return vx.attack - vy.attack;
        });
        L.log("sort attack :");
        for (int i = 0; i < hs.size(); i++) {
            Hero h = hs.get(i);
            HeroValue hv = null;
            if (h.heroValues.size() > 0) {
                hv = h.heroValues.get(h.heroValues.size() - 1);
            }

            String s = String.format("%s, %s, %s, %s", h.nameJa, h.attribute, h.role, hv);
            L.log("#%d : %s", i, s);
        }
    }

    // Compute the log(attack) + log(HP)
    private static void peekHeroHpAttack(List<Hero> hs) {
        Collections.sort(hs, (x, y) -> {
            int hx = x.heroValues.size();
            int hy = y.heroValues.size();
            // no last -> as smallest
            if (hx == 0 || hy == 0) {
                return hx - hy;
            }
            HeroValue vx = x.heroValues.get(x.heroValues.size() - 1);
            HeroValue vy = y.heroValues.get(y.heroValues.size() - 1);
            double zx = Math.log10(vx.attack) + Math.log(vx.hp);
            double zy = Math.log10(vy.attack) + Math.log(vy.hp);
            return Double.compare(zx, zy);
        });
        L.log("sort log(attack)+log(HP) :");
        for (int i = 0; i < hs.size(); i++) {
            Hero h = hs.get(i);
            HeroValue hv = null;
            if (h.heroValues.size() > 0) {
                hv = h.heroValues.get(h.heroValues.size() - 1);
            }

            String s = String.format("%s, %s, %s, %s", h.nameJa, h.attribute, h.role, hv);
            L.log("#%d : %s", i, s);
        }
    }

    private static void peekHeroView(List<Hero> hs) {
        Collections.sort(hs, (x, y) -> {
            int hx = x.heroValues.size();
            int hy = y.heroValues.size();
            // no last -> as smallest
            if (hx == 0 || hy == 0) {
                return hx - hy;
            }
            HeroValue vx = x.heroValues.get(x.heroValues.size() - 1);
            HeroValue vy = y.heroValues.get(y.heroValues.size() - 1);
            return vx.view - vy.view;
        });
        L.log("sort view :");
        for (int i = 0; i < hs.size(); i++) {
            Hero h = hs.get(i);
            HeroValue hv = null;
            if (h.heroValues.size() > 0) {
                hv = h.heroValues.get(h.heroValues.size() - 1);
            }

            String s = String.format("%s, %s, %s, %s", h.nameJa, h.attribute, h.role, hv);
            L.log("#%d : %s", i, s);
        }
    }

    private static void peekHeroHP(List<Hero> hs) {
        Collections.sort(hs, (x, y) -> {
            int hx = x.heroValues.size();
            int hy = y.heroValues.size();
            // no last -> as smallest
            if (hx == 0 || hy == 0) {
                return hx - hy;
            }
            HeroValue vx = x.heroValues.get(x.heroValues.size() - 1);
            HeroValue vy = y.heroValues.get(y.heroValues.size() - 1);
            return vx.hp - vy.hp;
        });
        L.log("sort HP :");
        for (int i = 0; i < hs.size(); i++) {
            Hero h = hs.get(i);
            HeroValue hv = null;
            if (h.heroValues.size() > 0) {
                hv = h.heroValues.get(h.heroValues.size() - 1);
            }

            String s = String.format("%s, %s, %s, %s", h.nameJa, h.attribute, h.role, hv);
            L.log("#%d : %s", i, s);
        }
    }

    // will be rejected... in moe
    // https://zh.moegirl.org.cn/File:Akashi_summerdive2108_hero_5_ico.jpg
    // Maybe download from
    // https://wikiwiki.jp/live-a-hero/%E3%82%B5%E3%82%A4%E3%83%89%E3%82%AD%E3%83%83%E3%82%AF/ID%E5%88%A5
    private static void imageMoe() {
        Heros[] all = Heros.values();
        for (int i = 0; i < all.length; i++) {
            Heros h = all[i];
            String link = h.getMoeLink();
            L.log("#%2d : %s", i, link);
        }
    }
    // download hero images
    private static void heroImages() {
        // https://liveahero-wiki.github.io/charas/
        final String base = "https://liveahero-wiki.github.io";
        String link = base + "/charas/";
        Document doc = fetcher.getDocument(link);
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

    private static void loadHero() {
        Hero[] list = new Hero[1];
        list = GsonUtil.loadFile(mHeros.getFile().getFile(), list.getClass());
        loadedHero.clear();
        loadedHero.addAll(Arrays.asList(list));
        Set<String> roles = new HashSet<>();
        for (Hero h : list) {
            roles.add(h.role);
        }
        L.log("list = %s", list);
        L.log("roles = %s", roles);
    }

    private static void saveHero() {
        GsonUtil.writePrettyJson(mHeros.getFile().getFile(), sortedHero);
    }

    // create hero from Hero ID order
    // https://wikiwiki.jp/live-a-hero/ID%E5%88%A5
    private static void heroBasic() {
        String link = "https://wikiwiki.jp/live-a-hero/ID%E5%88%A5";
        Document doc;
        Elements es;
        doc = fetcher.getDocument(link);
        es = doc.getElementsByTag("tr");
        for (int i = 1; i < es.size(); i++) {
            Element ei = es.get(i);
            Elements row = ei.getElementsByTag("td");
            String ja = row.get(1).text();
            Hero me = allHeros.get(ja);
            if (me == null) {
                L.log("missing %s", ja);
            } else {
                me.nameJa = ja;
                me.rankFirst = row.get(2).text().trim().length();
                me.attribute = row.get(3).text();
                me.role = row.get(4).text();
                me.designer = row.get(5).text();
                me.characterVoice = row.get(6).text();
                // 7 = 実装時
            }
            L.log("#%2d hero = %s", i, me);
        }
        // fill in sidekick only
        // value is copied from web row
        fillSidekick("エクシオ\t☆☆☆☆\t朱交赤成\t千葉一伸\tv2.3.2");
        fillSidekick("メリデ\t☆☆☆☆\tふぐり\t夏怜\tv2.2.0");
        fillSidekick("セイイチロウ\t☆☆☆☆\tやきそばおおもり\t小山力也\tv2.4.10");
        fillSidekick("主人公\t☆☆☆\t朱交赤成\t選択式\tv1.0.0");
    }

    // from
    // https://wikiwiki.jp/live-a-hero/%E3%82%B5%E3%82%A4%E3%83%89%E3%82%AD%E3%83%83%E3%82%AF/ID%E5%88%A5
    private static void fillSidekick(String s) {
        String[] ss = s.split("\t");
        Hero me = allHeros.get(ss[0]);
        me.nameJa = ss[0];
        me.rankFirst = ss[1].length();
        me.designer = ss[2];
        me.characterVoice = ss[3];
    }

    // moe
    // https://zh.moegirl.org.cn/LIVE_A_HERO
    // create heroSkills
    // https://github.com/liveahero-wiki/liveahero-wiki.github.io/blob/master/_data/SkillMaster.json
    private static void skills() {
        Heros[] all = Heros.values();
        for (Heros each : all) {
            String link = each.getWikiLink();
            String key = each.nameJa;
            Hero hero = allHeros.get(key);
            Hero hero2 = null;
            if (each.hero2() != null) {
                hero2 = allHeros.get(each.hero2().nameJa);
            }
            L.log("\nFor = %s, link = %s", hero.nameEn, link);
            if (hero2 == null) {
                L.log("hero2 = N/A");
            } else {
                L.log("hero2 = %s", hero2.nameEn);
            }

            Document doc = fetcher.getDocument(link);
            Elements ts = doc.getElementsByTag("table");
            Elements ts2 = doc.getElementsByClass("re-table");
            //L.log("%s tables", ts.size());
            for (int i = 0; i < ts.size() + ts2.size(); i++) {
                Element ti;
                if (i < ts.size()) {
                    ti = ts.get(i);
                } else {
                    ti = ts2.get(i - ts.size());
                }
                String txt = ti.text();
                if (txt.startsWith("Rarity")) { // hero
                    List<HeroValue> ci = readHeroValue(ti);

                    // Add hero and then hero 2
                    if (hero.heroValues.isEmpty()) {
                        hero.heroValues.addAll(ci);
                    } else if (hero2 != null && hero2.heroValues.isEmpty()) {
                        hero2.heroValues.addAll(ci);
                    }
                } else if (txt.startsWith("Skill Name")) {
                    HeroSkillInfo s = readHeroSkill(ti);
                    List<HeroSkill> si = s.skills;

                    if (s.type == HeroSkillInfo.TwoPlus) {
                        // sidekick
                        if (hero.sideSkills.isEmpty()) {
                            hero.sideSkills.addAll(si);
                            if (hero2 != null) {
                                hero2.sideSkills.addAll(si);
                            }
                        }
                    } else if (s.type == HeroSkillInfo.HeroSkill) {
                        if (hero.heroSkills.isEmpty()) {
                            hero.heroSkills.addAll(si);
                            hero.heroPlus = s.heroSkillPlus;
                        } else if (hero2 != null && hero2.heroSkills.isEmpty()) {
                            hero2.heroSkills.addAll(si);
                            hero2.heroPlus = s.heroSkillPlus;
                        }
                    } else if (s.type == HeroSkillInfo.Equip) { // side equip skill
                        if (hero.sideEquips.isEmpty()) {
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
                    } else if (s.type == HeroSkillInfo.Passive) {
                        if (hero.passive.isEmpty()) {
                            hero.passive.addAll(si);
                        }
                    }
                } else if (txt.startsWith("Level")) {
                    if (hero.sideValues.isEmpty()) {
                        List<SideValue> ci = readSideValue(ti);

                        hero.sideValues.addAll(ci);
                        if (hero2 != null) {
                            hero2.sideValues.addAll(ci);
                        }
                    }
                }
            }
            L.log("hero  %s", hero);
            L.log("hero2 %s", hero2);
        }
    }

    private static class HeroSkillInfo {
        static final int TwoPlus = 1;
        static final int HeroSkill = 2;
        static final int Equip = 3;
        static final int Passive = 4;
        int type;
        int heroSkillPlus = -1;
        List<HeroSkill> skills = new ArrayList<>();
    }

    private static HeroSkillInfo readHeroSkill(Element e) {
        HeroSkillInfo it = new HeroSkillInfo();
        if (e == null) return it;

        // 0 = <td title="1001101" class="translate" data-translate="燃ゆる白球">Burning Baseball</td>
        // 1 = <td title="敵単体に70%ダメージ。40%の確率で2ターンの間火傷を付与。"> [base skill] Deal 70% of damage to target enemy /100%<br> [base skill] Decrease HP by -10% to target enemy for 2 turn(s) /40%<br> </td>
        // 2 = <td>0</td>
        //Elements tds = e.getElementsByTag("div");
        Elements es = e.children();
        final int R = 3; // row = 3 items
        int cnt = es.size() / R;
        for (int i = 1; i < cnt; i++) {
            int k = R * i;
            HeroSkill s = new HeroSkill();
            s.name    = es.get(k).attr("data-translate").trim();
            s.view    = parseInt(es.get(k + 1).text().trim());
            s.content = es.get(k + 2).attr("title").trim();
            it.skills.add(s);
        }

        int n = it.skills.size();
        boolean is2Plus = n == 3 &&
                it.skills.get(1).name.endsWith("+") &&
                it.skills.get(2).name.endsWith("++");
        // WolfmanShadow or WolfmanWood has 3 hero skill, no +
        boolean isHeroSkill = n == 4 || (!is2Plus && n == 3);
        boolean isEquip = n == 6;
        boolean isPassive = n < 3;
        if (is2Plus) {
            it.type = HeroSkillInfo.TwoPlus;
        } else if (isHeroSkill) {
            it.type = HeroSkillInfo.HeroSkill;
            it.heroSkillPlus = findSkillPlus(it.skills);
        } else if (isEquip) {
            it.type = HeroSkillInfo.Equip;
        } else if (isPassive) {
            it.type = HeroSkillInfo.Passive;
        } else {
            L.log("Missing skills %s", it.skills);
        }
        return it;
    }

    private static int findSkillPlus(List<HeroSkill> li) {
        if (li.size() >= 4) {
            HeroSkill plus = li.get(3);
            for (int i = 0; i < 3; i++) {
                HeroSkill h = li.get(i);
                if (plus.name.startsWith(h.name)) {
                    return i;
                }
            }
        }
        return -1;
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

    private static final WebFetcher fetcher = new WebFetcher();
    private static final String FOLDER = "liveAHero";
    private static LF mImage = new LF(FOLDER, "image2");
    private static LF mHeros = new LF(FOLDER, "hero.json");
    //private static LF mLf = new LF(FOLDER);
    //private static TicTac2 clock = new TicTac2();
    //private static OnWebLfTT onWeb = new OnWebLfTT(mLf, clock);
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
peekGotcha()
    fileToCard = 12 files, #{number} {filename} {cardCount}
    #0	2章公開記念.txt	97
    #1	3章公開記念.txt	97
    #2	4章公開記念.txt	97
    #3	5章公開記念.txt	97
    #4	サマーダイブ・アミューズメント！.txt	99
    #5	サーチ詳細.txt	97
    #6	スペース・タクシー・チェイサーズ！.txt	98
    #7	バレンタイン・サイバーウォーズ.txt	99
    #8	探索！騎士と遺跡アドベンチャー.txt	98
    #9	暁のゴールデンスピリット.txt	97
    #10	極楽！聖夜の熱湯戦線.txt	100
    #11	第2部1章公開記念.txt	97

cardInDraw = 106
#0	★★★ サイドキック アルキバ	12	[2章公開記念.txt
#1	★★★ サイドキック アルフェッカ	12	[2章公開記念.txt
#2	★★★ サイドキック アンドリュー	12	[2章公開記念.txt
#3	★★★ サイドキック イサリビ	12	[2章公開記念.txt
#4	★★★ サイドキック ガイウス	12	[2章公開記念.txt
#5	★★★ サイドキック ガンメイ	12	[2章公開記念.txt
#6	★★★ サイドキック キョウイチ	12	[2章公開記念.txt
#7	★★★ サイドキック キルシュ	12	[2章公開記念.txt
#8	★★★ サイドキック クローネ	12	[2章公開記念.txt
#9	★★★ サイドキック コウキ	12	[2章公開記念.txt
#10	★★★ サイドキック ゴメイサ	12	[2章公開記念.txt
#11	★★★ サイドキック サダヨシ	12	[2章公開記念.txt
#12	★★★ サイドキック サンテツ	12	[2章公開記念.txt
#13	★★★ サイドキック シャフト	12	[2章公開記念.txt
#14	★★★ サイドキック ショウエン	12	[2章公開記念.txt
#15	★★★ サイドキック スハイル	12	[2章公開記念.txt
#16	★★★ サイドキック スバル	12	[2章公開記念.txt
#17	★★★ サイドキック ディグラム	12	[2章公開記念.txt
#18	★★★ サイドキック トウシュウ	12	[2章公開記念.txt
#19	★★★ サイドキック ナリヒト	12	[2章公開記念.txt
#20	★★★ サイドキック ネッセン	1	[極楽！聖夜の熱湯戦線.txt]
#21	★★★ サイドキック ハイドール	12	[2章公開記念.txt
#22	★★★ サイドキック ハロン	12	[2章公開記念.txt
#23	★★★ サイドキック バレル	12	[2章公開記念.txt
#24	★★★ サイドキック ヒトミ	12	[2章公開記念.txt
#25	★★★ サイドキック プロキー	12	[2章公開記念.txt
#26	★★★ サイドキック ボレアリス	12	[2章公開記念.txt
#27	★★★ サイドキック ポラリスマスク	12	[2章公開記念.txt
#28	★★★ サイドキック マクラータ	12	[2章公開記念.txt
#29	★★★ サイドキック マルフィク	12	[2章公開記念.txt
#30	★★★ サイドキック モノマサ	12	[2章公開記念.txt
#31	★★★ サイドキック ヨシオリ	12	[2章公開記念.txt
#32	★★★ サイドキック ライキ	12	[2章公開記念.txt
#33	★★★ サイドキック ライラック	12	[2章公開記念.txt
#34	★★★ サイドキック ラクタ	12	[2章公開記念.txt
#35	★★★ サイドキック ルティリクス	12	[2章公開記念.txt
#36	★★★ サイドキック ヴィクトム	12	[2章公開記念.txt
#37	★★★ ヒーロー アカシ 火	12	[2章公開記念.txt
#38	★★★ ヒーロー アルキバ 影	12	[2章公開記念.txt
#39	★★★ ヒーロー イサリビ 水	12	[2章公開記念.txt
#40	★★★ ヒーロー キルシュ 火	12	[2章公開記念.txt
#41	★★★ ヒーロー ゴロウ 木	12	[2章公開記念.txt
#42	★★★ ヒーロー スイ 水	12	[2章公開記念.txt
#43	★★★ ヒーロー ディグラム 影	12	[2章公開記念.txt
#44	★★★ ヒーロー ナリヒト 影	12	[2章公開記念.txt
#45	★★★ ヒーロー プロキー 光	12	[2章公開記念.txt
#46	★★★ ヒーロー モクダイ 木	12	[2章公開記念.txt
#47	★★★ ヒーロー ラクタ 火	12	[2章公開記念.txt
#48	★★★ ヒーロー ロレン 水	12	[2章公開記念.txt
#49	★★★★ サイドキック アカシ	12	[2章公開記念.txt
#50	★★★★ サイドキック エクシオ	12	[2章公開記念.txt
#51	★★★★ サイドキック オキタカ	12	[2章公開記念.txt
#52	★★★★ サイドキック カラスキ	12	[2章公開記念.txt
#53	★★★★ サイドキック ゴロウ	12	[2章公開記念.txt
#54	★★★★ サイドキック スイ	12	[2章公開記念.txt
#55	★★★★ サイドキック セイイチロウ	12	[2章公開記念.txt
#56	★★★★ サイドキック ハックル	12	[2章公開記念.txt
#57	★★★★ サイドキック フラミー	12	[2章公開記念.txt
#58	★★★★ サイドキック メリデ	12	[2章公開記念.txt
#59	★★★★ サイドキック モクダイ	12	[2章公開記念.txt
#60	★★★★ サイドキック ヤスヒコ	12	[2章公開記念.txt
#61	★★★★ サイドキック ロイカー	12	[2章公開記念.txt
#62	★★★★ サイドキック ロレン	12	[2章公開記念.txt
#63	★★★★ ヒーロー アルフェッカ 火	12	[2章公開記念.txt
#64	★★★★ ヒーロー オキタカ 木	12	[2章公開記念.txt
#65	★★★★ ヒーロー カラスキ 火	12	[2章公開記念.txt
#66	★★★★ ヒーロー ガンメイ 木	12	[2章公開記念.txt
#67	★★★★ ヒーロー キョウイチ 木	12	[2章公開記念.txt
#68	★★★★ ヒーロー クローネ 影	12	[2章公開記念.txt
#69	★★★★ ヒーロー スバル 木	12	[2章公開記念.txt
#70	★★★★ ヒーロー トウシュウ 影	12	[2章公開記念.txt
#71	★★★★ ヒーロー ハイドール 水	12	[2章公開記念.txt
#72	★★★★ ヒーロー ハックル 光	12	[2章公開記念.txt
#73	★★★★ ヒーロー ハロン 水	12	[2章公開記念.txt
#74	★★★★ ヒーロー バレル 光	12	[2章公開記念.txt
#75	★★★★ ヒーロー ヒトミ 光	12	[2章公開記念.txt
#76	★★★★ ヒーロー フラミー 火	12	[2章公開記念.txt
#77	★★★★ ヒーロー ボレアリス 影	12	[2章公開記念.txt
#78	★★★★ ヒーロー マクラータ 水	12	[2章公開記念.txt
#79	★★★★ ヒーロー ヤスヒコ 光	12	[2章公開記念.txt
#80	★★★★ ヒーロー ヨシオリ 水	12	[2章公開記念.txt
#81	★★★★ ヒーロー ライキ 光	12	[2章公開記念.txt
#82	★★★★ ヒーロー ロイカー 光	12	[2章公開記念.txt
#83	★★★★ ヒーロー ヴィクトム 火	12	[2章公開記念.txt
#84	★★★★ ヒーロー 波音のヒトミ 水	1	[サマーダイブ・アミューズメント！.txt]
#85	★★★★ ヒーロー 隠密のショウエン 木	1	[バレンタイン・サイバーウォーズ.txt]
#86	★★★★★ ヒーロー アンドリュー 光	12	[2章公開記念.txt
#87	★★★★★ ヒーロー ガイウス 水	12	[2章公開記念.txt
#88	★★★★★ ヒーロー コウキ 光	12	[2章公開記念.txt
#89	★★★★★ ヒーロー ゴメイサ 木	12	[2章公開記念.txt
#90	★★★★★ ヒーロー サダヨシ 水	12	[2章公開記念.txt
#91	★★★★★ ヒーロー サンテツ 木	12	[2章公開記念.txt
#92	★★★★★ ヒーロー シャフト 影	12	[2章公開記念.txt
#93	★★★★★ ヒーロー ショウエン 水	12	[2章公開記念.txt
#94	★★★★★ ヒーロー スハイル 火	12	[2章公開記念.txt
#95	★★★★★ ヒーロー ネッセン 水	1	[極楽！聖夜の熱湯戦線.txt]
#96	★★★★★ ヒーロー ポラリスマスク 火	12	[2章公開記念.txt
#97	★★★★★ ヒーロー マルフィク 影	12	[2章公開記念.txt
#98	★★★★★ ヒーロー モノマサ 木	12	[2章公開記念.txt
#99	★★★★★ ヒーロー ライラック 木	12	[2章公開記念.txt
#100	★★★★★ ヒーロー ルティリクス 光	12	[2章公開記念.txt
#101	★★★★★ ヒーロー 探険のマルフィク 水	1	[探索！騎士と遺跡アドベンチャー.txt]
#102	★★★★★ ヒーロー 水弾のアカシ 木	1	[サマーダイブ・アミューズメント！.txt]
#103	★★★★★ ヒーロー 潜行のキョウイチ 火	1	[バレンタイン・サイバーウォーズ.txt]
#104	★★★★★ ヒーロー 追跡のアルキバ 火	1	[スペース・タクシー・チェイサーズ！.txt]
#105	★★★★★ ヒーロー 酔虎のライキ 影	1	[極楽！聖夜の熱湯戦線.txt]
*/