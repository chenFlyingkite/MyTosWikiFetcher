package main.card

import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.awt.SystemColor.info

class TosGet {
    companion object me {
        private val printTdHtml = false

        // Extract for TosCard's big image and links
        // http://zh.tos.wikia.com/wiki/001
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

        fun getHtml(element: Element): String {
            return element?.html() ?: ""
        }

        fun getHtmlAt(index: Int, elements: Elements): String {
            return elements?.get(index)?.html() ?: ""
        }

        fun getSummonerTable(index: Int, elements: Elements): TableInfo {
            val info = TableInfo()
            val no = index >= elements.size
            if (no) return info

            val element = elements.get(index)
            val ths = element.getElementsByTag("th");
            ths.forEachIndexed { i, ele -> run {
                info.headers.add(ele.text())
            }}

            val tds = element.getElementsByTag("td");
            tds.forEachIndexed { i, ele -> run {
                info.cells.add(ele.text())
            }}
            return info;
        }

        // Extract for TosCard
        // http://zh.tos.wikia.com/wiki/001
        fun getCardTds(element: Element): CardTds? {
            val td2 = element.getElementsByTag("td")

            val result = CardTds()
            val td = result.Tds
            val evos = result.Evolutions
            var x = 0
            for (e in td2) {
                val tables = e.getElementsByTag("table")?.size ?: 0

                // omit the big table
                val noNeed = e.text().isEmpty()
                        // Table of big content of "基本屬性",
                        // <td rowspan="3" colspan="10" style="display:none" class="hidden">
                        || e.attr("class").toLowerCase() == "hidden"
                        // Inner html contains <table>, like "昇華"
                        || tables > 0

                // Takes the images of evolution
                val noscript = e.getElementsByTag("noscript")

                if (!noNeed) {
                    td.add(e.text())
                } else if (noscript.size > 0) {
                    x++
                    // We want the evolution's icons
                    for (nos in noscript) {
                        val imgs = nos.getElementsByTag("img")
                        if (imgs.size > 0) {
                            val alt = imgs[0].attr("alt")
                            if (alt != null) {
                                evos.add(alt)
                            }
                        }
                    }
                    //td.add(e.html())
                } else {

                }

                if (printTdHtml) {
                    print("----text (${noscript.size})----")
                    print(e.text())
                    println("--------")
                    print("----html----")
                    print(e.html())
                    println("--------")
                }
            }
            if (printTdHtml) {
                println("--- x = $x -----")
            }
            if (td.size > 0) {
                return result
            }
            return null
        }

        @Deprecated("Not handy")
        fun getTr(element: Element): MutableList<String>? {
            val tbody = element.child(0)?.child(0)
            val chi = tbody?.children()
            val trs = ArrayList<Element>()

            chi?.forEachIndexed { i, ele -> run {
                // Add the elements of not in no
                val no = listOf(4, 5, 6)
                if (!no.contains(i)) {
                    trs.add(ele)
                }
            }}

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

        // Extract for icons
        // view-source:http://towerofsaviors.wikia.com/wiki/File:InvWater.png
        fun getIcon(element: Element): IconInfo {
            val es = element.getElementsByClass("internal")

            val n = es.size;
            val info = IconInfo()
            if (n > 0) {
                val e = es.get(0)
                info.link = e.attr("href")
                info.name = e.attr("title") // [0]
                //info.name = e.attr("download") // [1]
            }
            return info
        }
    }
}

class IconInfo {
    var link: String = ""
    var name: String = ""
}

class CardTds {
    val Tds: MutableList<String> = ArrayList()
    val Evolutions: MutableList<String> = ArrayList()
}

class TableInfo {
    val headers: ArrayList<String> = ArrayList()
    val cells: ArrayList<String> = ArrayList()
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