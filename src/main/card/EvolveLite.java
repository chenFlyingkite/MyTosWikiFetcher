package main.card;

import com.google.gson.annotations.SerializedName;

public class EvolveLite {

    /** Evolution from card idNorm */
    @SerializedName(TC.evFm)
    public String evolveFrom = "";

    /** Evolution to card idNorm */
    @SerializedName(TC.evTo)
    public String evolveTo = "";

    @Override
    public String toString() {
        return String.format("%s <= %s", evolveTo, evolveFrom);
    }
}
