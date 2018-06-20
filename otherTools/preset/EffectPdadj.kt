package com.cyberlink.yousnap.preset

import com.cyberlink.clgpuimage.GPUImageFilter
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName

class EffectPdadj {
    @SerializedName("guid")
    var guid = ""

    @SerializedName("name")
    var name: Map<String, String> = HashMap()

    @SerializedName("version")
    var version = ""

    @SerializedName("extras")
    var extras: List<Extras> = ArrayList()

    @SerializedName("body")
    var body: List<Map<String, String>> = ArrayList<HashMap<String, String>>()

    override fun toString(): String {
        return GsonBuilder().setPrettyPrinting().create().toJson(this)
    }

    fun toGPUImageFilter(): GPUImageFilter? {
        return GPUImageFilterCreator.toGPUImageFilter(this)
    }
}

class Extras {
    @SerializedName("os")
    var os = ""

    @SerializedName("minAppVersion")
    var minAppVersion = ""

    override fun toString(): String {
        return GsonBuilder().setPrettyPrinting().create().toJson(this)
    }
}