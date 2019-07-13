package main.kt

import flyingkite.files.FileUtil
import flyingkite.log.L
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.File
import java.io.IOException
import java.util.HashMap

open class XliffParser {
    companion object me {
        fun addStringsToIos() {
            // -- Parameters :
            // path of ios
            val iosPath = "D:\\ASD\\PHD_Strings"
            // path of Android
            val andoPath = "D:\\PhotoDirector_Android\\PHD_01\\PhotoDirector\\src\\main\\res"
            // String keys of Android.<string.name> : ios.<source>
            val andIos = HashMap<String, String>()
            andIos["common_Mirror"] = "Mirror"
            andIos["Info_Mirror_Effect"] = "Tap to select where to apply the effect"
            andIos["common_Photo_Blender"] = "Blender"
            // apply = Produce file
            val apply = true
            // Created file as prefix
            val prefix = ""
            val deletePrefix = true

            //------ Main body
            // Listing ios xliff files

            val ios = File(iosPath)
            val xlf = ".xliff"
            val fs = FileUtil.listAllFiles(ios)
            val xliff = FileUtil.findFile(fs) { z ->
                val name = z.name
                var omit = name == "en.xliff"
                if (!prefix.isEmpty()) {
                    omit = omit or name.startsWith(prefix)
                    if (name.startsWith(prefix) && deletePrefix) {
                        L.log("Delete %s", z)
                        z.delete()
                    }
                }

                !omit && name.contains(xlf)
            }

            pt<File>(xliff)

            // String folder convert---
            val m = HashMap<String, String>()
            m["pt-BR"] = "pt-rBR"
            m["zh-Hans"] = "zh-rCN"
            m["zh-Hant"] = "zh-rTW"

            // Check if missing
            if (xliff.size > 0) {
                XliffParser.checkTarget(xliff[0])
            }

            for (ioss in xliff) {
                // Get string file mapping
                val key = ioss.name.replace(xlf, "")
                var to: String? = m[key]
                if (to == null) {
                    to = key
                }
                val ands = File("$andoPath/values-$to/strings.xml")
                L.log("   %s\n-> %s", ioss.absolutePath, ands.absolutePath)

                //String andoAll = FileUtil.readFileAsString(ands);
                // old way
                // https://howtodoinjava.com/xml/parse-string-to-xml-dom/
                val iosStrings = FileUtil.readFromFile(ioss)
                try {
                    val andd = Jsoup.parse(ands, "UTF-8")
                    val aa = andd.getElementsByTag("string").size
                    //L.log("%s strings in android", aa);

                    val iosd = Jsoup.parse(ioss, "UTF-8")
                    val an = iosd.getElementsByTag("trans-unit").size
                    //L.log("%s strings in ios", an);

                    val need = HashMap(andIos)

                    for (i in iosStrings.indices) {
                        val si = iosStrings[i]
                        val sn = si.trim()
                        for (ka in need.keys) {
                            val vi = need[ka]
                            // Format of ios
                            val fmt = "<source>$vi</source>"
                            val si1 = if (i + 1 == iosStrings.size) "<target>" else iosStrings[i + 1]
                            val meet = sn.matches(fmt.toRegex()) && !si1.trim().startsWith("<target>")
                            if (meet) {
                                val a = andd.getElementsByAttributeValue("name", ka).first()
                                val sa = "        <target>" + a.text() + "</target>"
                                val end = si + "\n" + sa

                                L.log("-- #%04d = at ios (%s), add\n", i, key)
                                L.log("%s\n", end)
                                if (apply) {
                                    iosStrings[i] = end
                                }
                            }
                        }
                    }
                    if (apply) {
                        val fn = File(ioss.parent, prefix + ioss.name)
                        fn.delete()
                        fn.createNewFile()
                        FileUtil.writeToFile(fn, iosStrings, false)
                    }
                    L.log("===")
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
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
            val n = a?.size ?: 0
            L.log("%s items", n)
            for (i in 0 until n) {
                L.log("#%2d : %s", i, a!![i])
            }
        }
    }
}