package main.fetcher;

import main.card.TosCard;

public class TosWikiFixer {
    private TosWikiFixer() {}

    private enum Fixes {
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
