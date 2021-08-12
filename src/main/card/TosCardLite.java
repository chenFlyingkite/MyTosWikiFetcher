package main.card;

import com.google.gson.annotations.SerializedName;

public class TosCardLite {
    /** Normalized ID, in form of %04d */
    @SerializedName(TC.idNorm)
    public String idNorm = "";

    /** Http link to wiki page */
    @SerializedName(TC.name)
    public String name = "";

    @Override
    public String toString() {
        return idNorm + " (" + name + ")";
    }
}
