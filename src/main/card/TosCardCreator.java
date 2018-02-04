package main.card;

import java.util.ArrayList;
import java.util.List;

public class TosCardCreator {
    private TosCardCreator() {}
    public static final TosCardCreator me = new TosCardCreator();

    public static class CardInfo {
        public List<String> data = new ArrayList<>();
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
        String icon = info.icon;
        String bigImg = info.bigImage;
        String link = info.wikiLink;

        TosCard c = new TosCard();

        fillImage(c, icon, bigImg, link);
        fillBasic(c, list.subList(0, 10));
        //-- Skill Active name #10
        fillSkillActive(c, list.subList(11, 15));
        //-- Skill Leader name #15
        fillSkillLeader(c, list.subList(16, 18));

        return c;
    }

    private TosCard asTosCard_28(CardInfo info) {
        List<String> list = info.data;
        String icon = info.icon;
        String bigImg = info.bigImage;
        String link = info.wikiLink;

        TosCard c = new TosCard();

        fillImage(c, icon, bigImg, link);
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
        String icon = info.icon;
        String bigImg = info.bigImage;
        String link = info.wikiLink;

        TosCard c = new TosCard();

        fillImage(c, icon, bigImg, link);
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
        String icon = info.icon;
        String bigImg = info.bigImage;
        String link = info.wikiLink;

        TosCard c = new TosCard();

        fillImage(c, icon, bigImg, link);
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
        String icon = info.icon;
        String bigImg = info.bigImage;
        String link = info.wikiLink;

        TosCard c = new TosCard();
        fillImage(c, icon, bigImg, link);

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
        String icon = info.icon;
        String bigImg = info.bigImage;
        String link = info.wikiLink;

        TosCard c = new TosCard();
        fillImage(c, icon, bigImg, link);

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
        String icon = info.icon;
        String bigImg = info.bigImage;
        String link = info.wikiLink;

        TosCard c = new TosCard();

        fillImage(c, icon, bigImg, link);
        fillBasic(c, list.subList(0, 10));
        //-- Skill Active name #10
        fillSkillActive(c, list.subList(11, 15));
        fillSkillActive2(c, list.subList(15, 19));
        //-- Skill Leader name #19
        fillSkillLeader(c, list.subList(20, 22));
        fillAmelioration(c, list.subList(23, 31));

        return c;
    }

    private void fillImage(TosCard c, String icon, String bigImg, String link) {
        c.icon = icon;
        c.bigImage = bigImg;
        c.wikiLink = link;
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
        c.idNorm = String.format("%04d", Integer.parseInt(c.id));
    }

    private void fillSkillActive(TosCard c, List<String> list) {
        //-- Skill Active name #10
        c.skillName = list.get(0);
        c.skillCDMin = Integer.parseInt(list.get(1));
        c.skillCDMax = Integer.parseInt(list.get(2));
        c.skillDesc = list.get(3);
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
