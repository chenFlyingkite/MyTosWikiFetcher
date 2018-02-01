package main.card

import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class TosGet {
    companion object me {

        fun getImage(element: Element): String {
            val nos = element.getElementsByTag("noscript")
            val x: Element
            val imgs: Elements?
            val size = nos?.size ?: 0;
            if (size > 0) {
                x = nos[0]
                imgs = x?.getElementsByTag("img")
                val s = imgs?.size ?: 0
                if (s > 0) {
                    return imgs?.get(0)?.attr("src") ?: ""
                }
            }
            return ""
        }

        fun getTd(element: Element): MutableList<String>? {
            val td = element.getElementsByTag("td")
            val n = td?.size ?: 0;
            if (n > 0) {
                return td.eachText()
            }
            return null;
        }
    }
}
//
//fun TosGet.getImage(element: Element) :String {
//    val nos = element.getElementsByTag("noscript")
//    val x: Element
//    val imgs: Elements?
//    val size = nos?.size ?: 0;
//    if (size > 0) {
//        x = nos[0]
//        imgs = x?.getElementsByTag("img")
//        val s = imgs?.size ?: 0
//        if (s > 0) {
//            return imgs?.get(0)?.attr("src") ?: ""
//        }
//    }
//    return ""
//}