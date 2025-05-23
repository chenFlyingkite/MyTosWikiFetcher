package main.fetcher.hero.card.field;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Hero {
    @SerializedName("nameEn")
    public String nameEn = "";

    @SerializedName("nameJa")
    public String nameJa = "";

    // initial rank when get from Gotcha
    @SerializedName("rankFirst")
    public int rankFirst;

    @SerializedName("designer")
    public String designer = "";

    @SerializedName("CV")
    public String characterVoice = "";

    ///-- Hero
    @SerializedName("attribute")
    public String attribute = "";

    // 役割
    @SerializedName("role")
    public String role = "";

    // Hero's main three skill + last plus skill
    // wolfman has no plus skill
    @SerializedName("heroSkills")
    public List<HeroSkill> heroSkills = new ArrayList<>();

    // Hero's value table for level 30, 40, 50
    @SerializedName("heroValues")
    public List<HeroValue> heroValues = new ArrayList<>();

    // sidekick skill + 2 plus skill
    @SerializedName("sideSkills")
    public List<HeroSkill> sideSkills = new ArrayList<>();

    // sidekick equip + 5 plus skill
    @SerializedName("sideEquips")
    public List<SideSkill> sideEquips = new ArrayList<>();

    // side values for level, 1, 50, 60, 70, 80, 90, 100
    @SerializedName("sideValues")
    public List<SideValue> sideValues = new ArrayList<>();

    // generated, hero[heroPlus] has skill +
    @SerializedName("heroPlus")
    public int heroPlus = -1;

    @SerializedName("passive")
    public List<HeroSkill> passive = new ArrayList<>();

    public String title() {
        return nameJa + ", " + attribute + ", " + role;
    }

    public String heroRelated() {
        String s = "";
        if (heroSkills.size() > 0) {
            s += heroSkills.get(heroSkills.size() - 1);
        } else {
            s += "--";
        }
        s += ", ";
        if (heroValues.size() > 0) {
            s += heroValues.get(heroValues.size() - 1);
        } else {
            s += "--";
        }
        return s;
    }

    public String sidekickRelated() {
        String s = "";
        if (sideSkills.size() > 0) {
            s += sideSkills.get(sideSkills.size() - 1);
        } else {
            s += "--";
        }
        s += ", ";
        if (sideEquips.size() > 0) {
            s += sideEquips.get(sideEquips.size() - 1);
        } else {
            s += "--";
        }
        return s;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
