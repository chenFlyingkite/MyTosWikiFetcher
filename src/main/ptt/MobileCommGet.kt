package main.ptt

import org.jsoup.select.Elements

open class MobileCommGet {
    companion object me {
        fun get(elements: Elements?) : List<MobileCommArticle> {
            val list = ArrayList<MobileCommArticle>();
            if (elements != null) {
                for (ei in elements) {
                    val nrec = ei.getElementsByClass("nrec")
                    val mark = ei.getElementsByClass("mark")
                    val title = ei.getElementsByClass("title")
                    val date = ei.getElementsByClass("date")
                    val author = ei.getElementsByClass("author")
                    val links = title?.get(0)?.getElementsByAttribute("href")
                    var link = ""
                    if (links != null && links.size > 0) {
                        link = links.get(0)?.attr("href") ?: ""
                    }
                    list.add(0, MobileCommArticle(nrec.text(), mark.text(),
                            title.text(), link, date.text(), author.text()
                    ))
                }
            }
            return list
        }
    }
}