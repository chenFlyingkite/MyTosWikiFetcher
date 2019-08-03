package main.kt

import flyingkite.files.FileUtil
import flyingkite.functional.Projector
import flyingkite.log.L
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.File
import java.io.IOException
import java.util.HashMap

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
            val iosPath = "D:\\ASD\\PHD_Strings\\PhotoDirector"
            val iosXliffPath = "D:\\ASD\\PHD_Strings\\Parsed"
            // path of Android
            val andoPath = "D:\\PhotoDirector_Android\\PHD_01\\PhotoDirector\\src\\main\\res"
            // String keys of Android.<string.name> : ios.<source>
            val andIos = HashMap<String, String>()
            andIos["message_rating_title"] = "Your Photo Looks Perfect!"
            andIos["message_rating_info"] = "We'd appreciate a good rating for PhotoDirector"
            andIos["common_NoThanks"] = "No, thanks!"
            andIos["common_GiveGoodRate"] = "Rate us"
            val iosMe = HashMap<String, String>() // Key is unused, only use value
            val str = arrayListOf<String>()
            str.add("Title")
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
            val xliff = FileUtil.findFile(fs, { z ->
                val name = z.name
                val isPx = name.startsWith(prefix)
                var omit = false//name == "en.xliff"
                if (!prefix.isEmpty()) {
                    omit = omit || isPx
                    if (isPx && deletePrefix) {
                        L.log("Delete %s", z)
                        z.delete()
                    }
                }

                !omit && name.contains(xlf)// && name == "fr.xliff"
            })

            pt(xliff)

            // String folder map : [pt-BR] -> [pt-rBR] (ios -> android)
            // => [pt-BR].xliff -> values-[pt-rBR]/string.xml
            val m = HashMap<String, String>()
            m["pt-BR"] = "pt-rBR"
            m["zh-Hans"] = "zh-rCN"
            m["zh-Hant"] = "zh-rTW"

            // Check if missing
            if (xliff.size > 0) {
                XliffParser.checkTarget(xliff[0])
            }

            for (ioss in xliff) {
                if (ioss.name == "en.xliff") {
                    // Copy xliffs to folder
                    val fn = File(ioss.parent, prefix + ioss.name)
                    val fx = File(iosXliffPath, prefix + ioss.name)
                    fx.parentFile.mkdirs()
                    FileUtil.copy(fn.absolutePath, fx.absolutePath)
                } else {
                    // ioss = ios string
                    // ands = android string
                    // Get string file mapping
                    val key = ioss.name.replace(xlf, "")
                    val to = m[key] ?: key
                    val ands = File("$andoPath/values-$to/strings.xml")
                    L.log("   %s\n-> %s", ioss.absolutePath, ands.absolutePath)

                    //String andoAll = FileUtil.readFileAsString(ands);
                    // old way
                    // https://howtodoinjava.com/xml/parse-string-to-xml-dom/
                    val iosStrings = FileUtil.readFromFile(ioss)
                    try {
                        // Find android's nodes, <string>
                        val andd = Jsoup.parse(ands, "UTF-8")
                        val aa = andd.getElementsByTag("string").size
                        //L.log("%s strings in android", aa);

                        // Find ios's nodes, <trans-unit>
                        val iosd = Jsoup.parse(ioss, "UTF-8")
                        val an = iosd.getElementsByTag("trans-unit").size
                        //L.log("%s strings in ios", an);

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
                                // Meet if it has source node, no target node
                                val meet = sn.startsWith(fmt) && !si1.startsWith("<target>")
                                if (meet) {
                                    var a: Element
                                    if (useIosSelf) {
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
                                        //using from android
                                        a = andd.getElementsByAttributeValue("name", ka).first()
                                    }
                                    // Target string is s
                                    var s = a.text()
                                    // TODO : \n ?
                                    // Replacing \' to '
                                    if (s.contains("\\")) {
                                        L.log("---- has \\ => %s", s)
                                    }
                                    s = s.replace("\\'", "'")

                                    // Compose target string and print if apply
                                    val sa = "$lead<target>$s</target>"
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
                            // Write to file
                            val fn = File(ioss.parent, prefix + ioss.name)
                            fn.delete()
                            fn.createNewFile()
                            FileUtil.writeToFile(fn, iosStrings, false)

                            // Copy xliffs to folder
                            val fx = File(iosXliffPath, prefix + ioss.name)
                            fx.parentFile.mkdirs()
                            FileUtil.copy(fn.absolutePath, fx.absolutePath)
                        }
                        L.log("===")
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
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