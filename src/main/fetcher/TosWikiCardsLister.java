package main.fetcher;

import flyingkite.log.LF;
import flyingkite.tool.GsonUtil;
import main.kt.CardItem;
import main.kt.TosGet;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

// Listing card links from /wiki/圖鑑
public class TosWikiCardsLister extends TosWikiBaseFetcher {
    public static final TosWikiCardsLister me = new TosWikiCardsLister();
    private static final String folder = "myCard";
    private LF mLf = new LF(folder, "log_links.txt");
    private LF mLfLink = new LF(folder, "links.txt");
    public String VALAID_LINKS = mLfLink.getFile().getFile().toString();

    // /wiki/圖鑑
    //private static final String tosCardLists = "http://zh.tos.wikia.com/wiki/%E5%9C%96%E9%91%92";
    private static final String tosCardLists = "https://tos.fandom.com/zh/wiki/%E5%9C%96%E9%91%92";

    @Override
    public void run() {
        mLf.getFile().open(false);
        List<String> groups = getCardGroups();
        mLf.log("%s cards groups", groups.size());
        int n = 0;
        List<CardItem> allItems = new ArrayList<>();
        for (String s : groups) {
            mLf.log("%s", s);
            List<CardItem> cards = getCardsInLink(s);
            allItems.addAll(cards);
            n += cards.size();

            // Print out all the cards
            if (false) {
                printList(cards, mLf, "cards");
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

    // https://tos.fandom.com/zh/api.php
    // https://tos.fandom.com/zh/api.php?format=json&action=expandtemplates&text=%7B%7B1234%7CfullstatsMax}}

}
