package main.kt

import org.jsoup.select.Elements
import java.util.ArrayList

class BotGet {
    companion object me {
        fun goldTable(es: Elements) : List<String> {
            val ans = ArrayList<String>()
            if (es.size < 1) return ans

            val e = es[0]
            val trs = e.getElementsByTag("tr")
            for (i in 1 until trs.size) {
                val tr = trs[i]
                val tds = tr.getElementsByTag("td")
                var s = ""
                for (td in tds) {
                    if (!s.isEmpty()) {
                        s += ","
                    }
                    s += td.text()
                }
                ans.add(s)
            }
            return ans
        }
    }
}