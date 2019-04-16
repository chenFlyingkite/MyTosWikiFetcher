package main.card;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class EvolvePath {

    /** Evolution material card idNorm */
    @SerializedName(TC.evLn)
    public List<String> evolvePath = new ArrayList<>();

    /** Evolution to card idNorm */
    @SerializedName(TC.evTo)
    public String evolveTo = "";

    @Override
    public String toString() {
        return String.format("%s <= %s", evolveTo, evolvePath);
    }
}
