package main.kt

import flyingkite.log.L
import main.fetcher.data.stock.StockGroup
import main.fetcher.data.stock.YHDividend
import main.fetcher.data.stock.YHStockPrice
import main.fetcher.data.stock.YHYearDiv
import main.fetcher.web.WebFetcher
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import kotlin.math.min

open class YahooGet {

    companion object me {
        private val baseLink = "https://tw.stock.yahoo.com"
        private val fetcher = WebFetcher()

        // <div><a href="/class-quote?sectorId=22&amp;exchange=TAI">金融業</a></div>
        fun fetchStockInfo(e : Element) : List<StockGroup> {
            val all = ArrayList<StockGroup>()
            val box = e.getElementsByTag("div")
            for (i in 0 until box.size) {
                val each = box[i]
                val si = StockGroup()
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

//        Taiwan Cement Corp. (1101.TW)
//        Taiwan - Taiwan Delayed Price. Currency in TWD
//        Add to watchlist
//        Quote Lookup
//        49.75-0.15 (-0.30%)
//        At close: 01:30PM CST
        // Previous Close	49.90
        //Open	49.70
        //Bid	49.70 x 0
        //Ask	49.75 x 0
        //Day's Range	49.45 - 50.00
        //52 Week Range	45.65 - 58.70
        //Volume	9,890,711
        //Avg. Volume	10,935,280
        //Market Cap	304.279B
        //Beta (5Y Monthly)	0.40
        //PE Ratio (TTM)	15.24
        //EPS (TTM)	3.27
        //Earnings Date	May 10, 2022 - May 16, 2022
        //Forward Dividend & Yield	3.37 (6.75%)
        //Ex-Dividend Date	Aug 12, 2021
        //1y Target Est	47.40
        //--
//        台泥        1101        水泥
//        49.75        0.15        (0.30%)
//        收盤 | 2022/04/01 14:30 更新
//        9,847        成交量
//        15.55 (24.64)        本益比 (同業平均)
//        連3漲→跌 (        0.30%        )        連漲連跌
//        成交        49.75
//        開盤        49.70
//        最高        50.0
//        最低        49.45
//        均價        49.76
//        成交值(億)        4.90
//        昨收        49.90
//        漲跌幅        0.30%
//        漲跌        0.15
//        總量        9,847
//        昨量        15,106
//        振幅        1.10%

        @Deprecated("We use getPrices")
        fun dealTable(e: Element) : List<Double> {
            val ans = ArrayList<Double>()

            val trs = e.getElementsByTag("tr")
            for (i in 1 until trs.size) {
                val tr = trs[i]
                val tds = tr.getElementsByTag("td")
                var s = "0"
                if (tds[3].text() != "-") {
                    s = tds[3].text()
                } else if (tds[8].text() != "-") {
                    s = tds[8].text()
                }
                val price = s.toDouble()
                ans.add(price)
            }
            return ans
        }

        fun companyLink(id : String) : String {
            return "https://tw.stock.yahoo.com/d/s/company_$id.html"
        }

        // Get dividend from
        //https://tw.stock.yahoo.com/quote/1101/dividend
        fun getDividend(isin: String) : YHDividend {
            // peek the recent years of dividend, since it saves 20 years
            val recentYears = 7
            val log = false
            //-- content
            val link = "https://tw.stock.yahoo.com/quote/${isin}/dividend"
            val doc = fetcher.getDocument(link)
            val ans = YHDividend()
            if (log) {
                L.log("getDividend ${link}")
            }
            if (doc == null) return ans
            var q1 = doc.getElementById("main-2-QuoteDividend-Proxy")
            var es = q1.getElementsByClass("Fw(b)")
            //ln(es)
            var z = 0
            // https://tw.stock.yahoo.com/quote/2012/dividend
            // 已連 9 年發放股利，合計 6.60 元。近 5 年平均殖利率 : 4.36%
            // https://tw.stock.yahoo.com/quote/1441/dividend
            // 近 5 年平均殖利率 : 4.36%
            for (i in 0 until es.size) {
                val si = es[i]
                if (si.classNames().size == 1) {
                    if (z == 0) {
                        ans.recentYear = es[i].text().toInt()
                        z++
                    } else if (z == 1) {
                        ans.recentReturn = es[i].text().replace("%", "").toDouble()
                        z++
                        break
                    }
                }
            }
            es = doc.getElementsByClass("table-body-wrapper")
            if (es.size > 0) {
                es = es[0].getElementsByTag("li")
                //ln(es)
                var recent = min(recentYears, es.size)
                //recent = es.size
                for (i in 0 until recent) {
                    val ei = es[i]
                    val row = ei.child(0).children()
                    if (row.size < 8) continue //?
                    // row has 8 child
                    // 股利所屬期間 現金股利 股票股利 除息日 除權日 現金股利發放日 股票股利發放日 填息天數
                    val yd = YHYearDiv()
                    yd.year = row[0].text()
                    yd.cash = row[1].text()
                    yd.stock = row[2].text()
                    yd.exDividendDate = row[3].text()
                    yd.exRightsDate = row[4].text()
                    yd.cashDate = row[5].text()
                    yd.stockDate = row[6].text()
                    yd.fillInInterval = row[7].text()
                    yd.trim()
                    ans.years.add(yd)
                    if (log) {
                        L.log("#%s : %s from \"%s\"", i, yd, row.text())
                    }
                }
            }
            return ans
        }
        // 2910 empty string?
        private fun toDouble(s : String) : Double {
            return s.replace(",", "").trim().toDouble()
        }

        private fun toLong(s : String) : Long {
            return s.replace(",", "").trim().toLong()
        }

        // Get prices from
        // https://finance.yahoo.com/quote/1101.TW
        fun getPrices(isin: String) : YHStockPrice {
            val log = 1 > 0
            //-- content
            val link = "https://finance.yahoo.com/quote/${isin}"
            val doc = fetcher.getDocument(link)
            val ans = YHStockPrice()
            if (log) {
                L.log("getPrices ${link}")
            }
            if (doc == null) return ans
            var q1 = doc.getElementById("quote-summary")
            var es = q1.getElementsByTag("td")

            // [Previous Close, 43.75, Open, 43.25, Bid, 43.75 x 0, Ask, 44.00 x 0, Day's Range, 43.25 - 43.95, 52 Week Range, 28.50 - 48.10, Volume, 6,177, Avg. Volume, 15,679, Market Cap, 4.438B, Beta (5Y Monthly), 0.02, PE Ratio (TTM), 60.87, EPS (TTM), 0.72, Earnings Date, N/A, Forward Dividend & Yield, 1.50 (3.41%), Ex-Dividend Date, Dec 13, 2021, 1y Target Est, N/A]
            val str = ArrayList<String>()
            for (i in 0 until es.size) {
                val si = es[i]
                val ti = si.text()
                val tj = if (i+1 < es.size) {
                    es[i+1].text()
                } else {
                    ""
                }
                // or data-test="PE_RATIO-value" ?
                when (ti) {
                    "Previous Close" -> {
                        //ans.previousClose = toDouble(tj)
                        ans.previousClose = tj
                    }
                    "Open" -> {
                        //ans.open = toDouble(tj)
                        ans.open = tj
                    }
                    "Bid" -> {
                        // bid 33.30 x 0
                        val tk = tj.substringBefore("x").trim()
                        //ans.bid = toDouble(tk)
                        ans.bid = tk
                    }
                    "Ask" -> {
                        // ask 33.35 x 0
                        val tk = tj.substringBefore("x").trim()
                        //ans.ask = toDouble(tk)
                        ans.ask = tk
                    }
                    "Day's Range" -> {
                        val tk = tj.split("-")
//                        ans.rangeLow = toDouble(tk[0])
//                        ans.rangeHigh = toDouble(tk[1])
                        ans.rangeLow = tk[0]
                        ans.rangeHigh = tk[1]
                    }
                    "52 Week Range" -> {
                        val tk = tj.split("-")
//                        ans.rangeLowW52 = toDouble(tk[0])
//                        ans.rangeHighW52 = toDouble(tk[1])
                        ans.rangeLowW52 = tk[0]
                        ans.rangeHighW52 = tk[1]
                    }
                    "Volume" -> {
                        //ans.volume = toLong(tj)
                        ans.volume = tj
                    }
                    "Avg. Volume" -> {
                        //ans.volumeAvg = toLong(tj)
                        ans.volumeAvg = tj
                    }
                    "Beta (5Y Monthly)" -> {
                        ans.beta = tj
                    }
                    "PE Ratio (TTM)" -> {
                        ans.PERatio = tj
                    }
                    "EPS (TTM)" -> {
                        ans.EPS = tj
                    }
                }
                str.add(ti)
            }
            //--
            // fill in closing price
            // "quote-header-info"
            //  data-field="regularMarketPrice"
            q1 = doc.getElementById("quote-header-info")
            val keyR = "data-field"
            val valR = "regularMarketPrice"
            es = q1.getElementsByAttributeValue(keyR, valR)
            if (log) {
                //L.log("%s=%s : %s", keyR, valR, es)
            }
            if (es.size > 0) {
                var si = es[0].text()
                si = es[0].attr("value")
                //ans.closingPrice = toDouble(si)
                ans.closingPrice = si
            }
            return ans
        }

        private fun ln(e : Elements) {
            L.log("${e.size} elements")
//            for (i in 0 until e.size) {
//                val it = e[i]
//                L.log("#${i} : ${it}")
//            }
        }

        @Deprecated("Old implementation")
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