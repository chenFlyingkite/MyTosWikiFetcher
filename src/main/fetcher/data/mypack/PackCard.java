package main.fetcher.data.mypack;

import com.google.gson.annotations.SerializedName;

public class PackCard {
    @SerializedName("id")
    public int id;

    @SerializedName("level")
    public int level;

    @SerializedName("index")
    public int index;

    @SerializedName("skillLevel")
    public int skillLevel;

    @SerializedName("enhanceLevel")
    public int enhanceLevel;

    @SerializedName("acquiredAt")
    public long acquiredAt;

}
/*
"cards": [
    {
      "level": 99,
      "id": 1046,
      "index": 1,
      "skillLevel": 12,
      "enhanceLevel": 4,
      "acquiredAt": 1383546309
    },
*/