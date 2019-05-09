package main.fetcher;

import main.card.TosCard;

public class TosWikiFixer {

    private enum Fixes {
        // Fixme : Fix ids 0296 -> 6006, 妖蛋造型
        // FIXME : http://zh.tos.wikia.com/wiki/6006

        // Fixme : Fix ids 0408 -> 6011, 主角造型
        // FIXME : http://zh.tos.wikia.com/wiki/6011

        // Fixme : Fix ids 0455 -> 6064, 暗地精造型
        // FIXME : http://zh.tos.wikia.com/wiki/6064

        // Fixme : Fix ids 0481 -> 6001, 妹子造型
        // FIXME : http://zh.tos.wikia.com/wiki/6001

        // Fixme : Fix ids 0595 -> 6036, 妲己造型
        // FIXME : http://zh.tos.wikia.com/wiki/6036

        // Fixme : Fix ids 0641 -> 6070, 埃及神造型
        // FIXME : http://zh.tos.wikia.com/wiki/6070

        // Fixme : Fix ids 0674 -> 6118, 薩胖造型
        // FIXME : http://zh.tos.wikia.com/wiki/6118

        // Fixme : Fix ids 0703 -> 6125, 日月郎動態造型
        // FIXME : http://zh.tos.wikia.com/wiki/6118

        // Fixme : Fix ids 0742 -> 6128, 樹王動態造型
        // FIXME : http://zh.tos.wikia.com/wiki/6128

        // Fixme : Fix ids 0822 -> 6041, 天秤造型
        // FIXME : http://zh.tos.wikia.com/wiki/6041

        // Fixme : Fix ids 0827 -> 6148, 雙魚造型
        // FIXME : http://zh.tos.wikia.com/wiki/6048

        // Fixme : Fix ids 0998 -> 6063, 路西法造型
        // FIXME : http://zh.tos.wikia.com/wiki/6063

        // Fixme : Fix ids 1022 -> 6048, 北歐造型
        // FIXME : http://zh.tos.wikia.com/wiki/6048

        // Fixme : Fix ids 1041 -> 6065, 封神造型
        // FIXME : http://zh.tos.wikia.com/wiki/6065

        // Fixme : Fix ids 1046 -> 6038, 三巫造型
        // FIXME : http://zh.tos.wikia.com/wiki/6038

        // Fixme : Fix ids 1066 -> 6075, 龍使造型
        // FIXME : http://zh.tos.wikia.com/wiki/6075

        // Fixme : Fix ids 1089 -> 6037, 轉生妲己造型
        // FIXME : http://zh.tos.wikia.com/wiki/6037

        // Fixme : Fix ids 1091 -> 6017, 怪物彈珠造型
        // FIXME : http://zh.tos.wikia.com/wiki/6037

        // Fixme : Fix ids 1102 -> 6053, 兼具史原魔造型
        // FIXME : http://zh.tos.wikia.com/wiki/6037

        // Fixme : Fix ids 1134 -> 6026, 加美什造型
        // FIXME : http://zh.tos.wikia.com/wiki/6037

        // Fixme : Fix ids 1154 , 遺失暗芙 (誤植光)
        // FIXME : http://zh.tos.wikia.com/wiki/1154

        // Fixme : Fix ids 1164 -> 6034, 假面判官造型
        // FIXME : http://zh.tos.wikia.com/wiki/6034

        // Fixme : Fix ids 1189 -> 6027, 水火黑妍希造型
        // FIXME : http://zh.tos.wikia.com/wiki/6027

        // Fixme : Fix ids 1193 -> 6142, 木暗玩具造型
        // FIXME : http://zh.tos.wikia.com/wiki/6142

        // Fixme : Fix ids 1198 -> 6035, 光魔記版派蒙造型
        // FIXME : http://zh.tos.wikia.com/wiki/6035

        // Fixme : Fix ids 1216 -> 6029, 巴比倫造型
        // FIXME : http://zh.tos.wikia.com/wiki/6029

        // Fixme : Fix ids 1239 -> 6058, 切西亞造型
        // FIXME : http://zh.tos.wikia.com/wiki/6058

        // Fixme : Fix ids 1245 -> 6042, 撒旦造型
        // FIXME : http://zh.tos.wikia.com/wiki/6042

        // Fixme : Fix ids 1245 -> 6042, 火木光暗童話魔造型
        // FIXME : http://zh.tos.wikia.com/wiki/6042

        // Fixme : Fix ids 1291 -> 6059, 呂布造型
        // FIXME : http://zh.tos.wikia.com/wiki/6059

        // Fixme : Fix ids 1313 -> 6115, 聖酒女武神造型
        // FIXME : http://zh.tos.wikia.com/wiki/6115

        // Fixme : Fix ids 1346, 1348, 1349 無 萊昂內爾
        // FIXME : http://zh.tos.wikia.com/wiki/1347

        // Fixme : Fix ids 1406 -> 6092, 德魯伊造型
        // FIXME : http://zh.tos.wikia.com/wiki/6092

        // Fixme : Fix ids 1406 -> 6092, 伊邪那岐造型
        // FIXME : http://zh.tos.wikia.com/wiki/6092

        // Fixme : Fix ids 1500 -> 6080, 神魔小妹造型
        // FIXME : http://zh.tos.wikia.com/wiki/6080

        // Fixme : Fix ids 1518 -> 6106, 棄天地造型
        // FIXME : http://zh.tos.wikia.com/wiki/6106

        // Fixme : Fix ids 1523, 無卡
        // FIXME : http://zh.tos.wikia.com/wiki/1522

        // Fixme : Fix ids 1572 > 6081, 拳皇造型
        // FIXME : http://zh.tos.wikia.com/wiki/6081

        // Fixme : Fix ids 1604 > 6098, 妖嬈花夢 櫻花造型
        // FIXME : http://zh.tos.wikia.com/wiki/6098

        // Fixme : Fix ids 1639 > 6107, 宇宙序章 火魔2消造型
        // FIXME : http://zh.tos.wikia.com/wiki/6107

        // Fixme : Fix ids 1672 > 6134, 三國2造型
        // FIXME : http://zh.tos.wikia.com/wiki/6134
        a("", null);

        private String wikiLink;
        private Fixer fixer;

        Fixes(String link, Fixer fix) {
            wikiLink = link;
            fixer = fix;
        }
    }

    private interface Fixer {
        TosCard fix(TosCard card);
    }

    public void fixIfNeed(TosCard card) {

    }

}
