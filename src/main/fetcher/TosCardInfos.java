package main.fetcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import flyingkite.log.L;
import flyingkite.log.LF;
import flyingkite.tool.GsonUtil;
import main.card.Evolve;
import main.card.EvolveLite;
import main.card.EvolvePath;
import main.card.TosCard;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class TosCardInfos implements Runnable {
    public static final TosCardInfos me = new TosCardInfos();

    private static final String folder = "myCard";
    private LF mCardJson = new LF(folder, "cardList.json");
    private File source = mCardJson.getFile().getFile();
    private File evo = new File(folder, "evo.json"); // [{from : to},]
    private File evoList = new File(folder, "evoL.json"); // [{to : [from3, from2, from1]},]

    @Override
    public void run() {
        List<TosCard> allCards = load();
        List<EvolveLite> lite = createEvolves(allCards);
        createEvolveList(lite);
    }

    private void createEvolveList(List<EvolveLite> lite) {
        Map<String, String> src = new TreeMap<>();
        for (int i = 0; i < lite.size(); i++) {
            EvolveLite e = lite.get(i);
            src.put(e.evolveTo, e.evolveFrom);
        }
        Set<String> to = new TreeSet<>(src.keySet());
        to.removeAll(src.values());
        L.log("%s evo paths", to.size());

        Map<String, EvolvePath> map = new TreeMap<>();
        for (String s : to) {
            EvolvePath p = new EvolvePath();
            p.evolveTo = s;
            String t = s;
            String f = src.get(t);
            p.evolvePath.add(t); // Add to
            // Add each from until no from, 1831 <= [1831, 0024, 0023, 0022, 0021]
            do {
                p.evolvePath.add(f);
                f = src.get(f);
            } while (f != null);
            map.put(s, p);
        }

        Gson g = new GsonBuilder().setPrettyPrinting().create();
        g = new Gson();
        GsonUtil.writeFile(evoList, g.toJson(map.values().toArray(), EvolvePath[].class));
    }

    private List<EvolveLite> createEvolves(List<TosCard> cards) {
        List<EvolveLite> lites = new ArrayList<>();

        Set<String> s = new TreeSet<>();
        for (int i = 0; i < cards.size(); i++) {
            TosCard c = cards.get(i);
            List<Evolve> e = c.evolveInfo;
            for (int j = 0; j < e.size(); j++) {
                Evolve ej = e.get(j);
                EvolveLite f = new EvolveLite();
                f.evolveFrom = ej.evolveFrom;
                f.evolveTo = ej.evolveTo;
                String key = f.evolveTo;
                if (!s.contains(key)) {
                    s.add(key);
                    lites.add(f);
                }
            }
        }
        L.log("%s evolutions of A <- B", lites.size());

        Collections.sort(lites, Comparator.comparing(o -> o.evolveTo));

        Gson g = new GsonBuilder().setPrettyPrinting().create();
        g = new Gson();
        GsonUtil.writeFile(evo, g.toJson(lites.toArray(), EvolveLite[].class));
        return lites;
    }

    private static String sc(TosCard c) {
        return "#" + c.idNorm + "," + c.name
                + "\n  " + c.skillDesc1 + "," + c.skillDesc2
                + "\n  " + c.skillLeaderDesc
                ;
        //return String.format("#%4s,%s\n      %s\n      %s", c.idNorm, c.name, c.skillDesc1 + "," + c.skillDesc2, c.skillLeaderDesc);
    }

    private List<TosCard> load() {
        TosCard[] allCards = GsonUtil.loadFile(source, TosCard[].class);
        return Arrays.asList(allCards);
    }
}
