package wikia.articles.result;

import com.google.gson.annotations.SerializedName
import wikia.articles.UnexpandedArticle

/**
 * Get articles list in alphabetical order
 * URL: /api/v1/Articles/List
 */
open class UnexpandedListArticleResultSet (
    @SerializedName("items")
    var items: List<UnexpandedArticle> = arrayListOf()
        // Array<UnexpandedArticle>? = null
    ,
    @SerializedName("offset")
    var offset: String? = null
    ,
    @SerializedName("basepath")
    var basePath: String? = null
){
    /*
    @SerializedName("items")
    val items: Array<UnexpandedArticle>? = null,

    @SerializedName("offset")
    val offset: String? = null,

    @SerializedName("basepath")
    val basePath: String? = null
    */

    override fun toString() : String {
        return "${items.size} items"
    }
}