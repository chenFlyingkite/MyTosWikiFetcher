package main.card

import com.google.gson.annotations.SerializedName
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import org.jsoup.select.Elements
import flyingkite.tool.TextUtil

class TosGet {
    companion object me {
        private val printTdHtml = false

        // Extract for TosCard's big image and links
        // http://zh.tos.wikia.com/wiki/001
        fun getImage(element: Element): String {
            val imgs = getImageTag(element)
            val s = imgs?.size ?: 0
            if (s > 0) {
                return imgs?.get(0)?.attr("src") ?: ""
            }
            return ""
        }

        fun getImageTag(element: Element): Elements? {
            val nos = element.getElementsByTag("noscript")
            val size = nos?.size ?: 0
            if (size > 0) {
                val x = nos[0]
                return x?.getElementsByTag("img")
            }
            return null
        }

        fun getImgAlt(element: Element): String? {
            val imgs = element.getElementsByTag("img")
            if (imgs.size > 0) {
                return imgs[0].attr("alt")
            }
            return null
        }

        fun getImgInfo2(element: Element?): ImageInfo2 {
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

        fun getTosMainInfo(element: Element?, wikiBase: String): HomeTable {
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

        fun getTosPageImageInfo(element: Element?): ImageInfo2? {
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

        fun getTosPageEntryContent(doc: Document): String {
            val info = ImageInfo2()
            val conts = doc.getElementsByClass("entry-content");
            val n = conts?.size ?: 0
            if (n > 0) {
                val entry = conts[0]
                return entry.html()
            }
            return ""
        }

        fun getHtml(element: Element): String {
            return element.html() ?: ""
        }

        fun getHtmlAt(index: Int, elements: Elements): String {
            return elements.get(index)?.html() ?: ""
        }

        fun getSummonerTable(index: Int, elements: Elements): TableInfo {
            val info = TableInfo()
            val no = index >= elements.size
            if (no) return info

            return getSummonerTable(elements[index])
        }

        fun getSummonerTable(element: Element): TableInfo {
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

        fun getStageTable(element: Element): StageInfo {
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

        fun getAmeSkillTable(es: Elements, baseWiki: String): List<SkillInfo> {
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
                        var k: Int
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
                                    val s = TosCardCreator.me.normEvoId(getImgAlt(child[m]))
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

        fun getActiveSkillTable(es: Elements, baseWiki: String): SkillInfo {
            // Each table
            val ame = SkillInfo()
            val ei = es[0]
            val itemTrs = ei.getElementsByTag("tr")
            val nTr = itemTrs?.size ?: 0
            if (nTr > 5) {
                // itemTr[0] is header, omit it
                ame.skillName = getTdText(itemTrs[1], 0);// itemTrs[0].getElementsByTag("td")
                ame.skillLink = ei.baseUri()
                ame.skillCDMin = Integer.parseInt(getTdText(itemTrs[2], 0))
                ame.skillCDMax = Integer.parseInt(getTdText(itemTrs[3], 0))
                ame.skillDesc = getTdText(itemTrs[4], 0)
                // Fill in monsters
                val end = nTr - 1
                val td0 = getTdElement(itemTrs[end], 0)
                if (td0 != null) {
                    val s = TosCardCreator.me.normEvoId(getImgAlt(td0))
                    if (!TextUtil.isEmpty(s)) {
                        ame.monsters.add(s)
                    }
                }
            } else {
                print("Omit this items: $nTr trs => ${ei.baseUri()}")
            }
            return ame
        }

        fun getCardItems(e: Element?, baseWiki: String) : List<CardItem>  {
            val ans = ArrayList<CardItem>()
            if (e == null) return emptyList()

            val s = e.getElementsByTag("a")
            for (item in s) {
                //
                val valid = item.hasClass("image image-thumbnail link-internal")
                if (valid) {
                    val c = CardItem()
                    c.link = baseWiki + item.attr("href")
                    c.title = item.attr("title")

                    val n = item.children()?.size ?: 0
                    if (n > 0) {
                        val img = item.child(0)
                        c.id = TosCardCreator.me.normEvoId(img.attr("alt"))
                    }
                    ans.add(c)
                }
            }
            return ans
        }

        fun getCardGroup(e: Element?, baseWiki: String) : List<String>  {
            val ans = ArrayList<String>()
            if (e == null) return emptyList()

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

        fun getTdText(e: Element, index: Int): String {
            return getTdElement(e, index)?.text() ?: ""
        }

        fun getTdElement(e: Element, index: Int): Element? {
            val tds = e.getElementsByTag("td")
            val n = tds?.size ?: 0
            if (n > index) {
                return tds[index]
            }
            return null
        }

        fun getSkill(ame: SkillInfo?, e: Element, baseWiki: String): SkillInfo {
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

        fun getImageFileInfo(doc: Document, wikiBase: String): List<ImageFileInfo> {
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

        private fun isSameSize(vararg elements: Elements): Boolean {
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
        fun getCardTds(element: Element): CardTds? {
            val td2 = element.getElementsByTag("td")

            val result = CardTds()
            val td = result.Tds
            val inImgs = result.Images
            val evos = result.Evolve
            val coms = result.Combine
            var x = 0
            val td2n = td2?.size ?: 0
            for (i in 0 until td2n) {
                // Check evolution content
                val isEvo = i > 0 && td2[i - 1].text().contains("進化列表")
                val isCom = i > 0 && td2[i - 1].text().contains("合體列表")
                val isPow = i > 0 && td2[i - 1].text().contains("潛能解放") // Power Release

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
                    td.add(e.text())
                } else if (noscript.size > 0) {
                    x++

                    // We want the evolution's icons
                    for (nos in noscript) {
                        val alt = getImgAlt(nos)
                        if (alt != null) {
                            inImgs.add(alt)
                            if (isEvo || isPow) {
                                evos.add(alt)
                            } else if (isCom) {
                                coms.add(alt)
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
            val imgs = monster?.getElementsByClass("image image-thumbnail link-internal")

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

        fun getCardDetails(doc: Document): String {
            //((Element) details.get(0).childNodes.get(4)).text()
            val details = doc.getElementsByClass("module move")
            val s1 = details?.size ?: 0
            if (s1 > 0) {
                val first = details?.get(0)
                val size = first?.childNodeSize() ?: 0
                if (size > 0) {
                    val ele = first?.childNode(size - 1)
                    if (ele is Element) {
                        //return ele.text()
                        return concatTextNodes(ele)
                    }
                }
            }
            return ""
        }

        fun getCardDetailsNormed(doc: Document): String {
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

        fun concatTextNodes(e: Element): String {
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
            return s;
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
        fun getIcon(element: Element): IconInfo {
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
    val Tds: MutableList<String> = ArrayList()
    val Images: MutableList<String> = ArrayList()
    val Evolve: MutableList<String> = ArrayList()
    val Combine: MutableList<String> = ArrayList()
}

open class TableInfo {
    val headers: ArrayList<String> = ArrayList()
    val cells: ArrayList<String> = ArrayList()
}

class SkillInfo {
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
    var monsters = ArrayList<String>() // of idNorm

    override fun toString(): String {
        return "$skillCDMin ~ $skillCDMax -> $skillName => $skillLink\n => $skillDesc\nmon = $monsters"
    }
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
    fun txts(td: Element): String {
        var sb = StringBuilder()
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

    override fun toString(): String {
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

    override fun toString(): String {
        return "$uploader => $title => $filename => $wikiPage"
    }
}

class CardItem {
    var title = ""
    var id = ""
    var link = ""

    override fun toString(): String {
        return "#$id : $title -> $link"
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