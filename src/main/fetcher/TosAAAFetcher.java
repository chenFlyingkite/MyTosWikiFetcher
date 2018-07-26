package main.fetcher;

import flyingkite.log.LF;

public class TosAAAFetcher extends TosWikiBaseFetcher {
    private TosAAAFetcher() {}
    public static final TosAAAFetcher me = new TosAAAFetcher();
    private static final String folder = "myEnemySkill";
    private LF mLf = new LF(folder);

    private String getPage() {
        // 關卡敵人技能/敵人技能列表
        return "http://zh.tos.wikia.com/wiki/%E9%97%9C%E5%8D%A1%E6%95%B5%E4%BA%BA%E6%8A%80%E8%83%BD/%E6%95%B5%E4%BA%BA%E6%8A%80%E8%83%BD%E5%88%97%E8%A1%A8";
    }

    @Override
    public void run() {
        mLf.getFile().open(false);
        clock.tic();
        // Start here



        clock.tac("%s Done", tag());
        mLf.getFile().close();
    }
}
