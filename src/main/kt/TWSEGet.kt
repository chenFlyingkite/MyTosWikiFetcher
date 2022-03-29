package main.kt

import main.twse.TWEquity
import main.twse.TWEquityItem
import main.twse.TWEquityList
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

open class TWSEGet {
    companion object me {
        fun parseEquityList(doc : Element, type:Int) : TWEquityList {
            val ans = TWEquityList()
            val cs: Elements = doc.getElementsByTag("center")
            if (cs.size > 0) {
                ans.version = cs[0].text().substringAfter(":").trim()
            }
            val trs = doc.getElementsByTag("tr");
            val trsn = trs.size
            val column = trs[0].getElementsByTag("td").size
            var each = TWEquityItem()
            for (i in 1 until trsn) {
                val row = trs[i]
                val rowTd = row.getElementsByTag("td")
                if (rowTd.size == column) {
                    // next data
                    val eq = TWEquity()
                    readTWEquity(eq, type, rowTd)
                    each.list.add(eq)
                    // for direct table, no header
                    if (ans.list.size == 0) {
                        ans.list.add(each)
                    }
                } else {
                    // new group
                    each = TWEquityItem()
                    each.name = row.text()
                    ans.list.add(each)
                }
            }
            return ans
        }

        private fun readTWEquity(eq: TWEquity, type: Int, es: Elements) {
            when (type) {
                1, 8 -> {
                    // 1 = 未上市，未上櫃
                    // 有價證券代號及名稱	國際證券辨識號碼(ISIN Code)	公開發行日	產業別	CFICode	備註
                    // 8 = 未公開發行之創櫃板證券
                    // 有價證券代號及名稱	國際證券辨識號碼(ISIN Code)	登錄日	市場別	CFICode	備註
                    val r0t = es[0].text()
                    val r0 = r0t.split("　")
                    eq.code = r0[0]
                    eq.name = r0[1]
                    eq.ISINCode = es[1].text()
                    eq.listedDate = es[2].text()
                    if (type == 8) {
                        eq.market = es[3].text()
                    } else {
                        eq.industry = es[3].text()
                    }
                    eq.CFICode = es[4].text()
                    eq.remarks = es[5].text()
                }
                9, 10, 11 -> {
                    // 9 = 黃金現貨
                    // 有價證券代號及名稱	國際證券辨識號碼(ISIN Code)	掛牌日	CFICode	備註
                    // 10 = 外幣計價可轉換定期存單
                    // 有價證券名稱	國際證券辨識號碼(ISIN Code)	發行日	CFICode	備註
                    // 11 = 本國指數
                    // 指數代號及名稱	國際證券辨識號碼(ISIN Code)	發布日	CFICode	備註
                    val r0t = es[0].text()
                    if (type == 10) {
                        eq.name = r0t
                    } else {
                        val r0 = r0t.split("　")
                        eq.code = r0[0]
                        eq.name = r0[1]
                    }
                    eq.ISINCode = es[1].text()
                    eq.listedDate = es[2].text()
                    eq.CFICode = es[3].text()
                    eq.remarks = es[4].text()
                }
                3 -> {
                    // 3 = 上市債券，上櫃債券，國際證券
                    // 有價證券代號及名稱	國際證券辨識號碼(ISIN Code)	發行日	到期日	利率值	市場別	產業別	CFICode	備註
                    val r0t = es[0].text()
                    val r0 = r0t.split("　")
                    eq.code = r0[0]
                    eq.name = r0[1]
                    eq.ISINCode = es[1].text()
                    eq.listedDate = es[2].text()
                    eq.maturityDate = es[3].text()
                    eq.interest = es[4].text()
                    eq.market = es[5].text()
                    eq.industry = es[6].text()
                    eq.CFICode = es[7].text()
                    eq.remarks = es[8].text()
                }
                7 -> {
                    // 7 = 開放式證券投資信託基金
                    // 有價證券代號及名稱	國際證券辨識號碼(ISIN Code)	發行日	CFICode	備註
                    val r0t = es[0].text()
                    val r0 = r0t.split("　")
                    eq.code = r0[0]
                    eq.name = r0[1]
                    eq.ISINCode = es[1].text()
                    eq.listedDate = es[2].text()
                    //eq.market = es[3].text() // N/A
                    //eq.industry = es[3].text() N/A
                    eq.CFICode = es[3].text()
                    eq.remarks = es[4].text()
                }
                2, 4, 5, 6 -> {
                    // 5 = 興櫃證券
                    // 4 = 上櫃證券
                    // 2 = 上市證券
                    // 有價證券代號及名稱	國際證券辨識號碼(ISIN Code)	上市日	市場別	產業別	CFICode	備註
                    val r0t = es[0].text()
                    val r0 = r0t.split("　")
                    eq.code = r0[0]
                    eq.name = r0[1]
                    eq.ISINCode = es[1].text()
                    eq.listedDate = es[2].text()
                    eq.market = es[3].text()
                    eq.industry = es[4].text()
                    eq.CFICode = es[5].text()
                    eq.remarks = es[6].text()
                }
            }
        }
    }
}