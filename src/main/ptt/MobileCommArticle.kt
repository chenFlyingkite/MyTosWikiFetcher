package main.ptt

open class MobileCommArticle(
    val nrec : String?
    ,
    val mark : String?
    ,
    val title : String?
    ,
    val link : String?
    ,
    val date : String?
    ,
    val author : String?
) {
    override fun toString() : String {
        return "$nrec, $mark, $author, $date , $title, $link"
    }

}