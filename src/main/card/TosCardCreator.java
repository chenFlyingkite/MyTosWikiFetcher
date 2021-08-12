package main.card;

import flyingkite.log.L;
import flyingkite.log.Loggable;
import flyingkite.tool.TicTac2;
import main.fetcher.TosCardExtras;
import main.fetcher.TosCraftFetcher;
import main.fetcher.TosSkillFetcher;
import main.fetcher.TosSkillFetcher.AllSkill;
import main.kt.CardTds;
import main.kt.Craft;
import main.kt.FullStatsMax;
import main.kt.NameLink;
import main.kt.SkillInfo;
import main.kt.SkillInfo2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TosCardCreator {
    private TosCardCreator() {}
    public static final TosCardCreator me = new TosCardCreator();

    public static class CardInfo {
        public CardTds cardTds = null;
        public int[] anchors;
        public String icon = "";
        public String bigImage = "";
        public String wikiLink = "";
        public String idNorm = "";
        public List<String> basic = new ArrayList<>();
        public List<String> hpValues = new ArrayList<>();
        public List<String> expInfos = new ArrayList<>();
        public String details = "";
        public List<String> activeSkills = new ArrayList<>();
        public List<String> leaderSkills = new ArrayList<>();
        public List<String> amelioSkills = new ArrayList<>();
        public List<String> ameStages = new ArrayList<>();
        public List<String> awkStages = new ArrayList<>();
        public List<NameLink> powStages = new ArrayList<>();
        public List<String> virStages = new ArrayList<>();
        public List<SkillInfo> skillChange = new ArrayList<>();
        public List<String> sameSkills = new ArrayList<>();

        public String signature() {
            StringBuilder e = new StringBuilder();
            if (icon.length() == 0) {
                e.append(" icon");
            }
            if (bigImage.length() == 0) {
                e.append(" bigImage");
            }
            if (wikiLink.length() == 0) {
                e.append(" wikiLink");
            }
            if (idNorm.length() == 0) {
                e.append(" idNorm");
            }
            e.append(" bas=").append(basic.size());
            e.append(" hps=").append(hpValues.size());
            e.append(" exp=").append(expInfos.size());
            e.append(" act=").append(activeSkills.size());
            e.append(" ldr=").append(leaderSkills.size());
            e.append(" amS=").append(amelioSkills.size());
            e.append(" amT=").append(ameStages.size());
            e.append(" awk=").append(awkStages.size());
            e.append(" pow=").append(powStages.size());
            e.append(" vir=").append(virStages.size());
            e.append(" skc=").append(skillChange.size());
            e.append(" sam=").append(sameSkills.size());
            return e.toString();
        }
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

    private Map<String, List<Craft>> allArmCrafts;
    private AllSkill allTypeSkills;
    private Map<String, String> allAMBonus;

    private void loadExtraInfo() {
        if (allArmCrafts == null) {
            allArmCrafts = TosCraftFetcher.me.getArmCrafts();
        }
        if (allTypeSkills == null) {
            allTypeSkills = TosSkillFetcher.me.getAllTypedSkills();
        }
        if (allAMBonus == null) {
            allAMBonus = TosCardExtras.me.loadAllMaxBonus();
        }
    }

    public TosCard asTosCard(CardInfo info) {
        // Prepare other data
        loadExtraInfo();

        // Main body
        TosCard c = null;

        if (info != null) {
//            int n = info.basic.size();
//            switch (n) {
//                case 18: c = asTosCard_18(info); break;
//                case 27:
//                case 28: c = asTosCard_28(info); break;
//                case 16: c = asTosCard_16(info); break;
//                case 22: c = asTosCard_22(info); break;
//                //case 33:
//                case 32: c = asTosCard_32(info); break;
//                case 24: c = asTosCard_24(info); break;
//                case 31: c = asTosCard_31(info); break;
//                case 29: c = asTosCard_29(info); break;
//            }
            c = asTosCard_(info);
            updateAmes(c);
        }
        return c;
    }

    private TosCard asTosCard_(CardInfo info) {
        TosCard c = new TosCard();
        fillCommon(c, info);
        fillSkillActive(c, info);
        fillSkillLeader(c, info.leaderSkills);
        fillAmelioration(c, info.amelioSkills);
        return c;
    }

    private <T> void print(List<T> li) {
        for (int i = 0; i < li.size(); i++) {
            L.log("#%d : %s", i, li.get(i));
        }
    }

    private TosCard asTosCard_18(CardInfo info) {
        List<String> list = info.basic;
        TosCard c = new TosCard();

        fillCommon(c, info);

        //-- Skill Active name #10
        fillSkillActive1(c, list.subList(11, 15));
        //-- Skill Leader name #15
        fillSkillLeader(c, list.subList(16, 18));

        return c;
    }

    private TosCard asTosCard_28(CardInfo info) {
        List<String> list = info.basic;

        TosCard c = new TosCard();

        fillCommon(c, info);
        // A = (超人貝利亞)  2162 : 10 = 主動技, 19 = 隊長技 22 = 昇華
        // B = (青龍孟章神君)  24 : 10 = 主動技, 15 = 隊長技 18 = 昇華
        boolean isA = "主動技".equals(list.get(10))
                && "隊長技".equals(list.get(19))
                && "昇華".equals(list.get(22))
                ;

        if (isA) {
            //-- Skill Active name #10
            fillSkillActive1(c, list.subList(11, 15));
            fillSkillActive2(c, list.subList(15, 19));
            //-- Skill Leader name #15
            fillSkillLeader(c, list.subList(20, 22));
            //-- Amelioration name #18
            fillAmelioration(c, list.subList(23, 27));
        } else {
            //-- Skill Active name #10
            fillSkillActive1(c, list.subList(11, 15));
            //-- Skill Leader name #15
            fillSkillLeader(c, list.subList(16, 18));
            //-- Amelioration name #18
            fillAmelioration(c, list.subList(19, 27));
        }

        return c;
    }

    private TosCard asTosCard_16(CardInfo info) {
        List<String> list = info.basic;

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
        List<String> list = info.basic;

        TosCard c = new TosCard();

        fillCommon(c, info);
        //-- Skill Active name #10
        fillSkillActive1(c, list.subList(11, 15));
        fillSkillActive2(c, list.subList(15, 19));
        //-- Skill Leader name #19
        fillSkillLeader(c, list.subList(20, 22));

        return c;
    }

    private TosCard asTosCard_32(CardInfo info) {
        List<String> list = info.basic;

        TosCard c = new TosCard();
        fillCommon(c, info);
        //-- Skill Active name #10
        fillSkillActive1(c, list.subList(11, 15));
        fillSkillActive2(c, list.subList(15, 19));
        //-- Skill Leader name #19
        fillSkillLeader(c, list.subList(20, 22));
        //-- Amelioration # 22
        fillAmelioration(c, list.subList(23, 31));

        return c;
    }

    private TosCard asTosCard_24(CardInfo info) {
        List<String> list = info.basic;

        TosCard c = new TosCard();
        fillCommon(c, info);
        //-- Skill Active name #10
        fillSkillActive1(c, list.subList(11, 15));
        //-- Skill Leader name #15
        fillSkillLeader(c, list.subList(16, 18));
        fillAmelioration(c, list.subList(19, 23));

        return c;
    }

    private TosCard asTosCard_29(CardInfo info) {
        List<String> list = info.basic;

        TosCard c = new TosCard();

        fillCommon(c, info);
        //-- Skill Active name #10
        fillSkillActive1(c, list.subList(11, 15));
        fillSkillActive2(c, list.subList(15, 19));
        //-- Skill Leader name #19
        fillSkillLeader(c, list.subList(20, 22));
        fillAmelioration(c, list.subList(23, 29));

        return c;
    }

    private TosCard asTosCard_31(CardInfo info) {
        List<String> list = info.basic;

        TosCard c = new TosCard();

        fillCommon(c, info);
        //-- Skill Active name #10
        fillSkillActive1(c, list.subList(11, 15));
        fillSkillActive2(c, list.subList(15, 19));
        //-- Skill Leader name #19
        fillSkillLeader(c, list.subList(20, 22));
        fillAmelioration(c, list.subList(23, 31));

        return c;
    }

    public void inspectCard(TosCard c, Loggable log) {
        if (c == null) return;

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
        fillEvolution(c, info.cardTds.getEvolve()); // 進化列表
        fillEvolution(c, info.cardTds.getSupreme()); // 究極融煉
        fillVirRebirth(c, info);
        fillArmCraft(c, info);
        fillSwitch(c, info);
        c.cardDetails = info.details;
        c.sameSkills = info.sameSkills;
        fillStageLinks(c, info);
        fillSkillChange(c, info);
        fillAllMax(c, info);
    }

    private void fillLinks(TosCard c, CardInfo info) {
        c.icon = info.icon;
        c.bigImage = info.bigImage;
        c.wikiLink = info.wikiLink;
    }

    private void fillExpInfo(TosCard c, List<String> list) {
        String s = list.get(0);
        c.expCurve = parseInt(s.substring(0, s.indexOf("萬")));
        c.minExpSacrifice = parseInt(list.get(1));
        c.perLvExpSacrifice = parseInt(list.get(2));

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
        c.maxHP = parseInt(list.get(0));
        c.maxAttack = parseInt(list.get(1));
        c.maxRecovery = parseInt(list.get(2));

        c.minHP = parseInt(list.get(3));
        c.minAttack = parseInt(list.get(4));
        c.minRecovery = parseInt(list.get(5));
    }

    private void fillEvolution(TosCard c, List<String> list) {
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
                L.log("X_X wrong Evolution");
            }
            if (e != null) {
                evos.add(e);
            }
        }
        c.evolveInfo.addAll(evos);
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
        int n = list.size();
        if (n < 1) return;
        c.rebirthFrom = normId_nnnni(list.get(0));
        if (n < 4) return;
        c.rebirthChange = normId_nnnni(list.get(3));
    }

    private void fillSwitch(TosCard c, CardInfo info) {
        List<String> list = info.cardTds.getSwitching();
        if (list.size() == 0) return;
        c.switchChange = normId_nnnni(list.get(0));
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

        c.skillPowBattle.addAll(info.powStages);

        list = info.virStages;
        if (list.size() > 0) {
            c.skillVirBattleName = list.get(0);
            c.skillVirBattleLink = list.get(1);
        }
    }

    private void fillSkillChange(TosCard c, CardInfo info) {
        // Find all changed skill info
        List<SkillInfo2> sinf = new ArrayList<>();
        for (SkillInfo e : info.skillChange) {
            String name = e.getSkillName();
            boolean add = addSkills(sinf, name, c.idNorm);
            if (!add) {
                L.log("X_X missing skill %s, %s", c.idNorm, name);
            }
        }
        // Convert to Skill
        for (SkillInfo2 s : sinf) {
            c.skillChange.add(s.lite());
        }
    }

    private void fillAllMax(TosCard c, CardInfo info) {
        String src = allAMBonus.get(c.idNorm);
        FullStatsMax f = new FullStatsMax().parse(src);
        if (f.exists()) {
            c.allMaxAddHp = f.getAMhp();
            c.allMaxAddAttack = f.getAMAttack();
            c.allMaxAddRecovery = f.getAMRecovery();
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
            return String.format(Locale.US, "%04d", parseInt(s.substring(0, s.length() - 1)));
        } else {
            return s;
        }
    }

    public String normId_Cnnnn(String s) {
        boolean beginC = s != null && s.startsWith("C");
        if (beginC) {
            // Parse "C3101" to "3101"
            return s.substring(1);
        } else {
            return s;
        }
    }

    private void fillCardIds(TosCard c, CardInfo info) {
        List<String> list = info.basic;
        c.name = list.get(0);
        c.attribute = list.get(1);
        c.id = list.get(2);
        c.rarity = parseInt(list.get(3).substring(0, 1));
        c.cost = parseInt(list.get(4));
        c.race = list.get(5);
        c.series = list.get(6);
        c.LvMax = parseInt(list.get(7));
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
            c.idNorm = String.format(Locale.US, "%04d", parseInt(c.id));

            int end = c.wikiLink.lastIndexOf("/") + 1;
            String s = c.wikiLink.substring(end);
            if (s.matches("[0-9]+")) {
                int num = parseInt(s);
                if (isSkinID(num)) { // This is those card of 造型, like "id": "481", -> 水妹
                    c.idNorm = String.format(Locale.US, "%04d", num);
                }
            }
        }
    }


    public static boolean isSkinID(String s) {
        return isSkinID(Integer.parseInt(s));
    }

    public static boolean isSkinID(int id) {
        return 6000 <= id && id < 7000;
    }

    private void fillSkillActive(TosCard c, CardInfo info) {
        List<String> li = info.activeSkills;
        int n = li.size();
        if (n == 2) {
            fillSkillActive0(c, li);
        } else {
            if (n >= 4) {
                fillSkillActive1(c, li.subList(0, 4));
            }
            if (n >= 8) {
                fillSkillActive2(c, li.subList(4, 8));
            }
        }
    }

    private void fillSkillActive0(TosCard c, List<String> list) {
        c.skillName1 = list.get(0);
        c.skillCDMin1 = 0;
        c.skillCDMax1 = 0;
        c.skillDesc1 = list.get(1);
    }

    private void fillSkillActive1(TosCard c, List<String> list) {
        c.skillName1 = list.get(0);
        c.skillCDMin1 = parseInt(list.get(1));
        c.skillCDMax1 = parseInt(list.get(2));
        c.skillDesc1 = list.get(3);
    }

    private void fillSkillActive2(TosCard c, List<String> list) {
        c.skillName2 = list.get(0);
        c.skillCDMin2 = parseInt(list.get(1));
        c.skillCDMax2 = parseInt(list.get(2));
        c.skillDesc2 = list.get(3);
    }

    private void fillSkillLeader(TosCard c, List<String> list) {
        c.skillLeaderName = list.get(0);
        c.skillLeaderDesc = list.get(1);
    }

    private void fillAmelioration(TosCard c, List<String> list) {
        int n = list.size();
        if (n >= 2) {
            c.skillAmeName1 = list.get(0);
            c.skillAmeCost1 = parseInt(list.get(1));
        }
        if (n >= 4) {
            c.skillAmeName2 = list.get(2);
            c.skillAmeCost2 = parseInt(list.get(3));
        }
        if (n >= 6) {
            c.skillAmeName3 = list.get(4);
            c.skillAmeCost3 = parseInt(list.get(5));
        }
        if (n >= 8) {
            c.skillAmeName4 = list.get(6);
            c.skillAmeCost4 = parseInt(list.get(7));
        }
    }

    private void updateAmes(TosCard c) {
        updateHPAme(c);
        updateAttackAme(c);
        updateRecoveryAme(c);
        updateCDMinAme(c);
    }

    private void updateHPAme(TosCard c) {
        c.maxHPAme = c.maxHP;
        int add = getAmeChange(c, "召喚獸生命力 + ");
        c.maxHPAme += add;
    }

    private void updateAttackAme(TosCard c) {
        c.maxAttackAme = c.maxAttack;
        int add = getAmeChange(c, "召喚獸攻擊力 + ");
        c.maxAttackAme += add;
    }


    private void updateRecoveryAme(TosCard c) {
        c.maxRecoveryAme = c.maxRecovery;
        int add = getAmeChange(c, "召喚獸回復力 + ");
        c.maxRecoveryAme += add;
    }

    private void updateCDMinAme(TosCard c) {
        c.skillCDMaxAme = c.skillCDMax1;
        int add = getAmeChange(c, "召喚獸技能冷卻回合 - ");
        c.skillCDMaxAme -= add;
    }

    private int getAmeChange(TosCard c, String key) {
        String[] s = {c.skillAmeName1, c.skillAmeName2, c.skillAmeName3, c.skillAmeName4};
        int sum = 0;
        for (String t : s) {
            int p = t.indexOf(key);
            if (p >= 0) {
                String word = t.substring(p + key.length());
                String[] items = word.replaceAll(" ", "").split("[,|召]");
                if (items.length > 0) {
                    sum += parseInt(items[0]);
                }
            }
        }
        return sum;
    }

    private int parseInt(String s) {
        return parseInt(s, 0);
    }

    private int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return def;
        }
    }

}
