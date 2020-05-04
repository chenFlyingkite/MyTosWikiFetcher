package main.fetcher.data.mypack;

import com.google.gson.annotations.SerializedName;

public class PackBaseResponse {
    @SerializedName("isSuccess")
    public int isSuccess;

    @SerializedName("errorCode")
    public int errorCode;

    @SerializedName("errorMessage")
    public String errorMessage = "";
}
