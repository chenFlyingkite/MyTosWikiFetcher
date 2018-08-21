package main.kt

import com.google.gson.annotations.SerializedName
import flyingkite.functional.MeetSS
import flyingkite.tool.TextUtil
import main.card.SkillLite
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import org.jsoup.select.Elements
import java.util.*
import kotlin.collections.ArrayList

class TosGet {
    companion object me {
        private val printTdHtml = false
        private val zhLinkFile = "http://zh.tos.wikia.com/wiki/File:"
        private val imageClass = "image image-thumbnail link-internal"

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

        fun getImageTag(element: Element) : Elements? {
            val nos = element.getElementsByTag("noscript")
            val size = nos?.size ?: 0
            if (size > 0) {
                val x = nos[0]
                return x?.getElementsByTag("img")
            }
            return null
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
            val title = child.get(0).text()
            homeTable.title = title

            val allRows = ArrayList<HomeRow>()
            val trs = child.get(1).getElementsByTag("tr")
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
                                link = wikiBase + "" + taga[0].attr("href")
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
            val info = ImageInfo2()
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
                                    val s = normEvoId(getImgAlt(child[m]))
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
                        //L.log("ZZZ %s %s -> %s", tds.size, ski.skillName, e.baseUri())
                    }
                    "組合技" -> {
                        // 三原獵刃
                        ski.skillName = getTextAt(tds, 1)
                        ski.skillCDMin = getTextAt(tds, 2).toInt()
                        ski.skillDesc = getTextAt(tds, 3)
                        ski.monsters = getImgAltIds(tds[4])
                    }
                    else -> {
                        //L.log("Omit %s", ski.type)
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
                    list.add(normEvoId(img.attr("alt")))
                }
            }
            return list
        }

        private fun getAltId(e: Element) : String {
            return normEvoId(getImgAlt(e))
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
                    val s = normEvoId(getImgAlt(td0))
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
                    c.link = baseWiki + item.attr("href")
                    c.title = item.attr("title")

                    val n = item.children()?.size ?: 0
                    if (n > 0) {
                        val img = item.child(0)
                        c.id = normEvoId(img.attr("alt"))
                        c.linkId = baseWiki + "/wiki/" + c.id.toInt()
                    }
                    ans.add(c)
                }
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
                    val href = ax[0].attr("href")
                    ans.add(baseWiki + href)
                }
            }
            return ans
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
                        //si.iconKey = zhLinkFile + aimg.attr("data-image-key")
                        c.icons.add(si)
                    } else {
                        if (c.name.isEmpty()) {
                            c.name = axi.text()
                            val des = axi.child(0).attr("data-texttip")
                            c.detail = des.substringAfter("<br>")//.replace("<br>", "\n")
                            c.link = baseWiki + axi.attr("href")
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
                    //c.name = si.attr("data-texttip").substringAfter("<br>")

                    // Take the 1st <img> or we should use item.child(0).child(0) ?
                    // <a><img/></a>
                    val ax = si.getElementsByTag("a")
                    if (ax.size == 0) continue
                    val a = ax[0]
                    val gx = si.getElementsByTag("img")
                    if(gx.size == 0) continue
                    val g = gx[0]
                    c.name = a.attr("title")
                    c.link = baseWiki + a.attr("href")
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

        private fun getIconLink(e: Element) : String {
            val gx = e.getElementsByTag("img")
            if (gx.size == 0) return ""

            val g = gx[0]
            return getVignette(g, "data-src", "src")
        }


        private fun normEvoId(s: String?): String {
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
        fun getCraft(e: Element?, simple: SimpleCraft, baseWiki: String) : Craft {
            val ans = Craft(simple)
            if (e == null) return ans

            val s = e.getElementsByTag("td")
            // Update the icon link to be large one
            val isArm = isArmCraft(e)
            ans.icon.iconLink = getIconLink(s[0])

            if (isArm) {
                ans.rarity = s[3].text().replace("★", "").trim().toInt()
                ans.level = s[4].text().replace("Lv.", "", true).trim().toInt()
                ans.mode = s[7].text()
                ans.charge = s[8].text()

                // find card limit
                val cl = s[5].getElementsByTag("a")
                for (c in cl) {
                    // Add the card name
                    ans.cardLimitName.add(c.attr("title"))
                    // Add the card normId
                    val ci = getImageTag(c) // i-th card
                    val cn = ci?.size ?: 0
                    if (ci != null && cn > 0) {
                        ans.cardLimit.add(normEvoId(ci[0].attr("alt")))
                    }
                }
                // Self checking the limit should name length
                if (ans.cardLimit.size != ans.cardLimitName.size) {
                    println("!! Craft card limit should same but\n" +
                            "ids = ${ans.cardLimit}\nname = ${ans.cardLimitName}\n")
                }

                val anx = getAnchors(s, "技能", "能力提升")
                var up = anx[0]
                if (up >= 0) {
                    for (i in anx[0] + 1 until anx[1]) {
                        val cs = CraftSkill()
                        cs.level = i - anx[0]
                        cs.detail = s[i].text()
                        ans.craftSkill.add(cs)
                    }
                }

                up = anx[1]
                if (up >= 0) {
                    ans.upHp = s[up + 1].text()
                    ans.upAttack = s[up + 2].text()
                    ans.upRecovery = s[up + 3].text()
                }
            } else {
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
            r.skillLink = baseWiki + a.attr("href")
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
                        //println("#$i -> ${info.title} >> ${info.uploader} >> ${info.filename}\n    ${info.wikiPage}\n")
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
            val evos = result.Evolve
            val coms = result.Combine
            val virs = result.Rebirth
            val arms = result.ArmCraft
            var x = 0
            val td2n = td2?.size ?: 0
            for (i in 0 until td2n) {
                // Check evolution content
                val isEvo = i > 0 && td2[i - 1].text().contains("進化列表")
                val isCom = i > 0 && td2[i - 1].text().contains("合體列表")
                val isPow = i > 0 && td2[i - 1].text().contains("潛能解放") // Power Release
                val isVir = i > 0 && td2[i - 1].text().contains("異空轉生")
                val isVr2 = i > 0 && td2[i - 1].text().contains("異力轉換")
                val isArm = i > 0 && td2[i - 1].text().contains("武裝龍刻")
                val isVr3 = i > 1 && td2[i - 2].text().contains("異空轉生")
                            && td2[i].attr("colspan") == "3"

                val e = td2[i]

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
                    rawTds.add(e)
                    td.add(e.text())
                } else if (noscript.size > 0) {
                    x++

                    // We want the evolution's icons
                    for (nos in noscript) {
                        val alt = getImgAlt(nos)
                        if (alt != null) {
                            inImgs.add(alt)
                            if (isEvo || isPow || isVir) {
                                evos.add(alt)
                            } else if (isCom) {
                                coms.add(alt)
                            } else if (isVr2 || isVr3) {
                                virs.add(alt)
                            } else if (isArm) {
                                arms.add(alt)
                            } else {
                                alt
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

        fun getCardImagedLink(doc: Document) : List<IconInfo> {
            val result = ArrayList<IconInfo>()
            val monster = doc.getElementById("monster-data")
            val imgs = monster?.getElementsByClass(imageClass)

            val imgSize = imgs?.size ?: 0
            for (i in 0 until imgSize) {
                val ei = imgs?.get(i)
                val info = IconInfo()
                info.link = ei?.attr("href") ?: ""
                info.name = ei?.attr("title") ?: ""
                if (!info.isEmpty()) {
                    result.add(info)
                }
            }
            return result
        }

        fun getCardDetails(doc: Document) : CardDetail {
            val de = CardDetail()
            //((Element) details.get(0).childNodes.get(4)).text()
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
                                de.sameSkills.add(normEvoId(getImgAlt(a)))
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


//            newLn.forEachIndexed { i, li: String -> run {
//                s = s.replace(li, "\n" + li)
//            }}

            return s
        }

        fun concatTextNodes(e: Element) : String {
            val nodes = e.childNodes()//e.textNodes()
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
            //print("s = -> $s")
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
        fun getIcon(element: Element) : IconInfo {
            val es = element.getElementsByClass("internal")

            val n = es.size
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

    fun isEmpty() : Boolean {
        return TextUtil.isEmpty(link) && TextUtil.isEmpty(name)
    }
}

class CardTds {
    var RawTds : Elements = Elements()
    val Tds: MutableList<String> = ArrayList()
    val Images: MutableList<String> = ArrayList()
    val Evolve: MutableList<String> = ArrayList()
    val Combine: MutableList<String> = ArrayList()
    val Rebirth: MutableList<String> = ArrayList()
    val ArmCraft: MutableList<String> = ArrayList()
}

open class TableInfo {
    val headers: ArrayList<String> = ArrayList()
    val cells: ArrayList<String> = ArrayList()
}

open class SkillInfo {
    @SerializedName("skillName")
    var skillName = ""
    @SerializedName("skillLink")
    var skillLink = ""
    @SerializedName("skillCDMin")
    var skillCDMin = -1
    @SerializedName("skillCDMax")
    var skillCDMax = -1
    @SerializedName("skillDesc")
    var skillDesc = ""
    @SerializedName("skillMonsters")
    var monsters: MutableList<String> = ArrayList() // of idNorm

    override fun toString() : String {
        return "$skillCDMin ~ $skillCDMax -> $skillName => $skillLink\n => $skillDesc\nmon = $monsters"
    }

    fun lite(): SkillLite {
        val s = SkillLite()
        s.name = skillName;
        s.cdMin = skillCDMin;
        s.cdMax = skillCDMax;
        s.effect = skillDesc;
        return s
    }
}

class SkillInfo2 : SkillInfo() {
    var type = ""
}


class StageInfo : TableInfo() {
    var title: String = ""
}

class HomeTable {
    var title = ""
    val rows = ArrayList<HomeRow>()

    fun isEmpty() : Boolean {
        return false
        //return TextUtil.isEmpty(link) && TextUtil.isEmpty(imageName)
    }
}

class HomeRow {
    var dateStart :Long = 0
    var dateEnd :Long = 0
    var link = ""
    var tds = ArrayList<Element>()

    fun asTexts() : List<String> {
        val list = ArrayList<String>()
        for (td in tds) {
            //list.add(td.text())
            list.add(txts(td))
        }
        return list
    }

    // Similar to td.text(), but we also takes the span node's title
    fun txts(td: Element) : String {
        val sb = StringBuilder()
        for (cn in td.childNodes()) {
            if (cn is TextNode) {
                sb.append(cn.text())
            } else if (cn is Element) {
                var s = cn.text()
                if (s.isEmpty()) {
                    s = cn.attr("title") // Fetch 魔法石 x 1, 共 x7
                }
                sb.append(s)
            }
        }
        return sb.toString().trim()
    }

    override fun toString() : String {
        return "$dateStart ~ $dateEnd -> ${asTexts()}\n  link = $link"
    }
}

class ImageInfo2 {
    var imageName = ""
    var alt = ""
    var width = 0
    var height = 0
    var link = ""

    fun isEmpty() : Boolean {
        return TextUtil.isEmpty(link) && TextUtil.isEmpty(imageName)
    }
}

class ImageFileInfo {
    var wikiPage = ""
    var title = ""
    var uploader = ""
    var filename = ""

    override fun toString() : String {
        return "$uploader => $title => $filename => $wikiPage"
    }
}

class CardItem {
    @SerializedName("title")
    var title = ""
    @SerializedName("id")
    var id = ""
    @SerializedName("link")
    var link = ""
    @SerializedName("linkId")
    var linkId = ""

    override fun toString() : String {
        return "#$id : $title -> $link , $linkId"
    }
}

/**
 * Item in 關卡敵人技能/1-500
 *
 * view-source:http://zh.tos.wikia.com/wiki/%E9%97%9C%E5%8D%A1%E6%95%B5%E4%BA%BA%E6%8A%80%E8%83%BD/1-500
 */
class EnemySkill {
    @SerializedName("id")
    var id = ""
    @SerializedName("link")
    var link = "" // Enemy skill link
    @SerializedName("name")
    var name = ""
    @SerializedName("detail")
    var detail = ""
    @SerializedName("esCode")
    var esCode = "" // Enemy Skill code, like {{ES1}}
    @SerializedName("icons")
    val icons = ArrayList<SimpleIcon>()

    override fun toString() : String {
        return "#$id  $esCode : [${icons.size}]  -> $name -> $detail -> $link"
    }
}

class SimpleIcon {
    @SerializedName("iconLink")
    var iconLink = ""

    @SerializedName("iconKey")
    var iconKey = ""
    override fun toString() : String {
        return "$iconKey $iconLink"
    }
}

open class SimpleCraft {
    @SerializedName("idNorm")
    var idNorm = "" // %4d
    @SerializedName("name")
    var name = ""
    @SerializedName("link")
    var link = ""
    @SerializedName("icon")
    var icon = SimpleIcon()

    constructor()
    constructor(a : SimpleCraft) {
        name = a.name
        link = a.link
        icon = a.icon
        idNorm = a.idNorm
    }

    override fun toString() : String {
        return "#$idNorm : $name -> $link"
    }
}

class Craft : SimpleCraft {
    // Both have
    @SerializedName("rarity")
    var rarity = 0 // 1, 2, 3
    @SerializedName("level")
    var level = 0
    @SerializedName("mode")
    var mode = ""
    @SerializedName("charge")
    var charge = ""
    @SerializedName("craftSkill")
    var craftSkill = ArrayList<CraftSkill>()

    //-- For Common
    @SerializedName("attrLimit")
    var attrLimit = ""
    @SerializedName("raceLimit")
    var raceLimit = ""

    //-- For Arm
    @SerializedName("cardLimit")
    var cardLimit = ArrayList<String>()
    @SerializedName("cardLimitName")
    var cardLimitName = ArrayList<String>()
    @SerializedName("upHp")
    var upHp = ""
    @SerializedName("upAttack")
    var upAttack = ""
    @SerializedName("upRecovery")
    var upRecovery = ""

    constructor()
    constructor(a : SimpleCraft) : super(a) {
    }

    override fun toString(): String {
        return "${super.toString()}\n" +
                "$rarity★ Lv. $level $upHp $upAttack $upRecovery $mode $charge\n" +
                "$cardLimit $cardLimitName $attrLimit $raceLimit\n" +
                "$craftSkill"
    }
}

class CraftSkill {
    @SerializedName("level")
    var level = 0
    @SerializedName("score")
    var score = 0
    @SerializedName("detail")
    var detail = ""

    override fun toString(): String {
        return "$level : $score -> $detail"
    }
}

class CardDetail {
    var detail = ""
    var sameSkills = ArrayList<String>()

    override fun toString(): String {
        return "$sameSkills -> $detail"
    }
}

//
//fun TosGet.getImage(element: Element) :String {
//    val nos = element.getElementsByTag("noscript")
//    val x: Element
//    val imgs: Elements?
//    val size = nos?.size ?: 0
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