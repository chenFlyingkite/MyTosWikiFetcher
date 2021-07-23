package main.fetcher.hero.card.field;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Sidekick {
    @SerializedName("idNorm")
    public String idNorm = "";

    @SerializedName("nameEn")
    public String nameEn = "";

    @SerializedName("nameJa")
    public String nameJa = "";

    @SerializedName("heroSkills")
    public List<HeroSkill> skills = new ArrayList<>();

    @SerializedName("equips")
    public List<SideSkill> equips = new ArrayList<>();

    @SerializedName("heroValues")
    public List<SideValue> status = new ArrayList<>();
}
