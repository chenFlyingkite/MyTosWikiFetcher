package com.cyberlink.yousnap.preset

import android.util.Log
import com.cyberlink.clgpuimage.CLHdrEdgeFilter
import com.cyberlink.clgpuimage.CLHdrGlowFilter
import com.cyberlink.clgpuimage.GPUImageBrightnessFilter
import com.cyberlink.clgpuimage.GPUImageClarityFilter
import com.cyberlink.clgpuimage.GPUImageContrastFilter
import com.cyberlink.clgpuimage.GPUImageFilter
import com.cyberlink.clgpuimage.GPUImageFilterGroup
import com.cyberlink.clgpuimage.GPUImageHSVExFilter
import com.cyberlink.clgpuimage.GPUImageHighlightShadowFilter
import com.cyberlink.clgpuimage.GPUImageSaturationFilter
import com.cyberlink.clgpuimage.GPUImageToneCurveRGBFilter
import com.cyberlink.clgpuimage.GPUImageWhiteBalanceFilter
import kotlin.math.roundToInt
import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction2

object GPUImageFilterCreator {
    fun toGPUImageFilter(effect: EffectPdadj?) : GPUImageFilter? {
        if (effect == null) return null

        val filter = GPUImageFilterGroup(false)
        val version = effect.version
        val n = effect.body.size
        for (i in 0 until n) {
            val f = getGPUImageFilter(effect.body[i], version)
            if (f != null) {
                filter.addFilter(f)
            } else {
                Log.e("Hi", "X_X no add -> ${effect.body[i]["name"]}")
            }
        }
        return filter
    }

    //package com.cyberlink.photodirector.widgetpool.panel.effectpanel;
    // public class EffectXmlParser
    // package com.cyberlink.photodirector.kernelctrl.glviewengine
    //public class GPUImageFilterBuilder
    fun getGPUImageFilter(params: Map<String, String>, version: String) : GPUImageFilter? {
        val name = params["name"]

        when (version) {
            "5.0" -> {

            }
            "6.0" -> {
                when (name) {
                    "XXX" -> { // This is sample method
                        val f = GPUImageContrastFilter()
                        setFloat(params, "intensity", f::setContrast)
                        return f
                    }
                    "CLHdrEdgeFilter" -> {
                        val f = CLHdrEdgeFilter()
                        setFloat(params, "edge", f::setStrength)
                        setFloat(params, "radius", f::setRadius)
                        setFloat(params, "balance", f::setBalance)
                        return f
                    }
                    "CLHdrGlowFilter" -> {
                        val f = CLHdrGlowFilter()
                        setFloat(params, "glow", f::setStrength)
                        setFloat(params, "radius", f::setRadius)
                        setFloat(params, "balance", f::setBalance)
                        return f
                    }
                    "GPUImageBrightnessFilter" -> {
                        val f = GPUImageBrightnessFilter()
                        setFloat(params, "intensity", f::setBrightness)
                        return f
                    }
                    "GPUImageClarityFilter" -> {
                        val f = GPUImageClarityFilter()
                        setFloat(params, "intensity", f::setClarity)
                        setRoundedInt(params, "radius", f::setRadius)
                        return f
                    }
                    "GPUImageContrastFilter" -> {
                        val f = GPUImageContrastFilter()
                        setFloat(params, "intensity", f::setContrast)
                        return f
                    }
                    "GPUImageHighlightShadowFilter" -> {
                        val f = GPUImageHighlightShadowFilter()
                        setFloat(params, "shadow", f::setShadows)
                        setFloat(params, "highlight", f::setHighlights)
                        return f
                    }
                    "GPUImageHSVExFilter" -> {
                        val f = GPUImageHSVExFilter()
                        // HSV's set method order
                        val hsv = arrayOf("hue", "saturation", "lightness")
                        val fns = arrayOf(f::setHue, f::setSaturation, f::setLuminance)
                        val key = arrayOf("red", "orange", "yellow", "green", "aqua", "blue", "purple")
                        for (i in 0 until hsv.size) {
                            for (j in 0 until key.size) {
                                val jsonKey = hsv[i] + "_" + key[j]
                                setIntFloat(params, jsonKey, i, fns[i])
                            }
                        }
                        return f
                    }
                    "GPUImageSaturationFilter" -> {
                        val f = GPUImageSaturationFilter()
                        setFloat(params, "intensity", f::setSaturation)
                        return f
                    }
                    "GPUImageToneCurveRGBFilter" -> {
                        val f = GPUImageToneCurveRGBFilter()
                        setFloatArray_B(params, "curve", f::setRgbCurve)
                        return f
                    }
                    "GPUImageWhiteBalanceFilter" -> {
                        val f = GPUImageWhiteBalanceFilter()
                        setFloat(params, "tint", f::setTint)
                        setFloat(params, "temperature", f::setTemperature)
                        return f
                    }
                }
            }
        }
        return null
    }

    /*
    private class SetsV<T> {
        fun setFloatArray_B(params: Map<String, String>, key: String, run: KFunction1<FloatArray, T>) {
            val b = parseFloatArray(params[key])
            if (b != null) {
                run.invoke(b)
            }
        }
    }
    */

    private fun setIntFloat(params: Map<String, String>, key: String, int1: Int, run: KFunction2<Int, Float, Unit>) {
        val b = params[key]?.toFloat()
        if (b != null) {
            run.invoke(int1, b)
        }
    }

    private fun setFloatArray_B(params: Map<String, String>, key: String, run: KFunction1<FloatArray, Boolean>) {
        val b = parseFloatArray(params[key])
        if (b != null) {
            run.invoke(b)
        }
    }

    private fun setFloatArray(params: Map<String, String>, key: String, run: KFunction1<FloatArray, Unit>) {
        val b = parseFloatArray(params[key])
        if (b != null) {
            run.invoke(b)
        }
    }

    private fun setFloat(params: Map<String, String>, key: String, run: KFunction1<@ParameterName(name = "Value") Float, Unit>) {
        val b = params[key]?.toFloat()
        if (b != null) {
            run.invoke(b)
        }
    }

    private fun setRoundedInt(params: Map<String, String>, key: String, run: KFunction1<@ParameterName(name = "Value") Int, Unit>) {
        val b = params[key]?.toFloat()?.roundToInt()
        if (b != null) {
            run.invoke(b)
        }
    }

    private fun setInt(params: Map<String, String>, key: String, run: KFunction1<@ParameterName(name = "Value") Int, Unit>) {
        val b = params[key]?.toInt()
        if (b != null) {
            run.invoke(b)
        }
    }

    /**
     * [0.1, 0.2, 0.3] => float[]{0.1, 0.2, 0.3}
     */
    private fun parseFloatArray(str: String?) : FloatArray? {
        if (str == null) {
            return null
        } else {
            // Replace 0x5B = [, 0X5D = ]
            val vs = str.replace(Regex("[\\x5B\\x5D]"), "")
            val v = vs.split(",")
            val n = v.size
            val result = FloatArray(n)
            for (i in 0 until n) {
                result[i] = v[i].trim().toFloat()
            }
            return result
        }
    }
}