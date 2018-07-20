package wikia.articles.result

import com.google.gson.annotations.SerializedName

/**
 * Get list of new articles on this wiki
 * URL: /api/v1/Articles/New
 *
 * Generic class for Wikia Article Results
 */
open class WAResult<T> (
    @SerializedName("items")
    val items :List<T> = ArrayList()
    ,
    @SerializedName("basepath")
    val basepath :String =""
) {
    override fun toString(): String {
        return "${items.size} items based at $basepath"
    }
}