package wikia.articles

import com.google.gson.annotations.SerializedName

open class Creator (
    @SerializedName("avatar")
    val avatar :String = ""
    ,
    @SerializedName("name")
    val name :String = ""
)
{
    override fun toString(): String {
        return name
    }
}