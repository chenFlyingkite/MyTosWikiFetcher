package com.cyberlink.yousnap.preset

import android.content.res.AssetManager
import android.util.Log

class PresetManager {
    companion object me {
        val allPreset = arrayOf(
                  "preset/bw_06.json"
                , "preset/hdr_dreamy.json"
                , "preset/hdr_highlight.json"
                , "preset/hdr_realistic.json")

        fun loadPresets(assets: AssetManager) {
            for (s in allPreset) {
                load(s, assets)
            }
        }

        private fun load(name: String, am: AssetManager) {
            val p = PdadjParser.parseAsset(name, am)
            val f = p?.toGPUImageFilter()
            Log.e("Hi", "filter = $f")
            Log.e("Hi", "pdadj = ${p?.guid}")
        }
    }
}