package main.card;

import util.logging.Loggable;
import util.tool.TicTac2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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
        public List<String> hpValues = new ArrayList<>();
        public List<String> expInfos = new ArrayList<>();
        public String detailsHtml = "";
        public List<String> ameStages = new ArrayList<>();
        public List<String> awkStages = new ArrayList<>();
        public List<String> powStages = new ArrayList<>();
    }

//  node length, page
//18, card = http://zh.tos.wikia.com/wiki/001
//28, card = http://zh.tos.wikia.com/wiki/024
//16, card = http://zh.tos.wikia.com/wiki/1001
//22, card = http://zh.tos.wikia.com/wiki/1017
//32, card = http://zh.tos.wikia.com/wiki/1063
//24, card = http://zh.tos.wikia.com/wiki/651
//31, card = http://zh.tos.wikia.com/wiki/656

    public TosCard asTosCard(CardInfo info) {
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

        if (c.evolveFrom.length() > 0 && !c.idNorm.equals(c.evolveFrom)) {
            log.log("Evolve not self? %s", c.wikiLink);
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
        }

        if (c.skillAmeliorationBattleName.length()> 0 && c.skillAmeliorationCost1 == 0) {
            log.log("HaveAme but no cost? %s", c.wikiLink);
        }
    }

    private void fillCommon(TosCard c, CardInfo info) {
        fillLinks(c, info);
        fillCardIds(c, info.data.subList(0, 10));
        fillHPValues(c, info.hpValues);
        fillExpInfo(c, info.expInfos);
        fillCombination(c, info);
        fillEvolution(c, info);
        c.cardDetails = info.detailsHtml;
        fillStageLinks(c, info);
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
        // Depends on CardFetcher's anchors
        if (info.anchors[5] < 0) return;

        List<String> list = info.cardTds.getEvolve();

        int plus = list.indexOf("EvoPlus");
        int end = list.lastIndexOf("EvoArrow");
        int fmIndex = -1;
        if (plus > 0) {
            fmIndex = plus;
        } else if (end > 0) {
            fmIndex = end;
        }
        if (plus > 0 && fmIndex > 0) {
            c.evolveFrom = list.get(fmIndex - 1);

            if (0 < end && end < list.size() - 1) {
                c.evolveTo = list.get(end + 1);
                c.evolveNeed = fmIndex == end
                        ? new ArrayList<>()
                        : new ArrayList<>(list.subList(fmIndex + 1, end));
            }
        }

        // Normalize
        c.evolveFrom = normEvoId(c.evolveFrom);
        c.evolveTo = normEvoId(c.evolveTo);
        for (int i = 0; i < c.evolveNeed.size(); i++) {
            String s = c.evolveNeed.get(i);
            c.evolveNeed.set(i, normEvoId(s));
        }
    }

    private void fillCombination(TosCard c, CardInfo info) {
        if (info.anchors[6] < 0) return;

        List<String> list = info.cardTds.getCombine();

        // Omit tail, Fill in the combine material
        for (int i = 0; i < list.size() - 1; i++) {
            String s = list.get(i);
            boolean endI = s.endsWith("i");
            boolean evos = s.contains("Evo"); // EvoPlus or EvoArrow
            if (endI && !evos) {
                c.combineFrom.add(normEvoId(s));
            }
        }

        // Add the combined card
        int end = list.lastIndexOf("EvoArrow");
        if (end < list.size() - 1) {
            c.combineTo.add(normEvoId(list.get(end + 1)));
        }
    }

    private void fillStageLinks(TosCard c, CardInfo info) {
        List<String> list;
        // Fill in Amelioration stage name & link
        list = info.ameStages;
        if (list.size() > 0) {
            c.skillAmeliorationBattleName = list.get(0);
            c.skillAmeliorationBattleLink = list.get(1);
        }

        list = info.awkStages;
        if (list.size() > 0) {
            c.skillAwakenRecallName = list.get(0);
            c.skillAwakenRecallBattleName = list.get(1);
            c.skillAwakenRecallBattleLink = list.get(2);
        }

        list = info.powStages;
        if (list.size() > 0) {
            c.skillPowBattleName = list.get(0);
            c.skillPowBattleLink = list.get(1);
            //c.skillAwakenRecallBattleName = list.get(1);
            //c.skillAwakenRecallBattleLink = list.get(2);
        }
    }

    private String normEvoId(String s) {
        boolean endI = s != null && s.endsWith("i");
        if (endI) {
            // Parse "12i" to "0012"
            return String.format(Locale.US, "%04d", Integer.parseInt(s.substring(0, s.length() - 1)));
        } else {
            return s;
        }
    }

    private void fillCardIds(TosCard c, List<String> list) {
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

        setNormId(c);
    }

    // Fill in Normalized ID
    private void setNormId(TosCard c) {
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

    private void fillSkillActive(TosCard c, List<String> list) {
        //-- Skill Active name #10
        c.skillName1 = list.get(0);
        c.skillCDMin1 = Integer.parseInt(list.get(1));
        c.skillCDMax1 = Integer.parseInt(list.get(2));
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
        c.skillAmeliorationName1 = list.get(0);
        c.skillAmeliorationCost1 = Integer.parseInt(list.get(1));
        c.skillAmeliorationName2 = list.get(2);
        c.skillAmeliorationCost2 = Integer.parseInt(list.get(3));
        if (list.size() <= 4) return;

        c.skillAmeliorationName3 = list.get(4);
        c.skillAmeliorationCost3 = Integer.parseInt(list.get(5));
        c.skillAmeliorationName4 = list.get(6);
        c.skillAmeliorationCost4 = Integer.parseInt(list.get(7));
    }

}
