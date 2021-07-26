package main.fetcher.hero.card.field;

import com.google.gson.annotations.SerializedName;

public class SideSkill {
    @SerializedName("name")
    public String name = "";

    @SerializedName("content")
    public String content = "";

    public SideSkill() {

    }

    public SideSkill(String n, String c) {
        name = n;
        content = c;
    }

    @Override
    public String toString() {
        return String.format("%s = %s", name, content);
    }
}
