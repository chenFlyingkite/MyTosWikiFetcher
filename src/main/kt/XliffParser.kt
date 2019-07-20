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
            // ios already have translations in other places of xibs
            val useIosSelf = true
            // path of ios
            val iosPath = "D:\\ASD\\PHD_Strings\\PhotoDirector2"
            // path of Android
            val andoPath = "D:\\PhotoDirector_Android\\PHD_01\\PhotoDirector\\src\\main\\res"
            // String keys of Android.<string.name> : ios.<source>
            val andIos = HashMap<String, String>()
            andIos["common_Mirror"] = "Mirror"
            andIos["Info_Mirror_Effect"] = "Tap to select where to apply the effect"
            andIos["common_Photo_Blender"] = "Blender"
            val iosMe = HashMap<String, String>() // Key is unused, only use value
            val str = arrayListOf<String>()
            str.add("Mirror")
            /*
            iosMe["a"] = "Take a shot"
            iosMe["b"] = "Choose from gallery"
            iosMe["c"] = "Open Photo"
            */
            //-- produce as iosMe
            for (i in 0 until str.size) {
                iosMe[""+i] = str[i]
            }
            //--
            // apply = Produce file
            // Try to produce : apply =  true, prefix = "f_"
            // Delete tried   : apply = false, prefix = "f_"
            // Write source   : apply =  true, prefix = ""
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

                    var need: HashMap<String, String>
                    if (useIosSelf) {
                        need = HashMap(iosMe)
                    } else {
                        need = HashMap(andIos)
                    }

                    val n = iosStrings.size - 2
                    for (i in 0..n) {
                        val si = iosStrings[i]
                        val sn = si.trim()
                        for (ka in need.keys) {
                            val vi = need[ka]
                            // Format of ios
                            val fmt = "<source>$vi</source>"
                            val si1 = iosStrings[i + 1].trim()
                            val meet = sn.startsWith(fmt) && !si1.startsWith("<target>")
                            if (meet) {
                                var sa = ""
                                if (useIosSelf) {
                                    val xs = iosd.getElementsMatchingOwnText(vi).first()
                                    val a = xs.getElementsByTag("target").first()
                                    sa = "        <target>" + a.text() + "</target>"
                                } else {
                                    //using from android
                                    val a = andd.getElementsByAttributeValue("name", ka).first()
                                    sa = "        <target>" + a.text() + "</target>"
                                }
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