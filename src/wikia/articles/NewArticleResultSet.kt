package wikia.articles

import com.google.gson.annotations.SerializedName

open class NewArticleResultSet (
    @SerializedName("quality")
    val quality :Int = 0
    ,
    @SerializedName("original_dimensions")
    val original_dimensions :OriginalDimension = OriginalDimension()
    ,
    @SerializedName("url")
    val url :String = ""
    ,
    @SerializedName("ns")
    val ns :Int = 0
    ,
    @SerializedName("abstract")
    val abstract :String = ""
    ,
    @SerializedName("creator")
    val creator: Creator = Creator()
    ,
    @SerializedName("thumbnail")
    val thumbnail :String = ""
    ,
    @SerializedName("creation_date")
    val creation_date :String = ""
    ,
    @SerializedName("id")
    val id :Int = 0
    ,
    @SerializedName("title")
    val title :String = ""
) {
    override fun toString() : String {
        return "#$id : $creation_date, ${creator}, $title, $url"
    }
}