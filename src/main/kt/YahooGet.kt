package main.kt

import main.fetcher.data.StockInfo
import main.fetcher.web.WebFetcher
import org.jsoup.nodes.Element

open class YahooGet {

    companion object me {
        private val baseLink = "https://tw.stock.yahoo.com"
        private val fetcher = WebFetcher()

        fun classTable(e : Element) : List<StockInfo> {
            val all = ArrayList<StockInfo>()
            val tds = e.getElementsByTag("td")
            for (i in 0 until tds.size) {
                val td = tds[i]
                val si = StockInfo()
                si.clazz = td.text()
                val sl = td.getElementsByTag("a")[0].attr("href")
                val end = sl.substring(0, sl.indexOf("&"))
                si.link = baseLink + end
                all.add(si)
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

        fun dividend(id : String) : Double {
            // "https://tw.stock.yahoo.com/d/s/company_1101.html"
            val link = "https://tw.stock.yahoo.com/d/s/company_$id.html"
            val doc = fetcher.sendAndParseDom(link, null)
            val tables = doc.getElementsByTag("table")
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

            return cash + stock + stock2 + stock3
        }

        @Deprecated("Wrong with class page")
        fun price(id : String) : Double {

            val link = "https://tw.stock.yahoo.com/q/ts?s=$id"
            val doc = fetcher.sendAndParseDom(link, null)
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