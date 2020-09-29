package main.fetcher.data.mypack;

import com.google.gson.annotations.SerializedName;

public class PackTokenResponse extends PackBaseResponse {

    @SerializedName("token")
    public String token = "";

    @SerializedName("user")
    public PackTokenUser user;
}
