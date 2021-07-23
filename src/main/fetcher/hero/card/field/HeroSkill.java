package main.fetcher.hero.card.field;

import com.google.gson.annotations.SerializedName;

public class HeroSkill {
    @SerializedName("name")
    public String name = "";

    @SerializedName("content")
    public String content = "";

    @SerializedName("view")
    public int view = 0;

    public HeroSkill() {

    }

    public HeroSkill(int v, String n, String c) {
        name = n;
        content = c;
        view = v;
    }

    @Override
    public String toString() {
        return String.format("view = %d, %s = %s", view, name, content);
    }
}
