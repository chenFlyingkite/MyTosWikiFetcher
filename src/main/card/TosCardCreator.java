package main.card;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import flyingkite.log.L;
import flyingkite.log.Loggable;
import flyingkite.tool.TicTac2;
import main.fetcher.TosCardFetcher;
import main.fetcher.TosCraftFetcher;
import main.fetcher.TosSkillFetcher;
import main.fetcher.TosSkillFetcher.AllSkill;
import main.kt.CardTds;
import main.kt.Craft;
import main.kt.SkillInfo;
import main.kt.SkillInfo2;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TosCardCreator {
    private TosCardCreator() {}
    public static final TosCardCreator me = new TosCardCreator();

    public static class CardInfo {
        public List<String> data = new ArrayList<>();
        public CardTds cardTds = null;
        //public List<String> evolution = new ArrayList<>();
        public int[] anchors;
        public String icon = "";
        public String bigImage = "";
        public String wikiLink = "";
        public String idNorm = "";
        public List<String> hpValues = new ArrayList<>();
        public List<String> expInfos = new ArrayList<>();
        public String details = "";
        public List<String> ameStages = new ArrayList<>();
        public List<String> awkStages = new ArrayList<>();
        public List<String> powStages = new ArrayList<>();
        public List<String> virStages = new ArrayList<>();
        public List<SkillInfo> skillChange = new ArrayList<>();
        public List<String> sameSkills = new ArrayList<>();
    }

    private class EvoMeta {
        /** Evolution from card idNorm */
        public String evolveFrom = "";

        /** Evolution material card idNorm */
        public List<String> evolveNeed = new ArrayList<>();

        /** Evolution to card idNorm */
        public String evolveTo = "";
    }

//  node length, page
//18, card = http://zh.tos.wikia.com/wiki/001
//28, card = http://zh.tos.wikia.com/wiki/024
//16, card = http://zh.tos.wikia.com/wiki/1001
//22, card = http://zh.tos.wikia.com/wiki/1017
//32, card = http://zh.tos.wikia.com/wiki/1063
//24, card = http://zh.tos.wikia.com/wiki/651
//31, card = http://zh.tos.wikia.com/wiki/656

//    private Map<String, List<Skill>> skillActive;
//    private Map<String, List<Skill>> skillChangeLeader;
//    private Map<String, List<Skill>> skillChangeActive;
    private Map<String, List<Craft>> allArmCrafts;
    private AllSkill allTypeSkills;

    private void loadAllSkills() {
//        if (skillActive == null) {
//            skillActive = TosActiveSkillFetcher.me.getActiveSkills();
//        }
//        if (skillChangeLeader == null) {
//            skillChangeLeader = TosAmeSkillFetcher.me.getAllSkillsLeader();
//        }
//        if (skillChangeActive == null) {
//            skillChangeActive = TosAmeSkillFetcher.me.getAllSkillsActive();
//        }
        if (allArmCrafts == null) {
            allArmCrafts = TosCraftFetcher.me.getArmCrafts();
        }
        if (allTypeSkills == null) {
            allTypeSkills = TosSkillFetcher.me.getAllTypedSkills();
        }
    }

    public TosCard asTosCard(CardInfo info) {
        // Prepare other data
        //skillChangeLeader = TosAmeSkillFetcher.getAllSkillsLeader();
        //skillChangeActive = TosAmeSkillFetcher.getAllSkillsActive();
        loadAllSkills();

        // Main body
        TosCard c = null;

        if (info != null) {
            int n = info.data.size();
            switch (n) {
                case 18: c = asTosCard_18(info); break;
                case 28: c = asTosCard_28(info); break;
                case 16: c = asTosCard_16(info); break;
                case 22: c = asTosCard_22(info); break;
                case 32: c = asTosCard_32(info); break;
                case 24: c = asTosCard_24(info); break;
                case 31: c = asTosCard_31(info); break;
            }
        }
        return c;
    }


    private TosCard asTosCard_18(CardInfo info) {
        List<String> list = info.data;

        TosCard c = new TosCard();

        fillCommon(c, info);
        //-- Skill Active name #10
        fillSkillActive(c, list.subList(11, 15));
        //-- Skill Leader name #15
        fillSkillLeader(c, list.subList(16, 18));

        return c;
    }

    private TosCard asTosCard_28(CardInfo info) {
        List<String> list = info.data;

        TosCard c = new TosCard();

        fillCommon(c, info);
        //-- Skill Active name #10
        fillSkillActive(c, list.subList(11, 15));
        //-- Skill Leader name #15
        fillSkillLeader(c, list.subList(16, 18));
        fillAmelioration(c, list.subList(19, 27));

        return c;
    }

    private TosCard asTosCard_16(CardInfo info) {
        List<String> list = info.data;

        TosCard c = new TosCard();

        fillCommon(c, info);
        //-- Skill Active name #10
        c.skillName1 = list.get(11);
        c.skillCDMin1 = 0;
        c.skillCDMax1 = 0;
        c.skillDesc1 = list.get(12);
        //-- Skill Leader name #15
        fillSkillLeader(c, list.subList(14, 16));

        return c;
    }

    private TosCard asTosCard_22(CardInfo info) {
        List<String> list = info.data;

        TosCard c = new TosCard();

        fillCommon(c, info);
        //-- Skill Active name #10
        fillSkillActive(c, list.subList(11, 15));
        fillSkillActive2(c, list.subList(15, 19));
        //-- Skill Leader name #19
        fillSkillLeader(c, list.subList(20, 22));

        return c;
    }

    private TosCard asTosCard_32(CardInfo info) {
        List<String> list = info.data;

        TosCard c = new TosCard();
        fillCommon(c, info);
        //-- Skill Active name #10
        fillSkillActive(c, list.subList(11, 15));
        fillSkillActive2(c, list.subList(15, 19));
        //-- Skill Leader name #19
        fillSkillLeader(c, list.subList(20, 22));
        //-- Amelioration # 22
        fillAmelioration(c, list.subList(23, 31));

        return c;
    }

    private TosCard asTosCard_24(CardInfo info) {
        List<String> list = info.data;

        TosCard c = new TosCard();
        fillCommon(c, info);
        //-- Skill Active name #10
        fillSkillActive(c, list.subList(11, 15));
        //-- Skill Leader name #15
        fillSkillLeader(c, list.subList(16, 18));
        fillAmelioration(c, list.subList(19, 23));

        return c;
    }

    private TosCard asTosCard_31(CardInfo info) {
        List<String> list = info.data;

        TosCard c = new TosCard();

        fillCommon(c, info);
        //-- Skill Active name #10
        fillSkillActive(c, list.subList(11, 15));
        fillSkillActive2(c, list.subList(15, 19));
        //-- Skill Leader name #19
        fillSkillLeader(c, list.subList(20, 22));
        fillAmelioration(c, list.subList(23, 31));

        return c;
    }

    public void inspectCard(TosCard c, Loggable log) {
        if (c == null) return;

        //if (c.evolveFrom.length() > 0 && !c.idNorm.equals(c.evolveFrom)) {
            //log.log("Evolve not self? %s", c.wikiLink);
            // 禮物黑手黨 ‧ 馴鹿組
            // http://zh.tos.wikia.com/wiki/1308
            // 日月巨狼 ‧ 芬爾厄
            // http://zh.tos.wikia.com/wiki/1522
            // 超獸魔神
            // http://zh.tos.wikia.com/wiki/620
            // 格蕾琴與海森堡 ‧ 戰鯨吐息
            // http://zh.tos.wikia.com/wiki/656 ~ 660
            // 冰耀獸神兵
            // http://zh.tos.wikia.com/wiki/666 ~ 670
            // 仇龍英雄 ‧ 貝奧武夫
            // http://zh.tos.wikia.com/wiki/721 ~ 725
            // 憂懼之罪 ‧ 梅塔特隆
            // http://zh.tos.wikia.com/wiki/961 ~ 965
        //}

        if (c.skillAmeBattleName.length() > 0 && c.skillAmeCost1 == 0) {
            log.log("HaveAme but no cost? %s", c.wikiLink);
        }
    }

    private void fillCommon(TosCard c, CardInfo info) {
        fillLinks(c, info);
        fillCardIds(c, info);
        fillHPValues(c, info.hpValues);
        fillExpInfo(c, info.expInfos);
        fillCombination(c, info);
        fillEvolution(c, info);
        fillVirRebirth(c, info);
        fillArmCraft(c, info);
        c.cardDetails = info.details;
        c.sameSkills = info.sameSkills;
        fillStageLinks(c, info);
        fillSkillChange(c, info);
    }

    private void fillLinks(TosCard c, CardInfo info) {
        c.icon = info.icon;
        c.bigImage = info.bigImage;
        c.wikiLink = info.wikiLink;
    }

    private void fillExpInfo(TosCard c, List<String> list) {
        String s = list.get(0);
        c.expCurve = Integer.parseInt(s.substring(0, s.indexOf("萬")));
        c.minExpSacrifice = Integer.parseInt(list.get(1));
        c.perLvExpSacrifice = Integer.parseInt(list.get(2));

        int[] maxEco = getExpUtilityLevel(c);
        c.maxMUPerLevel = maxEco[0];
        c.maxTUAllLevel = maxEco[1];
    }

    private TicTac2 clock = new TicTac2();
    private int[] accuExp = new int[100];
    private int[] scfyExp = new int[100];

    private int[] getExpUtilityLevel(TosCard c) {
        // Parameters
        int expCurve = c.expCurve * 10000;
        int minExpSc = c.minExpSacrifice;
        int dExp = c.perLvExpSacrifice;

        // Computations initial
        int max = c.LvMax + 1;
        Arrays.fill(accuExp, 0);
        Arrays.fill(scfyExp, 0);

        int maxMu = -1;
        int maxTu = -1;
        double dx;
        for (int i = 1; i < max; i++) {
            dx = 1.0F * (i - 1) / 98;
            accuExp[i] = (int) Math.ceil(expCurve * dx * dx);
            scfyExp[i] = minExpSc + dExp * (i - 1);

            if (accuExp[i] - accuExp[i - 1] <= dExp) {
                maxMu = i;
            }

            if (accuExp[i] <= scfyExp[i]) {
                maxTu = i;
            }
        }

        return new int[]{maxMu, maxTu};
    }

    private void fillHPValues(TosCard c, List<String> list) {
        c.maxHP = Integer.parseInt(list.get(0));
        c.maxAttack = Integer.parseInt(list.get(1));
        c.maxRecovery = Integer.parseInt(list.get(2));

        c.minHP = Integer.parseInt(list.get(3));
        c.minAttack = Integer.parseInt(list.get(4));
        c.minRecovery = Integer.parseInt(list.get(5));
    }

    private void fillEvolution(TosCard c, CardInfo info) {
        List<String> list = info.cardTds.getEvolve();
        if (list.size() == 0) return;
        EvoMeta em = new EvoMeta();

        int plus = list.indexOf("EvoPlus");
        int end = list.lastIndexOf("EvoArrow");
        int fmIndex = -1;
        if (plus > 0) {
            fmIndex = plus;
        } else if (end > 0) {
            fmIndex = end;
        }
        if (plus > 0) {
            em.evolveFrom = list.get(fmIndex - 1);

            if (0 < end && end < list.size() - 1) {
                em.evolveTo = list.get(end + 1);
                em.evolveNeed = fmIndex == end
                        ? new ArrayList<>()
                        : new ArrayList<>(list.subList(fmIndex, end + 1));
            }
        } else if (plus < 0 && 0 <= end && end < list.size() - 1) { // Only have arrow
            if (info.virStages.size() > 0) { // Has Virtual rebirth
                c.rebirthFrom = normId_nnnni(list.get(end - 1)); // list[0]
                return;
            }
        }

        // Normalize
        normEvo(c, em);
    }

    private void normEvo(TosCard c, EvoMeta em) {
        List<String> evo = new ArrayList<>();
        if (!em.evolveFrom.isEmpty()) {
            evo.add(normId_nnnni(em.evolveFrom));
        }
        for (int i = 0; i < em.evolveNeed.size(); i++) {
            String s = em.evolveNeed.get(i);
            if (!s.isEmpty()) {
                evo.add(normId_nnnni(s));
            }
        }
        if (!em.evolveTo.isEmpty()) {
            evo.add(normId_nnnni(em.evolveTo));
        }

        List<Evolve> evos = new ArrayList<>();
        int n = 0;
        int MAX = evo.size();
        int plus = -1, arrow = -1;
        while (n < MAX) {
            plus = indexOf(evo, "EvoPlus", plus + 1, MAX);//evo.indexOf("EvoPlus");
            arrow = indexOf(evo, "EvoArrow", arrow + 1, MAX);//evo.indexOf("EvoArrow");
            Evolve e = null;
            if (plus < 0 && arrow < 0) { // Common evolve
                e = sliceEvolve(n, MAX - 1, evo);
                n = MAX;
            } else if (plus >= 0 && arrow >= 0) {
                e = sliceEvolve(n, arrow + 1, plus + 1, arrow, evo);
                n = arrow + 2;
            } else {
                L.log("XXX X_X");
            }
            if (e != null) {
                evos.add(e);
            }
        }
        c.evolveInfo = evos;
    }

    private int indexOf(List<String> list, String o, int from, int to) {
        if (o == null) {
            for (int i = from; i < to; i++)
                if (list.get(i) == null)
                    return i;
        } else {
            for (int i = from; i < to; i++)
                if (o.equals(list.get(i)))
                    return i;
        }
        return -1;
    }

    private Evolve sliceEvolve(int from, int to, List<String> evo) {
        return sliceEvolve(from, to, from + 1, to, evo);
    }
    private Evolve sliceEvolve(int fromAt, int toAt, int from, int to, List<String> evo) {
        Evolve e = new Evolve();
        e.evolveFrom = evo.get(fromAt);
        e.evolveTo = evo.get(toAt);
        e.evolveNeed = evo.subList(from, to);
        return e;
    }

    private void fillCombination(TosCard c, CardInfo info) {
        List<String> list = info.cardTds.getCombine();
        if (list.size() == 0) return;

        // Omit tail, Fill in the combine material
        for (int i = 0; i < list.size() - 1; i++) {
            String s = list.get(i);
            boolean endI = s.endsWith("i");
            boolean evos = s.contains("Evo"); // EvoPlus or EvoArrow
            if (endI && !evos) {
                c.combineFrom.add(normId_nnnni(s));
            }
        }

        // Add the combined card
        int end = list.lastIndexOf("EvoArrow");
        if (end < list.size() - 1) {
            c.combineTo.add(normId_nnnni(list.get(end + 1)));
        }
    }

    private void fillVirRebirth(TosCard c, CardInfo info) {
        List<String> list = info.cardTds.getRebirth();
        if (list.size() == 0) return;
        c.rebirthChange = normId_nnnni(list.get(0));
    }

    private void fillArmCraft(TosCard c, CardInfo info) {
        List<String> list = info.cardTds.getArmCraft();
        //if (list.size() == 0) return;
        for (int i = 0; i < list.size(); i++) {
            c.armCrafts.add(normId_Cnnnn(list.get(i)));
        }
        List<Craft> crafts = allArmCrafts.get(c.idNorm);
        if (crafts != null) {
            for (Craft cf : crafts) {
                String id = cf.getIdNorm();
                if (c.armCrafts.contains(id)) {
                    // OK
                } else { // Add it
                    c.armCrafts.add(id);
                }
            }
        }
    }


    private void fillStageLinks(TosCard c, CardInfo info) {
        List<String> list;
        // Fill in Amelioration stage name & link
        list = info.ameStages;
        if (list.size() > 0) {
            c.skillAmeBattleName = list.get(0);
            c.skillAmeBattleLink = list.get(1);
        }

        list = info.awkStages;
        if (list.size() > 0) {
            c.skillAwkName = list.get(0);
            c.skillAwkBattleName = list.get(1);
            c.skillAwkBattleLink = list.get(2);
        }

        list = info.powStages;
        if (list.size() > 0) {
            c.skillPowBattleName = list.get(0);
            c.skillPowBattleLink = list.get(1);
        }

        list = info.virStages;
        if (list.size() > 0) {
            c.skillVirBattleName = list.get(0);
            c.skillVirBattleLink = list.get(1);
        }
    }

    private void fillSkillChange(TosCard c, CardInfo info) {
        List<Element> ameChg = new ArrayList<>();

        if (c.idNorm.equals("1379")) {
            c.idNorm.length();
        }

        int ax = 2;
        int[] anchors = info.anchors;
        int anxer = anchors[ax];
        if (anxer >= 0) { // See any skills change in amelioration
            int at = TosCardFetcher.getPositiveMin(anchors, ax + 1, anchors.length);
            at = at - info.ameStages.size() / 2;
            Elements es = info.cardTds.getRawTds();
            for (int i = anxer; i < at; i++) {
                Element ei = es.get(i);
                if (ei != null) {
                    Elements eas = ei.getElementsByTag("a");
                    ameChg.addAll(eas);
                }
            }
        }

        if (ameChg.size() > 0) {
            // Find all changed skill info
            List<SkillInfo2> sinf = new ArrayList<>();
            for (Element e : ameChg) {
                String name = e.attr("title");
                boolean add = addSkills(sinf, name, c.idNorm);
                if (!add) {
                    L.log("XX_XXXZZ miss skill %s, %s", c.idNorm, name);
                }
            }
            // Convert to Skill
            for (SkillInfo2 s : sinf) {
                c.skillChange.add(s.lite());
            }
            c.skillChange.size();
        }
    }

    private boolean addSkills(List<SkillInfo2> box, String key, String idNorm) {
        List<Map<String, List<SkillInfo2>>> findOrder = Arrays.asList(
                allTypeSkills.ameLeader, allTypeSkills.ameActive
                , allTypeSkills.leader, allTypeSkills.active
        );

        boolean added;
        for (Map<String, List<SkillInfo2>> m : findOrder) {
            added = addSkills(box, m, key, idNorm);
            if (added) {
                return true;
            }
        }
        return false;
    }

    private boolean addSkills(List<SkillInfo2> box, Map<String, List<SkillInfo2>> table, String key, String idNorm) {
        if (table == null) return false;

        List<SkillInfo2> s = table.get(key);
        if (s != null) {
            box.add(s.get(s.size() - 1));
            return true;
        }
        return false;
    }

    public String normId_nnnni(String s) {
        boolean endI = s != null && s.endsWith("i");
        if (endI) {
            // Parse "12i" to "0012"
            return String.format(Locale.US, "%04d", Integer.parseInt(s.substring(0, s.length() - 1)));
        } else {
            return s;
        }
    }

    public String normId_Cnnnn(String s) {
        boolean beginC = s != null && s.startsWith("C");
        if (beginC) {
            // Parse "C3101" to "3101"
            //return String.format(Locale.US, "%04d", Integer.parseInt(s.substring(0, s.length() - 1)));
            return s.substring(1);
        } else {
            return s;
        }
    }

    private void fillCardIds(TosCard c, CardInfo info) {
        List<String> list = info.data;
        c.name = list.get(0);
        c.attribute = list.get(1);
        c.id = list.get(2);
        c.rarity = Integer.parseInt(list.get(3).substring(0, 1));
        c.cost = Integer.parseInt(list.get(4));
        c.race = list.get(5);
        c.series = list.get(6);
        c.LvMax = Integer.parseInt(list.get(7));
        //-- Exp curve #8
        c.ExpMax = Long.parseLong(list.get(9));
        setNormId(c, info.idNorm);
    }

    // Fill in Normalized ID
    private void setNormId(TosCard c, String xxi) {
        if (!xxi.isEmpty()) {
            c.idNorm = normId_nnnni(xxi);
        } else {
            // old one for the deprecated one
            c.idNorm = String.format(Locale.US, "%04d", Integer.parseInt(c.id));

            int end = c.wikiLink.lastIndexOf("/") + 1;
            String s = c.wikiLink.substring(end);
            if (s.matches("[0-9]+")) {
                int num = Integer.parseInt(s);
                if (6000 <= num && num < 7000) { // This is those card of 造型, like "id": "481", -> 水妹
                    c.idNorm = String.format(Locale.US, "%04d", num);
                }
            }
        }
    }

    private void fillSkillActive(TosCard c, List<String> list) {
        //-- Skill Active name #10
        c.skillName1 = list.get(0);
        c.skillCDMin1 = "1809".equals(c.idNorm) ? 0 : Integer.parseInt(list.get(1));
        c.skillCDMax1 = "1809".equals(c.idNorm) ? 0 : Integer.parseInt(list.get(2));
        c.skillDesc1 = list.get(3);
    }

    private void fillSkillActive2(TosCard c, List<String> list) {
        //-- Skill Active name #10
        c.skillName2 = list.get(0);
        c.skillCDMin2 = Integer.parseInt(list.get(1));
        c.skillCDMax2 = Integer.parseInt(list.get(2));
        c.skillDesc2 = list.get(3);
    }

    private void fillSkillLeader(TosCard c, List<String> list) {
        //-- Skill Leader name #15
        c.skillLeaderName = list.get(0);
        c.skillLeaderDesc = list.get(1);
    }

    private void fillAmelioration(TosCard c, List<String> list) {
        //-- Skill Leader name #15
        c.skillAmeName1 = list.get(0);
        c.skillAmeCost1 = Integer.parseInt(list.get(1));
        c.skillAmeName2 = list.get(2);
        c.skillAmeCost2 = Integer.parseInt(list.get(3));
        if (list.size() <= 4) return;

        c.skillAmeName3 = list.get(4);
        c.skillAmeCost3 = Integer.parseInt(list.get(5));
        c.skillAmeName4 = list.get(6);
        c.skillAmeCost4 = Integer.parseInt(list.get(7));
    }

}
