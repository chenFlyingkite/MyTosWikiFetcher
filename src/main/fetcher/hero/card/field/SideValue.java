package main.fetcher.hero.card.field;

import com.google.gson.annotations.SerializedName;

public class SideValue {
    @SerializedName("level")
    public int level = 0;

    @SerializedName("hp")
    public int hp = 0;

    @SerializedName("attack")
    public int attack = 0;

    @SerializedName("speed")
    public int speed = 0;

    @SerializedName("view")
    public int view = 0;

    public SideValue() {

    }

    public SideValue(int lv, int h, int atk, int spd, int vp) {
        level = lv;
        hp = h;
        attack = atk;
        speed = spd;
        view = vp;
    }

    @Override
    public String toString() {
        return String.format("LV %d, HP = %d, ATK = %d, SPD = %d, view = %d"
                , level, hp, attack, speed, view);
    }
}
