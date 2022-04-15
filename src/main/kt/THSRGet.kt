package main.kt

import flyingkite.log.L
import main.fetcher.data.thsr.THSRFare
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

open class THSRGet {
    companion object me {
        fun fare(es : Elements) : ArrayList<THSRFare> {
            var tryParsePrice = false
            val log = 0 > 0

            val ans = arrayListOf<THSRFare>()
            for (i in 0 until es.size) {
                val ei = es[i]
                val each = THSRFare()
                var title = firstTag(ei, arrayOf("h3", "caption"))
                each.name = title
                // stations
                var etrs = ei.getElementsByTag("tr")
                if (etrs.size == 0) return ans
                var ek = etrs[0].getElementsByTag("th")
                if (log) {
                    L.log("ek = %s, %s", ek.size, ek)
                }
                // stations
                val sta = arrayListOf<String>()
                for (j in 1 until ek.size) {
                    sta.add(ek[j].text())
                }
                val tbody = ei.getElementsByTag("tbody").last() // [0] fail for early bird
                ek = tbody.getElementsByTag("td")
                val STA = sta.size
                if (log) {
                    L.log("STA = %s, %s", STA, sta)
                    L.log("td = %s, %s", ek.size, ek)
                }
                for (col in 0 until STA) {
                    for (row in 0 until STA) {
                        val at = col * STA + row
                        if (at >= ek.size) continue
                        val key = "${sta[col]}-${sta[row]}"
                        val value = ek[at].text().trim()
                        if (log) {
                            L.log("${col} - ${row} : ${key} => ${value}")
                        }
                        each.prices[key] = value
                        if (tryParsePrice) {
                            val p = each.price(key) // try to read OK
                            L.log("${key} = $${p}")
                        }
                    }
                }
                ans.add(each)
            }
            return ans
        }

        private fun firstTag(ei : Element, tags : Array<String>) : String {
            for (i in 0 until tags.size) {
                val tag = tags[i]
                val e = ei.getElementsByTag(tag)
                if (e.size > 0) {
                    return e[0].text()
                }
            }
            return ""
        }
    }
}