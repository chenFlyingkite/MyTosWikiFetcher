package main.card

import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.util.*
import kotlin.collections.ArrayList

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
            val td2 = element.getElementsByTag("td")

            val td = ArrayList<String>()
            for (e in td2) {
                // omit the big table
                val noNeed = e.text().isEmpty() ||
                        // Table of big content of "基本屬性",
                        // <td rowspan="3" colspan="10" style="display:none" class="hidden">
                        e.attr("class").toLowerCase() == "hidden"
                if (!noNeed) {
                    td.add(e.text());
                }
            }
            if (td.size > 0) {
                return td
            }
            return null
        }

        @Deprecated("Not handy")
        fun getTr(element: Element): MutableList<String>? {
            val tbody = element.child(0)?.child(0)
            val chi = tbody?.children()
            val trs = ArrayList<Element>()
            if (chi != null) {
                chi.forEachIndexed { i, ele -> run {
                    // Add the elements of not in no
                    val no = listOf(4, 5, 6)
                    if (!no.contains(i)) {
                        trs.add(ele)
                    }
                }}
            }

            val td2 = element.getElementsByTag("tr")


            val td = ArrayList<String>()
            for (e in td2) {
                if (e.text().isNotEmpty() && e.attr("class").toLowerCase() != "hidden") {
                    td.add(e.text());
                }
            }
            if (td.size > 0) {
                return td
            }
            return null
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