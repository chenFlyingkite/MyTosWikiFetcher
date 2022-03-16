package main.kt

import flyingkite.log.L
import main.fetcher.data.StockInfo
import main.fetcher.web.WebFetcher
import org.jsoup.nodes.Element

open class YahooGet {

    companion object me {
        private val baseLink = "https://tw.stock.yahoo.com"
        private val fetcher = WebFetcher()

        // <div><a href="/class-quote?sectorId=22&amp;exchange=TAI">金融業</a></div>
        fun fetchStockInfo(e : Element) : List<StockInfo> {
            val all = ArrayList<StockInfo>()
            val box = e.getElementsByTag("div")
            for (i in 0 until box.size) {
                val each = box[i]
                val si = StockInfo()
                si.name = each.text()
                val aa = each.getElementsByTag("a")
                if (aa.size > 0) {
                    val sl = aa[0].attr("href")
                    si.link = baseLink + sl
                    all.add(si)
                }
            }
            return all
        }

        fun numberTable(e: Element) : List<String> {
            val ans = ArrayList<String>()

            val trs = e.getElementsByTag("tr")
            for (i in 1 until trs.size) {
                val tr = trs[i]
                val tds = tr.getElementsByTag("td")
                val id = tds[1].text()
                ans.add(id)
            }
            return ans
        }

        fun dealTable(e: Element) : List<Double> {
            val ans = ArrayList<Double>()

            val trs = e.getElementsByTag("tr")
            for (i in 1 until trs.size) {
                val tr = trs[i]
                val tds = tr.getElementsByTag("td")
                var s = ""
                if (tds[3].text() != "-") {
                    s = tds[3].text()
                } else if (tds[8].text() != "-") {
                    s = tds[8].text()
                } else {
                    s = "0"
                }
                val price = s.toDouble()
                ans.add(price)
            }
            return ans
        }

        fun companyLink(id : String) : String {
            return "https://tw.stock.yahoo.com/d/s/company_$id.html"
        }

        fun dividend(id : String) : DoubleArray {
            // "https://tw.stock.yahoo.com/d/s/company_1101.html"
            val link = "https://tw.stock.yahoo.com/d/s/company_$id.html"
            val doc = fetcher.getDocument(link)
            if (doc == null) return doubleArrayOf(0.0)
            val tables = doc.getElementsByTag("table")
            if (tables.size < 2) {
                L.log("X_X Link not found dividend %s", link)
                return doubleArrayOf(-1.0, -1.0, -1.0, -1.0)
            }
            val e = tables[2]
            val tds = e.getElementsByTag("td")
            // 現金股利
            val cash = toMoney(tds[5].text())
            // 股票股利
            val stock = toMoney(tds[9].text())
            // 盈餘配股
            val stock2 = toMoney(tds[13].text())
            // 公積配股
            val stock3 = toMoney(tds[17].text())

            return doubleArrayOf(cash, stock, stock2, stock3)
        }

        @Deprecated("Wrong with class page")
        fun price(id : String) : Double {

            val link = "https://tw.stock.yahoo.com/q/ts?s=$id"
            val doc = fetcher.getDocument(link)
            if (doc == null) return 0.0
            val table = doc.getElementsByTag("table")
            //[6] = 14:30:00, [9] = 成交價
            val tds = table[3].getElementsByTag("td")
            if (tds.size < 10) return -1.0 // probably no transaction today

            val e = table[3].getElementsByTag("td")[9]
            return e.text().toDouble()
        }

        private fun toMoney(s : String) : Double {
            if (s.contains("元")) {
                return s.replace("元", "").toDouble()
            } else {
                return 0.0
            }
        }
    }
}