package main.card;

import util.logging.L;

import java.util.ArrayList;
import java.util.List;

public class TosCardCreator {
    private TosCardCreator() {}
    public static final TosCardCreator me = new TosCardCreator();

    public static class CardInfo {
        public List<String> data = new ArrayList<>();
        public List<String> evolution = new ArrayList<>();
        public String icon = "";
        public String bigImage = "";
        public String wikiLink = "";
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
        if (info == null) return null;

        int n = info.data.size();
        switch (n) {
            case 18: return asTosCard_18(info);
            case 28: return asTosCard_28(info);
            case 16: return asTosCard_16(info);
            case 22: return asTosCard_22(info);
            case 32: return asTosCard_32(info);
            case 24: return asTosCard_24(info);
            case 31: return asTosCard_31(info);
        }
        return null;
    }


    private TosCard asTosCard_18(CardInfo info) {
        List<String> list = info.data;

        TosCard c = new TosCard();

        fillImage(c, info);
        fillWikiEvolution(c, info);
        fillBasic(c, list.subList(0, 10));
        //-- Skill Active name #10
        fillSkillActive(c, list.subList(11, 15));
        //-- Skill Leader name #15
        fillSkillLeader(c, list.subList(16, 18));

        return c;
    }

    private TosCard asTosCard_28(CardInfo info) {
        List<String> list = info.data;

        TosCard c = new TosCard();

        fillImage(c, info);
        fillWikiEvolution(c, info);
        fillBasic(c, list.subList(0, 10));
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

        fillImage(c, info);
        fillWikiEvolution(c, info);
        fillBasic(c, list.subList(0, 10));
        //-- Skill Active name #10
        c.skillName = list.get(11);
        c.skillCDMin = 0;
        c.skillCDMax = 0;
        c.skillDesc = list.get(12);
        //-- Skill Leader name #15
        fillSkillLeader(c, list.subList(14, 16));

        return c;
    }

    private TosCard asTosCard_22(CardInfo info) {
        List<String> list = info.data;

        TosCard c = new TosCard();

        fillImage(c, info);
        fillWikiEvolution(c, info);

        fillBasic(c, list.subList(0, 10));
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
        fillImage(c, info);
        fillWikiEvolution(c, info);

        fillBasic(c, list.subList(0, 10));
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
        fillImage(c, info);
        fillWikiEvolution(c, info);

        fillBasic(c, list.subList(0, 10));
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

        fillImage(c, info);
        fillWikiEvolution(c, info);
        fillBasic(c, list.subList(0, 10));
        //-- Skill Active name #10
        fillSkillActive(c, list.subList(11, 15));
        fillSkillActive2(c, list.subList(15, 19));
        //-- Skill Leader name #19
        fillSkillLeader(c, list.subList(20, 22));
        fillAmelioration(c, list.subList(23, 31));

        return c;
    }

    private void fillImage(TosCard c, CardInfo info) {
        c.icon = info.icon;
        c.bigImage = info.bigImage;
    }

    private void fillWikiEvolution(TosCard c, CardInfo info) {
        String link = info.wikiLink;
        List<String> list = info.evolution;

        c.wikiLink = link;
        int from = list.indexOf("EvoArrow");
        int plus = list.indexOf("EvoPlus");
        int end = list.lastIndexOf("EvoArrow");
        if (from > 0) {
            c.evolveFrom = list.get(from - 1);
        }
        if (plus > 0 && end > 0) {
            c.evolveTo = list.get(end + 1);
            c.evolveNeed = new ArrayList<>(list.subList(plus + 1, end));
        } else {
            L.log("No evolutions in creator? %s", link);
        }

        // Normalize
        c.evolveFrom = normEvoId(c.evolveFrom);
        c.evolveTo = normEvoId(c.evolveTo);
        for (int i = 0; i < c.evolveNeed.size(); i++) {
            String s = c.evolveNeed.get(i);
            c.evolveNeed.set(i, normEvoId(s));
        }
    }

    private String normEvoId(String s) {
        boolean endI = s != null && s.endsWith("i");
        if (endI) {
            // Parse "12i" to "0012"
            return String.format("%04d", Integer.parseInt(s.substring(0, s.length() - 1)));
        } else {
            return s;
        }
    }

    private void fillBasic(TosCard c, List<String> list) {
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
        // Fill in Normalized ID
        //c.idNorm = String.format("%04d", Integer.parseInt(c.id));
        setNormId(c);
    }

    private void setNormId(TosCard c) {
        // Fill in Normalized ID
        c.idNorm = String.format("%04d", Integer.parseInt(c.id));

        int end = c.wikiLink.lastIndexOf("/") + 1;
        String s = c.wikiLink.substring(end);
        if (s.matches("[0-9]+")) {
            int num = Integer.parseInt(s);
            if (6000 <= num && num < 7000) { // This is those card of 造型, like "id": "481", -> 水妹
                c.idNorm = String.format("%04d", num);
            }
        }
    }

    private void fillSkillActive(TosCard c, List<String> list) {
        //-- Skill Active name #10
        c.skillName = list.get(0);
        try {
            c.skillCDMin = Integer.parseInt(list.get(1));
            c.skillCDMax = Integer.parseInt(list.get(2));
        } catch (NumberFormatException e) {
            L.log("Failed Active c = %s, %s, link = %s"
                    , c.idNorm, c.name, c.wikiLink);
            e.printStackTrace();
            c.skillCDMin = 0;
            c.skillCDMax = 0;
        }
        c.skillDesc = list.get(3);
    }

    private void fillSkillActive2(TosCard c, List<String> list) {
        //-- Skill Active name #10
        c.skillName2 = list.get(0);
        try {
            c.skillCDMin2 = Integer.parseInt(list.get(1));
            c.skillCDMax2 = Integer.parseInt(list.get(2));
        } catch (NumberFormatException e) {
            L.log("Failed Active #2 c = %s, %s, link = %s"
                    , c.idNorm, c.name, c.wikiLink);
            e.printStackTrace();
            c.skillCDMin2 = 0;
            c.skillCDMax2 = 0;
        }
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
