package main.kt

import flyingkite.functional.MeetSS
import flyingkite.log.L
import flyingkite.math.MathUtil
import flyingkite.tool.TextUtil
import flyingkite.tool.TicTac2
import main.card.TC.id
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import org.jsoup.select.Elements
import java.io.IOException
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class TosGet {
    companion object me {
        private val printTdHtml = false
        @Deprecated("")
        private val zhLinkFile = "http://zh.tos.wikia.com/wiki/File:" // TODO : Update link
        private val imageClass = "image image-thumbnail link-internal"
        private val wikiBaseZh = "https://tos.fandom.com"
        private val wikiBaseZhOld = "http://zh.tos.wikia.com"
        private val imageSrc = "https://vignette.wikia.nocookie.net"

        // Extract for TosCard's big image and links
        // http://zh.tos.wikia.com/wiki/001
        fun getImage(element: Element) : String {
            val imgs = getImageTag(element)
            val s = imgs?.size ?: 0
            if (s > 0) {
                return imgs?.get(0)?.attr("src") ?: ""
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
            if (imgs.size > 0) {
                return imgs[0].attr("alt")
            }
            return null
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

        fun getSummonerTable(index: Int, elements: Elements) : TableInfo {
            val info = TableInfo()
            val no = index >= elements.size
            if (no) return info

            return getSummonerTable(elements[index])
        }

        fun getSummonerTable(element: Element) : TableInfo {
            val info = TableInfo()

            val ths = element.getElementsByTag("th")
            ths.forEachIndexed { i, ele -> run {
                info.headers.add(ele.text())
            }}

            val tds = element.getElementsByTag("td")
            tds.forEachIndexed { i, ele -> run {
                info.cells.add(ele.text())
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
                        ski.skillCDMin = getTextAt(tds, 2).toInt()
                        ski.skillCDMax = getTextAt(tds, 3).toInt()
                        ski.skillDesc = getTextAt(tds, 4)
                        ski.monsters = getImgAltIds(tds[5])
                    }
                    "主動技 ‧ 昇華" -> {
                        // 昇華後 : 金睛真火 ‧ 凝煉
                        ski.skillName = getTextAt(tds, 1)
                        ski.skillCDMin = getTextAt(tds, 2).toInt()
                        ski.skillCDMax = getTextAt(tds, 3).toInt()
                        ski.skillDesc = getTextAt(tds, 4)
                        // 6
                        ski.monsters = getImgAltIds(tds.last())
                    }
                    "組合技" -> {
                        // 三原獵刃
                        ski.skillName = getTextAt(tds, 1)
                        ski.skillCDMin = getTextAt(tds, 2).toInt()
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

            val s = e.getElementsByTag("a")
            for (item in s) {
                val valid = item.hasClass(imageClass)
                if (valid) {
                    // Start parse here
                    val c = CardItem()
                    c.link = getWikiLink(item.attr("href"), baseWiki)
                    c.title = item.attr("title")

                    val n = item.children()?.size ?: 0
                    if (n > 0) {
                        val img = item.child(0)
                        c.id = normCardId(img.attr("alt"))
                        val id = c.id.toInt()
                        c.linkId = wikiBaseZhOld + "/wiki/" + id
                    }
                    ans.add(c)
                }
            }
            return ans
        }

        fun getAllMaxBonusSrc(id : Int) : String {
            // %7B = {   %7D = }
            val link = "https://tos.fandom.com/zh/api.php?format=json&action=expandtemplates&text=%7B%7B" +
                    id.toString() + "|fullstatsMax%7D%7D"
            val data = getApiBody(link)
            // Substring body part & split
            val prefix = "{\"expandtemplates\":{\"*\":\""
            val px = data.indexOf(prefix)
            val suffix = "\"}}"
            val sx = data.indexOf(suffix)

            val src = data.substring(px + prefix.length, sx)
            return src
        }

        fun getApiBody(link : String) : String {
            val client = OkHttpClient().newBuilder()
                    .readTimeout(120, TimeUnit.SECONDS)
                    .build()
            val request = Request.Builder().url(link).build()
            var answer = ""
            try {
                val t = TicTac2()
                t.tic()
                val response = client.newCall(request).execute()
                val body = response.body()
                answer = body?.string() ?: ""
                t.tac("Done ${link}")
            } catch (e : IOException) {
                e.printStackTrace()
            }
            return answer;
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
                    val href = ax[0].attr("href")
                    ans.add(baseWiki + href)
                }
            }
            return ans
        }

        fun getMainStages(e: Element?, baseWiki: String) : List<MainStage> {
            val ans = ArrayList<MainStage>()
            if (e == null) return ans

            val items = e.getElementsByClass("tabbertab")

            for (i in 0 until items.size) {
                val item = items[i]
                val tit = item.attr("title")
                val eas = item.getElementsByClass(imageClass)
                val bs = item.getElementsByTag("b")
                val bs2 = item.getElementsByTag("b") // use for looping pack
                var k = 0 // The index of eas

                val main = MainStage()
                main.title = bs[0].text()
                if (eas != null) {
                    var pack = intArrayOf(0)
                    var ten = false
                    var nine = false
                    var six = false

                    when (tit) {
                        "第10封印" -> {
                            ten = true
                            pack = intArrayOf(5, 5, 5, 5)
                            bs2.removeAt(0)
                        }
                        "第7-9封印" -> {
                            nine = true
                            pack = intArrayOf(1, 2, 2, // 9
                                    1, 3, 3, 3, 3, 3, // 8
                                    6) // 7
                            bs2.removeAt(11) // <b>第七封印</b>
                            bs2.removeAt(4) // <b>第八封印</b>
                            bs2.removeAt(0) // <b>第九封印</b>
                        }
                        "第1-6封印" -> {
                            six = true
                            pack = intArrayOf(6, 6, 6, 6, 6, 7)
                        }
                    }
                    var main2 = MainStage()
                    pack.forEachIndexed { pk, pv ->
                        if (ten) {
                            when (pk) {
                                0 -> {
                                    main2 = MainStage()
                                    main2.title = bs[0].text()
                                }
                                else -> {
                                }
                            }
                        } else if (nine) {
                            // Split info
                            when (pk) {
                                0 -> {
                                    main2 = MainStage()
                                    main2.title = bs[0].text()
                                }
                                3 -> { // <b>第八封印</b>
                                    main2 = MainStage()
                                    main2.title = bs[4].text()
                                }
                                9 -> { // <b>第七封印</b>
                                    main2 = MainStage()
                                    main2.title = bs[11].text()
                                }
                            }
                        } else if (six) {
                            // Create new stage info
                            main2 = MainStage()
                            main2.title = bs[pk].text()
                        }

                        val subs = StageGroup()
                        if (ten || nine) {
                            if (pk < bs2.size) {
                                subs.group = bs2[pk].text()
                            }
                        }
                        for (zi in 0 until pv) {
                            val ea = eas[k]
                            val ms = getStageInfo(ea, baseWiki)
                            subs.stages.add(ms)
                            k++
                        }

                        //if (nine || six) {
                            main2.substages.add(subs)
                        if (ten) {
                            if (pk in intArrayOf(0, 6, 11)) {
                                ans.add(main2)
                            }
                        } else if (nine) {
                            if (pk in intArrayOf(2, 8, 9)) {
                                // add for 第7-9封印
                                ans.add(main2)
                            }
                        } else if (six) {
                            // add for 第1-6封印
                            ans.add(main2)
                        }
                        //} else {
                        //    main.substages.add(subs)
                        //}
                    }

                    // add only when 第十封印
                    if (nine || six) {
                    } else {
                        //ans.add(main)
                    }
                }
            }
            return ans
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

        private fun getStageInfo(e: Element, baseWiki: String) : Stage {
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
                    if (axi.hasClass(imageClass)) {
                        val aimg = axi.child(0)
                        val si = SimpleIcon()
                        // For 1~3 icons, its data-src did not fill in content
                        // Need "https://vignette.wikia.nocookie.net/tos/images"...
                        si.iconLink = getVignette(aimg, "data-src", "src")
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

        fun getVignette(e: Element, vararg attrs: String) : String {
            var img = ""
            for (key in attrs) {
                img = e.attr(key)
                if (img.startsWith("https://vignette.wikia.nocookie.net/tos/images")) {
                    return img
                }
            }
            return img
        }

        fun getRelicPassStages(e: Element?, baseWiki: String) : List<RelicStage> {
            val ans = ArrayList<RelicStage>()
            if (e == null) return ans

            val im = e.getElementsByClass(imageClass)
            val tt = e.getElementsByClass("tt-text")

            // Assert their size
            if (im.size != tt.size) {
                println("Why table has ${im.size} images & ${tt.size} tt-texts?")
            }

            // Start to parse
            val n = Math.min(im.size, tt.size)
            for (i in 0 until n) {
                val imi = im[i]
                val tti = tt[i]
                val rs = RelicStage()
                rs.name = imi.attr("title")
                rs.link = getWikiLink(imi.attr("href"), baseWiki)
                rs.icon = getAltId(imi)
                rs.coin = tti.parent().text().replace("x ", "").toInt()
                ans.add(rs)
            }

            return ans
        }

        // Similar with getMainStages()
        fun getVoidRealmStages(e: Element?, baseWiki: String) : List<MainStage> {
            val ans = ArrayList<MainStage>()
            if (e == null) return ans

            val items = e.getElementsByClass("tabbertab")

            for (i in 0 until items.size) {
                val item = items[i]
                val tit = item.attr("title")
                val eas = item.getElementsByClass(imageClass)
                val bs = item.getElementsByTag("b")
                val bs2 = item.getElementsByTag("b") // use for looping pack
                var k = 0 // The index of eas

                val main = MainStage()
                main.title = bs[0].text()
                if (eas != null) {
                    var pack = intArrayOf(0)
                    var hero = false
                    var iron = false
                    val map = HashMap<Int, Int>()

                    when (tit) {
                        "英靈時代" -> {
                            hero = true
                            // 序章 1
                            // 分支一︰奧丁支線 2, 4, 1, 1, 1, 2, 1, 1, 1
                            // 分支二︰洛基支線 5, 1, 1, 1, 1, 3, 1,
                            // 終章 3
                            pack = intArrayOf(
                                    1, // 序章
                                    2, 4, 1, 1, 1, 2, 1, 1, 1, //分支一︰奧丁支線
                                    5, 1, 1, 1, 1, 3, 1, // 分支二︰洛基支線
                                    3 // 終章
                            )
                            // Remove the header of group title for group name
                            bs2.removeAt(21) // 終章
                            bs2.removeAt(12) // 分支二
                            bs2.removeAt(2) // 分支一
                            bs2.removeAt(0) // 序章
                            // (0, 0) initial,
                            // (x, y) bold title indices
                            // for sub-stage x (left part bold title count), its title is at y
                            // map[key] = value
                            map[0] = 0
                            map[1] = 2
                            map[10] = 12
                            map[17] = 21
                        }
                        "黑鐵時代" -> {
                            iron = true
                            // 分支一︰狂魔支線 16
                            // 分支二︰玩具支線 15, 4
                            // 分支三︰機械支線 10, 12
                            pack = intArrayOf(16, // 分支一︰狂魔支線
                                    15, 4, // 分支二︰玩具支線
                                    10, 12) // 分支三︰機械支線
                            bs2.removeAt(5) // 分支三
                            bs2.removeAt(2) // 分支二
                            bs2.removeAt(0) // 分支一
                            map[0] = 0
                            map[1] = 2
                            map[3] = 5
                        }
                    }
                    var main2 = MainStage()
                    pack.forEachIndexed { pk, pv ->
                        if (hero || iron) {
                            val v = map[pk]
                            if (v != null) {
                                main2 = MainStage()
                                main2.title = bs[v].text()
                            }
                        }

                        val subs = StageGroup()
                        if (pk < bs2.size) {
                            subs.group = bs2[pk].text()
                        }

                        for (zi in 0 until pv) {
                            val ea = eas[k]
                            val ms = getStageInfo(ea, baseWiki)
                            subs.stages.add(ms)
                            k++
                        }

                        main2.substages.add(subs)
                        if (hero || iron) {
                            if (pk in map.keys) {
                                ans.add(main2)
                            }
                        } else {
                            main.substages.add(subs)
                        }
                    }
                }
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
                    c.icon.iconLink = getVignette(g, "data-src", "src")// = getIconLink(si)
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
            } else {
                return baseWiki + src
            }
        }

        private fun getIconLink(e: Element) : String {
            val gx = e.getElementsByTag("img")
            if (gx.size == 0) return ""

            val g = gx[0]
            return getVignette(g, "data-src", "src")
        }


        private fun normCardId(s: String?): String {
            val endI = s != null && s.endsWith("i")
            if (endI && s != null) {
                // Parse "12i" to "0012"
                return String.format(Locale.US, "%04d", s.substring(0, s.length - 1).toInt())
            } else {
                return s ?: ""
            }
        }

        private fun normCraftId(s: String): String {
            val beginC = s.startsWith("C")
            if (beginC) {
                return String.format(Locale.US, "%04d", s.substring(1).toInt())
            } else{
                return s
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
            var x = 0
            val td2n = td2?.size ?: 0
            var part = CardPart.Main

            for (i in 0 until td2n) {
                val e = td2[i]
                val etxt = e.text()
                part = part.next(etxt)
                // Break points ----------------
                if (part == CardPart.VirtualRebirth) {
                    part
                }
                if (i in intArrayOf(60, 82, 84, 85)) {
                    i
                }
                // Break points ----------------

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
                }

                if (!noNeed) {
                    rawTds.add(e)
                    td.add(etxt)
                }
                val inStage = e.parent().text().contains("關卡")
                if (inStage) {
                    val ea = e.getElementsByTag("a")
                    ea.forEachIndexed { j, ej ->
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
                    }
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
                                CardPart.Dragonware -> {
                                    arms.add(alt)
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

                if (printTdHtml) {
                    print("-- text --")
                    print(e.text())
                    println("----------")
                    //print("----html----")
                    //print(e.html())
                    //println("--------")
                }
                //println("Part #$i = $part")
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
            val imgs = monster?.getElementsByClass(imageClass)

            val imgSize = imgs?.size ?: 0
            for (i in 0 until imgSize) {
                val ei = imgs?.get(i)
                val info = NameLink()
                info.link = getWikiLink(ei?.attr("href") ?: "", wikiBaseZh)
                info.name = ei?.attr("title") ?: ""
                if (!info.isEmpty()) {
                    result.add(info)
                }
            }
            return result
        }

        fun getCardDetails(doc: Document) : CardDetail {
            val de = CardDetail()
            val details = doc.getElementsByClass("module move")

            for (det in details) {
                val cif = det.text().indexOf("卡牌資訊")
                if (cif in 0..9) { // Fine card info node, not story node
                    val size = det.childNodeSize()
                    if (size > 0) {
                        val ele = det.childNode(size - 1)
                        if (ele is Element) {
                            //return ele.text()
                            de.detail = concatTextNodes(ele)
                            val tas = ele.getElementsByClass(imageClass)
                            for (a in tas) {
                                de.sameSkills.add(getAltId(a))
                            }
                        }
                    }
                }
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
}