package main.kt

import flyingkite.files.FileUtil
import flyingkite.log.L
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import org.jsoup.select.Elements
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

const val LocalStr = "Localizable.strings"
open class XliffParser {
    // Ruler
    // 0         1         2         3         4         5         6
    // 0123456789012345678901234567890123456789012345678901234567890
    companion object me {
        fun addStringsToIos() {
            // -- Parameters :
            // ios already have translations in other places of xibs
            val useIosSelf = false
            // path of ios
            val iosPath = "D:\\ASD\\PHD_Strings\\PhotoDirector_Psp01"
            val iosXliffPath = "D:\\ASD\\PHD_Strings\\Parsed"
            // Load the lproj files and commit to Localizable.strings
            val targetSvnSrc = "D:\\PhotoDirector_iOS\\Main_01"
            //"D:\\PhotoDirector_iOS\\Branch_MirrorBlend_2.5.1\\PhotoDirector\\PhotoDirector\\Resource"
            // path of Android
            val andPathSrc = "D:\\PhotoDirector_Android\\PHD_01"
            //"D:\\PhotoDirector_Android\\PHD_01\\PhotoDirector\\src\\main\\res"
            // String keys of Android.<string.name> : ios.<source>
            val andIos = HashMap<String, String>()
            //andIos["message_rating_title"] = "Your Photo Looks Perfect!"
            //andIos["message_rating_info"] = "We'd appreciate a good rating for PhotoDirector"
            andIos["common_PerspectiveCorrection"] = "Perspective"
            andIos["common_Vertical"] = "Vertical"
            andIos["common_Horizontal"] = "Horizontal"
            val str = arrayListOf<String>()
            //str.add("Dismiss")
            //--
            val targetSvn = "$targetSvnSrc\\PhotoDirector\\PhotoDirector\\Resource"
            //"D:\\PhotoDirector_iOS\\Branch_MirrorBlend_2.5.1\\PhotoDirector\\PhotoDirector\\Resource"
            // path of Android
            val andPath = "$andPathSrc\\PhotoDirector\\src\\main\\res"

            // apply = Produce file
            // Try to produce : apply =  true, prefix = "f_"
            // Delete tried   : apply = false, prefix = "f_"
            // Write source   : apply =  true, prefix = ""
            val apply = true
            // Created file as prefix
            val tryPrefix = "f_"
            val deleteTry = true

            //------ Main body
            // Replace the strings if target translation has this
            val replace = HashMap<String, String>()
            // Fr.Your Photo Looks Perfect!
            // & -> &amp; ?
            replace["\\'"] = "'"
            replace["&pos;"] = "'"

            //-- produce as iosMe // converting keys
            val iosMe = HashMap<String, String>() // Key is unused, only use value
            for (i in 0 until str.size) {
                iosMe[""+i] = str[i]
            }

            // Let xliffs = All of *.xliff files in iosPath
            val ios = File(iosPath)
            val xliffs = FileUtil.listAllFiles(ios, { z ->
                val name = z.name
                val isPx = name.startsWith(tryPrefix)
                var omit = false
                if (tryPrefix.isNotEmpty()) {
                    omit = omit || isPx
                    if (isPx && deleteTry) {
                        L.log("Delete %s", z)
                        z.delete()
                    }
                }

                !omit && name.contains(".xliff")
            })

            pt(xliffs)

            // Check if missing
            if (xliffs.size > 0) {
                XliffParser.checkTarget(xliffs[0])
            }

            for (xlf in xliffs) {
                if (xlf.name == "en.xliff") {
                    // Copy xliffs to folder
                    val fn = File(xlf.parent, tryPrefix + xlf.name)
                    val fx = File(iosXliffPath, tryPrefix + xlf.name)
                    fx.parentFile.mkdirs()
                    // Change the new ln
                    val all = FileUtil.readFromFile(fn)
                    FileUtil.writeToFile(fx, all, false)
                } else {
                    // Get string file mapping
                    val iosLang = xlf.name.replace(".xliff", "")
                    val andLang = keyInAndroid(iosLang)
                    val andFile = File("$andPath/values-$andLang/strings.xml")
                    L.log("   %s\n-> %s", xlf.absolutePath, andFile.absolutePath)

                    //String andoAll = FileUtil.readFileAsString(ands);
                    // old way
                    // https://howtodoinjava.com/xml/parse-string-to-xml-dom/
                    val iosStrings = FileUtil.readFromFile(xlf)
                    try {
                        // Find android's nodes, <string>
                        val andd = Jsoup.parse(andFile, "UTF-8")
                        val aa = andd.getElementsByTag("string").size
                        //L.log("%s strings in android", aa);

                        // Find ios's nodes, <trans-unit>
                        val iosd = Jsoup.parse(xlf, "UTF-8")
                        val an = iosd.getElementsByTag("trans-unit").size
                        //L.log("%s strings in ios", an);
                        val added = HashMap<String, String>()

                        val need = if (useIosSelf) iosMe else andIos

                        val n = iosStrings.size - 2
                        for (i in 0..n) {
                            //line #i = si
                            val si = iosStrings[i]
                            val sn = si.trim()
                            val lead = "        "
                            for (ka in need.keys) {
                                val vi = need[ka]
                                // Format of ios
                                val fmt = "<source>$vi</source>"
                                val si1 = iosStrings[i + 1].trim()
                                // - Meet if it has source node, no target node
                                val meet = sn.startsWith(fmt) && !si1.startsWith("<target>")
                                if (meet) {
                                    var a: Element // = target element contains string
                                    if (useIosSelf) {
                                        // - Find target inside ios
                                        val xss = iosd.getElementsMatchingOwnText(vi)
                                        var xs: Element? = null
                                        xssi@
                                        for (xssi in xss) {
                                            if (xs != null) break@xssi
                                            val meet = xssi.tagName() == "trans-unit" && xssi.getElementsByTag("target").size == 1
                                            if (meet) {
                                                xs = xssi
                                            }
                                        }

                                        //val xs = iosd.getElementsMatchingOwnText(vi).first()
                                        if (xs != null) {
                                            a = xs.getElementsByTag("target").first()
                                        } else {
                                            a = iosd.getElementsMatchingOwnText(vi).first()
                                        }
                                    } else {
                                        // - Find target from android
                                        a = andd.getElementsByAttributeValue("name", ka).first()
                                    }
                                    // Target string is s
                                    var s = a.text()
                                    // TODO : \n ?
                                    // Replacing \' to '
                                    if (s.contains("\\")) {
                                        L.log("---- has \\ => %s", s)
                                    }
                                    for (k in replace.keys) {
                                        val v = replace[k]
                                        if (v != null) {
                                            s = s.replace(k, v)
                                        }
                                    }

                                    // - Compose target string to replace that line
                                    // - and print if apply
                                    val sa = "$lead<target>$s</target>"
                                    val end = si + "\n" + sa

                                    L.log("-- #%04d = at ios (%s), add\n", i, iosLang)
                                    L.log("%s\n", end)
                                    if (apply) {
                                        iosStrings[i] = end
                                    }
                                    if (vi != null) {
                                        added[vi] = s
                                    }
                                }
                            }
                        }
                        if (added.isNotEmpty()) {
                            L.log("%s added: ", added.size)
                            for (k in added.keys) {
                                val v = added[k]
                                L.log("$k -> $v")
                            }
                        }

                        if (apply) {
                            // - Write to file
                            val fn = File(xlf.parent, tryPrefix + xlf.name)
                            fn.delete()
                            fn.createNewFile()
                            FileUtil.writeToFile(fn, iosStrings, false)

                            // Copy xliffs to folder
                            val fx = File(iosXliffPath, tryPrefix + xlf.name)
                            fx.parentFile.mkdirs()
                            FileUtil.copy(fn.absolutePath, fx.absolutePath)
                        }
                        //
                        if (apply) {
                            // Load lproj as table
                            val lzFile = File("$targetSvn/$iosLang.lproj/Localizable.strings")
                            val maps = TreeMap<String, LoclzNode>(loadLz(lzFile))

                            for (k in added.keys) {
                                val v = added[k]
                                val node = LoclzNode()
                                if (v != null) {
                                    node.key = k
                                    node.value = v
                                    node.comment = " No comment provided by engineer. "
                                    maps[k] = node
                                } else {

                                }
                            }
                            writeToLz(lzFile, maps)
                        }
                        L.log("===")
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        // Deprecated, we have merged

        // String table mapping from ios to android
        // String folder map : [pt-BR] -> [pt-rBR] (ios -> android)
        // => [pt-BR].xliff -> values-[pt-rBR]/string.xml
        private fun keyInAndroid(iosKey : String) : String {
            when (iosKey) {
                "pt-BR"   -> return "pt-rBR"
                "zh-Hans" -> return "zh-rCN"
                "zh-Hant" -> return "zh-rTW"
            }
            return iosKey
        }

        private fun iosLangs() : List<String> {
            return listOf("de", "en", "es", "fr", "it", "ja", "ko",
                    "pt-BR", "ru", "zh-Hans", "zh-Hant"
            )
        }

        @Deprecated("No need")
        // Returns the nodes that src node has those <tag attr="name"></tag>
        fun findNodes(src: Element, tag: String, attr: String, name: Regex) : Elements {
            val ans = Elements()
            val es = src.getElementsByTag(tag)
            for (e in es) {
                if (e.attr(attr).contains(name)) {
                    ans.add(e)
                }
            }
            return ans
        }

        @Deprecated("No need")
        fun listTransUnit(src : Element) : Map<String, XliffNode> {
            val ans = TreeMap<String, XliffNode>()

            val trans = src.getElementsByTag("trans-unit")
            L.log("%d units", trans.size)
            for (e in trans) {
                // FIXME
                //val s = e.getElementsByTag("source").first() // Fail to get <source></source>
                val s = e.childNode(2) as TextNode
                val t = e.getElementsByTag("target").first()
                val x = e.getElementsByTag("note").first()
                val node = XliffNode()
                node.source = s.text()
                node.target = t.text()
                node.note = x.text()
                val k = s.text()
                ans[k] = node
            }
            L.log("%d in map, same = %s", ans.size, ans.size == trans.size)
            return ans
        }

        fun test() {
            val p = "D:\\PhotoDirector_iOS\\Branch_MirrorBlend_2.5.1\\PhotoDirector\\PhotoDirector\\Resource\\zh-Hant.lproj"
            val s = "$p\\$LocalStr"
            //val x = loadLz(File(s))
            //writeToLz(File("$p\\x.txt"), x)
            val q = File(p).parentFile
            val lzs = FileUtil.listAllFiles(q, { f ->
                f.name.contains(LocalStr)
            })
            for (z in lzs) {
                L.log("Load $z")
                loadLz(z)
            }
        }

        fun loadLz(src : File) : Map<String, LoclzNode> {
            val ans = TreeMap<String, LoclzNode>()
            val all = FileUtil.readFromFile(src)
            val n = all.size - 1
            // Parse each part
            var i = 0
            while (i < n) {
                val si = all[i]
                val sj = all[i+1]
                if (si.startsWith("/*") && sj.startsWith("\"")) {
                    val d = LoclzNode()
                    d.from(si, sj)
                    val lower = false// Use lower case as key or not
                    if (lower) {
                        val k = d.key.toLowerCase()
                        if (ans.containsKey(k)) {
                            L.log("!! key conflicts on ${d.key}:\n old $k -> ${ans[k]?.value}\n new ${d.key} -> ${d.value}")
                        }
                        ans[k] = d
                    } else {
                        ans[d.key] = d
                    }
                    i += 1
                }
                i++
            }
            return ans
        }

        // Write mapping to file of Localization.strings
        fun writeToLz(dst: File, map: Map<String, LoclzNode>) {
            // Key's sorting order ?
//            val keys = map.keys.sortedWith(Comparator<String> { x, y -> {
//
//            } }
            val keys = map.keys.sortedWith(String.CASE_INSENSITIVE_ORDER)
            // Print as strings and write to file
            val strs = ArrayList<String>()
            for (k in keys) {
                val lz = map[k]
                if (lz != null) {
                    if (strs.isNotEmpty()) {
                        strs.add("")
                    }
                    // /* comment */
                    strs.add("/*${lz.comment}*/")
                    // "key" = "value";
                    strs.add("\"${lz.key}\" = \"${lz.value}\";")
                }
            }
            FileUtil.writeToFile(dst, strs, false)
        }

        fun checkTarget(ios : File) {
            var n = 0
            try {
                val iosd = Jsoup.parse(ios, "UTF-8")
                val es = iosd.getElementsByTag("trans-unit")
                for (e in es) {
                    val ts = e.getElementsByTag("target")
                    if (ts.isEmpty()) {
                        L.log("Missing?--\n\n$e\n");
                        n++
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            L.log("$n items may missing");

        }

        fun <T> pt(a: List<T>?) {
            Util.print(a)
        }

        fun <T> pt(a: Array<T>) {
            val n = a.size
            for (i in 0 until n) {
                val s = a[i]
                L.log("#%04d %s", i, s)
            }
        }
    }
}

// file of Localizable.strings
open class LoclzNode {
    var comment = ""
    var key = ""
    var value = ""

    override fun toString(): String {
        return "$key = $value, $comment"
    }

    fun from(s1:String, s2: String) {
        // parse comment
        comment = Util.stringWithin(s1, "/*", "*/")

        //parse k-v
        val a = IntArray(4)
        val x = s2.replace("\\\"", "\\-") // Handle for cases string "asd" = "a \" s \"d"
        a[0] = x.indexOf("\"")
        for (i in 1 until a.size) {
            a[i] = x.indexOf("\"", a[i-1] + 1)
        }
        key = s2.substring(a[0] + 1, a[1])
        value = s2.substring(a[2] + 1, a[3])
    }
}

// file of en.xliff
open class XliffNode {
    var source = ""
    var target = ""
    var note = ""

    override fun toString(): String {
        return "s = $source, t = $target, n = $note"
    }
}