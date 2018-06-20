package com.cyberlink.yousnap.preset

import android.content.res.AssetManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.Reader

class PdadjParser {
    companion object me{
        val gson = Gson()
        val gson2 = GsonBuilder().setPrettyPrinting().create()

        fun parse(str: String): EffectPdadj? {
            return gson.fromJson(str, EffectPdadj::class.java)
        }

        fun parseFile(file: File): EffectPdadj? {
            return parse(getReader(file))
        }

        fun parseAsset(assetFile: String, am: AssetManager): EffectPdadj? {
            return parse(getReader(assetFile, am))
        }

        fun parse(reader: Reader?): EffectPdadj? {
            if (reader == null) {
                return null
            } else {
                return gson.fromJson(reader, EffectPdadj::class.java);
            }
        }

        private fun getReader(assetFile: String, am: AssetManager): Reader? {
            try {
                return InputStreamReader(am.open(assetFile))
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }
        }

        private fun getReader(file: File): Reader? {
            try {
                return FileReader(file)
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }
        }
    }
}