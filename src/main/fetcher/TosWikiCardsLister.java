package main.fetcher;

import main.kt.CardItem;
import main.card.TosCard;
import main.kt.TosGet;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import flyingkite.log.LF;
import flyingkite.tool.GsonUtil;
import flyingkite.tool.IOUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TosWikiCardsLister extends TosWikiBaseFetcher {
    private TosWikiCardsLister() {}
    public static final TosWikiCardsLister me = new TosWikiCardsLister();
    private static final String folder = "myCard";
    private LF mLf = new LF(folder, "log_links.txt");
    private LF mLfLink = new LF(folder, "links.txt");
    public String VALAID_LINKS = mLfLink.getFile().getFile().toString();

    // /wiki/圖鑑
    private static final String tosCardLists = "http://zh.tos.wikia.com/wiki/%E5%9C%96%E9%91%92";
    private static final String TOS_ALL_CARD = TosWikiCardFetcher.me.TOS_ALL_CARD;

    @Override
    public void run() {
        mLf.getFile().open(false);
        List<String> groups = getCardGroups();
        mLf.log("%s cards groups", groups.size());
        int n = 0;
        List<CardItem> allItems = new ArrayList<>();
        for (String s : groups) {
            mLf.log("%s", s);
            List<CardItem> cards =  getCardsInLink(s);
            allItems.addAll(cards);
            n += cards.size();
            // Print out all the cards
            boolean print = false;
            if (print) {
                mLf.log("-   %s cards", cards.size());
                for (CardItem c : cards) {
                    mLf.log("--  %s", c);
                }
            }
        }
        mLf.log("%s cards in 圖鑑", n);
        allItems = removeInvalidCards(allItems);
        mLf.log("%s cards is 有效", allItems.size());
        CardItem[] a = allItems.toArray(new CardItem[0]);
        GsonUtil.writeFile(mLfLink.getFile().getFile(), mGson.toJson(a, CardItem[].class));
        mLf.getFile().close();
    }

    private List<CardItem> removeInvalidCards(List<CardItem> allItems) {
        // #6024 : S24 Terry(造型) -> http://zh.tos.wikia.com/wiki/Terry(%E9%80%A0%E5%9E%8B)
        // #6025 : S25 Terence(造型) -> http://zh.tos.wikia.com/wiki/Terence(%E9%80%A0%E5%9E%8B)
        String[] ids = {"6024", "6025"};

        List<CardItem> ans = new ArrayList<>();
        for (CardItem c : allItems) {
            boolean valid = true;
            for (int i = 0; i < ids.length; i++) {
                if (c.getId().equals(ids[i])) {
                    valid = false;
                }
            }
            if (valid) {
                ans.add(c);
            }
        }
        return ans;
    }

    private List<String> getCardGroups() {
        Document doc = getDocument(tosCardLists);

        Element main = doc.getElementById("mw-content-text");
        return TosGet.me.getLiAHref(main, wikiBaseZh);
    }

    private List<CardItem> getCardsInLink(String link) {
        Document doc = getDocument(link);

        Element main = doc.getElementById("mw-content-text");
        return TosGet.me.getCardItems(main, wikiBaseZh);
    }

    @Deprecated
    private void findMissingCard(List<CardItem> ids) {
        TosCard[] allCards = GsonUtil.load(IOUtil.getReader(TOS_ALL_CARD), TosCard[].class);
        Set<String> normIds = new HashSet<>();
        if (allCards != null) {
            for (TosCard c : allCards) {
                String key = c.idNorm;
                if (normIds.contains(key)) {
                    // Self checking to ensure no duplicate
                    throw new IllegalArgumentException(key);
                } else {
                    normIds.add(key);
                }
            }
        }

        for (CardItem c : ids) {
            if (normIds.contains(c.getId())) {

            } else {
                mLf.log("Missing %s", c);
            }
        }
    }
}
