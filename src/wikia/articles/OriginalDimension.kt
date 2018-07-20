package wikia.articles

import com.google.gson.annotations.SerializedName

open class OriginalDimension (
    @SerializedName("width")
    val width :Int = 0
    ,
    @SerializedName("height")
    val height :Int = 0
)
{
    override fun toString(): String {
        return "$width x $height"
    }
}