package main.kt

import com.google.gson.annotations.SerializedName
import flyingkite.tool.TextUtil
import main.card.SkillLite
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import org.jsoup.select.Elements

class MainStage {
    @SerializedName("title")
    var title = "" // 第十封印

    @SerializedName("substages")
    val substages = ArrayList<StageGroup>()

    override fun toString(): String {
        return "$title : \n  $substages"
    }
}

class StageGroup {
    @SerializedName("group")
    var group = "" // 殺戮戰窟 in 10

    @SerializedName("stages")
    val stages = ArrayList<Stage>()

    override fun toString(): String {
        var s = "$group > \n"
        for (i in 0 until stages.size) {
            val si = stages[i]
            s += "  #$i = $si\n"
        }
        return s
    }
}

open class Stage {
    @SerializedName("link")
    var link = ""

    @SerializedName("name")
    var name = ""

    @SerializedName("icon")
    var icon = ""

    override fun toString(): String {
        return "$icon, $name -> $link"
    }
}

open class RelicStage : Stage() {
    @SerializedName("coin")
    var coin = 0

    override fun toString(): String {
        return "$$coin = ${super.toString()}"
    }
}

open class NameLink {
    @SerializedName("link")
    var link = ""

    @SerializedName("name")
    var name = ""

    fun isEmpty() : Boolean {
        return TextUtil.isEmpty(link) && TextUtil.isEmpty(name)
    }

    override fun toString(): String {
        return "$name -> $link"
    }
}

open class Awaken : NameLink {
    var skill = ""

    constructor()
    constructor(icf: NameLink) {
        link = icf.link
        name = icf.name
    }
}

class CardTds {
    var RawTds : Elements = Elements()
    val Tds: MutableList<String> = ArrayList()
    val Amelio = ArrayList<NameLink>()
    val PowRel = ArrayList<NameLink>()
    val Awaken = ArrayList<Awaken>()
    val VirReb = ArrayList<NameLink>()
    val Images: MutableList<String> = ArrayList()
    val Evolve: MutableList<String> = ArrayList()
    val Combine: MutableList<String> = ArrayList()
    val Rebirth: MutableList<String> = ArrayList()
    val ArmCraft: MutableList<String> = ArrayList()
    val Switching: MutableList<String> = ArrayList()
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
        s.name = skillName
        s.cdMin = skillCDMin
        s.cdMax = skillCDMax
        s.effect = skillDesc
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

    fun isChallenge() : Boolean {
        // Last 50 hrs
        //val hr = Math.round((dateEnd + 60 - dateStart) / 3600.0)
        //return hr == 50L
        return asTexts().toString().contains(Regex("(地獄級|夢魘級|戰慄級|雙週任務)"))
    }

    fun toStage() : Stage {
        val n = tds.size
        if (n == 0) return Stage()

        val ex = tds[n - 1]
        return TosGet.getStageInfo(ex.getElementsByTag("a").first())
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

class FullStatsMax {
    var AMhp = 0
    var AMAttack = 0
    var AMRecovery = 0

    fun parse(src : String?) : FullStatsMax {
        if (src == null) return this

        val vs = src.split(",")
        if (vs.size < 6) return this

        AMhp = vs[3].toIntOrNull() ?: 0
        AMAttack = vs[4].toIntOrNull() ?: 0
        AMRecovery = vs[5].toIntOrNull() ?: 0
        return this
    }

    fun exists() : Boolean {
        return AMhp > 0 || AMAttack > 0 || AMRecovery > 0
    }

    override fun toString(): String {
        return "$AMhp|$AMAttack|$AMRecovery"
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
    @SerializedName("extraSkill")
    var extraSkill = ArrayList<CraftSkill>()

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

// Listing similar Anchors.java
enum class CardPart(_key: String) {
    Main("Icon"),
    Basic("基本屬性"),
    SkillActive("主動技"),
    SkillLeader("隊長技"),
    Amelioration("昇華"),
    AwakenRecall("極限突破"),
    Evolution("進化列表"),
    PowerRelease("潛能解放"),
    Combination("合體列表"),
    VirtualRebirth("異空轉生"),
    VirRebirTrans("異力轉換"),
    Dragonware("武裝龍刻"),
    Source("來源"),
    Empty("Empty"),
    ;

    private val key = _key

    fun next(s : String) : CardPart {
        for (v in values()) {
            if (v.ordinal > this.ordinal && s.contains(v.key)) {
                return v
            }
        }
        return this
    }
}

open class CopyInfo(srcF : String, srcN : String, dstF : String, dstN : String) {
    var srcFolder = srcF
    var srcName = srcN
    var dstFolder = dstF
    var dstName = dstN
}