package main.fetcher.data.mypack;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PackResponse extends PackBaseResponse{

    @SerializedName("userData")
    public PackUserData userData;

    @SerializedName("cards")
    public List<PackCard> card = new ArrayList<>();

}
