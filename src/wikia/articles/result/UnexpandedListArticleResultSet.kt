package wikia.articles.result;

import com.google.gson.annotations.SerializedName
import wikia.articles.UnexpandedArticle

/**
 * Get articles list in alphabetical order
 * URL: /api/v1/Articles/List
 */
open class UnexpandedListArticleResultSet (
    @SerializedName("items")
    val items: Array<UnexpandedArticle>? = null
    ,
    @SerializedName("offset")
    val offset: String? = null
    ,
    @SerializedName("basepath")
    val basePath: String? = null
){
    /*
    @SerializedName("items")
    val items: Array<UnexpandedArticle>? = null,

    @SerializedName("offset")
    val offset: String? = null,

    @SerializedName("basepath")
    val basePath: String? = null
    */

    override fun toString(): String {
        val size = items?.size ?: "N/A"

        return "($size items, basePath = $basePath, offset = $offset)"
    }
}

// Should we use this?  But data class is final ...
/*
open class UnexpandedListArticleResultSet(
        @SerializedName("items")
        private val items: Array<UnexpandedArticle>? = null,

        @SerializedName("offset")
        private val offset: String? = null,

        @SerializedName("basepath")
        private val basePath: String? = null
){

    override fun toString(): String {
        val size = items?.size ?: "N/A"
        return "($size items, offset = $offset, basePath = $basePath)"
    }
}
*/