package main.kt

import flyingkite.functional.MeetSS
import flyingkite.log.L
import flyingkite.math.MathUtil
import flyingkite.tool.TextUtil
import flyingkite.tool.TicTac2
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import org.jsoup.select.Elements
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class TosGet {
    companion object me {
        private val printTdHtml = false
        @Deprecated("")
        private val zhLinkFile = "http://zh.tos.wikia.com/wiki/File:" // TODO : Update link
        private val imageClass = "image image-thumbnail link-internal"
        private val wikiBaseZh = "https://tos.fandom.com"
        private val wikiBaseZhOld = "http://zh.tos.wikia.com"
        private val imageSrc = "https://vignette.wikia.nocookie.net"
        private val imageSrc2 = "https://static.wikia.nocookie.net"

        // Extract for TosCard's big image and links
        // http://zh.tos.wikia.com/wiki/001
        fun getImage(element: Element) : String {
            val imgs = getImageTag(element) ?: return ""
            if (imgs.size > 0) {
                return getVignette(imgs[0])
            }
            return ""
        }

        fun getImageTag(e: Element) : Elements? {
            val nos = e.getElementsByTag("noscript")
            val size = nos?.size ?: 0
            if (size > 0) {
                val x = nos[0]
                return x?.getElementsByTag("img")
            } else {
                return e.getElementsByTag("img")
            }
        }

        fun getImgAlt(element: Element) : String? {
            val imgs = element.getElementsByTag("img")
            var ans = "";
            if (imgs.size > 0) {
                ans = imgs[0].attr("alt").replace(".png", "");
            }
            return ans
        }

        fun getImgInfo2(element: Element?) : ImageInfo2 {
            val info = ImageInfo2()
            if (element != null) {
                val imgs = getImageTag(element)
                val s = imgs?.size ?: 0
                if (s > 0) {
                    val item = imgs?.get(0)
                    info.alt = item?.attr("alt") ?: ""
                    info.link = item?.attr("src") ?: ""
                    info.imageName = item?.attr("data-image-name") ?: ""
                    info.width = Integer.parseInt(item?.attr("width"))
                    info.height = Integer.parseInt(item?.attr("height"))
                }
            }
            return info
        }

        fun getTosMainInfo(element: Element?, wikiBase: String) : HomeTable {
            val homeTable = HomeTable()
            if (element == null) return homeTable
            val child = element.children()
            // 1. Fetch title
            val title = child.get(0).text() // class="titleBar1"
            homeTable.title = title

            val allRows = ArrayList<HomeRow>()
            val trs = child.get(2).getElementsByTag("tr") // class="wikitable"
            for (tr in trs) {
                val row = HomeRow()
                val list = ArrayList<Element>()
                var link = ""

                val chd = tr.children()
                val n = chd?.size ?: 0
                if (n > 0) {
                    val texts = ArrayList<String>()
                    for (ch in chd) {
                        list.add(ch)

                        val img = getImageTag(ch)
                        if (img == null) {
                            texts.add(ch.text())
                        } else {
                            val taga = ch.getElementsByTag("a")
                            val an = taga?.size ?: 0
                            if (an > 0) {
                                link = getWikiLink(taga[0].attr("href"), wikiBase)
                            }
                        }
                    }
                    //println("${texts.size}, child = $texts")
                }
                // Add to data holder
                row.dateStart = asLong(tr.attr("data-start"))
                row.dateEnd = asLong(tr.attr("data-end"))
                row.tds = list
                row.link = link
                //println("row = $row")
                allRows.add(row)
            }
            homeTable.rows.addAll(allRows)

            return homeTable
        }

        fun getTosSealEvent(element: Element?, wikiBase: String) : SealEventTable {
            val main = SealEventTable()
            if (element == null) return main

            val tbs = element.getElementsByTag("table")

            val allRows = ArrayList<SealEventItem>()
            for (tb in tbs) {
                val row = SealEventItem()

                // Add to data holder
                row.dateStart = asLong(tb.attr("data-start"))
                row.dateEnd = asLong(tb.attr("data-end"))
                row.text = tb.text()

                val xs = tb.getElementsContainingOwnText("角色")
                if (xs.size > 0) {
                    val x = xs[0]
                    // new cards & bird
                    row.ids.addAll(getImgAltIds(x))
                }
                //println("row = $row")
                allRows.add(row)
            }
            main.rows.addAll(allRows)
            return main
        }

        private fun asLong(text :String?, failed :Long = 0) :Long {
            var value = failed
            try {
                value = text?.toLong() ?: failed
            } catch (e :NumberFormatException) {
            }
            return value
        }

        fun getTosPageImageInfo(element: Element?) : ImageInfo2? {
            if (element == null) {
                return null
            } else {
                val info = ImageInfo2()
                val imgs = element.getElementsByTag("img")
                val s = imgs?.size ?: 0
                if (s > 0) {
                    val item = imgs?.get(0)
                    info.alt = item?.attr("alt") ?: ""
                    info.link = item?.attr("src") ?: ""
                    info.width = Integer.parseInt(item?.attr("width"))
                    info.height = Integer.parseInt(item?.attr("height"))
                }
                return info
            }
        }

        fun getTosPageEntryContent(doc: Document) : String {
            val conts = doc.getElementsByClass("entry-content")
            val n = conts?.size ?: 0
            if (n > 0) {
                val entry = conts[0]
                return entry.html()
            }
            return ""
        }

        fun getHtml(element: Element) : String {
            return element.html() ?: ""
        }

        fun getHtmlAt(index: Int, elements: Elements) : String {
            return elements.get(index)?.html() ?: ""
        }

        fun getSummonerTable(index: Int, elements: Elements) : SummonInfo {
            val info = SummonInfo()
            val no = index >= elements.size
            if (no) return info

            return getSummonerTable(elements[index])
        }


        fun getSummonerTable(element: Element) : SummonInfo {
            val info = SummonInfo()

            val ths = element.getElementsByTag("th")
            ths.forEachIndexed { i, ele -> run {
                info.headers.add(ele.text())
            }}

            val tds = element.getElementsByTag("td")
            tds.forEachIndexed { i, ele -> run {
                //info.cells.add(ele.text())
            }}
            return info
        }

        fun getStageTable(element: Element) : StageInfo {
            val info = StageInfo()

            val ths = element.getElementsByTag("th")
            ths.forEachIndexed { i, ele -> run {
                when (i) {
                    0 -> {
                        info.title = ele.text()
                    }
                    else -> {
                        info.headers.add(ele.text())
                    }
                }
            }}

            val tds = element.getElementsByTag("td")
            tds.forEachIndexed { i, ele -> run {
                var item = ""
                when (i % info.headers.size) {
                    1 -> { // Node of attributes
                        val spans = ele.getElementsByTag("span")
                        spans.forEachIndexed { j, span -> run {
                            if (j > 0) {
                                item += ","
                            }
                            item += span.attr("title")
                        }}
                    }
                    0 -> {
                        item = ele.text()
                    }
                    else -> {
                        item = ele.ownText()
                    }
                }
                info.cells.add(item)
            }}
            return info
        }

        fun getAmeSkillTable(es: Elements, baseWiki: String) : List<SkillInfo> {
            val result = ArrayList<SkillInfo>()
            // Each table
            for (i in 0 until es.size) {
                val ei = es[i]
                val itemTrs = ei.getElementsByTag("tr")
                val nTr = itemTrs?.size ?: 0
                for (j in 1 until nTr) {
                    val rowTds = itemTrs[j].getElementsByTag("td")
                    val nTd = rowTds?.size ?: 0

                    // The indices for rowTds
                    var idx: IntArray? = null
                    if (nTd == 5) { // 主動技列表/昇華技能
                        idx = intArrayOf(0, 1, 2, 3, 4)
                    } else if (nTd == 3) { // 隊長技列表/昇華技能
                        idx = intArrayOf(0, -1, -1, 1, 2)
                    } else {

                    }

                    if (idx != null) {
                        val ame = SkillInfo()
                        var k = 0
                        k = idx[0]
                        if (k >= 0) {
                            getSkill(ame, rowTds[k], baseWiki)
                        }
                        k = idx[1]
                        if (k >= 0) {
                            ame.skillCDMin = Integer.parseInt(rowTds[k].text())
                        }
                        k = idx[2]
                        if (k >= 0) {
                            ame.skillCDMax = Integer.parseInt(rowTds[k].text())
                        }
                        k = idx[4]
                        if (k >= 0) {
                            ame.skillDesc = rowTds[k].text()
                        }
                        // Fill in monsters
                        k = idx[3]
                        if (k >= 0) {
                            val child = rowTds[k].children()
                            val nch = child?.size ?: 0
                            for (m in 0 until nch) {
                                if (child[m] is Element) {
                                    val s = getAltId(child[m])
                                    if (!TextUtil.isEmpty(s)) {
                                        ame.monsters.add(s)
                                    }
                                }
                            }
                        }
                        result.add(ame)
                    } else {
                        print("Omit this items: $nTd tds => $rowTds")
                    }
                }
            }
            return result
        }

        fun getAllSkillTable(es: Elements) : List<SkillInfo2> {
            // Each table
            // 5 Types = [技能類型 隊長技, 技能類型 組合技, 技能類型 主動技, 技能類型 隊長技 ‧ 昇華, 技能類型 主動技 ‧ 昇華]
            // 隊長技 : 妖精領域
            // 組合技 : 三原圍城 ‧ 強
            // 主動技 : 三原利刃 ‧ 人族
            // 隊長技 ‧ 昇華 : 護甲金尊
            // 主動技 ‧ 昇華 : 諸刃轟擊
            // seems not important?
            val skis = ArrayList<SkillInfo2>()
            for (e in es) {
                val ski = SkillInfo2()
                val tds = e.getElementsByTag("td")

                ski.type = tds[0].text()
                ski.skillLink = e.baseUri()
                var valid = true
                when (ski.type) {
                    "隊長技" -> {
                        // 隊長技 : 護甲金身
                        ski.skillName = getTextAt(tds, 1)
                        ski.skillDesc = getTextAt(tds, 2)
                        ski.monsters = getImgAltIds(tds[3])
                    }
                    "隊長技 ‧ 昇華" -> {
                        // 昇華後 : 護甲金身 ‧ 強
                        ski.skillName = getTextAt(tds, 1)
                        ski.skillDesc = getTextAt(tds, 2)
                        ski.monsters = getImgAltIds(tds[4])
                    }
                    "主動技" -> {
                        // 主動技 : 金睛火眼 ‧ 凝煉
                        ski.skillName = getTextAt(tds, 1)
                        ski.skillCDMin = asInt(getTextAt(tds, 2))
                        ski.skillCDMax = asInt(getTextAt(tds, 3))
                        ski.skillDesc = getTextAt(tds, 4)
                        ski.monsters = getImgAltIds(tds[5])
                    }
                    "主動技 ‧ 昇華" -> {
                        // 昇華後 : 金睛真火 ‧ 凝煉
                        ski.skillName = getTextAt(tds, 1)
                        ski.skillCDMin = asInt(getTextAt(tds, 2))
                        ski.skillCDMax = asInt(getTextAt(tds, 3))
                        ski.skillDesc = getTextAt(tds, 4)
                        // 6
                        ski.monsters = getImgAltIds(tds.last())
                    }
                    "組合技" -> {
                        // 三原獵刃
                        ski.skillName = getTextAt(tds, 1)
                        ski.skillCDMin = asInt(getTextAt(tds, 2))
                        ski.skillDesc = getTextAt(tds, 3)
                        ski.monsters = getImgAltIds(tds[4])
                    }
                    else -> {
                        valid = false
                    }
                }
                if (valid) {
                    skis.add(ski)
                }
            }

            return skis
        }

        private fun asInt(s : String) : Int {
            try {
                return s.toInt()
            } catch (e :NumberFormatException) {
                return 0
            }
        }
        
        private fun getImgAltIds(e: Element) : MutableList<String> {
            val list = ArrayList<String>()
            val imgs = e.getElementsByTag("img")
            for (img in imgs) {
                val inNos = img.parent().tagName() == "noscript"
                if (inNos) {
                    // omit since is <noscript><img/></noscript>
                } else {
                    list.add(normCardId(img.attr("alt")))
                }
            }
            return list
        }

        private fun getAltId(e: Element) : String {
            return normCardId(getImgAlt(e))
        }

        private fun getTextAt(e: Elements, k: Int) : String {
            if (0 <= k && k < e.size) {
                return e[k].text()
            } else {
                return ""
            }
        }


        fun getActiveSkillTable(es: Elements, baseWiki: String) : SkillInfo {
            // Each table
            val ame = SkillInfo()
            val ei = es[0]
            val itemTrs = ei.getElementsByTag("tr")
            val nTr = itemTrs?.size ?: 0
            if (nTr > 5) {
                // itemTr[0] is header, omit it
                ame.skillName = getTdText(itemTrs[1], 0)// itemTrs[0].getElementsByTag("td")
                ame.skillLink = ei.baseUri()
                ame.skillCDMin = Integer.parseInt(getTdText(itemTrs[2], 0))
                ame.skillCDMax = Integer.parseInt(getTdText(itemTrs[3], 0))
                ame.skillDesc = getTdText(itemTrs[4], 0)
                // Fill in monsters
                val end = nTr - 1
                val td0 = getTdElement(itemTrs[end], 0)
                if (td0 != null) {
                    val s = getAltId(td0)
                    if (!TextUtil.isEmpty(s)) {
                        ame.monsters.add(s)
                    }
                }
            } else {
                print("Omit this items: $nTr trs => ${ei.baseUri()}")
            }
            return ame
        }

        /**
         * Fetch <a>'s item as CardItem
         */
        fun getCardItems(e: Element?, baseWiki: String) : List<CardItem>  {
            val ans = ArrayList<CardItem>()
            if (e == null) return ans

            val tb = e.getElementsByTag("tbody")
            if (tb.size == 0) return ans
            val s = tb[0].getElementsByTag("a")
            for (item in s) {
                // Start parse
                val c = CardItem()
                c.link = getWikiLink(item.attr("href"), baseWiki)
                c.title = item.attr("title")

                val n = item.children()?.size ?: 0
                if (n > 0) {
                    val img = item.child(0)
                    c.id = normCardId(img.attr("data-image-name"))
                    val id = c.id.toInt()
                    c.linkId = wikiBaseZhOld + "/wiki/" + id
                }
                ans.add(c)
            }
            return ans
        }

        /**
         * Get the element's all <li><a href="/link"></li> -> baseWiki + "/link"
         */
        fun getLiAHref(e: Element?, baseWiki: String) : List<String>  {
            val ans = ArrayList<String>()
            if (e == null) return ans

            val s = e.getElementsByTag("li")
            for (item in s) {
                // Take the 1st <a> or we should use item.child(0) ?
                val ax = item.getElementsByTag("a")
                val n = ax?.size ?: 0
                if (n > 0) {
                    // Take the 1st a
                    var href = ax[0].attr("href")
                    if (href.startsWith("/")) {
                        href = baseWiki + href
                    }
                    ans.add(href)
                }
            }
            return ans
        }

        fun getMainStages(e: Element?, baseWiki: String) : List<MainStage> {
            val ans = ArrayList<MainStage>()
            if (e == null) return ans

            val tbs = e.getElementsByClass("wikitable")
            // 0 = 第一封印至第七封印
            // 1 = 第八封印
            // 2 = 第九封印
            // 3 = 第十封印
            for (i in 0 until tbs.size) {
                val main = MainStage()
                val ei = tbs[i]
                val subs = ei.getElementsByTag("td")
                val stg = StageGroup()
                for (si in subs) {
                    val zs = si.getElementsByTag("a")
                    for (z in zs) {
                        // no image case, only text link
                        if (z.getElementsByTag("img").size == 0) continue

                        val x = Stage()
                        x.name = z.attr("title")
                        x.link = getWikiLink(z.attr("href"), baseWiki)
                        x.icon = getAltId(z)
                        if (x.icon.startsWith("C") || x.icon.startsWith("8")) {
                            // 龍刻 or 存音石
                        } else {
                            stg.stages.add(0, x)
                        }
                    }
                }
                // 42 stages = 6 stage * 7 group
                main.substages.add(0, stg)
                ans.add(0, main)
            }
            return ans
        }

        fun getUltimateStages(e: Element, baseWiki: String) : List<Stage> {
            val list = ArrayList<Stage>()
            val mob = e.getElementsByClass("mobileHide")
            if (mob.size == 0) return list

            val stg = mob[0].getElementsByTag("a")
            // remove unused
            stg.removeAt(stg.size - 1)
            for (i in stg.size-1 downTo 0) {
                if (i % 2 == 1) {
                    stg.removeAt(i)
                }
            }
            // parse
            for (i in 0 until stg.size) {
                val ei = stg[i]
                val si = getStageInfo(ei, baseWiki)
                list.add(si)
            }
            return list
        }

        fun getStoryStages(e: Element, baseWiki: String) : List<StageGroup> {
            val list = ArrayList<StageGroup>()
            val tit = e.getElementsByClass("titleBar2")
            val stg = e.getElementsByTag("a")
            val cnt = intArrayOf(6, 6
                    , 5, 5, 5, 5, 5, 5, 5
                    // , 7, 5, 5, 5 // Disney
            )

            var k = 0
            for (i in 0 until cnt.size) {
                val sg = StageGroup()
                sg.group = tit[i].text()
                val st = sg.stages
                val ni = cnt[i]
                for (j in 0 until ni) {
                    val g = Stage()
                    g.icon = getAltId(stg[k])
                    g.name = stg[k + 1].text()
                    g.link = getWikiLink(stg[k].attr("href"), baseWiki)
                    st.add(g)
                    k += 2
                }
                list.add(sg)
            }
            return list
        }

        fun getStageInfo(e: Element, baseWiki: String = wikiBaseZh) : Stage {
            val m = Stage()
            m.icon = getAltId(e)
            m.link = getWikiLink(e.attr("href"), baseWiki)
            m.name = e.attr("title")
            return m
        }

        /**
         * Fetch <a>'s item as skill
         */
        fun getSkillItems(e: Element?, baseWiki: String) : List<EnemySkill> {
            val ans = ArrayList<EnemySkill>()
            if (e == null) return ans

            val s = e.getElementsByTag("td")
            val max = s.size / 3
            for (i in 0 until max) {
                val c = EnemySkill()
                var k = 3 * i
                // <td>1</td>
                c.id = s[k].text()
                // <td>{{ES1}}</td>
                c.esCode = s[k + 2].text()

                val ax = s[k + 1].getElementsByTag("a")
                for (axi in ax) {
                    val imgs = axi.getElementsByTag("img")
                    if (imgs.size > 0) {
                        val aimg = imgs[0]
                        val si = SimpleIcon()
                        // For 1~3 icons, its data-src did not fill in content
                        // Need "https://vignette.wikia.nocookie.net/tos/images"...
                        si.iconLink = getVignette(aimg)
                        si.iconKey = aimg.attr("data-image-key")
                        c.icons.add(si)
                    } else {
                        if (c.name.isEmpty()) {
                            c.name = axi.text()
                            if (axi.children().isNotEmpty()) {
                                val des = axi.child(0).attr("data-texttip")
                                c.detail = des.substringAfter("<br>").substringBefore("<br><img")//.replace("<br>", "\n")
                            }
                            c.link = getWikiLink(axi.attr("href"), baseWiki)
                        }
                    }
                }
                ans.add(c)
            }
            return ans
        }

        fun getVignette(e: Element) : String {
            return getVignette(e, "data-src", "src")
        }

        fun getVignette(e: Element, vararg attrs: String) : String {
            var img = ""
            for (key in attrs) {
                img = e.attr(key)
                if (img.startsWith("https://static.wikia.nocookie.net/tos/images")) {
                    return img
                }
                if (img.startsWith("https://vignette.wikia.nocookie.net/tos/images")) {
                    return img
                }
            }
            return img
        }

        fun getRelicPassStages(e: Elements?, baseWiki: String) : List<List<RelicStage>> {
            val ans = ArrayList<List<RelicStage>>()
            if (e == null) return ans

            for (i in 0 until e.size) {
                val ei = e[i]
                val im = ei.getElementsByTag("a")
                val td = ei.getElementsByTag("td")
                val rei = ArrayList<RelicStage>()
                // get coin
                for (j in 0 until im.size) {
                    if (j % 2 == 1) continue

                    val ej = im[j]
                    val rs = RelicStage()
                    rs.name = ej.attr("title")
                    rs.link = getWikiLink(ej.attr("href"), baseWiki)
                    rs.icon = getAltId(ej)

                    var coin = ""
                    // 0 for 遠古遺跡, 3 for 活動遺跡
                    var use3 = i == 0 || i == 3
                    if (use3) {
                        // 200_000
                        coin = td[3].text()
                    } else {
                        coin = td[2*j+3].text()
                    }
                    coin = coin.replace(Regex("[x,×\\s]"), "")
                    rs.coin = coin.toIntOrNull() ?: 0
                    rei.add(rs)
                }
                ans.add(rei)
            }
            return ans
        }

        // Similar with getMainStages()
        fun getVoidRealmStages(e: Element?, baseWiki: String) : List<MainStage> {
            val ans = ArrayList<MainStage>()
            if (e == null) return ans

            val items = e.getElementsByClass("wikitable")
            val titles = arrayOf("魔導紀元", "英靈時代", "黑鐵時代")
            //"混沌紀元" "英雄時代" "召喚師時代"

            for (i in 0 until items.size) {
                val main = MainStage()
                val item = items[i]
                val eas = item.getElementsByTag("a")
                val subs = StageGroup()
                for (ea in eas) {
                    if (ea.hasClass("selflink")) continue

                    val stg = getStageInfo(ea, baseWiki)
                    subs.stages.add(stg)
                }
                // decide title
                var tit = ""
                if (i < titles.size) {
                    tit = titles[i]
                } else {
                    tit = "Need update"
                }
                subs.group = tit

                main.substages.add(subs)
                ans.add(main)
            }
            return ans
        }

        /**
         * Fetch <span>'s item as simple craft
         */
        fun getSimpleCraftItems(e: Element?, baseWiki: String) : List<SimpleCraft> {
            val ans = ArrayList<SimpleCraft>()
            if (e == null) return ans

            val s = e.getElementsByTag("span")
            val max = s.size
            for (i in 0 until max) {
                val si = s[i]
                if (si.hasClass("tt-text")) {
                    val c = SimpleCraft()

                    // Take the 1st <img> or we should use item.child(0).child(0) ?
                    // <a><img/></a>
                    val ax = si.getElementsByTag("a")
                    if (ax.size == 0) continue
                    val a = ax[0]
                    val gx = si.getElementsByTag("img")
                    if (gx.size == 0) continue
                    val g = gx[0]
                    c.name = a.attr("title")
                    c.link = getWikiLink(a.attr("href"), baseWiki)
                    //c.id = g.attr("alt")
                    c.idNorm = normCraftId(g.attr("alt"))
                    c.icon.iconLink = getVignette(g)// = getIconLink(si)
                    c.icon.iconKey = g.attr("data-image-key")
                    ans.add(c)
                } else {

                }
            }
            return ans
        }

        fun getWikiLink(src: String, baseWiki: String) : String {
            // Handle for the link with baseWiki prefix
            if (src.isEmpty()) {
                return src
            } else if (src.startsWith(baseWiki)) {
                return src
            } else if (src.startsWith(imageSrc)) {
                return src
            } else if (src.startsWith(imageSrc2)) {
                return src
            } else {
                return baseWiki + src
            }
        }

        private fun getIconLink(e: Element) : String {
            val gx = e.getElementsByTag("img")
            if (gx.size == 0) return ""

            val g = gx[0]
            return getVignette(g)
        }


        private fun normCardId(s: String?): String {
            if (s == null) return ""
            var t = s
            t = t.replace(".png", "")
            val endI = t.matches(Regex("(\\d+)i"))
            if (endI) {
                t = t.substring(0, t.length - 1)
                // Parse "12i" to "0012"
                return String.format(Locale.US, "%04d", t.toInt())
            } else {
                return t
            }
        }

        private fun normCraftId(s: String): String {
            var t = s
            t = t.replace(".png", "")
            val beginC = t.matches(Regex("C(\\d+)"))
            if (beginC) {
                t = t.substring(1)
                // Parse "C0012" to "0012"
                return String.format(Locale.US, "%04d", t.toInt())
            } else {
                return t
            }
        }

        /**
         * Fetch <span>'s item & simple craft as craft
         */
        fun getCraft(e: Element?, simple: SimpleCraft) : Craft {
            val ans = Craft(simple)
            if (e == null) return ans

            val s = e.getElementsByTag("td")
            // Update the icon link to be large one
            val isArm = isArmCraft(e)
            ans.icon.iconLink = getIconLink(s[0])

            val id = simple.idNorm.toLong()
            val is7xxx = MathUtil.isInRange(id, 7000, 8000)

            if (isArm) {
                ans.rarity = s[3].text().replace("★", "").trim().toInt()
                ans.level = s[4].text().replace("Lv.", "", true).trim().toInt()

                if (is7xxx) {
                    ans.mode = s[8].text()
                    ans.charge = s[9].text()
                } else {
                    ans.mode = s[7].text()
                    ans.charge = s[8].text()
                }

                // find card limit
                if (is7xxx) {
                    ans.attrLimit = s[5].text()
                    ans.raceLimit = s[6].text()
                } else {
                    val cl = s[5].getElementsByTag("a")
                    for (c in cl) {
                        // Add the card name
                        ans.cardLimitName.add(c.attr("title"))
                        // Add the card normId
                        val ci = getImageTag(c) // i-th card
                        val cn = ci?.size ?: 0
                        if (ci != null && cn > 0) {
                            ans.cardLimit.add(normCardId(ci[0].attr("alt")))
                        }
                    }
                    // Self checking the limit should name length
                    if (ans.cardLimit.size != ans.cardLimitName.size) {
                        println("!! Craft card limit should same but\n" +
                                "ids = ${ans.cardLimit}\nname = ${ans.cardLimitName}\n")
                    }
                }

                // Header of Row
                val anx = getAnchors(s, "龍刻技能", "召喚獸能力提升", "常駐附加效果")
                // 1st
                var up = anx[0]
                if (up >= 0) {
                    val sks =  getCraftSkills(s, up, anx[1])
                    ans.craftSkill.addAll(sks)
                }

                // 2nd
                up = anx[1]
                if (up >= 0) {
                    ans.upHp = s[up + 1].text()
                    ans.upAttack = s[up + 2].text()
                    ans.upRecovery = s[up + 3].text()
                }

                // 3rd
                up = anx[2]
                if (up >= 0) {
                    for (i in up + 1 until s.size) {
                        val cs = CraftSkill()
                        cs.level = i - up
                        cs.detail = s[i].text()
                        ans.extraSkill.add(cs)
                    }
                }
            } else {
                ans.rarity = s[3].text().replace("★", "").trim().toInt()
                ans.level = s[4].text().replace("Lv.", "", true).trim().toInt()
                ans.attrLimit = s[5].text()
                ans.raceLimit = s[6].text()
                ans.mode = s[8].text()
                ans.charge = s[9].text()
                // Header of Row
                val x = getAnchors(s, "龍刻技能", "來源")
                val sks =  getCraftSkills(s, x[0], x[1])
                ans.craftSkill.addAll(sks)
            }
            return ans
        }

        // parse llike => Skill1 , 1 回合內，隊伍回復力提升 50% , 發動積分：2000
        private fun getCraftSkills(s: Elements, from: Int, to: Int) : List<CraftSkill> {
            val ans = ArrayList<CraftSkill>()
            val k = 2 // k elements as craft
            val skillN = (to - from - 1) / k // skillN crafts
            for (i in 0 until skillN) {
                val at = from + k * i
                val cs = CraftSkill()
                cs.level = i + 1
                cs.detail = s[at + 1].text()
                cs.score = s[at + 2].text().substringAfter("：").toInt()
                ans.add(cs)
            }
            return ans
        }

        private fun isArmCraft(e: Element) : Boolean {
            val s = e.getElementsByTag("th")
            val a = getAnchorsContain(s
                    , "卡片 限制", "生命力", "攻擊力", "回復力"
                    , "能力提升", "屬性 限制", "種族 限制")
            var isArm = a[0] >= 0 || a[1] >= 0 || a[2] >= 0 || a[3] >= 0
            return isArm
        }

        /**
         * Fetch <span>'s item & simple craft as craft
         */
        fun getArmCraft(e: Element?, simple: SimpleCraft, baseWiki: String) : Craft {
            val ans = Craft(simple)
            if (e == null) return ans

            val s = e.getElementsByTag("td")
            val max = s.size
            val a = s[1].text().equals(ans.name)
            ans.rarity = s[3].text().replace("★", "").trim().toInt()
            ans.level = s[4].text().replace("Lv.", "", true).trim().toInt()
            ans.attrLimit = s[5].text()
            ans.raceLimit = s[6].text()
            ans.mode = s[8].text()
            ans.charge = s[9].text()
            val index = getAnchors(s, "技能", "來源")
            val skillN = (index[1] - index[0] - 1) / 2
            for (i in 0 until skillN) {
                val at = index[0] + 2 * i
                val cs = CraftSkill()
                cs.level = i + 1
                cs.detail = s[at + 1].text()
                cs.score = s[at + 2].text().substringAfter("：").toInt()
                ans.craftSkill.add(cs)
            }
            return ans
        }

        private fun getAnchorsContain(e: Elements, vararg s: String) : IntArray {
            return getAnchorsMeet(strContain, e, *s)
        }

        private fun getAnchors(e: Elements, vararg s: String) : IntArray {
            return getAnchorsMeet(strEqual, e, *s)
        }


        private val strEqual = object : MeetSS<String, Boolean> {
            override fun meet(a: String?, b: String?): Boolean {
                if (a == null) {
                    return b == null
                } else {
                    return a.equals(b)
                }
            }
        }

        private val strContain = object : MeetSS<String, Boolean> {
            override fun meet(a: String?, b: String?): Boolean {
                if (a == null) {
                    return b == null
                } else {
                    if (b == null) {
                        return false
                    } else {
                        return a.contains(b)
                    }
                }
            }
        }

        private fun getAnchorsMeet(f: MeetSS<String, Boolean>, e: Elements, vararg s: String) : IntArray {
            val ans = IntArray(s.size)
            ans.fill(-1)
            for (i in 0 until e.size) {
                for (j in 0 until s.size) {
                    if (ans[j] < 0 && f.meet(e[i].text(), s[j])) {
                        ans[j] = i
                    }
                }
            }
            return ans
        }

        fun getTdText(e: Element, index: Int) : String {
            return getTdElement(e, index)?.text() ?: ""
        }

        fun getTdElement(e: Element, index: Int) : Element? {
            val tds = e.getElementsByTag("td")
            val n = tds?.size ?: 0
            if (n > index) {
                return tds[index]
            }
            return null
        }

        fun getSkill(ame: SkillInfo?, e: Element, baseWiki: String) : SkillInfo {
            val r = ame ?: SkillInfo()
            val a = e.getElementsByTag("a")
            r.skillLink = getWikiLink(a.attr("href"), baseWiki)
            var s = a.text()
            if (s.isEmpty()) {
                s = a.attr("title")
            }
            r.skillName = s

            return r
        }

        fun getImageFileInfo(doc: Document, wikiBase: String) : List<ImageFileInfo> {
            val allInfo = ArrayList<ImageFileInfo>()
            val content = doc.getElementById("mw-content-text")
            if (content != null) {
                // Get those elements to fill ImageFileInfo
                // 1. Check content size
                val items = content.getElementsByClass("wikia-gallery-item")
                // 2. Data componenets
                val light = content.getElementsByClass("lightbox")
                val user = content.getElementsByClass("wikia-gallery-item-user")
                val major = content.getElementsByClass("thumbimage lzy lzyPlcHld")

                val valid = isSameSize(items, light, user, major)
                if (valid) {
                    val cn = items?.size ?: 0
                    for (i in 0 until cn) {
                        val info = ImageFileInfo()
                        info.title = light[i].attr("title")
                        info.wikiPage = wikiBase + "" + light[i].attr("href")
                        info.uploader = user[i].text()
                        info.filename = major[i].attr("data-image-name")
                        allInfo.add(info)
                    }
                }
            }

            return allInfo
        }

        private fun isSameSize(vararg elements: Elements) : Boolean {
            if (elements.size > 1) {
                val n = elements[0].size
                for (e in elements) {
                    if (e.size != n) {
                        return false
                    }
                }
            }
            return true
        }

        // Extract for TosCard
        // http://zh.tos.wikia.com/wiki/001
        fun getCardTds(element: Element) : CardTds? {
            // debug
            var log = false

            // formal
            val td2 = element.getElementsByTag("td")

            val result = CardTds()
            val rawTds = result.RawTds
            val td = result.Tds
            val inImgs = result.Images
            val ames = result.Amelio
            val awks = result.Awaken
            val pows = result.PowRel
            val vrbs = result.VirReb
            val evos = result.Evolve
            val coms = result.Combine
            val virs = result.Rebirth
            val arms = result.ArmCraft
            val swch = result.Switching
            val supr = result.Supreme
            var x = 0
            val td2n = td2?.size ?: 0
            var part = CardPart.Main

            if (log) {
                L.log("part = %s", part)
            }
            for (i in 0 until td2n) {
                val e = td2[i]
                val etxt = e.text()
                // Break points ----------------
                if (part == CardPart.AwakenRecall) {
                    part
                }
                // basic, active, leader, ... text anchors in tds
                if (i in intArrayOf(11, 54, 55, 60, 63, 81, 86, 88)) {
                    i
                    // 11 = basic, 54 = active, 60 = leader, 63, 81, 86, 88, 89
                }
                // Break points ----------------

                if (log) {
                    if (part != part.next(etxt)) {
                        L.log("part = %s -> next = %s", part, part.next(etxt))
                    }
                }
                part = part.next(etxt)
                if (log) {
                    L.log("#%3d : %s \n   %s", i, etxt, e)
                }

                val tables = e.getElementsByTag("table")?.size ?: 0

                // omit the big table
                val noNeed = etxt.isEmpty()
                        // Table of big content of "基本屬性",
                        // <td rowspan="3" colspan="10" style="display:none" class="hidden">
                        || e.attr("class").toLowerCase() == "hidden"
                        // Inner html contains <table>, like "昇華"
                        || tables > 0

                // Takes the images of evolution
                val noscript = getImgExceptNoscript(e)

                // Check if switch 變身
                var isSwt = false
                when (part) {
                    CardPart.SkillActive -> {
                        isSwt = hasKeyInText("EvoArrow", noscript)
                    }
                    else -> {

                    }
                }

                if (!noNeed) {
                    rawTds.add(e)
                    td.add(etxt)
                }
                val inStage = e.parent().text().contains("關卡")
                if (inStage) {
                    val ea = e.getElementsByTag("a")
                    ea.forEachIndexed({ j, ej ->
                        when (part) {
                            CardPart.Amelioration -> {
                                if (j % 2 == 1) {
                                    ames.add(asIconInfo(ej))
                                }
                            }
                            CardPart.PowerRelease -> {
                                if (j % 2 == 1) {
                                    pows.add(asIconInfo(ej))
                                }
                            }
                            CardPart.AwakenRecall -> {
                                if (j % 2 == 1) {
                                    val aki = Awaken(asIconInfo(ej))
                                    aki.skill = td2[i-1].text()
                                    awks.add(aki)
                                }
                            }
                            CardPart.VirtualRebirth -> {
                                if (j % 2 == 1) {
                                    vrbs.add(asIconInfo(ej))
                                }
                            }
                            else -> {}
                        }
                    })
                }

                if (noscript.size > 0) {
                    x++

                    // We want the evolution's icons
                    for (nos in noscript) {
                        val alt = getImgAlt(nos)
                        if (alt != null) {
                            // Add for no table, exclude duplicate Refine1~4
                            if (tables == 0) {
                                inImgs.add(alt)
                            }
                            when (part) {
                                CardPart.Evolution,
                                CardPart.PowerRelease,
                                CardPart.VirtualRebirth,
                                CardPart.VirRebirTrans -> {
                                    if (part != CardPart.VirRebirTrans) {
                                        evos.add(alt)
                                    }
                                    if (part == CardPart.VirtualRebirth || part == CardPart.VirRebirTrans) {
                                        var virTrans = false
                                        // Find 異力轉換 的 rebirth from & change
                                        if (part == CardPart.VirRebirTrans) {
                                            virs.add("") // 0 = from -> No from
                                            virs.add("") // see TosCardCreator#fillVirRebirth()
                                            virs.add("")
                                            virTrans = true
                                        } else { // CardPart.VirtualRebirth
                                            val eThs = e.parent().parent().getElementsByTag("th")
                                            for (eTh in eThs) {
                                                if ("異力轉換" == eTh.text()) {
                                                    virTrans = true
                                                }
                                            }
                                        }
                                        if (virTrans) {
                                            virs.add(alt)
                                        }
                                    }
                                }
                                CardPart.Combination -> {
                                    coms.add(alt)
                                }
                                CardPart.SupremeReckon -> {
                                    supr.add(alt)
                                }
                                else -> {

                                }
                            }
                            if (isSwt) {
                                val endI = alt.matches(Regex("\\d+i")) //  1167i
                                if (endI) {
                                    swch.add(alt)
                                }
                            }
                        }
                    }
                } else {

                }
            }
            if (td.size > 0) {
                return result
            }
            return null
        }

        private fun getImgExceptNoscript(e: Element) : Elements {
            val es = e.getElementsByTag("img")
            val ans = Elements()
            for (e in es) {
                if (e.parent().tagName() != "noscript") {
                    ans.add(e)
                }
            }
            return ans
        }

        fun hasKeyInText(key: String, es: Elements) : Boolean {
            for (e in es) {
                val alt = getImgAlt(e)
                if (alt != null && alt.contains(key)) {
                    return true
                }
            }
            return false
        }

        fun getCardImagedLink(doc: Document) : List<NameLink> {
            val result = ArrayList<NameLink>()
            val monster = doc.getElementById("monster-data")
            val ths = monster.getElementsByTag("th")

            if (ths != null) {
                for (th in ths) {
                    val txt = th.text();
                    if (txt.contains(Regex("(關卡|能力)"))) {
                        val taga = th.parent().getElementsByTag("a")
                        if (taga.size > 0) {
                            val ei = taga[0]
                            val info = NameLink()
                            info.link = getWikiLink(ei?.attr("href") ?: "", wikiBaseZh)
                            info.name = ei?.attr("title") ?: ""
                            if (!info.isEmpty()) {
                                result.add(info)
                            }
                        }
                    }
                }
            }
            return result
        }

        fun getCardDetails(doc: Document) : CardDetail {
            val de = CardDetail()
            //val details = doc.getElementsByClass("module move")
            val tbs = doc.getElementsByTag("table")
            if (tbs.size == 0) return de
            val details = tbs.last().getElementsByClass("box")

            var teamInfo = ""
            var cardInfo = ""
            for (det in details) {
                val txt = det.text()
                val tif = txt.indexOf("隊伍技能")
                if (tif in 0..9) { // Find team info node, not story node
                    val size = det.childNodeSize()
                    if (size > 0) {
                        val ele = det.childNode(size - 1)
                        if (ele is Element) {
                            teamInfo = concatTextNodes(ele)
                        }
                    }
                }

                val cif = txt.indexOf("卡牌資訊")
                // Fill in same skills in node
                if (cif in 0..9) { // Find card info node, not story node
                    val size = det.childNodeSize()
                    if (size > 0) {
                        val ele = det.childNode(size - 1)
                        if (ele is Element) {
                            cardInfo = concatTextNodes(ele)
                            val tas = ele.getElementsByTag("a")
                            for (a in tas) {
                                de.sameSkills.add(getAltId(a))
                            }
                        }
                    }
                }
            }
            val noTeam = teamInfo.isEmpty()
            if (noTeam) {
                de.detail = cardInfo
            } else {
                de.detail = teamInfo + "\n\n\n" + cardInfo
            }
            return de
        }

        fun getCardDetailsNormed(doc: Document) : CardDetail {
            var s = getCardDetails(doc)
            val oneLn = arrayOf("發動條件")
            val newLn = arrayOf("隊伍技能", "發動條件", "合成時加入技能", "＊", "隊伍效果"
                , "組合技能：", "機械族特性", "當所有機械族成員"//, "條件："
                , "素材用途", "指定系列包括", "此素材出處", "此潛解素材出處", "此進化素材出處", "此強化素材出處")


            return s
        }

        fun concatTextNodes(e: Element) : String {
            val nodes = e.childNodes()
            var s = ""
            nodes.forEachIndexed { i, node -> run {
                var str = ""
                var tag = ""
                if (node is TextNode) {
                    str = node.text().trim()
                } else if (node is Element) {
                    str = node.text().trim()
                    tag = node.tagName()
                }
                if ("br" == tag) {
                    s += str + "\n"
                } else if (str.isNotEmpty()) {
                    s += str
                }
            }}
            return s
        }

        @Deprecated("Not handy")
        fun getTr(element: Element) : MutableList<String>? {
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
                    td.add(e.text())
                }
            }
            if (td.size > 0) {
                return td
            }
            return null
        }

        // Extract for icons
        // view-source:http://towerofsaviors.wikia.com/wiki/File:InvWater.png
        fun getIcon(element: Element) : NameLink {
            val es = element.getElementsByClass("internal")

            val n = es.size
            val info = NameLink()
            if (n > 0) {
                val e = es[0]
                info.link = getWikiLink(e.attr("href"), wikiBaseZh)
                info.name = e.attr("title") // [0]
                //info.name = e.attr("download") // [1]
            }
            return info
        }

        private fun asIconInfo(e: Element) : NameLink {
            val info = NameLink()
            info.link = getWikiLink(e.attr("href"), wikiBaseZh)
            info.name = e.text()//e.attr("title")
            return info
        }
    }

    fun getAllMaxBonusSrc(id : Int) : String {
        // %7B = {   %7D = }
        val link = "https://tos.fandom.com/zh/api.php?format=json&action=expandtemplates&text=%7B%7B" +
                id.toString() + "|fullstatsMax%7D%7D"
        val data = getApiBody(link)
        // Substring body part & split
        val prefix = "\"expandtemplates\":{\"*\":\""
        val px = data.lastIndexOf(prefix)
        val suffix = "\"}}"
        val sx = data.indexOf(suffix, px)

        val src = data.substring(px + prefix.length, sx)
        return src
    }

    private val mainClient = OkHttpClient().newBuilder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .build()
    fun getApiBody(link : String) : String {
        val t = TicTac2()
        val client = mainClient
        val request = Request.Builder().url(link).build() // need longer for first time
        var answer = ""
        try {
            t.tic()
            val response = client.newCall(request).execute() // 99% time
            val body = response.body() // fast
            answer = body?.string() ?: ""
            t.tac("Done ${link}")
        } catch (e : IOException) {
            e.printStackTrace()
        }
        return answer
    }
}