package main.fetcher;

import flyingkite.log.LF;
import wikia.articles.NewArticleResultSet;
import wikia.articles.result.WAResult;

/**
 * 維基動態 最近動態
 */
public class TosWikiArticlesFetcher extends TosWikiBaseFetcher {
    public static final TosWikiArticlesFetcher me = new TosWikiArticlesFetcher();
    private static final String folder = "myWikiArticle";
    private LF mLf = new LF(folder);
    //private LF mLfSkills = new LF(folder, "actSkills");

    // Get list of new articles on this wiki
    // URL: /api/v1/Articles/New
    // http://zh.tos.wikia.com/api/v1#!/Articles/getNew_get_6
    private final String tosApi = "http://zh.tos.wikia.com/api/v1/Articles/New?minArticleQuality=10&limit=200";


    @Override
    public void run() {
        NewArticle nars = mGson.fromJson(getApiBody(tosApi, mLf), NewArticle.class);

        int size = nars.getItems().size();
        mLf.getFile().open();
        if (size > 0) {
            mLf.log("%s items in /api/v1/Articles/New", size);
            mLf.log("id, creation_date, creator, title, url");
        }
        for (int i = 0; i < nars.getItems().size(); i++) {
            NewArticleResultSet a = nars.getItems().get(i);
            mLf.log("%s, %s, %s, %s, %s"
                    , a.getId(), a.getCreation_date(), a.getCreator(), a.getTitle()
                    , nars.getBasepath() + a.getUrl());
        }
        mLf.getFile().close();
    }

    public final class NewArticle extends WAResult<NewArticleResultSet> {
    }
}
