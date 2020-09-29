package main.fetcher.data.mypack;

import com.google.gson.annotations.SerializedName;

public class PackTokenUser {
    @SerializedName("uid")
    public long uid;

    @SerializedName("name")
    public String name = "";

    @SerializedName("campaignLoginDays")
    public int campaignLoginDays;

    @SerializedName("level")
    public int level;

    @SerializedName("role")
    public int role;
}
